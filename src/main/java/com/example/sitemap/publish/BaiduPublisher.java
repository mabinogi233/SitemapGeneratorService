package com.example.sitemap.publish;

import com.alibaba.fastjson.JSONObject;
import com.example.sitemap.utils.HttpRequest;

import java.util.Arrays;
import java.util.List;

public class BaiduPublisher {

    private static final String URL = "http://data.zz.baidu.com/urls";

    public static synchronized boolean publish(String token,String site,String resUrl){
        try {
            String params = "?site=" + site + "&token=" + token;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(resUrl);
            String s = HttpRequest.doPost(URL + params, stringBuilder.toString());
            System.out.println(s);
            if(JSONObject.parseObject(s).getInteger("success")!=null){
                if(JSONObject.parseObject(s).getInteger("success")==1){
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static boolean publish(String token, String site, List<String> resUrls){
        try {
            String params = "?site=" + site + "&token=" + token;
            StringBuilder stringBuilder = new StringBuilder();
            for(String resUrl:resUrls) {
                stringBuilder.append(resUrl);
                stringBuilder.append("\n");
            }
            stringBuilder.delete(stringBuilder.length()-1,stringBuilder.length());
            String s = HttpRequest.doPost(URL + params, stringBuilder.toString());
            System.out.println(s);
            if(JSONObject.parseObject(s).getInteger("success")!=null){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
