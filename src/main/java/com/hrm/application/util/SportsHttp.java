package com.hrm.application.util;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SportsHttp {

  private static final Logger logger = LoggerFactory.getLogger(SportsHttp.class);
  private static final PoolingHttpClientConnectionManager connMgr;
  private static final RequestConfig requestConfig;

  static {
    // 设置连接池
    connMgr = new PoolingHttpClientConnectionManager();
    // 设置连接池大小
    connMgr.setMaxTotal(30000);
    connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
    int MAX_TIMEOUT = 80000;
    requestConfig = RequestConfig.custom()
        // 设置连接超时
        .setConnectTimeout(MAX_TIMEOUT)
        // 设置读取超时
        .setSocketTimeout(MAX_TIMEOUT)
        // 设置从连接池获取连接实例的超时
        .setConnectionRequestTimeout(MAX_TIMEOUT)
        // 在提交请求之前 测试连接是否可用
        .setStaleConnectionCheckEnabled(true)
        .build();
  }

  /**
   * GET请求
   */
  public static String doGet(String apiUrl, Header... headers) throws Exception {
    CloseableHttpResponse response = null;
    try (CloseableHttpClient httpClient = SSLClient.createSSLClientDefault()) {
      HttpGet httpGet = new HttpGet(apiUrl);
      httpGet.setConfig(requestConfig);
      httpGet.setHeaders(headers);
      response = httpClient.execute(httpGet);
      return EntityUtils.toString(response.getEntity(), "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
        } catch (IOException ex) {
          logger.error("关闭http流发生异常", ex);
        }
      }
    }
  }

  /**
   * POST请求 ,请求参数格式为JSON
   */
  public static String doPost(String url, String params, Header... headers) throws Exception {
    return post(url, null, ContentType.APPLICATION_JSON, params, headers);
  }

  public static String doPost(String url, HttpClientContext httpContext, String params, Header... headers) throws Exception {
    return post(url, httpContext, ContentType.APPLICATION_JSON, params, headers);
  }

  /**
   * POST请求 ,请求参数格式为form表单
   */
  public static String doPost(String url, Map<String, Object> params, Header... headers) throws Exception {
    return post(url, null, ContentType.APPLICATION_FORM_URLENCODED, params, headers);
  }

  public static String doPost(String url, HttpClientContext httpContext, Map<String, Object> params, Header... headers)
      throws Exception {
    return post(url, httpContext, ContentType.APPLICATION_FORM_URLENCODED, params, headers);
  }

  private static String post(String url, HttpClientContext httpContext, ContentType pattern, Object params, Header[] headers)
      throws Exception {
    CloseableHttpResponse response = null;
    try (CloseableHttpClient httpClient = SSLClient.createSSLClientDefault()) {
      HttpPost httpPost = new HttpPost(url);
      StringEntity stringEntity = null;
      httpPost.setConfig(requestConfig);
      httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
      httpPost.setHeader(HTTP.CONN_DIRECTIVE,HTTP.CONN_CLOSE);
      httpPost.setHeaders(headers);
      if (params != null) {
        if (ContentType.APPLICATION_JSON.equals(pattern)) {
          stringEntity = new StringEntity((String) params, "UTF-8");
          stringEntity.setContentEncoding("UTF-8");
          stringEntity.setContentType("application/json");
        } else if (ContentType.APPLICATION_FORM_URLENCODED.equals(pattern)) {
          if (!(params instanceof Map)) {
            throw new IllegalArgumentException("参数异常");
          } else {
            // 创建参数队列
            List<NameValuePair> formParams = new ArrayList<>();
            for (Entry<String, Object> entry : ((Map<String, Object>) params).entrySet()) {
              formParams.add(new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
            }
            stringEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            stringEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
          }
        }
      }
      httpPost.setEntity(stringEntity);
      response = httpClient.execute(httpPost, httpContext);
      HttpEntity entity = response.getEntity();
      int httpStatus = response.getStatusLine().getStatusCode();
      String responseStr = EntityUtils.toString(entity, "UTF-8");
      if (httpStatus != HttpStatus.SC_OK) {
        throw new HttpException(String.format("url=[%s],statusCode=[%d],response=\n%s", url, httpStatus, responseStr));
      }
      return responseStr;
    } catch (Exception e) {
      throw e;
    } finally {
      if (response != null) {
        try {
          EntityUtils.consume(response.getEntity());
          response.close();
        } catch (IOException ex) {
          logger.error("关闭http流发生异常", ex);
        }
      }
    }
  }

  public static String doPost(String url, List<NameValuePair> params,Header... headers) throws Exception {
    CloseableHttpResponse response = null;
    try (CloseableHttpClient httpClient = SSLClient.createSSLClientDefault()) {
      URI uri = new URIBuilder(url).setParameters(params).build();
      HttpPost post = new HttpPost(uri);
      post.setHeaders(headers);
      response = httpClient.execute(post);
      HttpEntity responseEntity = response.getEntity();
      int status = response.getStatusLine().getStatusCode();
      String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
      if (status == HttpStatus.SC_OK) {
        return responseStr;
      } else {
        logger.error("url=[{}],statusCode=[{}],response=[{}]", url, status, responseStr);
        throw new HttpException("http_error/" + status + ":" + responseStr, null);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (IOException e) {
          logger.error(e.toString());
        }
      }
    }
  }
}
