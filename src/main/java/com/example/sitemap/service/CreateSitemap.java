package com.example.sitemap.service;

import com.example.sitemap.Config;
import com.example.sitemap.sitemapcreator.WebSitemapGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateSitemap {

    /**
     * 创建/添加sitemap
     * @param isAppend
     * @param baseURL
     * @param resURL
     * @return
     */
    public synchronized static boolean add(boolean isAppend,String baseURL,String resURL){
        try {
            String dirName = baseURL.toLowerCase().replace("http://","")
                    .replace("https://","");
            String path = Config.SITEMAP_BASE_PATH + File.separator + dirName;
            if(!isAppend){
                deleteFile(new File(path));
            }
            File myDir = new File(path);
            if(!myDir.exists()){
                myDir.mkdirs();
            }
            WebSitemapGenerator wsg = new WebSitemapGenerator(baseURL, myDir);

            wsg.addUrl(resURL); // repeat multiple times

            wsg.write();
            wsg.writeSitemapsWithIndex();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建/添加sitemap
     * @param isAppend
     * @param baseURL
     * @param resURLs
     * @return
     */
    public synchronized static boolean add(boolean isAppend, String baseURL, List<String> resURLs){
        try {
            String dirName = baseURL.toLowerCase().replace("http://","")
                    .replace("https://","");
            String path = Config.SITEMAP_BASE_PATH + File.separator + dirName;
            if(!isAppend){
                deleteFile(new File(path));
            }
            File myDir = new File(path);
            if(!myDir.exists()){
                myDir.mkdirs();
            }
            WebSitemapGenerator wsg = new WebSitemapGenerator(baseURL, myDir);
            for(String resURL:resURLs) {
                wsg.addUrl(resURL); // repeat multiple times
            }
            wsg.write();
            wsg.writeSitemapsWithIndex();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 下载该网站的sitemap
     * @param baseURL
     * @return
     */
    public static synchronized File downloadSiteMap(String baseURL){
        try {
            String dirName = baseURL.toLowerCase().replace("http://", "")
                    .replace("https://", "");
            String path = Config.SITEMAP_BASE_PATH + File.separator + dirName;
            String zipPath = Config.SITEMAP_BASE_PATH + File.separator + dirName + ".zip";
            File myDir = new File(path);
            File zipF = new File(zipPath);
            if(myDir.exists()){
                if(zipF.exists()){
                    zipF.delete();
                }
                toZip(path,zipPath);
                zipF = new File(zipPath);
                if(zipF.exists()) {
                    return zipF;
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
    * @param source    待压缩文件/文件夹路径
    * @param destination   压缩后压缩文件路径
    * @return
    */
    public static synchronized boolean toZip(String source, String destination) {
        try {
            FileOutputStream out = new FileOutputStream(destination);
            ZipOutputStream zipOutputStream = new ZipOutputStream(out);
            File sourceFile = new File(source);

            // 递归压缩文件夹
            compress(sourceFile, zipOutputStream, sourceFile.getName());

            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("failed to zip compress, exception");
            return false;
        }
        return true;
    }

    private static synchronized void compress(File sourceFile, ZipOutputStream zos, String name) throws IOException {
        byte[] buf = new byte[1024];
        if (sourceFile.isFile()) {
            // 压缩单个文件，压缩后文件名为当前文件名
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 空文件夹的处理(创建一个空ZipEntry)
                zos.putNextEntry(new ZipEntry(name + "/"));
                zos.closeEntry();
            } else {
                // 递归压缩文件夹下的文件
                for (File file : listFiles) {
                    compress(file, zos, name + "/" + file.getName());
                }
            }
        }
    }

    private static void deleteFile(File file) throws IOException {
        /**
         * File[] listFiles()
         *  返回一个抽象路径名数组，这些路径名表示此抽象路径名表示的目录中的文件。
         */
        File[] files = file.listFiles();
        if (files!=null){//如果包含文件进行删除操作
            for (int i = 0; i <files.length ; i++) {
                if (files[i].isFile()){
                    //删除子文件
                    files[i].delete();
                }else if (files[i].isDirectory()){
                    //通过递归的方法找到子目录的文件
                    deleteFile(files[i]);
                }
                files[i].delete();//删除子目录
            }
        }
    }
}
