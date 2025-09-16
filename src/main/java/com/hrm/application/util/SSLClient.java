package com.hrm.application.util;

import com.hrm.application.model.Sports;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class SSLClient {

  private static SSLConnectionSocketFactory getDefaultSSLSocketFactory()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    //信任所有
    SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
    return new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);
  }

  public static RequestConfig.Builder defaultRequestConfig() {
    return RequestConfig.custom()
        .setCookieSpec(CookieSpecs.STANDARD)
        .setConnectTimeout(5000)
        .setSocketTimeout(10000)
        .setConnectionRequestTimeout(5000);
  }

  public static CloseableHttpClient createSSLClientDefault(HttpClientBuilder builder) {
    try {
      return builder.setSSLSocketFactory(getDefaultSSLSocketFactory()).build();
    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
      e.printStackTrace();
      return HttpClients.createDefault();
    }
  }

  public static CloseableHttpClient createSSLClientDefault(String userAgent, RequestConfig requestConfig) {
    HttpClientBuilder httpClientBuilder = HttpClients.custom()
        .setUserAgent(Optional.ofNullable(StringUtils.trimToNull(userAgent)).orElse(Sports.Default_UA))
        .setDefaultRequestConfig(Optional.ofNullable(requestConfig).orElse(defaultRequestConfig().build()));
    return createSSLClientDefault(httpClientBuilder);
  }

  public static CloseableHttpClient createSSLClientDefault() {
    return createSSLClientDefault(Sports.Default_UA, null);
  }
}
