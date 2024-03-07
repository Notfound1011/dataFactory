package com.phemex.dataFactory.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
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
    private static final int SOCKET_TIMEOUT = 20000;

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .build();

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
     * Description: get请求 不带参数
     *
     * @param url     路径
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String get(String url, Map<String, String> headers) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return getRequest(headers, httpGet);
    }

    /*
     * @Description: get请求 带参数
     * @Date: 2024/1/6
     * @Param url:
     * @Param headers:
     * @Param params:
     **/
    public static String get(String url, Map<String, String> headers, Map<String, Object> params) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return getRequest(headers, httpGet);
    }

    private static String getRequest(Map<String, String> headers, HttpGet httpGet) throws IOException {
        CloseableHttpClient httpClient = null;
        setHeader(headers, httpGet);
        httpGet.setConfig(requestConfig);
        httpClient = HttpClientBuilder.create().build();
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // Check the response status and handle accordingly
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
        } finally {
            release(null, httpClient);
        }
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
        try {
            httpClient = HttpClientBuilder.create().build();

            httpGet = new HttpGet(url);
            //设置请求头
            setHeader(headers, httpGet);
            httpGet.setConfig(requestConfig);
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
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            //设置请求头
            setHeader(headers, httpPost);
            //设置参数
            setParam(params, httpPost);

            httpPost.setConfig(requestConfig);

            result = getHttpClientResult(httpClient, httpPost);
//            httpResponse = httpClient.execute(httpPost);
        } finally {
            release(null, httpClient);
        }
        return result;
    }


    /*
     * @Description: Post请求，返回值类型为CloseableHttpResponse
     * @Date: 2024/1/6
     * @Param url:
     * @Param body:
     * @Param headers:
     * @return CloseableHttpResponse
     **/
    public static CloseableHttpResponse httpPost(String url, Map<String, Object> body, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            //设置请求头
            setHeader(headers, httpPost);
            //设置参数
            if (body != null && !body.isEmpty()) {
                String json = JSONObject.toJSONString(body);
                StringEntity stringEntity = new StringEntity(json, "UTF-8");
                httpPost.setEntity(stringEntity);
            }
            httpPost.setConfig(requestConfig);
            httpResponse = httpClient.execute(httpPost);
        } finally {
            release(null, httpClient);
        }
        return httpResponse;
    }


    /**
     * Description: post请求（用于请求json格式的参数）
     *
     * @param url     路径
     * @param body    json请求参数为String类型
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String jsonPost(String url, String body, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            //设置请求头
            setHeader(headers, httpPost);
            //设置参数
            if (body != null) {
                httpPost.setEntity(new StringEntity(body, ENCODING));
            }
            httpPost.setConfig(requestConfig);

            result = getHttpClientResult(httpClient, httpPost);
        } finally {
            release(null, httpClient);
        }
        return result;
    }


    /**
     * Description: post请求（用于请求json格式的参数）
     *
     * @param url     路径
     * @param body    json请求参数为Map类型
     * @param headers 请求头信息
     * @return 响应结果
     */
    public static String jsonPost(String url, Map<String, Object> body, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost;
        String result;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            //设置请求头
            setHeader(headers, httpPost);
            String jsonBody = JSONObject.toJSONString(body);
            //设置参数
            if (body != null) {
                httpPost.setEntity(new StringEntity(jsonBody, ENCODING));
            }
            httpPost.setConfig(requestConfig);

            result = getHttpClientResult(httpClient, httpPost);
        } finally {
            release(null, httpClient);
        }
        return result;
    }


    /**
     * Description: post请求（适用于请求体为json格式，无请求头）
     *
     * @param url  路径
     * @param body json参数
     * @return 响应结果
     */
    public static String jsonPost(String url, String body) throws Exception {
        return jsonPost(url, body, null);
    }


    /**
     * Description: post请求（适用于有请求头，无请求体）
     *
     * @param url     路径
     * @param headers 请求头信息
     * @return String 响应结果
     */
    public static String jsonPost(String url, Map<String, String> headers) throws Exception {
        return jsonPost(url, "", headers);
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

            httpPut.setConfig(requestConfig);
            result = getHttpClientResult(httpClient, httpPut);
        } finally {
            release(null, httpClient);
        }
        return result;
    }

    public static CloseableHttpResponse httpPut(String url, Map<String, Object> urlParams, Map<String, Object> params, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpPut httpPut;
        CloseableHttpResponse httpResponse = null;
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

            httpPut.setConfig(requestConfig);
            httpResponse = httpClient.execute(httpPut);
        } finally {
            release(null, httpClient);
        }
        return httpResponse;
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
        if (!list.isEmpty()) {
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
     * @param httpClient httpClient
     * @param httpMethod httpMethod
     * @return 响应结果
     * @throws Exception Exception
     */
    public static String getHttpClientResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        String result = null;
        // 执行请求
        CloseableHttpResponse httpResponse = httpClient.execute(httpMethod);
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