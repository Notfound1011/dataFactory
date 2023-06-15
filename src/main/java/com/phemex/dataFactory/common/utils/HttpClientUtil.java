package com.phemex.dataFactory.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Date: 2022年04月17日 19:30
 * @Description:
 */
public class HttpClientUtil {
    // 编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";

    // 设置连接超时时间，单位毫秒。
    private static final int CONNECT_TIMEOUT = 5000;

    // 请求获取数据的超时时间(即响应时间)，单位毫秒。
    private static final int SOCKET_TIMEOUT = 5000;

    /**
     * Description: get请求（适用于无请求头）
     *
     * @param url 路径
     * @return 响应结果
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * Description: post请求（适用于无请求头）
     *
     * @param url    请求路径
     * @param params url后带的参数
     * @return 响应结果
     */
    public static String post(String url, Map<String, Object> params) throws Exception {
        return post(url, params, null);
    }

    /**
     * Description: post请求（适用于请求体为json格式，无请求头）
     *
     * @param url    路径
     * @param body json参数
     * @return 响应结果
     */
    public static String jsonPost(String url, String body) throws Exception {
        return jsonPost(url, body, null);
    }


    /**
     * Description: post请求（适用于有请求头，无请求体）
     *
     * @param url 路径
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String jsonPost(String url, Map<String, String> headers) throws Exception {
        return jsonPost(url, null, headers);
    }

    /**
     * Description: get请求
     *
     * @param url     路径
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String get(String url, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpGet httpGet;
        CloseableHttpResponse httpResponse = null;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();

            httpGet = new HttpGet(url);
            //设置请求头
            setHeader(headers, httpGet);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpGet.setConfig(config);
            result = getHttpClientResult(httpResponse, httpClient, httpGet);
        } finally {
            release(httpResponse, httpClient);
        }
        return result;
    }


    /**
     * Description: httpGet请求
     *
     * @param url     路径
     * @param headers 请求头信息
     * @return CloseableHttpResponse
     */
    public static CloseableHttpResponse httpGet(String url, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpGet httpGet;
        CloseableHttpResponse httpResponse = null;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();

            httpGet = new HttpGet(url);
            //设置请求头
            setHeader(headers, httpGet);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpGet.setConfig(config);
            httpResponse = httpClient.execute(httpGet);
//            result = getHttpClientResult(httpResponse, httpClient, httpGet);
        } finally {
            release(httpResponse, httpClient);
        }
        return httpResponse;
    }


    /**
     * Description: post请求
     *
     * @param url     请求路径
     * @param params  url后带的参数
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost;
        CloseableHttpResponse httpResponse = null;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            //设置请求头
            setHeader(headers, httpPost);
            //设置参数
            setParam(params, httpPost);

            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpPost.setConfig(config);

            result = getHttpClientResult(httpResponse, httpClient, httpPost);
//            httpResponse = httpClient.execute(httpPost);
        } finally {
            release(httpResponse, httpClient);
        }
        return result;
    }


    /**
     * Description: post请求（用于请求json格式的参数）
     *
     * @param url     路径
     * @param body  json请求参数
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String jsonPost(String url, String body, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost;
        CloseableHttpResponse httpResponse = null;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            //设置请求头
            setHeader(headers, httpPost);
            //设置参数
            if(body != null){
                httpPost.setEntity(new StringEntity(body, ENCODING));
            }
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpPost.setConfig(config);

            result = getHttpClientResult(httpResponse, httpClient, httpPost);
        } finally {
            release(httpResponse, httpClient);
        }
        return result;
    }

    /**
     * @Description: PUT请求
     * @Date: 2022/4/17
     * @Param url:
     * @Param urlParams:
     * @Param params:
     * @Param headers:
     **/
    public static String put(String url, Map<String, Object> urlParams, Map<String, Object> params, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPut httpPut;
        CloseableHttpResponse httpResponse = null;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();
            if (null != urlParams) {
                httpPut = new HttpPut(url + "?" + setUrlParam(urlParams));
            } else {
                httpPut = new HttpPut(url);
            }
            //设置请求头
            setHeader(headers, httpPut);
            //设置参数
            setParam(params, httpPut);

            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpPut.setConfig(config);
            result = getHttpClientResult(httpResponse, httpClient, httpPut);
        } finally {
            release(httpResponse, httpClient);
        }
        return result;
    }

    /**
     * Description: 封装请求头
     *
     * @param headers    请求头
     * @param httpMethod httpMethod
     */
    public static void setHeader(Map<String, String> headers, HttpRequestBase httpMethod) {
        // 封装请求头
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                httpMethod.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Description: 封装请求参数
     *
     * @param params     请求参数
     * @param httpMethod httpMethod
     */
    public static void setParam(Map<String, Object> params, HttpEntityEnclosingRequestBase httpMethod) throws UnsupportedEncodingException {
        List<NameValuePair> list = mapToList(params);
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, ENCODING);
            httpMethod.setEntity(entity);
        }
    }


    /**
     * Description: 封装请求参数
     *
     * @param params 请求参数
     */
    public static String setUrlParam(Map<String, Object> params) {
        List<NameValuePair> list = mapToList(params);
        String paramsStr = null;
        try {
            paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(list, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramsStr;
    }

    /**
     * @Description: map转list
     * @Date: 2022/4/17
     * @Param params: 请求参数
     **/
    private static List<NameValuePair> mapToList(Map<String, Object> params) {
        List<NameValuePair> list = new ArrayList<>();
        if (null != params) {
            for (Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                list.add(new BasicNameValuePair(key, value));
            }
        }
        return list;
    }

    /**
     * Description: 获取响应结果
     *
     * @param httpResponse httpResponse
     * @param httpClient   httpClient
     * @param httpMethod   httpMethod
     * @return 响应结果
     * @throws Exception Exception
     */
    public static String getHttpClientResult(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        String result = null;
        // 执行请求
        httpResponse = httpClient.execute(httpMethod);
        // 获取返回结果
        if (httpResponse != null) {
            HttpEntity resEntity = httpResponse.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, ENCODING);
            }
        }
        return result;
    }


    /**
     * Description: 释放资源
     *
     * @param httpResponse HttpResponse
     * @param httpClient   HttpClient
     * @throws IOException IOException
     */
    public static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) throws IOException {
        if (httpResponse != null) {
            httpResponse.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }
}