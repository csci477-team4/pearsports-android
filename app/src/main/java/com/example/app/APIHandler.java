package com.example.app;

import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Shay on 2/17/14.
 */
public class APIHandler {

    static String response = null;
    public static final int POST = 456;
    public static final int GET = 654;

    public String sendAPIRequest(String path, int method) {
        return this.sendAPIRequest(path, method, null);
    }

    public String sendAPIRequest(String path, int method,
                                 List<NameValuePair> params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            String url = buildURL(path);

            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public String sendAPIRequestWithAuth(String path, int method,
    String email, String password) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            String url = buildURL(path);

            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
                httpPost.setHeader("Authorization",base64EncodedCredentials);
                httpResponse = httpClient.execute(httpPost);
            } else if (method == GET) {
                HttpGet httpGet = new HttpGet(url);
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
                httpGet.setHeader("Authorization",base64EncodedCredentials);
                httpResponse = httpClient.execute(httpGet);
            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String buildURL(String path)
    {
        StringBuilder url = new StringBuilder();
        url.append("https://cs477-backend.herokuapp.com");
        url.append("/");
        url.append(path);
        return url.toString();
    }

}
