package com.smartbracelet.com.smartbracelet.network;

import android.os.AsyncTask;

import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class SocketConnAsync extends AsyncTask<String, Void, String> implements ConstDefine {
    public AsyncResponse asyncResponse;

    private String mPostWord;
    private String mPostType;
    private String mParamsPost;

    private String postDetailRTR = null;

    public void setOnAsyncResponse(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    public SocketConnAsync(String url) {
        mPostWord = url;
    }

    @Override
    protected String doInBackground(String... params) {
        int index = 0;
        String url = EMPTY_STR;


        if (params != null && params.length > 0) {
            mPostType = params[0];
            mParamsPost = params[1];
        }


        //String httpUrl = "http://api.gigaset.com/cn/mobile/v1/demovideo/querydemo";

        url = "http://" + mPostWord;
        LogUtil.d("SocketConnAsync url" + url);

        LogUtil.d("SocketConnAsync mParamsPost" + mParamsPost);
        postDetailRTR = httpPostParams(url, mParamsPost);
        return postDetailRTR;
    }

    @Override
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
        if (msg != null) {
            asyncResponse.onDataReceivedSuccess(mPostType, msg);//将结果传给回调接口中的函数
        } else {
            asyncResponse.onDataReceivedFailed();
        }

    }

    private String httpPostParams(String url, String params) {
        String httpUrl = url;
        //创建httpRequest对象
        if (null == url) {
            return null;
        }


        try {
            HttpPost httpRequest = new HttpPost(httpUrl);

            //设置字符集
            //LogUtil.e("post, yangli:" + subitJson);


            StringEntity se = new StringEntity(params, "utf-8");
            //请求httpRequest
            httpRequest.setEntity(se);


            //取得默认的HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            //取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            //HttpStatus.SC_OK表示连接成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                //取得返回的字符串
                //String strResult = EntityUtils.toString(httpResponse.getEntity());
                //postRTR = "请求成功!";
                postDetailRTR = EntityUtils.toString(httpResponse.getEntity());

                LogUtil.e("post, yangli:" + postDetailRTR);
                //postDetailRTR = httpResponse.getEntity().toString();
            } else {
                //postRTR = "请求错误! 错误码" + httpResponse.getStatusLine().getStatusCode();
                postDetailRTR = null;
            }
        } catch (ClientProtocolException e) {
            //postRTR = "ClientProtocolException!";
            LogUtil.e("post, yangli:" + "ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            //postRTR = "IOException!";
            LogUtil.e("post, yangli:" + "IOException");
            e.printStackTrace();
        } catch (Exception e) {
            //postRTR = "Exception!";
            LogUtil.e("post, yangli:" + "Exception");
            e.printStackTrace();
        }

        return postDetailRTR;

    }
}
