package com.eldercare.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** 异步线程服务 */
@Component
public class ThreadService {

    @Autowired
    private MailService mailService;

    /**
     * 发送邮箱
     * @param to 收件人
     * @param theme 主题
     * @param content 内容
     */
    @Async("taskExecutor")
    public void sendSimpleMail(String to, String theme, String content) {
        mailService.sendSimpleMail(to, theme, content);
    }

    /**
     * 异步发送 HTML 邮件（验证码美化用）
     *
     * @param to      收件人
     * @param subject 主题
     * @param content HTML 正文
     */
    @Async("taskExecutor")
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            mailService.sendHtmlMail(to, subject, content);
        } catch (Exception e) {
            // 异步发送失败仅打印日志，避免吞掉异常
            e.printStackTrace();
        }
    }
}
