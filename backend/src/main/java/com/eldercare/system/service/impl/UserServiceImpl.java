package com.eldercare.system.service.impl;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.service.UserService;

import com.aliyuncs.exceptions.ClientException;
import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.constant.UserRoleConstant;
import com.eldercare.system.entity.Role;
import com.eldercare.system.entity.User;
import com.eldercare.system.config.JwtProperties;
import com.eldercare.system.mapper.*;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.util.PasswordUtil;
import com.eldercare.system.util.ImgUploadUtil;
import com.eldercare.system.dto.user.*;
import com.eldercare.system.vo.user.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.user.RoleNumVO;
import org.springframework.beans.factory.annotation.Autowired;
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
            userResult.setUserName(user0.getUserName());
            userResult.setRealName(user0.getRealName());
            userResult.setPhone(user0.getPhone());
            userResult.setEmail(user0.getEmail());
            userResult.setGender(user0.getGender());
            userResult.setIndex(user0.getUserId());
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
     * 批量删除用户
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

        int totalDeleted = 0;
        for (String userName : userNameList) {

            // 根据userName查询用户id,并将costumer表的user_id字段设置为null
            userMapper.updateUserId(userMapper.selectIdByUsername(userName));
            // 根据userid将nursing_record表中的del_flag字段设置为1
            nursingRecordMapper.updateNursingRecordDelFlagByUserId(userMapper.selectIdByUsername(userName));
            // 根据userid将outing_record表中的del_flag字段设置为1
            outingRecordMapper.updateOutingRecordDelFlagByUserId(userMapper.selectIdByUsername(userName));
            // 根据userid将checkout_record表中的del_flag字段设置为1
            checkoutRecordMapper.updateCheckoutRecordDelFlagByUserId(userMapper.selectIdByUsername(userName));
            //改为逻辑删除，将del_flag字段设置为1
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
        queryWrapper.ne("role_key","admin");
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
        //如果在用户表中查到了邮箱，则返回true
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("del_flag", "0");
        return userMapper.selectOne(queryWrapper) != null;
    }

    /** 本地上传目录 */
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 上传用户头像
     *
     * @param file  头像文件
     * @param token 登录令牌
     * @return 头像访问地址
     */
    @Override
    public ApiResult<String> uploadFile(MultipartFile file,String token) {
        ApiResult<String> result = new ApiResult<>();
        //根据Token获取用户名，根据用户名找到用户ID，将审批人id添加到记录表
        Long userId = null;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户信息不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            return result;
        }

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
     * @param token 登录令牌
     * @return 头像访问地址
     */
    @Override
    public ApiResult<String> getAvatar(String token) {
        ApiResult<String> result = new ApiResult<>();
        //根据Token获取用户名，根据用户名找到用户ID，将审批人id添加到记录表
        Long userId = null;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户信息不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            return result;
        }

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
     * @param token 登录令牌
     * @return 邮箱地址
     */
    @Override
    public ApiResult<String> getEmail(String token) {
        ApiResult<String> result = new ApiResult<>();
        //根据Token获取用户名，根据用户名找到用户ID，将审批人id添加到记录表
        Long userId = null;
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                result.setCode(401);
                result.setMessage("用户信息不存在");
                return result;
            }
            userId = Long.parseLong(userIdClaim.asString());
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            return result;
        }

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

}
