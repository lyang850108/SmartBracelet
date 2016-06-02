package com.smartbracelet.com.smartbracelet.network;

import android.os.AsyncTask;

import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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


            //Before
            //取得默认的HttpClient
            //HttpClient httpclient = new DefaultHttpClient();
            //Before end
            // 参数
            HttpParams httpParameters = new BasicHttpParams();
            // 设置连接超时
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            // 设置socket超时
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            //使用Https
            HttpClient httpclient = initHttpClient(httpParameters);

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

    private static HttpClient client = null;
    /**
     * 初始化HttpClient对象
     * @param params
     * @return
     */
    public static synchronized HttpClient initHttpClient(HttpParams params) {
        if(client == null){
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
                //允许所有主机的验证
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                // 设置http和https支持
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultHttpClient(params);
            }
        }
        return client;
    }

    public static class SSLSocketFactoryImp extends SSLSocketFactory {
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryImp(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {
                }
            };
            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
