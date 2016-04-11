package com.smartbracelet.com.smartbracelet.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.network.NetworkUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {
    private View mView;
    @Bind(R.id.timer_checkBox)
    CheckBox mTimerCB;

    @Bind(R.id.send_sentence_home)
    TextView mPostBackTx;

    @Bind(R.id.post_result_home)
    TextView mPostRtrTx;

    @Bind(R.id.post_result_details_home)
    TextView mPostDetailsRtrTx;

    @Bind(R.id.edit_text_home)
    EditText mEditText;

    @Bind(R.id.edit_params_home)
    EditText mEditParamsText;

    @Bind(R.id.timer_edit_text)
    EditText mTimerParamsText;

    @Bind(R.id.get_trams_button)
    Button mGetTramsButton;

    @Bind(R.id.update_number_button)
    Button mUpdateNumButton;

    @Bind(R.id.upload_gps_button)
    Button mPostButton;

    @Bind(R.id.upload_notify_button)
    Button mUploadNotifyButton;

    @Bind(R.id.push_message_button)
    Button mPushMsgButton;

    @Bind(R.id.post_package_home)
    Button mParamsPostButton;

    @Bind(R.id.stop_post_package)
    Button mSopPostParams;

    private static final int LOAD_MORE = 1;
    private static final int LOAD_NEW= 2;

    private static final int TYPE_GET_DEVICE_PARM = 0;
    private static final int TYPE_GET_NUM_PARM = 1;
    private static final int TYPE_UPLOAD_LOCATION = 2;
    private static final int TYPE_UPLOAD_NOTIFY = 3;
    private static final int TYPE_PUSH_MSG = 4;
    private static final int TYPE_PARAMS_POST = 5;
    private static int CURRENT_TYPE_POST = 9;
    private int mPendLoadType = 0;
    private List<ProgramItem> mNewPrograms;
    private int mLoadIndex = 0;
    private LoadDataTask mLoadTask;
    private PostDataTask mPostDataTask;

    private String methodStr;
    private String statusStr;
    private String deviceidStr;

    private boolean isTimerChecked =  false;

    String subitJson;

    private String postRTR;
    private String postDetailRTR;
    String strResult;

    private final int MSG_HTTP_POST = 0;
    private final int MSG_LOAD_DONE = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeHandler mHomeHandler;

    Timer timer = new Timer();
    TimerTask timerTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        mHomeHandler = new HomeHandler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, mView);
        initView();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        //Network check
        if (!NetworkUtil.isNetworkAvailable(mActivity)) {
            return;
        }

        //Task begin
        /*mLoadTask = new LoadDataTask(LOAD_MORE);
        mLoadTask.execute(mLoadIndex);*/
    }

    private LinearLayoutManager mLinearLayoutManager;
    //private HomeListAdapter mAdapter;
    private List<ProgramItem> mDatas = new ArrayList<ProgramItem>();

    private void initView() {


        // init recyclerView.
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        //mRecyclerView.setLayoutManager(mLinearLayoutManager);
        /*mAdapter = new HomeListAdapter(this, mActivity.getLayoutInflater(), mDatas);
        mAdapter.setOnItemClickListener(new HomeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int postion) {

            }
        });*/

        mTimerCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    isTimerChecked = true;
                } else {
                    isTimerChecked = false;
                }
            }
        });

        //mRecyclerView.setAdapter(mAdapter);
    }

    private class LoadDataTask extends AsyncTask<Integer, Void, Void> {

        private int mType;
        private String mWord;
        LoadDataTask(String url, int type) {
            mType = type;
            mWord = url;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int index = 0;
            /*if (params != null && params.length > 0) {
                index = params[0];
            }*/
            /*try {
                mNewPrograms = App.getRetrofitService().getProgramList(index);
                LogUtil.e("doInBackground, yangli:" + mNewPrograms.get(0).toString());
            } catch (Exception e) {
                LogUtil.e("doInBackgroun
                d, Exception:" + e.toString());
            }*/
            //Test

            httpGetParams(mWord, mType);



            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*mGetContentTx.setText(strResult);
            mMethodTx.setText("Method = " + methodStr);
            mStatusTx.setText("Status = " +statusStr);
            mDeviceIdTx.setText("DeviceID = " + deviceidStr);*/
        }
    }

    private void httpPostParams(String url, String params, int mPostType) {
        String httpUrl = url;
        //创建httpRequest对象
        if (null == url) {
            return;
        }


            try{
                LogUtil.e("doInBackground, Post httpUrl:" + httpUrl);
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
                LogUtil.e("post, yangli:" + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
                    //取得返回的字符串
                    //String strResult = EntityUtils.toString(httpResponse.getEntity());
                    postRTR = "请求成功!";
                    postDetailRTR = EntityUtils.toString(httpResponse.getEntity());
                    //postDetailRTR = httpResponse.getEntity().toString();
                }else{
                    postRTR = "请求错误! 错误码" +  httpResponse.getStatusLine().getStatusCode();
                    LogUtil.e("post, yangli:" + "请求错误");
                }
            }catch (ClientProtocolException e){
                postRTR = "ClientProtocolException!";
                LogUtil.e("post, yangli:" + "ClientProtocolException");
                e.printStackTrace();
            } catch (IOException e){
                postRTR = "IOException!";
                LogUtil.e("post, yangli:" + "IOException");
                e.printStackTrace();
            }catch (Exception e){
                postRTR = "Exception!";
                LogUtil.e("post, yangli:" + "Exception");
                e.printStackTrace();
            }

    }

    private void httpGetParams(String httpUrl, int type) {

        //httpUrl =  "fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q=" + "fuck";
        //String tempUrl = Utils.convertUrl(httpUrl);
        String url = "http://"+ httpUrl;
        LogUtil.e("doInBackground, httpUrl:" + url);
        if (null == url) {
            return;
        }
        try {
                /*URL url =  new URL("http://fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q=" + mWord);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);*/
            //创建httpRequest对象

            HttpGet httpRequest = new HttpGet(url);
            //取得HttpClient对象
            HttpClient httpclient = new DefaultHttpClient();
            //请求HttpClient，取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            //请求成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                //取得返回的字符串
                strResult = EntityUtils.toString(httpResponse.getEntity());
            }
            else
            {
                strResult= "请求错误!";
            }

            LogUtil.e("doInBackground, yangli:" + strResult);
            JSONObject jsonObject = new JSONObject(strResult);
            methodStr = jsonObject.getString("method");
            JSONObject jsonBasic = jsonObject.getJSONObject("params");
            statusStr = jsonBasic.getString("status");
            deviceidStr = jsonBasic.getString("deviceid");
            LogUtil.e("doInBackground, str:" + methodStr);
            LogUtil.e("doInBackground, str:" + statusStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PostDataTask extends AsyncTask<Integer, Void, Void> {

        private String mPostWord;
        private int mPostType;
        private String mParamsPost;

        HttpResponse httpResponse;

        PostDataTask(String url, String params, int type) {
            mPostWord = url;
            mPostType = type;
            mParamsPost = params;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int index = 0;
            String url = "";

            //String httpUrl = "http://api.gigaset.com/cn/mobile/v1/demovideo/querydemo";

            url = "http://"+ mPostWord;


            httpPostParams(url, mParamsPost, mPostType);
            return null;
        }



        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*if (null != mEditParamsText) {
                mEditParamsText.setText(subitJson);
            }*/
            mPostRtrTx.setText(postRTR);
            mPostDetailsRtrTx.setText(postDetailRTR);
        }
    }

    int cnt = 0;
    private class HomeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DONE:

                    break;
                case MSG_HTTP_POST:
                    cnt ++;
                    LogUtil.d(Integer.toString(cnt));

                    startPostTask();
                    break;
            }
        }
    }

    private void loadMore() {
        /*if (mLoadTask == null) {
            mLoadTask = new LoadDataTask(LOAD_MORE);
            mLoadTask.execute(mLoadIndex);
        } else {
            mPendLoadType = LOAD_MORE;
        }*/
    }


    @OnClick(R.id.get_trams_button)
     void onGetTramsButtonClick (View view) {
        CURRENT_TYPE_POST = TYPE_GET_DEVICE_PARM;
        subitJson= Utils.bindJOGetId().toString();
        LogUtil.e("doInBackground, Post subitJson:" + subitJson);
        mEditParamsText.setText(subitJson);
    }

    @OnClick(R.id.update_number_button)
    void onUpdateNumButtonClick (View view) {
        CURRENT_TYPE_POST = TYPE_UPLOAD_LOCATION;
        subitJson= Utils.bindJOTel().toString();
        LogUtil.e("doInBackground, Post subitJson:" + subitJson);
        mEditParamsText.setText(subitJson);
    }

    @OnClick(R.id.upload_gps_button)
    void onPostButtonClick (View view) {
        CURRENT_TYPE_POST = TYPE_UPLOAD_LOCATION;
        subitJson= Utils.bindJOGps(MainActivity.latitude, MainActivity.longtitude).toString();
        LogUtil.e("doInBackground, Post subitJson:" + subitJson);
        mEditParamsText.setText(subitJson);
    }

    @OnClick(R.id.upload_notify_button)
    void onUplaodNotifyButtonClick (View view) {
        CURRENT_TYPE_POST = TYPE_UPLOAD_NOTIFY;
        subitJson= Utils.bindJOWarning().toString();
        LogUtil.e("doInBackground, Post subitJson:" + subitJson);
        mEditParamsText.setText(subitJson);
    }

    @OnClick(R.id.push_message_button)
    void onPushMsgButtonClick (View view) {
        CURRENT_TYPE_POST = TYPE_PUSH_MSG;
        subitJson= Utils.bindJOMsgPush().toString();
        LogUtil.e("doInBackground, Post subitJson:" + subitJson);
        mEditParamsText.setText(subitJson);
    }

    @OnClick(R.id.post_package_home)
    void onParamsPostButtonClick (View view) {

        if (isTimerChecked) {
            int times = Integer.parseInt(mTimerParamsText.getText().toString());
            //Must cancel first

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //SEND MESSAGE TIMER
                    Message msg = new Message();
                    msg.what = MSG_HTTP_POST;
                    if (null != mHomeHandler) {
                        mHomeHandler.sendMessage(msg);
                    }
                }
            };
            timer.schedule(timerTask, 0, times);



        } else {
            startPostTask();
        }

    }

    @OnClick(R.id.stop_post_package)
    void onStopPostButtonClick (View view) {
        if (null != timerTask) {
            LogUtil.d("stop_post_package");
            timerTask.cancel();
        }
    }

    private void startPostTask() {
        String inputStr = mEditText.getText().toString();
        String inputParams = mEditParamsText.getText().toString();
        mPostDataTask = new PostDataTask(inputStr, inputParams, CURRENT_TYPE_POST);
        mPostDataTask.execute(mLoadIndex);
    }

}
