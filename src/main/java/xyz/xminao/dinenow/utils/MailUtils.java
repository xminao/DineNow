package xyz.xminao.dinenow.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.xminao.dinenow.properties.MailProperties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Component
public class MailUtils {
    @Autowired
    private MailProperties mailProperties;

    public void sendMail(String email, String code) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // 是否需要身份验证
        props.put("mail.smtp.host", mailProperties.getHost()); // 邮件服务器
        props.put("mail.smtp.port", mailProperties.getPort()); // 加密端口

        // 构建授权信息
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(mailProperties.getAccount(), mailProperties.getPassword());
            }
        };

        // 创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress from = new InternetAddress(mailProperties.getAccount());
        message.setFrom(from);
        // 设置收件人邮箱
        InternetAddress to = new InternetAddress(email);
        // 收件人和抄送人
        message.setRecipient(Message.RecipientType.TO, to);
        message.setRecipient(Message.RecipientType.CC, new InternetAddress(mailProperties.getAccount()));
        // 设置内容体
        message.setContent("尊敬的网站用户：您的注册验证码为：\n" + code + "\n一分钟内有效。", "text/html;charset=utf-8");
        Transport.send(message);
    }

    // 生成随机验证码
    public String achieveCode() {
        // 验证码字符集
        String[] beforeShuffle = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a",
                "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list); // 打乱字符集
        // 生成验证码
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        return sb.substring(3, 8); // 生成五位验证码
    }
}
