package com.example.sitemap.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.sitemap.Config;
import com.example.sitemap.sitemapcreator.ChangeFreq;
import com.example.sitemap.sitemapcreator.SitemapIndexUrl;
import com.example.sitemap.sitemapcreator.WebSitemapGenerator;
import com.example.sitemap.sitemapcreator.WebSitemapUrl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping("/home")
@ResponseBody
@org.springframework.stereotype.Controller("HomeController")
@EnableScheduling
public class HomeController {

    private static final String HOME_DIR = "/www/wwwroot/www.frogking.cn";

    private static final String SEO_DIR_NAME = "subpages";

    private static final String SEO_HTML_DIR_NAME = "pages";

    private static final String BASE_URL = "https://www.frogking.cn";
    /**
     * 获取所有页面列表
     * @return
     */
    @RequestMapping("/listPages")
    @ResponseBody
    public String listPages(){
        Map<String, Object> result = new HashMap<>();
        try{
            String dirPath = HOME_DIR + File.separator + SEO_HTML_DIR_NAME;
            File dir = new File(dirPath);
            if(!dir.exists()){
                result.put("code", "500");
                result.put("msg", "页面目录不存在");
                return JSONObject.toJSONString(result);
            }
            File[] files = dir.listFiles();
            if(files == null || files.length == 0){
                result.put("code", "500");
                result.put("msg", "页面目录为空");
                return JSONObject.toJSONString(result);
            }
            List<String> data = new ArrayList<>();
            for(File file : files){
                if(file.isDirectory()){
                    continue;
                }
                String fileName = file.getName();
                String[] nameArr = fileName.split("\\.");
                if(nameArr.length != 2 || !"html".equals(nameArr[1])){
                    continue;
                }
                String pageName = nameArr[0];
                data.add(pageName);
            }
            result.put("code", "200");
            result.put("msg", "获取页面列表成功");
            result.put("data", data);
            return JSONObject.toJSONString(result);
        }catch (Exception e) {
            e.printStackTrace();
            result.put("code", "500");
            result.put("msg", "获取页面列表失败");
            return JSONObject.toJSONString(result);
        }
    }

