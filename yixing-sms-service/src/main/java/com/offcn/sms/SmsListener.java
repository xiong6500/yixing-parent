package com.offcn.sms;

import com.offcn.util.SmsUtil;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class SmsListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        try {
            System.out.println("收到短信发送请求---》mobile:"+mapMessage.getString("mobile")+"  code:"+mapMessage.getString("code"));
            HttpResponse httpResponse = smsUtil.sendSms(mapMessage.getString("mobile"), mapMessage.getString("code"));
            // 结果是 00000 则为正常
            System.out.println("data:"+httpResponse.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
