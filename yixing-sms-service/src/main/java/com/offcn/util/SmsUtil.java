package com.offcn.util;

import com.yixing.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsUtil {
    @Value("${AppCode}")
    private String appcode;

    private String host = "https://zwp.market.alicloudapi.com";

    public HttpResponse sendSms(String mobile,String code){
        String path = "/sms/sendv2";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("content", "【儿童教务】您正在登录验证,验证码为"+code+" ,60s内有效,请尽快验证。");
        querys.put("mobile", mobile);
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            return response;
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送失败");
            return null;
        }
    }
}
