package com.eldercare.system.service.impl;
import com.eldercare.system.service.ThreadService;
import com.eldercare.system.service.CommonService;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.entity.*;
import com.eldercare.system.email.*;
import com.eldercare.system.mapper.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.common.NotificationVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.eldercare.system.util.PasswordUtil.hashPassword;

/**
 * 通用服务实现
 */
@Component
public class CommonServiceImpl implements CommonService {

    /** Redis 操作模板 */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /** 邮件发送线程服务 */
    @Autowired
    private ThreadService threadService;

    /** 用户服务实现 */
    @Autowired
    private UserServiceImpl userService;

    /** 用户 Mapper */
    @Autowired
    private UserMapper userMapper;

    /** 膳食日历 Mapper */
    @Autowired
    private DietCalendarMapper dietCalendarMapper;

    /** 膳食日历套餐关系 Mapper */
    @Autowired
    private DietCalendarSetMealMappingMapper dietCalendarSetMealMappingMapper;

    /** 外出记录 Mapper */
    @Autowired
    private OutingRecordMapper outingRecordMapper;

    /** 退住记录 Mapper */
    @Autowired
    private CheckOutRecordMapper checkOutRecordMapper;

    /**
     * 获取邮箱验证码请求权限码
     *
     * @param emailJson 邮箱参数 JSON
     * @return 权限码响应
     */
    @Override
    public R getRequestPermissionCode(String emailJson) {
        // 非空校验
        if (StringUtils.isBlank(emailJson)) return R.error(HttpStatusEnum.PARAM_ILLEGAL);

        // JSON转换，提取email的值
        String email = JSON.parseObject(emailJson).getString("email").trim();
        // 邮箱校验
        if (!StringUtil.checkEmail(email)) {
            return R.error(HttpStatusEnum.EMAIL_ERROR);
        }
        //去user表中查询该邮箱
        if (!userService.getUserByEmail(email)) {
            return R.error(HttpStatusEnum.EMAIL_ALREADY_EXIST);
        }
        // 随机生成权限码
        String permissionCode = UUID.randomUUID().toString();

        // 存入redis，缓存10s
        redisTemplate.opsForValue().set(RedisConstant.EMAIL_REQUEST_VERIFY + email, permissionCode, RedisConstant.EXPIRE_TEN_SECOND, TimeUnit.SECONDS);
        return R.ok().data("permissionCode", permissionCode);
    }

    /**
     * 发送邮箱验证码
     *
     * @param loginParam 邮箱和权限码参数
     * @return 发送结果
     */
    @Override
    public R sendEmailCode(LoginParam loginParam) {
        if (loginParam == null) return R.error(HttpStatusEnum.PARAM_ILLEGAL);

        // 获取权限码和邮箱
        String email = loginParam.getEmail();
        String permissionCode = loginParam.getCode();
        // 参数校验
        if (StringUtils.isAnyBlank(email, permissionCode)) {
            return R.error(HttpStatusEnum.PARAM_ILLEGAL);
        }else if (!StringUtil.checkEmail(email)) {
            // 邮箱校验
            return R.error(HttpStatusEnum.EMAIL_ERROR);
        }else {
            // 权限码比对
            String rightCode = redisTemplate.opsForValue().get(RedisConstant.EMAIL_REQUEST_VERIFY + email);
            if (!permissionCode.equals(rightCode)) {
                // 不通过
                return R.error(HttpStatusEnum.ILLEGAL_OPERATION);
            }
        }

        // 全部通过

        // 随机生成6位数字验证码
        String code = StringUtil.randomSixCode();

        // 正文内容
        String content = "亲爱的用户：\n" +
                "您此次的验证码为：\n\n" +
                code + "\n\n" +
                "此验证码5分钟内有效，请立即进行下一步操作。 如非你本人操作，请忽略此邮件。\n" +
                "感谢您的使用！";

        // 发送验证码
        threadService.sendSimpleMail(email, "您此次的验证码为：" + code, content);
        System.out.println("发送成功,您此次的验证码为："+ code+ content);
        // 丢入缓存，设置5分钟过期
        redisTemplate.opsForValue().set(RedisConstant.EMAIL + email, code, RedisConstant.EXPIRE_FIVE_MINUTE, TimeUnit.SECONDS);
        return R.ok();
    }

