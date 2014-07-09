package com.hsbc.gltc.globalkalendar.util;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.hsbc.gltc.globalkalendar.client.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Henyue-GZ on 2014/7/6.
 */
public class WeiboHelper {
    private static HttpClient client;
    private static HttpClient getClient() {
        if (client == null) {
            client = new DefaultHttpClient();
        }
        return client;
    }

    public static void sendWeibo(Context context, String accessToken, TextView msgTV) {
        HttpPost post = new HttpPost(getSysProperties(context, SysConstants.WEIBO_UPDATE_URL));
        Map<String, String> params = new HashMap<String, String>();
        params.put("source", "3G5oUM");
        params.put("status", msgTV.getText().toString());
        params.put("access_token", accessToken);
        try {
            post.setEntity(createFormEntity(params));
            HttpResponse response = getClient().execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Toast.makeText(context, R.string.test_weibo_sendMsg_success, Toast.LENGTH_SHORT).show();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSysProperties(Context context, SysConstants sysCons) {
        return DBHelper.getSysProperties(context, sysCons);
    }

    private static HttpEntity createFormEntity(Map<String, String> params) throws UnsupportedEncodingException {
        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        if (params != null && params.size() > 0) {
            Set<String> paramKeys = params.keySet();
            for (String key : paramKeys) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        return new UrlEncodedFormEntity(paramList, HTTP.UTF_8);
    }
}
