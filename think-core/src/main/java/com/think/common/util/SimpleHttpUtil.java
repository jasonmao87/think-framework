package com.think.common.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.think.core.annotations.Remark;
import okhttp3.*;

/**
 * 最简单的httpUtil类
 */
public class SimpleHttpUtil {


    public static OkHttpClient client = null;

    static {
        client = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();
//        client = new OkHttpClient();
    }

    /**
     * get请求
     * @param url
     * @return
     */
    public static String httpGet(String url) {

        String result = null;
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * post请求
     * @param url
     * @param data [提交的参数为 "key=value&key1=value1" 的形式]
     */
    public static String httpPost(String url, String data) {
        String result = null;
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/html;charset=StringUtil.UTF8"), data);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * post请求
     * @param url
     * @param headerMap
     * @param dataMap
     * @return
     */
    public static final String httpPost(String url, Map<String,String> headerMap, Map<String,String> dataMap){
        String result = null;
//        OkHttpClient httpClient = new OkHttpClient();
        FormBody.Builder builder =new FormBody.Builder();
        if(dataMap!=null) {
            for (Map.Entry<String, String> t : dataMap.entrySet()) {
                builder.add(t.getKey(), t.getValue());
            }
        }
        RequestBody body = builder.build();
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        if(headerMap!=null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(),entry.getValue());
            }
        }
        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