    /**
     * 通过邮箱验证码找回密码
     *
     * @param loginParam 找回密码参数
     * @return 找回密码结果
     */
    @Override
    public R findPassword(LoginParam loginParam) {
        if (loginParam == null) return R.error(HttpStatusEnum.PARAM_ILLEGAL);

        // 获取参数
        String email = loginParam.getEmail();
        String password = loginParam.getPassword();
        String code = loginParam.getCode();
        System.out.println("email:"+email+"password:"+password+"code:"+code);
        if (StringUtils.isAnyBlank(email, password, code)) {
            // 非空
            return R.error(HttpStatusEnum.PARAM_ILLEGAL);
        }else if (!StringUtil.checkEmail(email)) {
            // 邮箱格式校验
            return R.error(HttpStatusEnum.EMAIL_ERROR);
        }else if (!StringUtil.checkPassword(password) || code.length() != 6) {
            // 密码格式和验证码长度校验
            return R.error(HttpStatusEnum.PARAM_ILLEGAL);
        }

        // 获取正确的验证码
        String rightCode = redisTemplate.opsForValue().get(RedisConstant.EMAIL + email);
        if (!code.equals(rightCode)) {
            // 验证码比对
            return R.error(HttpStatusEnum.CODE_ERROR);
        }

        // 删除验证码
        redisTemplate.delete(RedisConstant.EMAIL + email);

        // 根据邮箱修改密码
        return this.userMapper.updateByEmail(email, hashPassword(password)) == 0 ? R.error(HttpStatusEnum.UNKNOWN_ERROR) : R.ok();
    }

    /**
     * 发送护工注册验证码
     * 仅当邮箱未被注册时发送，同一邮箱 60 秒内不可重复发送
     *
     * @param emailJson 邮箱参数 JSON
     * @return 发送结果
     */
    @Override
    public R sendRegisterCode(String emailJson) {
        // 非空校验
        if (StringUtils.isBlank(emailJson)) return R.error(HttpStatusEnum.PARAM_ILLEGAL);

        // 提取邮箱并校验格式
        String email = JSON.parseObject(emailJson).getString("email").trim();
        if (!StringUtil.checkEmail(email)) {
            return R.error(HttpStatusEnum.EMAIL_ERROR);
        }
        // 注册要求邮箱未被使用：已存在则拒绝
        if (userService.getUserByEmail(email)) {
            return R.error(HttpStatusEnum.EMAIL_REGISTERED);
        }
        // 60 秒防刷：防刷标记存在则拒绝
        if (Boolean.TRUE.equals(redisTemplate.hasKey(RedisConstant.REGISTER_CODE_LIMIT + email))) {
            return R.error(HttpStatusEnum.CODE_SEND_TOO_FREQUENT);
        }

        // 生成 6 位数字验证码
        String code = StringUtil.randomSixCode();
        String content = "亲爱的用户：\n" +
                "您此次的注册验证码为：\n\n" +
                code + "\n\n" +
                "此验证码5分钟内有效，请立即进行下一步操作。 如非你本人操作，请忽略此邮件。\n" +
                "感谢您的使用！";
        threadService.sendSimpleMail(email, "您此次的注册验证码为：" + code, content);
        System.out.println("注册验证码发送成功，email=" + email + "，code=" + code);

        // 验证码缓存 5 分钟
        redisTemplate.opsForValue().set(RedisConstant.REGISTER_CODE + email, code, RedisConstant.EXPIRE_FIVE_MINUTE, TimeUnit.SECONDS);
        // 防刷标记缓存 60 秒
        redisTemplate.opsForValue().set(RedisConstant.REGISTER_CODE_LIMIT + email, "1", RedisConstant.EXPIRE_ONE_MINUTE, TimeUnit.SECONDS);
        return R.ok();
    }

    /**
     * 查询指定日期的通知信息
     *
     * @param date 日期字符串
     * @return 通知信息
     */
    @Override
    public ApiResult<NotificationVO> getNotification(String date) {
        // 获取通知信息
        // 变量准备
        ApiResult<NotificationVO> result = new ApiResult<>();
        NotificationVO data = new NotificationVO();
        DietCalendar dietCalendar;
        // 数据库查询
        // 查询是否配置膳食
        try {
            dietCalendar = dietCalendarMapper.selectOne(new QueryWrapper<DietCalendar>().eq("date", date));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找膳食日历数据库错误");
            throw e;
        }
        if (dietCalendar == null) {
            data.setDietConfigured(false);
        } else {
            List<DietCalendarSetMealMapping> dietCalendarSetMealMappings;
            try {
                dietCalendarSetMealMappings = dietCalendarSetMealMappingMapper.selectList(new QueryWrapper<DietCalendarSetMealMapping>().eq("diet_calendar_id", dietCalendar.getDietCalendarId()));
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("查找膳食映射数据库错误");
                throw e;
            }
            data.setDietConfigured(!dietCalendarSetMealMappings.isEmpty());
        }
        // 查询待处理退住申请
        QueryWrapper<CheckOutRecord> checkOutRecordQueryWrapper = new QueryWrapper<>();
        checkOutRecordQueryWrapper.eq("status", "2");
        checkOutRecordQueryWrapper.eq("del_flag", "0");
        try {
            data.setCheckOutApplyCount(checkOutRecordMapper.selectCount(checkOutRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取待处理退住申请数量数据库错误");
            return result;
        }
        // 查询待处理外出申请
        QueryWrapper<OutingRecord> outingRecordQueryWrapper = new QueryWrapper<>();
        outingRecordQueryWrapper.eq("status", "2");
        outingRecordQueryWrapper.eq("del_flag", "0");
        try {
            data.setOutingApplyCount(outingRecordMapper.selectCount(outingRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取待处理外出申请数量数据库错误");
            return result;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }
}
