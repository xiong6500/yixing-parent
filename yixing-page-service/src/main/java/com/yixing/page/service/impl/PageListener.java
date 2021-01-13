package com.yixing.page.service.impl;

import com.yixing.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("接收到消息："+text);
            itemPageService.getItemHtml(Long.parseLong(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
