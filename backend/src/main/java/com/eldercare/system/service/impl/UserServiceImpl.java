package com.eldercare.system.service.impl;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.service.UserService;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eldercare.system.constant.UserRoleConstant;
import com.eldercare.system.entity.Role;
import com.eldercare.system.entity.User;
import com.eldercare.system.config.CaptchaProperties;
import com.eldercare.system.config.JwtProperties;
import com.eldercare.system.exception.BusinessException;
import com.eldercare.system.mapper.*;
import com.eldercare.system.security.LoginUser;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.util.PasswordUtil;
import com.eldercare.system.util.ImgUploadUtil;
import com.eldercare.system.dto.user.*;
import com.eldercare.system.email.EmailTemplate;
import com.eldercare.system.email.RedisConstant;
import com.eldercare.system.service.ThreadService;
import com.eldercare.system.vo.user.*;
import com.eldercare.system.util.ApiResult;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.eldercare.system.util.PasswordUtil.hashPassword;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService{

    /** 用户 Mapper */
    @Autowired
    private UserMapper userMapper;

    /** Redis 服务 */
    @Autowired
    private RedisService redisService;

    /** 角色 Mapper */
    @Autowired
    private RoleMapper roleMapper;

    /** 护理记录 Mapper */
    @Autowired
    private NursingRecordMapper nursingRecordMapper;

    /** 外出记录 Mapper */
    @Autowired
    private OutingRecordMapper outingRecordMapper;

    /** 退住记录 Mapper */
    @Autowired
    private CheckOutRecordMapper checkoutRecordMapper;

    /** 图片上传工具 */
    @Autowired
    private ImgUploadUtil imgUploadUtil;

    /** JWT 配置属性 */
    @Autowired
    private JwtProperties jwtProperties;

    /** 验证码配置 */
    @Autowired
    private CaptchaProperties captchaProperties;

    /** Redis 字符串模板，用于读取注册验证码 */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /** 异步邮件发送服务 */
    @Autowired
    private ThreadService threadService;

    /**
     * 获取 JWT 过期时间（毫秒），用于 Redis token 同步过期
     *
     * @return 过期毫秒数，配置缺失时默认 72000000
     */
    private long jwtTtlMillis() {
        Long ttl = jwtProperties.getTtl();
        return ttl != null && ttl > 0 ? ttl : 72000000L;
    }

    /**
     * 从 SecurityContextHolder 获取当前登录用户（principal 必须是 LoginUser）。
     *
     * <p>正常请求会先经 JwtAuthenticationFilter 写入 SecurityContext，理论不会为空；
     * 此处保留防御性检查，避免配置错误时直接空指针或强转异常。
     *
     * @return 当前登录用户
     * @throws BusinessException 当未登录或 principal 类型不符时抛出 401
     */
    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(401, "用户身份校验失败");
        }
        return loginUser;
    }

    /**
     * 用户登录
     *
     * @param user 登录参数
     * @return 登录结果
     */
    @Override
    public ApiResult<LoginResponseVO> login(UserLoginRequest user) {
        //根据用户名或邮箱查询用户
        ApiResult<LoginResponseVO> result = new ApiResult<>();
        LoginResponseVO loginResponse = new LoginResponseVO();

        // ===== 验证码校验（必须在账号密码查询之前） =====
        if (Boolean.TRUE.equals(captchaProperties.getEnabled())) {
            if (user.getCode() == null || user.getCode().isEmpty()
                    || user.getUuid() == null || user.getUuid().isEmpty()) {
                result.setCode(400);
                result.setMessage("请输入验证码");
                return result;
            }
            String redisKey = RedisConstant.LOGIN_CAPTCHA + user.getUuid();
            String storedAnswer = stringRedisTemplate.opsForValue().get(redisKey);
            if (storedAnswer == null) {
                result.setCode(400);
                result.setMessage("验证码已过期，请刷新后重试");
                return result;
            }
            if (!storedAnswer.equals(user.getCode())) {
                stringRedisTemplate.delete(redisKey);
                result.setCode(400);
                result.setMessage("验证码错误");
                return result;
            }
            // 验证码正确，删除 Redis key（一次性使用）
            stringRedisTemplate.delete(redisKey);
        }

        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.and(w -> w.eq("user_name", user.getAccount()).or().eq("email", user.getAccount()));
        qw.eq("del_flag", "0");
        User user0 = userMapper.selectOne(qw);
        Boolean flag = false;
        if(user0!=null)
             flag = PasswordUtil.checkPassword(user.getPassword(), user0.getPassword());
        // 判断用户是否存在且密码正确
        if (user0 != null && flag) {
            // 账号状态校验：2=禁用，拒绝登录
            Integer status = user0.getStatus();
            if (status != null && status == 2) {
                result.setCode(403);
                result.setData(loginResponse);
                result.setMessage("账号已被禁用，请联系管理员");
                return result;
            }
            // 根据 role_id 查询 role 表，获取 role_key 和 role_name
            Role role = roleMapper.selectById(user0.getRoleId());
            String roleKey = role != null ? role.getRoleKey() : "";
            String roleName = role != null ? role.getRoleName() : "";
            int statusValue = status != null ? status : 1;
            final int finalStatusValue = statusValue;
            String token = JWTUtil.getToken(new HashMap<>() {{
                put("userId", String.valueOf(user0.getUserId()));
                put("email", user0.getEmail());
                put("roleId", String.valueOf(user0.getRoleId()));
                put("roleKey", roleKey);
                put("status", String.valueOf(finalStatusValue));
            }});
            //将token传给redis，key 使用 email，过期时间与 jwt.ttl（毫秒）一致
            redisService.setToken(user0.getEmail(), token, jwtTtlMillis());
            System.out.println("登录成功,token为:" + token);
            result.setCode(200);
            loginResponse.setToken(token);
            loginResponse.setUserId(user0.getUserId());
            loginResponse.setEmail(user0.getEmail());
            loginResponse.setRealName(user0.getRealName());
            loginResponse.setRoleId(user0.getRoleId());
            loginResponse.setRoleKey(roleKey);
            loginResponse.setRoleName(roleName);
            loginResponse.setStatus(statusValue);
            result.setData(loginResponse);
            result.setMessage("登录成功!");
        } else {
            result.setCode(400);
            result.setData(loginResponse);
            result.setMessage("登录失败!请检查账号或密码是否正确！");
        }
        return result;
    }

    /**
     * 新增用户
     *
     * @param user 用户新增参数
     * @return 操作结果
     */
    @Override
    public ApiResult add(UserAddRequest user) {
        ApiResult result = new ApiResult();
        // 新体系下后台只能新增管理员，护工统一走注册流程
        Long roleId = UserRoleConstant.ADMIN_ID;
        // 查询是否已存在相同用户名
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUserName());
        queryWrapper.eq("del_flag", "0");
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            // 如果用户名已存在，返回错误信息
            result.setCode(500);
            result.setMessage("添加失败，用户名已存在");
            return result;
        }
        User user0 = new User();
        user0.setUserName(user.getUserName());
        user0.setPassword(hashPassword(user.getPassword()));
        user0.setRealName(user.getRealName());
        user0.setPhone(user.getPhone());
        user0.setEmail(user.getEmail());
        user0.setGender(user.getGender());
        user0.setRoleId(roleId);
        user0.setStatus(1);
        if (userMapper.insert(user0) > 0) {
            result.setCode(200);
            result.setMessage("添加成功");
        }
        else {
            result.setCode(500);
            result.setMessage("添加失败，其他错误");
        }
        return result;
    }

    /**
     * 护工注册：固定创建 role_id=3、status=0 的待审核账号
     *
     * @param request 注册参数
     * @return 注册结果
     */
    @Override
    public ApiResult register(CaregiverRegisterRequest request) {
        ApiResult result = new ApiResult();
        // 必填校验
        if (request.getUserName() == null || request.getUserName().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getCode() == null || request.getCode().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getRealName() == null || request.getRealName().isBlank()
                || request.getPhone() == null || request.getPhone().isBlank()
                || request.getGender() == null || request.getGender().isBlank()) {
            result.setCode(500);
            result.setMessage("注册失败，请填写完整信息");
            return result;
        }
        // 用户名格式：4-20 位字母/数字/下划线
        if (!request.getUserName().matches("^[a-zA-Z0-9_]{4,20}$")) {
            result.setCode(500);
            result.setMessage("注册失败，用户名需为 4-20 位字母、数字或下划线");
            return result;
        }
        // 用户名查重
        QueryWrapper<User> userNameWrapper = new QueryWrapper<>();
        userNameWrapper.eq("user_name", request.getUserName());
        userNameWrapper.eq("del_flag", "0");
        if (userMapper.selectOne(userNameWrapper) != null) {
            result.setCode(500);
            result.setMessage("注册失败，用户名已存在");
            return result;
        }
        // 邮箱查重
        if (getUserByEmail(request.getEmail())) {
            result.setCode(500);
            result.setMessage("注册失败，邮箱已被注册");
            return result;
        }
        // 验证码校验
        String rightCode = stringRedisTemplate.opsForValue().get(RedisConstant.REGISTER_CODE + request.getEmail());
        if (rightCode == null || !rightCode.equals(request.getCode())) {
            result.setCode(500);
            result.setMessage("注册失败，验证码错误或已过期");
            return result;
        }
        // 验证通过，删除验证码防止重复使用
        stringRedisTemplate.delete(RedisConstant.REGISTER_CODE + request.getEmail());

        // 创建护工账号，固定角色和待审核状态
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(hashPassword(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setRoleId(UserRoleConstant.CAREGIVER_ID);
        user.setStatus(0);
        if (userMapper.insert(user) > 0) {
            result.setCode(200);
            result.setMessage("注册成功，请等待管理员审核");
        } else {
            result.setCode(500);
            result.setMessage("注册失败，其他错误");
        }
        return result;
    }

    /** 重置密码时使用的默认明文密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    /**
     * 审核护工：将 status 从 0 改为 1
     */
    @Override
    public ApiResult audit(Long userId) {
        ApiResult result = new ApiResult();
        User user = userMapper.selectById(userId);
        if (user == null || "1".equals(user.getDelFlag())) {
            result.setCode(500);
            result.setMessage("审核失败，用户不存在");
            return result;
        }
        // 审核仅限护工，防止误审核管理员
        if (!UserRoleConstant.CAREGIVER_ID.equals(user.getRoleId())) {
            result.setCode(500);
            result.setMessage("审核失败，仅护工需要审核");
            return result;
        }
        if (user.getStatus() != null && user.getStatus() != 0) {
            result.setCode(500);
            result.setMessage("审核失败，该账号非待审核状态");
            return result;
        }
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId).set("status", 1);
        if (userMapper.update(null, uw) > 0) {
            result.setCode(200);
            result.setMessage("审核通过");
        } else {
            result.setCode(500);
            result.setMessage("审核失败，其他错误");
        }
        return result;
    }

    /**
     * 启用账号：将 status 从 2 改回 1，超级管理员不可被启用（本来就是启用状态）
     */
    @Override
    public ApiResult enable(Long userId) {
        ApiResult result = new ApiResult();
        User user = userMapper.selectById(userId);
        if (user == null || "1".equals(user.getDelFlag())) {
            result.setCode(500);
            result.setMessage("启用失败，用户不存在");
            return result;
        }
        // 超级管理员自我保护
        if (UserRoleConstant.SUPER_ADMIN_ID.equals(user.getRoleId())) {
            result.setCode(500);
            result.setMessage("启用失败，不允许操作超级管理员");
            return result;
        }
        if (user.getStatus() == null || user.getStatus() != 2) {
            result.setCode(500);
            result.setMessage("启用失败，该账号未被禁用");
            return result;
        }
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId).set("status", 1);
        if (userMapper.update(null, uw) > 0) {
            result.setCode(200);
            result.setMessage("启用成功");
        } else {
            result.setCode(500);
            result.setMessage("启用失败，其他错误");
        }
        return result;
    }

    /**
     * 禁用账号：将 status 改为 2，超级管理员不可被禁用，不可禁用自己
     */
    @Override
    public ApiResult disable(Long userId) {
        ApiResult result = new ApiResult();
        User user = userMapper.selectById(userId);
        if (user == null || "1".equals(user.getDelFlag())) {
            result.setCode(500);
            result.setMessage("禁用失败，用户不存在");
            return result;
        }
        // 超级管理员自我保护：不允许禁用
        if (UserRoleConstant.SUPER_ADMIN_ID.equals(user.getRoleId())) {
            result.setCode(500);
            result.setMessage("禁用失败，不允许禁用超级管理员");
            return result;
        }
        // 禁止禁用当前登录用户
        Long currentUserId = getCurrentLoginUser().getUserId();
        if (userId.equals(currentUserId)) {
            result.setCode(500);
            result.setMessage("禁用失败，不能禁用自己的账号");
            return result;
        }
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId).set("status", 2);
        if (userMapper.update(null, uw) > 0) {
            // 清除被禁用账号的登录态，立即生效
            if (user.getEmail() != null) {
                redisService.deleteToken(user.getEmail());
            }
            result.setCode(200);
            result.setMessage("禁用成功");
        } else {
            result.setCode(500);
            result.setMessage("禁用失败，其他错误");
        }
        return result;
    }

    /**
     * 重置密码为默认密码（123456）
     */
    @Override
    public ApiResult resetPassword(Long userId) {
        ApiResult result = new ApiResult();
        User user = userMapper.selectById(userId);
        if (user == null || "1".equals(user.getDelFlag())) {
            result.setCode(500);
            result.setMessage("重置失败，用户不存在");
            return result;
        }
        // 禁止重置超级管理员密码
        if (UserRoleConstant.SUPER_ADMIN_ID.equals(user.getRoleId())) {
            result.setCode(500);
            result.setMessage("重置失败，不允许重置超级管理员密码");
            return result;
        }
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId).set("password", hashPassword(DEFAULT_PASSWORD));
        if (userMapper.update(null, uw) > 0) {
            result.setCode(200);
            result.setMessage("重置成功，新密码为 " + DEFAULT_PASSWORD);
        } else {
            result.setCode(500);
            result.setMessage("重置失败，其他错误");
        }
        return result;
    }

    /**
     * 分页查询用户列表
     *
     * @param user 查询参数
     * @return 用户列表
     */
    @Override
    public ApiResult<UserListResultVO> list(UserListRequest user) {
        ApiResult<UserListResultVO> result = new ApiResult<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(user.getUserName() != null, "real_name", user.getUserName());
        queryWrapper.eq("del_flag", "0");
        queryWrapper.last("limit " + (user.getPageNum() - 1) * user.getPageSize() + "," + user.getPageSize());
        queryWrapper.ne("role_id","1");
        //倒序展示展示
        queryWrapper.orderByDesc("user_id");
        List<User> users = userMapper.selectList(queryWrapper);
        // 一次性查询所有角色，构建 roleId -> roleName 映射，避免逐条查询和硬编码 switch
        Map<Long, String> roleNameMap = new HashMap<>();
        for (Role role : roleMapper.selectList(null)) {
            roleNameMap.put(role.getRoleId(), role.getRoleName());
        }
        // 将User类型的users转换为UserResult类型的users
        List<UserResultVO> userResults =new java.util.ArrayList<>();
        Long index = 1L;
        for (User user0 : users) {
            UserResultVO userResult = new UserResultVO();
            userResult.setUserId(user0.getUserId());
            userResult.setUserName(user0.getUserName());
            userResult.setRealName(user0.getRealName());
            userResult.setPhone(user0.getPhone());
            userResult.setEmail(user0.getEmail());
            userResult.setGender(user0.getGender());
            userResult.setIndex(user0.getUserId());
            userResult.setStatus(user0.getStatus());
            // 角色 name 直接来自 role 表
            userResult.setRole(roleNameMap.get(user0.getRoleId()));
            userResults.add(userResult);
        }
        try {
            QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.like(user.getUserName() != null, "real_name", user.getUserName());
            queryWrapper2.eq("del_flag", "0");
            queryWrapper2.ne("role_id","1");
            Long count = userMapper.selectCount(queryWrapper2);
            int total = count == null ? 0 : count.intValue();
            result.setData(new UserListResultVO(userResults,total));
            result.setCode(200);
            result.setMessage("查询成功");

        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询失败");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 批量删除用户（仅超级管理员可调用，由 Security 过滤器保证）
     *
     * @param userNameList 用户名列表
     * @return 操作结果
     */
    @Override
    public ApiResult delete(List<String> userNameList) {
        ApiResult result = new ApiResult();
        if (userNameList == null || userNameList.isEmpty()) {
            result.setCode(500);
            result.setMessage("用户名列表不能为空");
            return result;
        }

        // 当前登录用户 ID，用于禁止删除自己的检查
        Long currentUserId = getCurrentLoginUser().getUserId();

        int totalDeleted = 0;
        for (String userName : userNameList) {
            Long userId = userMapper.selectIdByUsername(userName);

            // 查询目标用户角色，禁止删除超级管理员
            User targetUser = userMapper.selectById(userId);
            if (targetUser != null && UserRoleConstant.SUPER_ADMIN_ID.equals(targetUser.getRoleId())) {
                result.setCode(500);
                result.setMessage("删除失败，不能删除超级管理员账号");
                return result;
            }

            // 禁止删除当前登录用户
            if (currentUserId != null && currentUserId.equals(userId)) {
                result.setCode(500);
                result.setMessage("删除失败，不能删除自己的账号");
                return result;
            }

            // 将customer表的user_id字段设置为null
            userMapper.updateUserId(userId);
            // 将nursing_record表中的del_flag字段设置为1
            nursingRecordMapper.updateNursingRecordDelFlagByUserId(userId);
            // 将outing_record表中的del_flag字段设置为1
            outingRecordMapper.updateOutingRecordDelFlagByUserId(userId);
            // 将checkout_record表中的del_flag字段设置为1
            checkoutRecordMapper.updateCheckoutRecordDelFlagByUserId(userId);
            // 改为逻辑删除，将del_flag字段设置为1
            int count = userMapper.deleteUser(userName);
            if (count > 0) {
                totalDeleted += count;
            }
        }

        if (totalDeleted > 0) {
            result.setCode(200);
            result.setMessage("删除成功，共删除 " + totalDeleted + " 条记录");
        } else {
            result.setCode(500);
            result.setMessage("删除失败，未找到对应用户");
        }

        return result;
    }

    /**
     * 修改用户信息
     *
     * @param user 用户修改参数
     * @return 操作结果
     */
    @Override
    public ApiResult update(UserAddRequest user) {
        ApiResult result = new ApiResult();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUserName());
        User user0 = userMapper.selectOne(queryWrapper);
        if (user0 == null) {
            result.setCode(500);
            result.setMessage("修改失败，未找到用户");
            return result;
        }
        Long roleId = user0.getRoleId();
        user0.setUserName(user.getUserName());
        // 密码非空才更新，且必须加密存储；空则保留原密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user0.setPassword(hashPassword(user.getPassword()));
        }
        user0.setRealName(user.getRealName());
        user0.setRoleId(roleId);
        user0.setPhone(user.getPhone());
        user0.setEmail(user.getEmail());
        user0.setGender(user.getGender());
        if (userMapper.update(user0, queryWrapper) > 0) {
            result.setCode(200);
            result.setMessage("修改成功");
        } else {
            result.setCode(500);
            result.setMessage("修改失败，其他错误");
        }
        return result;
    }

    /**
     * 统计用户数量
     *
     * @return 用户数量
     */
    @Override
    public ApiResult<Long> count() {
        // 获取用户数量
        // 变量准备
        ApiResult<Long> result = new ApiResult<>();
        Long data;
        // 数据库查询
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", "0");
            queryWrapper.ne("role_id","1");
            data = userMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询用户数量数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 统计各角色用户数量
     *
     * @return 角色人数统计
     */
    @Override
    public ApiResult<List<RoleNumVO>> roleNum() {
        // 获取各个角色用户数量
        // 变量准备
        ApiResult<List<RoleNumVO>> result = new ApiResult<>();
        List<RoleNumVO> data = new ArrayList<>();
        List<Role> roles;
        // 数据库查询
        // 获取所有角色名称
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", "0");
        queryWrapper.ne("role_key","super_admin"); //roleNum() 排除 super_admin
        try {
            roles = roleMapper.selectList(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取角色名称数据库错误");
            throw e;
        }
        // 获取各个角色用户数量
        for (Role role : roles) {
            RoleNumVO roleNumResult = new RoleNumVO();
            roleNumResult.setName(role.getRoleName());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("del_flag", "0");
            userQueryWrapper.eq("role_id", role.getRoleId());
            Long count;
            try {
                count = userMapper.selectCount(userQueryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("获取角色用户数量数据库错误");
                throw e;
            }
            roleNumResult.setValue(count);
            data.add(roleNumResult);
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 根据邮箱判断用户是否存在
     *
     * @param email 邮箱地址
     * @return true=用户存在，false=用户不存在
     */
    @Override
    public boolean getUserByEmail(String email) {
        // 检查邮箱是否已被使用（用 count 而非 selectOne，避免多行异常）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("del_flag", "0");
        return userMapper.selectCount(queryWrapper) > 0;
    }

    /** 本地上传目录 */
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像访问地址
     */
    @Override
    public ApiResult<String> uploadFile(MultipartFile file) {
        ApiResult<String> result = new ApiResult<>();
        Long userId = getCurrentLoginUser().getUserId();

        try {
            String originalFilename = file.getOriginalFilename();
            String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String name = UUID.randomUUID().toString() + extendName;
            String upload = imgUploadUtil.upload(file.getBytes(), name, file.getContentType());
            userMapper.updateAvatar(upload, userId);
            result.setCode(200);
            result.setData(upload);
            result.setMessage("上传成功");
            return result;
        } catch (IOException e) {
            result.setCode(500);
            System.out.println("e: " + e);
            result.setMessage("上传失败");
            return result;
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前用户头像
     *
     * @return 头像访问地址
     */
    @Override
    public ApiResult<String> getAvatar() {
        ApiResult<String> result = new ApiResult<>();
        Long userId = getCurrentLoginUser().getUserId();

        // 获取用户头像
        try {
            String avatar = userMapper.selectAvatar(userId);
            if (avatar == null || avatar.isEmpty()) {
                avatar = "https://img95.699pic.com/element/40112/2503.png_300.png"; // 设置默认头像 URL
            }
            result.setCode(200);
            result.setData(avatar);
            result.setMessage("查询成功");
        }catch (Exception e){
            result.setData("https://img95.699pic.com/element/40112/2503.png_300.png");//默认头像
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 获取当前用户邮箱
     *
     * @return 邮箱地址
     */
    @Override
    public ApiResult<String> getEmail() {
        ApiResult<String> result = new ApiResult<>();
        Long userId = getCurrentLoginUser().getUserId();

        try {
            String email = userMapper.selectEmail(userId);
            result.setCode(200);
            result.setData(email);
            result.setMessage("查询成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 获取当前用户在数据库中的最新状态（不读 JWT，供待审核护工刷新感知审核结果）
     */
    @Override
    public ApiResult<Integer> getStatus() {
        ApiResult<Integer> result = new ApiResult<>();
        Long userId = getCurrentLoginUser().getUserId();

        try {
            User user = userMapper.selectById(userId);
            if (user == null || "1".equals(user.getDelFlag())) {
                result.setCode(500);
                result.setMessage("用户不存在");
                return result;
            }
            int statusValue = user.getStatus() != null ? user.getStatus() : 1;
            result.setCode(200);
            result.setData(statusValue);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 获取当前登录用户资料
     */
    @Override
    public ApiResult<UserProfileVO> getProfile() {
        ApiResult<UserProfileVO> result = new ApiResult<>();
        Long userId = getCurrentLoginUser().getUserId();
        try {
            User user = userMapper.selectById(userId);
            if (user == null || "1".equals(user.getDelFlag())) {
                result.setCode(500);
                result.setMessage("用户不存在");
                return result;
            }
            UserProfileVO vo = new UserProfileVO();
            vo.setUserName(user.getUserName());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setGender(user.getGender());
            if (user.getRoleId() != null) {
                Role role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    vo.setRoleName(role.getRoleName());
                }
            }
            result.setCode(200);
            result.setData(vo);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("数据库错误");
        }
        return result;
    }

    /**
     * 更新当前登录用户资料（禁止修改角色和状态）
     */
    @Override
    public ApiResult updateProfile(UserProfileUpdateRequest request) {
        ApiResult result = new ApiResult<>();
        Long userId = getCurrentLoginUser().getUserId();
        try {
            User user = userMapper.selectById(userId);
            if (user == null || "1".equals(user.getDelFlag())) {
                result.setCode(500);
                result.setMessage("用户不存在");
                return result;
            }
            if (request.getRealName() != null) {
                user.setRealName(request.getRealName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            // 邮箱不在此处更新，需走单独验证流程 POST /user/email/change
            if (request.getGender() != null) {
                user.setGender(request.getGender());
            }
            userMapper.updateById(user);
            result.setCode(200);
            result.setMessage("保存成功");
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("保存失败");
        }
        return result;
    }

    /**
     * 当前用户修改密码
     */
    @Override
    public ApiResult changePassword(UserPasswordUpdateRequest request) {
        ApiResult result = new ApiResult<>();
        LoginUser loginUser = getCurrentLoginUser();
        Long userId = loginUser.getUserId();

        if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
            result.setCode(500);
            result.setMessage("请输入当前密码");
            return result;
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            result.setCode(500);
            result.setMessage("请输入新密码");
            return result;
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            result.setCode(500);
            result.setMessage("请再次输入新密码");
            return result;
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            result.setCode(500);
            result.setMessage("两次输入的新密码不一致");
            return result;
        }
        if (!com.eldercare.system.email.StringUtil.checkPassword(request.getNewPassword())) {
            result.setCode(500);
            result.setMessage("新密码至少 6 位，规则与用户管理新增用户保持一致");
            return result;
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            result.setCode(500);
            result.setMessage("新密码不能与当前密码相同");
            return result;
        }

        try {
            User user = userMapper.selectById(userId);
            if (user == null || "1".equals(user.getDelFlag())) {
                result.setCode(500);
                result.setMessage("用户不存在");
                return result;
            }
            if (!PasswordUtil.checkPassword(request.getOldPassword(), user.getPassword())) {
                result.setCode(500);
                result.setMessage("当前密码错误");
                return result;
            }

            UpdateWrapper<User> uw = new UpdateWrapper<>();
            uw.eq("user_id", userId).set("password", hashPassword(request.getNewPassword()));
            if (userMapper.update(null, uw) > 0) {
                if (user.getEmail() != null) {
                    redisService.deleteToken(user.getEmail());
                }
                result.setCode(200);
                result.setMessage("密码修改成功，请使用新密码重新登录");
            } else {
                result.setCode(500);
                result.setMessage("密码修改失败");
            }
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("密码修改失败");
        }
        return result;
    }

    /**
     * 发送邮箱修改验证码到新邮箱（校验邮箱格式、唯一性、60 秒防刷后异步发送）
     *
     * @param request 新邮箱参数
     * @return 操作结果
     */
    @Override
    public ApiResult sendEmailChangeCode(EmailChangeCodeRequest request) {
        ApiResult result = new ApiResult<>();
        LoginUser loginUser = getCurrentLoginUser();

        // 1. 校验 newEmail 非空
        if (request.getNewEmail() == null || request.getNewEmail().isBlank()) {
            result.setCode(500);
            result.setMessage("请输入新邮箱");
            return result;
        }
        // 2. 校验邮箱格式
        if (!request.getNewEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            result.setCode(500);
            result.setMessage("邮箱格式不正确");
            return result;
        }
        // 3. 查询当前用户
        User user = userMapper.selectById(loginUser.getUserId());
        if (user == null || "1".equals(user.getDelFlag())) {
            result.setCode(500);
            result.setMessage("用户不存在");
            return result;
        }
        // 4. 新邮箱不能等于当前邮箱
        if (request.getNewEmail().equals(user.getEmail())) {
            result.setCode(500);
            result.setMessage("新邮箱不能与当前邮箱相同");
            return result;
        }
        // 5. 新邮箱未被其他用户使用
        if (getUserByEmail(request.getNewEmail())) {
            result.setCode(500);
            result.setMessage("该邮箱已被使用");
            return result;
        }
        // 6. 60 秒防刷
        String limitKey = RedisConstant.EMAIL_CHANGE_LIMIT + loginUser.getUserId();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(limitKey))) {
            result.setCode(500);
            result.setMessage("验证码发送过于频繁，请稍后再试");
            return result;
        }
        // 7. 生成 6 位验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 8. 异步发送邮件
        threadService.sendHtmlMail(request.getNewEmail(), "东软颐养中心 - 邮箱修改验证",
                EmailTemplate.buildVerifyCodeHtml("邮箱修改", code));
        // 9. 写入 Redis（验证码 5 分钟，防刷 60 秒）
        String codeKey = RedisConstant.EMAIL_CHANGE_CODE + loginUser.getUserId() + ":" + request.getNewEmail();
        stringRedisTemplate.opsForValue().set(codeKey, code,
                RedisConstant.EXPIRE_FIVE_MINUTE, java.util.concurrent.TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(limitKey, "1",
                RedisConstant.EXPIRE_ONE_MINUTE, java.util.concurrent.TimeUnit.SECONDS);
        result.setCode(200);
        result.setMessage("验证码已发送");
        return result;
    }

    /**
     * 校验验证码并修改邮箱（验证码校验、邮箱唯一性二次确认后更新数据库并清除 Redis token）
     *
     * @param request 新邮箱 + 验证码
     * @return 操作结果
     */
    @Override
    public ApiResult changeEmail(EmailChangeRequest request) {
        ApiResult result = new ApiResult<>();
        LoginUser loginUser = getCurrentLoginUser();

        // 1. 校验 newEmail 非空
        if (request.getNewEmail() == null || request.getNewEmail().isBlank()) {
            result.setCode(500);
            result.setMessage("请输入新邮箱");
            return result;
        }
        // 2. 校验 code 非空
        if (request.getCode() == null || request.getCode().isBlank()) {
            result.setCode(500);
            result.setMessage("请输入验证码");
            return result;
        }
        // 3. 校验邮箱格式
        if (!request.getNewEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            result.setCode(500);
            result.setMessage("邮箱格式不正确");
            return result;
        }
        // 4. 校验验证码为 6 位数字
        if (!request.getCode().matches("^\\d{6}$")) {
            result.setCode(500);
            result.setMessage("验证码格式不正确");
            return result;
        }
        // 5. 查询当前用户
        User user = userMapper.selectById(loginUser.getUserId());
        if (user == null || "1".equals(user.getDelFlag())) {
            result.setCode(500);
            result.setMessage("用户不存在");
            return result;
        }
        // 6. 新邮箱不能等于当前邮箱
        if (request.getNewEmail().equals(user.getEmail())) {
            result.setCode(500);
            result.setMessage("新邮箱不能与当前邮箱相同");
            return result;
        }
        // 7. 再次校验新邮箱未被其他用户使用
        if (getUserByEmail(request.getNewEmail())) {
            result.setCode(500);
            result.setMessage("该邮箱已被使用");
            return result;
        }
        // 8. 校验验证码
        String codeKey = RedisConstant.EMAIL_CHANGE_CODE + loginUser.getUserId() + ":" + request.getNewEmail();
        String rightCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (rightCode == null || !rightCode.equals(request.getCode())) {
            result.setCode(500);
            result.setMessage("验证码错误或已过期");
            return result;
        }
        // 9. 删除验证码 key
        stringRedisTemplate.delete(codeKey);
        // 10. 更新邮箱并清除旧登录态
        String oldEmail = user.getEmail();
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("user_id", loginUser.getUserId()).set("email", request.getNewEmail());
        if (userMapper.update(null, uw) == 0) {
            result.setCode(500);
            result.setMessage("邮箱修改失败");
            return result;
        }
        // 11. 删除旧邮箱的 Redis token（强制重新登录）
        if (oldEmail != null && !oldEmail.isBlank()) {
            redisService.deleteToken(oldEmail);
        }
        result.setCode(200);
        result.setMessage("邮箱修改成功，请重新登录");
        return result;
    }

}
