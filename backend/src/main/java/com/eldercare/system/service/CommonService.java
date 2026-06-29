package com.eldercare.system.service;

import com.eldercare.system.email.LoginParam;
import com.eldercare.system.email.R;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.common.NotificationVO;

/** 通用服务接口 */
public interface CommonService {

    /**
     * 获取请求权限码
     *
     * @param emailJson 邮箱参数JSON
     * @return 权限码响应
     */
    R getRequestPermissionCode(String emailJson);

    /**
     * 发送邮箱验证码
     *
     * @param loginParam 邮箱和权限码参数
     * @return 发送结果
     */
    R sendEmailCode(LoginParam loginParam);

    /**
     * 通过邮箱验证码找回密码
     *
     * @param loginParam 找回密码参数
     * @return 找回密码结果
     */
    R findPassword(LoginParam loginParam);

    /**
     * 查询指定日期的通知信息
     *
     * @param date 日期字符串
     * @return 通知信息
     */
    ApiResult<NotificationVO> getNotification(String date);
}
