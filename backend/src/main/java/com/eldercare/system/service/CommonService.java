package com.eldercare.system.service;

import com.eldercare.system.email.LoginParam;
import com.eldercare.system.email.R;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.common.result.Notification;

/** 通用服务接口 */
public interface CommonService {

    /**
     * 获取请求权限码
     * @param emailJson 邮箱
     * @return
     */
    R getRequestPermissionCode(String emailJson);

    /**
     * 发送邮箱验证码
     * @param loginParam （邮箱和权限码）
     * @return
     */
    R sendEmailCode(LoginParam loginParam);

    R findPassword(LoginParam loginParam);

    ApiResult<Notification> getNotification(String date);
}
