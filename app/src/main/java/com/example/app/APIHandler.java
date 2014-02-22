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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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

    /**
     *
     * @param path - e.g. "trainee_list" or "sign-in"
     * @param method - APIHandler.GET or APIHandler.POST
     * @return
     */
    public JSONObject sendAPIRequest(String path, int method) {
        return this.sendAPIRequest(path, method, null);
    }

    /**
     *
     * @param path - e.g. "trainee_list" or "sign-in"
     * @param method - APIHandler.GET or APIHandler.POST
     * @param params - List of key-value pairs
     * @return
     */
    public JSONObject sendAPIRequest(String path, int method,
                                 List<NameValuePair> params) {
        JSONObject jsonObject = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpParams httpParameters = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
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

        if (response != null) {
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     *
     * @param path - e.g. "trainee_list" or "sign-in"
     * @param method - APIHandler.GET or APIHandler.POST
     * @param key - username or token
     * @param value - password. if blank, assumes key is token
     * @return
     */
    public JSONObject sendAPIRequestWithAuth(String path, int method,
    String key, String value) {
        JSONObject jsonObject = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpParams httpParameters = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            String url = buildURL(path);

            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((key + ":" + value).getBytes(), Base64.NO_WRAP);
                httpPost.setHeader("Authorization",base64EncodedCredentials);
                httpResponse = httpClient.execute(httpPost);
            } else if (method == GET) {
                HttpGet httpGet = new HttpGet(url);
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((key + ":" + value).getBytes(), Base64.NO_WRAP);
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

        if (response != null) {
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     *
     * @param path - e.g. "trainee_list" or "sign-in"
     * @return
     */
    public static String buildURL(String path)
    {
        StringBuilder url = new StringBuilder();
        url.append("https://cs477-backend.herokuapp.com");
        url.append("/");
        url.append(path);
        return url.toString();
    }

}
