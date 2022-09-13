package com.example.sitemap.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.sitemap.publish.BaiduPublisher;
import com.example.sitemap.publish.BingPublisher;
import com.example.sitemap.service.CreateSitemap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RequestMapping("/main")
@ResponseBody
@org.springframework.stereotype.Controller("controller")
public class Controller {

    @RequestMapping("/addBingS")
    public String addBingS(@RequestBody String msg){
        try{
            JSONObject ss = JSONObject.parseObject(msg);
            String token = ss.getString("token");
            String site = ss.getString("site");
            JSONArray urls = ss.getJSONArray("urls");
            List<String> lUrls = new ArrayList<>();
            for(int i=0;i<urls.size();i++){
                lUrls.add(urls.getString(i));
            }
            if(BingPublisher.publish(token,site,lUrls)){
                return "success";
            }else {
                return "fail";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/addBing")
    public String addBing(@RequestBody String msg){
        try{
            JSONObject ss = JSONObject.parseObject(msg);
            String token = ss.getString("token");
            String site = ss.getString("site");
            String url = ss.getString("url");
            if(BingPublisher.publish(token,site,url)){
                return "success";
            }else {
                return "fail";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/addBaiduS")
    public String addBaiduS(@RequestBody String msg){
        try{
            JSONObject ss = JSONObject.parseObject(msg);
            String token = ss.getString("token");
            String site = ss.getString("site");
            JSONArray urls = ss.getJSONArray("urls");
            List<String> lUrls = new ArrayList<>();
            for(int i=0;i<urls.size();i++){
                lUrls.add(urls.getString(i));
            }
            if(BaiduPublisher.publish(token,site,lUrls)) {
                return "success";
            }else {
                return "fail";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/addBaidu")
    public String addBaidu(@RequestBody String msg){
        try{
            JSONObject ss = JSONObject.parseObject(msg);
            String token = ss.getString("token");
            String site = ss.getString("site");
            String url = ss.getString("url");
            if(BaiduPublisher.publish(token,site,url)){
                return "success";
            }else {
                return "fail";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/addSiteMap")
    public String addSiteMap(@RequestBody String msg){
        try{
            JSONObject ss = JSONObject.parseObject(msg);
            int isAppend = ss.getInteger("isAppend");
            String site = ss.getString("site");
            String url = ss.getString("url");

            if(CreateSitemap.add(isAppend==1,site,url)){
                return "success";
            }else {
                return "fail";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/addSiteMapS")
    public String addSiteMapS(@RequestBody String msg){
        try{
            JSONObject ss = JSONObject.parseObject(msg);
            int isAppend = ss.getInteger("isAppend");
            String site = ss.getString("site");
            JSONArray urls = ss.getJSONArray("urls");
            List<String> lUrls = new ArrayList<>();
            for(int i=0;i<urls.size();i++){
                lUrls.add(urls.getString(i));
            }
            if(CreateSitemap.add(isAppend==1,site,lUrls)){
                return "success";
            }else {
                return "fail";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/getSiteMap")
    public void getSiteMap(@RequestParam("site")String site, HttpServletResponse response){
        try{
            File file = CreateSitemap.downloadSiteMap(site);
            if(file==null){
                throw new IOException("文件不存在");
            }
            FileInputStream fis = new FileInputStream(file);

            response.setHeader("content-type", "application/octet-stream;charset=utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Length",String.valueOf(file.length()));
            response.setHeader("Content-Disposition",
                    "attachment;filename="
                            + new String(file.getName().getBytes("gbk"),
                            "ISO8859-1"));
            // 5. 处理下载流复制
            ServletOutputStream os = response.getOutputStream();
            int len;
            byte[] b = new byte[4096];

            while((-1 != (len = fis.read(b, 0, b.length)))){
                os.write(b, 0, len);
            }
            // 释放资源
            os.close();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
