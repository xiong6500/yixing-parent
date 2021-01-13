package com.yixing.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.yixing.pojo.TbItem;
import com.yixing.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            for (TbItem tbItem : tbItems) {
                Map specMap = JSON.parseObject(tbItem.getSpec(),Map.class);
                tbItem.setSpecMap(specMap);//给带注解的字段赋值
            }
            itemSearchService.importList(tbItems);
            System.out.println("成功导入索引库");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
