package com.smartbracelet.com.smartbracelet.ui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends BaseFragment {
    private View mView;

    @Bind(R.id.method_get_home)
    TextView mMethodTx;

    @Bind(R.id.device_id_get_home)
    TextView mDeviceIdTx;

    @Bind(R.id.get_content_home)
    TextView mGetContentTx;

    @Bind(R.id.send_sentence_home)
    TextView mPostBackTx;

    @Bind(R.id.status_get_home)
    TextView mStatusTx;

    @Bind(R.id.edit_text_home)
    EditText mEditText;

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

    private static final int LOAD_MORE = 1;
    private static final int LOAD_NEW= 2;

    private static final int TYPE_GET_DEVICE_PARM = 0;
    private static final int TYPE_GET_NUM_PARM = 1;
    private static final int TYPE_UPLOAD_LOCATION = 2;
    private static final int TYPE_UPLOAD_NOTIFY = 3;
    private static final int TYPE_PUSH_MSG = 4;
    private int mPendLoadType = 0;
    private List<ProgramItem> mNewPrograms;
    private int mLoadIndex = 0;
    private LoadDataTask mLoadTask;
    private PostDataTask mPostDataTask;

    private String methodStr;
    private String statusStr;
    private String deviceidStr;

    private String postRTR;
    String strResult;

    private final int MSG_REFRESH = 0;
    private final int MSG_LOAD_DONE = 1;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeHandler mHomeHandler;


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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            String httpUrl = "http://"+mWord;
            LogUtil.e("doInBackground, httpUrl:" + httpUrl);

            httpGetParams(httpUrl, mType);



            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mGetContentTx.setText(strResult);
            mMethodTx.setText("Method = " + methodStr);
            mStatusTx.setText("Status = " +statusStr);
            mDeviceIdTx.setText("DeviceID = " + deviceidStr);
        }
    }

    private void httpPostParams(String url, int mPostType) {
        String httpUrl = url;
        //创建httpRequest对象
        HttpPost httpRequest = new HttpPost(httpUrl);

        if (mPostType == TYPE_UPLOAD_LOCATION) {
            try{
                //设置字符集
                //LogUtil.e("post, yangli:" + subitJson);
                String subitJson= Utils.bindJOGps(MainActivity.latitude, MainActivity.longtitude).toString();
                LogUtil.e("doInBackground, Post subitJson:" + subitJson);
                StringEntity se = new StringEntity(subitJson, "utf-8");
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
                }else{
                    postRTR = "请求错误!";
                    LogUtil.e("post, yangli:" + "请求错误");
                }
            }catch (ClientProtocolException e){
                LogUtil.e("post, yangli:" + "ClientProtocolException");
                e.printStackTrace();
            } catch (IOException e){
                LogUtil.e("post, yangli:" + "IOException");
                e.printStackTrace();
            }catch (Exception e){
                LogUtil.e("post, yangli:" + "Exception");
                e.printStackTrace();
            }
        }

    }

    private void httpGetParams(String httpUrl, int type) {
        //String httpUrl =  "http://fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q=" + mWord;
        try {
                /*URL url =  new URL("http://fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q=" + mWord);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);*/
            //创建httpRequest对象
            HttpGet httpRequest = new HttpGet(httpUrl);
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
        }
    }

    private class PostDataTask extends AsyncTask<Integer, Void, Void> {

        private String mPostWord;
        private int mPostType;

        HttpResponse httpResponse;
        PostDataTask(String url, int type) {
            mPostWord = url;
            mPostType = type;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int index = 0;

            String httpUrl = "http://"+ mPostWord;
            LogUtil.e("doInBackground, Post httpUrl:" + httpUrl);
            httpPostParams(mPostWord, mPostType);
            return null;
        }



        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mPostBackTx.setText(postRTR);
        }
    }

    private class HomeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DONE:

                    break;
                case MSG_REFRESH:
                    loadNew();
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

    private void loadNew() {
        /*if (mLoadTask == null) {
            mLoadTask = new LoadDataTask(LOAD_NEW);
            mLoadTask.execute(0);
        } else {
            mPendLoadType = LOAD_NEW;
        }*/
    }

    @OnClick(R.id.get_trams_button)
     void onGetTramsButtonClick (View view) {
        String inputStr = mEditText.getText().toString();
        mLoadTask = new LoadDataTask(inputStr, TYPE_GET_DEVICE_PARM);
        mLoadTask.execute(mLoadIndex);
    }

    @OnClick(R.id.update_number_button)
    void onUpdateNumButtonClick (View view) {
        String inputStr = mEditText.getText().toString();
        mLoadTask = new LoadDataTask(inputStr, TYPE_GET_NUM_PARM);
        mLoadTask.execute(mLoadIndex);
    }

    @OnClick(R.id.upload_gps_button)
    void onPostButtonClick (View view) {
        String inputStr = mEditText.getText().toString();
        mPostDataTask = new PostDataTask(inputStr, TYPE_UPLOAD_LOCATION);
        mPostDataTask.execute(mLoadIndex);
    }

    @OnClick(R.id.upload_notify_button)
    void onUplaodNotifyButtonClick (View view) {
        String inputStr = mEditText.getText().toString();
        mPostDataTask = new PostDataTask(inputStr, TYPE_UPLOAD_NOTIFY);
        mPostDataTask.execute(mLoadIndex);
    }

    @OnClick(R.id.push_message_button)
    void onPushMsgButtonClick (View view) {
        String inputStr = mEditText.getText().toString();
        mPostDataTask = new PostDataTask(inputStr, TYPE_PUSH_MSG);
        mPostDataTask.execute(mLoadIndex);
    }

}
