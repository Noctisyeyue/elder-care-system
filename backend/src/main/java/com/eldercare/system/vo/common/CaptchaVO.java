package com.eldercare.system.vo.common;

import lombok.Data;

/**
 * 验证码接口返回数据。
 */
@Data
public class CaptchaVO {

    /** 是否开启验证码 */
    private Boolean captchaEnabled;

    /** 验证码唯一标识，验证码关闭时为空 */
    private String uuid;

    /** 完整图片 data URI（含 data:image/png;base64, 前缀），验证码关闭时为空 */
    private String img;
}
