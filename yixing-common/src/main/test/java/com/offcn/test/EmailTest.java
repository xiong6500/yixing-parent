package com.offcn.test;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new
                ClassPathXmlApplicationContext("spring/spring-mail.xml");
        JavaMailSenderImpl mailsend=(JavaMailSenderImpl) context.getBean("mailSender");
        //创建简单的邮件
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("2068228445@qq.com");
        msg.setTo("xiong652020@163.com");
        msg.setSubject("JAVA0115测试邮件");
        msg.setText("好好学习,天天向上!");

        //发送邮件

        mailsend.send(msg);
        System.out.println("send ok");
    }
}
