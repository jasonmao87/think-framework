package com.think.common.util;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {



    public static final String fileType(File file){
        try{
            return Files.probeContentType(Paths.get(file.getPath()));
        }catch (Exception e){}
        return null;
    }




    /**
     * 文件分片
     * @param index
     * @param partLen
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] getSplitPartFromInputStream(int index ,int partLen,InputStream inputStream) throws IOException{
        byte[] bytes = inputStreamToByteArray(inputStream);
        int start = index*partLen;
        int  max = bytes.length;
        if(max -start < partLen){
            partLen = max-start;
        }
        byte[] rb = new byte[partLen];
        for(int i =start;i<start+partLen ;i++){
            rb[i -start] = bytes[i];
        }
        return rb;

    }


    /**
     * inputStream 转 byte[]
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException{
        ByteArrayOutputStream buffer =new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead ;
        while ((nRead = inputStream.read(data,0,data.length) )!=-1){
            buffer.write(data);
        }
        return buffer.toByteArray();

    }
    /**
     * 文件转 byte【】
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException{
        InputStream inputStream = fileToInputStream(file);
        return inputStreamToByteArray(inputStream);
    }

    public static String readTextFromInputStream(InputStream inputStream) throws IOException{
        BufferedInputStream in = new BufferedInputStream(inputStream);
        byte[] bytes = new byte[2048];
        int n =-1;
        StringBuilder builder = new StringBuilder();
        while ((n = in.read(bytes,0,bytes.length)) != -1) {
            builder.append(new String(bytes,0,n,StringUtil.UTF8));
        }
        return builder.toString();


    }

    /**
     * 从文件读取文本
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readTextFromFile(String filePath) throws IOException {
        return readTextFromInputStream( new FileInputStream(filePath));
    }

    public static Iterator<String> readTextLineFromInputStream(InputStream inputStream) throws IOException{
//        StringBuilder buffer = new StringBuilder();
//        List<String> list = new ArrayList<>();
//        BufferedInputStream in = new BufferedInputStream(inputStream);
//        byte[] bytes = new byte[2048];
//        int n =-1;
//        while ((n = in.read(bytes,0,bytes.length)) != -1) {
//            Charset cs  = Charset.forName(StringUtil.UTF8);
//            ByteBuffer bb = ByteBuffer.allocate (bytes.length);
//            bb.put(bytes);
//            bb.flip();
//            char[] carray = cs.decode(bb).array();
//        }
//        return list.iterator();

        String s = readTextFromInputStream(inputStream).replaceAll("\r","\n").replaceAll("\n\n","\n");
        String[] arr = s.split("\n");
        List<String> list = new ArrayList<>();
        for(String t : arr){
            if(StringUtil.isNotEmpty(t)){
                list.add(t);
            }
        }
        return list.iterator();


    }

    /**
     * 从文件读取字符串 Iterator
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Iterator<String> readTextLineFromFile(String filePath) throws IOException{
       return readTextLineFromInputStream(new FileInputStream(filePath));

    }

    /**
     * 文件转 InputStream
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream fileToInputStream(File file)throws FileNotFoundException{
        InputStream inputStream =new FileInputStream(file);
        return inputStream;

    }
    /**
     * inputStreamToFile
     * InputStream 转成文件
     * @param inputStream
     * @param path
     * @return
     */
    public static boolean inputStreamToFile(InputStream inputStream , String path){
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        try{
            if(saveToFile(bis,path).exists()){
                return true ;
            }
        }catch (Exception e){
            ///E().error("inputStream转file异常",e);
            e.printStackTrace();
        }
        return false;


    }


    /**
     * 保存到文件
     * @param bis
     * @param path
     * @return
     * @throws IOException
     */
    private static File  saveToFile(BufferedInputStream bis ,String path) throws IOException {
        int size = 0 ;
        FileOutputStream fos = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        fos = new FileOutputStream(path);
        while ((size = bis.read(buf)) != -1) {
            fos.write(buf, 0, size);
        }
        fos.flush();
        return new File(path);

    }

    public static void write(String fileName, String content) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StringUtil.UTF8);
            osw.write(content);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拷贝文件 从老路径，到新路径 新路径
     * @param sourcePath
     * @param destPath
     */
    public static final void copyFile(String sourcePath ,String destPath){
        FileChannel input = null;
        FileChannel output = null;

        try {
            input = new FileInputStream(new File(sourcePath)).getChannel();
            output = new FileOutputStream(new File(destPath)).getChannel();
            output.transferFrom(input, 0, input.size());
        }catch (Exception e){
        }finally {
            try{
                output.close();
            }catch (Exception e){
            }
            try{
                input.close();
            }catch (Exception e){
            }

        }
    }

    public static void writeZip(String[] sourcePaths, String destPath  ,String fileName,boolean deleteSource) throws IOException {
        String finalZipFullPath =  destPath + fileName;
        if(!finalZipFullPath.toLowerCase().endsWith(".zip")){
            finalZipFullPath = finalZipFullPath+".zip";
        }
        OutputStream os = new BufferedOutputStream( new FileOutputStream( finalZipFullPath) );
        ZipOutputStream zos = new ZipOutputStream( os );
        byte[] buf = new byte[8192];
        int len;
        for (int i=0;i<sourcePaths.length;i++) {
            File file = new File( sourcePaths[i] );
            if ( !file.isFile() ){
                continue;
            }
            ZipEntry ze = new ZipEntry( file.getName() );
            zos.putNextEntry( ze );
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
            while ( ( len = bis.read( buf ) ) > 0 ) {
                zos.write( buf, 0, len );
            }
            zos.closeEntry();
        }
        zos.closeEntry();
        zos.close();

        if(deleteSource) {
            for (int i = 0; i < sourcePaths.length; i++) {
                File file = new File(sourcePaths[i]);
                file.delete();
            }
        }
    }


    public static File downloadFile(String urlPath ){
        File file = null;
        try {
            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            //设置超时
            httpURLConnection.setConnectTimeout(1000*5);
            //设置请求方式，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();
            // 文件大小
            int fileLength = httpURLConnection.getContentLength();

            // 控制台打印文件大小
            System.out.println("您要下载的文件大小为:" + fileLength / (1024 * 1024) + "MB");

            // 建立链接从请求中获取数据
            URLConnection con = url.openConnection();
            BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
            // 指定文件名称(有需求可以自定义)
            String fileFullName = "aaa.apk";
            // 指定存放位置(有需求可以自定义)
            String path =   File.separatorChar + fileFullName;
            file = new File(path);
            // 校验文件夹目录是否存在，不存在就创建一个目录
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            OutputStream out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[2048];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
                // 控制台打印文件下载的百分比情况
                System.out.println("下载了-------> " + len * 100 / fileLength + "%\n");
            }
            // 关闭资源
            bin.close();
            out.close();
            System.out.println("文件下载成功！");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("文件下载失败！");
        } finally {
            return file;
        }

    }




    public static void main(String[] args) throws IOException {

        //<a style="display: inline" class="file_name" docid="695422333802775552" docname="科主任查房3.pdf" onclick="showP(this)" href="javascript:void(0);">科主任查房3.pdf</a>
        String pngPath = "https://sh.thinkdid.com/images/tx.png";
        String xlsPath = "https://sh.thinkdid.com/files/52631635753400534.xlsx";
        String urlPath = "http://192.168.0.220:58089/docUrl?id=695422333732519936&fullfilename=%E7%A5%9E%E7%BB%8F%E5%86%85%E7%A7%91%E7%A7%91%E5%AE%A4%E9%9D%9E%E8%AE%A1%E5%88%92%E5%86%8D%E5%85%A5%E9%99%A212%E6%9C%88%E5%88%86%E6%9E%90%EF%BC%88%E6%A8%A1%E6%9D%BF%EF%BC%89.xlsx%22";
        URL url = new URL(pngPath);
        // 连接类的父类，抽象类
        URLConnection urlConnection = url.openConnection();
        // http的连接类
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        //设置超时
        httpURLConnection.setConnectTimeout(1000*5);
        //设置请求方式，默认是GET
        httpURLConnection.setRequestMethod("GET");
        // 设置字符编码
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        // 打开到此 URL引用的资源的通信链接（如果尚未建立这样的连接）。
        httpURLConnection.connect();

        int fileLength = httpURLConnection.getContentLength();

        System.out.println(url.getFile());
        //{
        //  null=[HTTP/1.1 200], Server=[nginx/1.9.9], X-Content-Type-Options=[nosniff], Connection=[keep-alive], Last-Modified=[Mon, 01 Nov 2021 07:56:40 GMT],
        //  Pragma=[no-cache], X-Application-Context=[adminClient:8800], Access-Control-Allow-Headers=[*], Date=[Wed, 03 Aug 2022 12:26:33 GMT],
        //  Accept-Ranges=[bytes], Strict-Transport-Security=[max-age=31536000 ; includeSubDomains], Cache-Control=[no-cache, no-store, max-age=0, must-revalidate],
        //  Expires=[0], X-XSS-Protection=[1; mode=block], Content-Length=[83899], Content-Type=[application/octet-stream;charset=UTF-8]
        // }
        System.out.println(httpURLConnection.getContentType());
        System.out.println(httpURLConnection.getContentType());
        System.out.println(httpURLConnection.getResponseMessage());
        System.out.println(httpURLConnection.getResponseCode());
        System.out.println(httpURLConnection.getHeaderField("Content-Disposition"));
        System.out.println(httpURLConnection.getHeaderFields());

    }

}
