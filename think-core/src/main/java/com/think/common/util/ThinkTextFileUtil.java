package com.think.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/26 13:38
 * @description :
 */
@Slf4j
public class ThinkTextFileUtil {
    public static final long fileSize(File file){
        return file.getTotalSpace();
    }


    public static String readLastLine(File file){

        return null;
    }

    private static final String rLastLine(File file){
        String lastLine = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String currentLine = "";
            while ((currentLine = bufferedReader.readLine()) != null) {
                lastLine = currentLine;
            }
        } catch (Exception e) {
        }
        return lastLine;
    }

    public static String readLastLineV1(File file) {
        // 存储结果
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            // 指针位置开始为0，所以最大长度为 length-1
            long fileLastPointer = randomAccessFile.length() - 1;
            // 从后向前读取文件
            for (long filePointer = fileLastPointer; filePointer != -1; filePointer--) {
                // 移动指针指向
                randomAccessFile.seek(filePointer);
                int readByte = randomAccessFile.readByte();
                if (0xA == readByte) {
                    //  LF='\n'=0x0A 换行
                    if (filePointer == fileLastPointer) {
                        // 如果是最后的换行，过滤掉
                        continue;
                    }
                    break;
                }
                if (0xD == readByte) {
                    //  CR ='\r'=0x0D 回车
                    if (filePointer == fileLastPointer - 1) {
                        // 如果是倒数的回车也过滤掉
                        continue;
                    }
                    break;
                }
                builder.append((char) readByte);
            }
        } catch (Exception e) {
            log.error("file read error, msg:{}", e.getMessage(), e);
        }
        return builder.reverse().toString();
    }

}
