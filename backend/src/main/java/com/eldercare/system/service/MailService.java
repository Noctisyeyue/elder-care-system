package com.eldercare.system.service;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/** 邮件服务 */
@Component
public class MailService  {

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送简单的邮箱
     *
     * @param to 收件人
     * @param theme 标题
     * @param content 正文内容
     * @param cc 抄送
     */
    public void sendSimpleMail(String to, String theme, String content, String... cc) {
        // 创建邮件对象
        SimpleMailMessage message = new SimpleMailMessage();

        try {
            message.setFrom(String.valueOf(new InternetAddress(from, "东软颐养中心", "UTF-8")));      // 发件人
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setTo(to);          // 收件人
        message.setSubject(theme);  // 标题
        message.setText(content);   // 内容

        if (ArrayUtils.isNotEmpty(cc)) {
            message.setCc(cc);
        }

        // 发送
        javaMailSender.send(message);
    }

    /**
     * 发送HTML邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param cc      抄送地址
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        try {
            // 设发件人显示名（解决邮箱直接显示发件人昵称/头像的问题）
            helper.setFrom(new InternetAddress(from, "东软颐养中心", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new jakarta.mail.MessagingException("发件人编码异常", e);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtils.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        javaMailSender.send(message);
    }
}