    /**
     * 创建首页的sitemap和html
     * @return
     */
    @RequestMapping("/createHomeSitemap")
    @ResponseBody
    public void createHomeSitemap(@RequestParam("password")String password){
        if(password == null || !password.equals("liuwenze0501")){
            return;
        }

        try {
            List<String> resURLs = new ArrayList<>();

            String dirPath = HOME_DIR + File.separator + SEO_DIR_NAME;
            File dir = new File(dirPath);

            if(!dir.exists() || !dir.isDirectory()){
                return;
            }

            File[] files = dir.listFiles();

            if(files == null || files.length == 0){
                return;
            }

            for(File file : files){
                if(file.isDirectory()){
                    continue;
                }
                String fileName = file.getName();
                String[] nameArr = fileName.split("\\.");
                if(nameArr.length != 2 || !"txt".equals(nameArr[1])){
                    continue;
                }
                String pageName = nameArr[0];
                //1,创建html文件
                String htmlPath = HOME_DIR + File.separator + SEO_HTML_DIR_NAME + File.separator +
                        URLEncoder.encode(pageName, StandardCharsets.UTF_8) + ".html";
                File htmlFile = new File(htmlPath);

                this.createHTML(file, htmlFile);
                if (!htmlFile.exists() || !htmlFile.isFile() || htmlFile.length() == 0){
                    continue;
                }
                //2,url
                String subPageUrl = BASE_URL + "/"+SEO_HTML_DIR_NAME+"/" + URLEncoder.encode(pageName, StandardCharsets.UTF_8) + ".html";
                resURLs.add(subPageUrl);
            }
            //3,创建sitemap
            String path = HOME_DIR;
            // delete old sitemap
            for(File file: new File(path).listFiles()){
                if(file.getName().startsWith("sitemap")){
                    file.delete();
                }
            }
            File myDir = new File(path);
            WebSitemapGenerator wsg = new WebSitemapGenerator(BASE_URL, myDir);

            WebSitemapUrl index = new WebSitemapUrl.Options(BASE_URL+"/index.html").priority(1.0).changeFreq(ChangeFreq.WEEKLY).build();
            wsg.addUrl(index);
            WebSitemapUrl info1 = new WebSitemapUrl.Options(BASE_URL+"/info1.html").priority(1.0).changeFreq(ChangeFreq.WEEKLY).build();
            wsg.addUrl(info1);
            WebSitemapUrl info2 = new WebSitemapUrl.Options(BASE_URL+"/info2.html").priority(1.0).changeFreq(ChangeFreq.WEEKLY).build();
            wsg.addUrl(info2);
            WebSitemapUrl info3 = new WebSitemapUrl.Options(BASE_URL+"/info3.html").priority(1.0).changeFreq(ChangeFreq.WEEKLY).build();
            wsg.addUrl(info3);

            for(String resURL:resURLs) {
                WebSitemapUrl sitemapIndexUrl = new WebSitemapUrl.Options(resURL).priority(1.0).changeFreq(ChangeFreq.WEEKLY).build();
                wsg.addUrl(sitemapIndexUrl);
                //wsg.addUrl(resURL); // repeat multiple times
            }
            wsg.write();
            wsg.writeSitemapsWithIndex();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 创建页面的html
     * @param txtFile
     * @param htmlFile
     */
    private void createHTML(File txtFile,File htmlFile) {
        FileWriter fileWriter = null;
        BufferedReader bufferedReader = null;
        try {
            StringBuilder page = new StringBuilder();

            FileReader fileReader = new FileReader(txtFile);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() < 15){
                    //标题
                    page.append("\t\t\t\t");
                    page.append("<h3 style=\"color: greenyellow;\">").append(line.strip()).append("</h3>");
                    page.append("\n");
                }else {
                    //内容
                    page.append("\t\t\t\t");
                    page.append("<p style=\"color: white;\">").append(line.strip()).append("</p>");
                    page.append("\n");
                }
            }




            String template = "<!DOCTYPE html>\n" +
                    "<html lang=\"zh\">\n" +
                    "<head>\n" +
                    "<meta charset=\"UTF-8\" />\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                    "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />\n" +
                    "<meta name=\"description\" content=\"月王小站 ｜ 月王软件工作室主页 ｜ 现有产品：自媒体文章、视频AI智能创作，AI伪原创，AI写作，文章转视频，热点资讯一站看，万本电子图书免费搜索，周易在线排盘，免费在线CAJ转PDF工具，A美港AI市场分析工具\">\n" +
                    "<title>月王小站</title>\n" +
                    "<script async src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-2380767499034752\"\n" +
                    "     crossorigin=\"anonymous\"></script>\n" +
                    "\n" +
                    "<!--默认样式-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/reset.css\">\n" +
                    "\n" +
                    "<!--响应式框架-->\n" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/bootstrap.css\">\n" +
                    "\n" +
                    "<!--css3动画库-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/animate.css\">\n" +
                    "\n" +
                    "<!--banner大图基础样式-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/slick.css\">\n" +
                    "\n" +
                    "<!--页面滚动基础样式-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/jquery.fullPage.css\" />\n" +
                    "\n" +
                    "<!--导航样式-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/head.css\" />\n" +
                    "\n" +
                    "<!--图片和滚屏样式-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/index.css\">\n" +
                    "\n" +
                    "<!--底部说明栏-->\n" +
                    "<link rel=\"stylesheet\" href=\"css/zzsc.css\">\n" +
                    "\n" +
                    "<style>\n" +
                    "    .beian-a:hover{\n" +
                    "        color:orange;\n" +
                    "    }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<!-- 导航 -->\n" +
                    "<header class=\"header\"> \n" +
                    "\t<div class=\"container clearfix\"> \n" +
                    "\t\t<div class=\"fr nav\"> \n" +
                    "\t\t\t<ul class=\"navbar_nav\" data-in=\"fadeInDown\" data-out=\"fadeOutUp\">\n" +
                    "\t\t\t\t<li class=\"active\">\n" +
                    "\t\t\t\t\t<a href=\"https://www.frogking.cn\">月王软件首页</a>\t\t\t\t\t\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t\t<li class=\"active\">\n" +
                    "\t\t\t\t\t<a href=\"https://tool.frogking.cn\">美墨工具箱</a>\t\t\t\t\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t\t\t<a href=\"javascript:void(0)\"> SEO|自媒体工具 </a>\n" +
                    "\t\t\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t\t\t<a href=\"https://ai.frogking.cn\">文章自动写作｜伪原创</a>\n" +
                    "\t\t\t\t\t\t<a href=\"https://mov.frogking.cn\">视频自动创作｜无剪辑</a>\t\t\t\t\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t\t\t<a href=\"javascript:void(0)\"> 资源站点 </a>\n" +
                    "\t\t\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t\t\t\n" +
                    "\t\t\t\t\t\t<a href=\"https://book.frogking.cn\">布克图书网</a>\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t\t\t<a href=\"javascript:void(0)\"> 实用工具 </a>\n" +
                    "\t\t\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t\t    <a href=\"https://tool.frogking.cn\">美墨工具箱</a>\n" +
                    "\t\t\t\t\t\t<a href=\"https://yj.frogking.cn\">周易在线排盘</a>\n" +
                    "\t\t\t\t\t\t<a href=\"https://caj2pdf.frogking.cn\">免费CAJ转PDF</a>\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t\t\t<a href=\"javascript:void(0)\"> 关于我们 </a>\n" +
                    "\t\t\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t\t\t<a href=\"javascript:void(0)\">月王软件工作室是一家致力于开发各种AI软件、实用工具、资源网站的工作室</a>\n" +
                    "\t\t\t\t\t</div>\t\t\t\t\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t\t\t<a href=\"javascript:void(0)\"> 联系合作 </a>\n" +
                    "\t\t\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t\t\t<a href=\"javascript:void(0)\"><img src=\"image/code.jpg\" width=\"80%\"/></a>\n" +
                    "\t\t\t\t\t</div>\t\t\t\t\n" +
                    "\t\t\t\t</li>\n" +
                    "\t\t\t</ul>\n" +
                    "\t\t</div>\n" +
                    "\t\t<a href=\"javascript:void(0)\" id=\"navToggle\">\n" +
                    "\t\t\t<span></span>\n" +
                    "\t\t</a>\n" +
                    "\t</div>\n" +
                    "</header>\n" +
                    "<!--移动端的导航-->\n" +
                    "<div class=\"m_nav\">\n" +
                    "\t<div class=\"top clearfix\">\n" +
                    "\t\t<img src=\"image/closed.png\" alt=\"\" class=\"closed\" />\n" +
                    "\t</div>\n" +
                    "\t<div class=\"logo\">\n" +
                    "\t\t<img src=\"image/logo2.jpg\" alt=\"\" />\n" +
                    "\t</div>\n" +
                    "\t<ul class=\"ul\" data-in=\"fadeInDown\" data-out=\"fadeOutUp\">\n" +
                    "\t\t<li class=\"active\">\n" +
                    "\t\t\t<a href=\"https://www.frogking.cn\">首页</a>\n" +
                    "\t\t</li>\n" +
                    "\t\t<li class=\"active\">\n" +
                    "\t\t\t<a href=\"https://tool.frogking.cn\">美墨工具箱</a>\t\t\t\t\t\n" +
                    "\t\t</li>\n" +
                    "\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t<a href=\"javascript:void(0)\"> SEO|自媒体工具 </a>\n" +
                    "\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t<a href=\"https://ai.frogking.cn\">文章自动写作｜伪原创</a>\n" +
                    "\t\t\t\t<a href=\"https://mov.frogking.cn\">视频自动创作｜无剪辑</a>\t\t\t\t\n" +
                    "\t\t\t</div>\n" +
                    "\t\t</li>\n" +
                    "\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t<a href=\"javascript:void(0)\"> 资源站点 </a>\n" +
                    "\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t\t<a href=\"https://book.frogking.cn\">布克图书网</a>\n" +
                    "\t\t\t</div>\n" +
                    "\t\t</li>\n" +
                    "\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t<a href=\"javascript:void(0)\"> 实用工具 </a>\n" +
                    "\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t    <a href=\"https://tool.frogking.cn\">美墨工具箱</a>\n" +
                    "\t\t\t\t<a href=\"https://yj.frogking.cn\">周易在线排盘</a>\n" +
                    "\t\t\t\t<a href=\"https://caj2pdf.frogking.cn\">免费CAJ转PDF</a>\n" +
                    "\t\t\t</div>\n" +
                    "\t\t</li>\n" +
                    "\t\t<<li class=\"dropdown\">\n" +
                    "\t\t\t<a href=\"javascript:void(0)\"> 关于我们 </a>\n" +
                    "\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t<a href=\"javascript:void(0)\">月王软件工作室是一家致力于开发各种AI软件、实用工具、资源网站的工作室</a>\n" +
                    "\t\t\t</div>\t\t\t\t\n" +
                    "\t\t</li>\n" +
                    "\t\t<li class=\"dropdown\">\n" +
                    "\t\t\t<a href=\"javascript:void(0)\"> 联系合作 </a>\n" +
                    "\t\t\t<div class=\"dropdown_menu\">\n" +
                    "\t\t\t\t<a href=\"javascript:void(0)\"><img src=\"image/code.jpg\" width=\"80%\"/></a>\n" +
                    "\t\t\t</div>\t\t\t\t\n" +
                    "\t\t</li>\n" +
                    "\t</ul>\n" +
                    "</div>\n" +
                    "<!-- 内容 -->\n" +
                    "<div id=\"index_main\" class=\"index_main\">\n" +
                    "\t<!--导航-->\n" +
                    "\n" +
                    "\t<div class=\"section section3\" style=\"background-image: url(image/21.jpg);\">\n" +
                    "\t\t<div class=\"container\">\n" +
                    "\t\t\t<div class=\"item\" style=\"overflow: scroll;height:400px;overflow-x: hidden;overflow-y: auto;scrollbar-width: none;-ms-overflow-style: none;-webkit-overflow-scrolling: touch;\" >\n" +

                    page +

                    "\t\t\t</div>\n" +
                    "\t\t</div>\n" +
                    "\t</div>"+
                    "</div>\n" +
                    "\n" +
                    "<div id=\"stickey_footer\">\n" +
                    "\t<div>\n" +
                    "\t\t<p style=\"color: white;font-size: 12px;\">月王软件工具导航｜月王软件©2020-2024 |<a class=\"beian-a\" href=\"https://www.frogking.cn/sitemap.xml\">站点地图</a></p>\n" +
                    "\t\t<p style=\"color: white;font-size: 12px;\"><a class=\"beian-a\" href=\"https://beian.miit.gov.cn\">ICP备案：冀ICP备20003712号-1 </a> | 公安备案： <img src=\"image/beian.png\" /><a class=\"beian-a\" href=\"http://www.beian.gov.cn/portal/registerSystemInfo\">13010202003153</a> </p>\n" +
                    "\t</div>\n" +
                    "</div>\n" +
                    "\n" +
                    "<div style=\"text-align:center;clear:both\">\n" +
                    "\n" +
                    "<script src=\"js/jquery.min.js\"></script>\n" +
                    "<script src=\"js/jquery.fullPage.min.js\"></script>\n" +
                    "<script src=\"js/index_slick.js\"></script>\n" +
                    "<script src=\"js/index.js\"></script>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
            //写入文件
            if (!htmlFile.exists()) {
                htmlFile.createNewFile();
            }
            fileWriter = new FileWriter(htmlFile);
            fileWriter.write(template);
            fileWriter.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
