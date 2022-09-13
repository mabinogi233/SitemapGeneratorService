package com.example.sitemap.publish;

import com.alibaba.fastjson.JSONObject;
import com.example.sitemap.utils.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingPublisher {


    private static final String URL = "https://www.bing.com/webmaster/api.svc/json/SubmitUrlbatch";

    public static synchronized boolean publish(String token,String site,String resUrl){
        try {
            String params = "?apikey=" + token;
            Map<String,Object> json = new HashMap<>();
            json.put("siteUrl",site);
            List<String> urls = new ArrayList<>();
            urls.add(resUrl);
            json.put("urlList",urls);
            String s = HttpRequest.doPost(URL + params, JSONObject.toJSONString(json),"application/json; charset=utf-8");
            System.out.println(s);
            if(JSONObject.parseObject(s).getInteger("d")!=null){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean publish(String token, String site, List<String> resUrls){
        try {
            String params = "?apikey=" + token;
            Map<String,Object> json = new HashMap<>();
            json.put("siteUrl",site);
            List<String> urls = new ArrayList<>();
            urls.addAll(resUrls);
            json.put("urlList",urls);
            String s = HttpRequest.doPost(URL + params, JSONObject.toJSONString(json),"application/json; charset=utf-8");
            System.out.println(s);
            if(JSONObject.parseObject(s).getInteger("d")!=null){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
