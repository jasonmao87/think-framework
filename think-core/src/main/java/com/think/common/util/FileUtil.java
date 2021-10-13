package com.think.common.util;


import java.io.*;
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


}
