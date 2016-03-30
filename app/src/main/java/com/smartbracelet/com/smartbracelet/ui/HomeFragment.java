package com.smartbracelet.com.smartbracelet.ui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.adapter.HomeListAdapter;
import com.smartbracelet.com.smartbracelet.model.BaseFragment;
import com.smartbracelet.com.smartbracelet.model.ProgramItem;
import com.smartbracelet.com.smartbracelet.network.NetworkUtil;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

    @Bind(R.id.recycler_view)
    LinearLayout mRecyclerView;

    @Bind(R.id.device_id_home)
    TextView mDeviceIdTx;

    @Bind(R.id.send_sentence_home)
    TextView mPostBackTx;

    @Bind(R.id.expalin_home)
    TextView mExplainTx;

    @Bind(R.id.edit_text_home)
    EditText mEditText;

    @Bind(R.id.get_trams_button)
    Button mButton;

    @Bind(R.id.set_trams_button)
    Button mPostButton;

    private static final int LOAD_MORE = 1;
    private static final int LOAD_NEW= 2;
    private int mPendLoadType = 0;
    private List<ProgramItem> mNewPrograms;
    private int mLoadIndex = 0;
    private LoadDataTask mLoadTask;
    private PostDataTask mPostDataTask;

    private final int MSG_REFRESH = 0;
    private final int MSG_LOAD_DONE = 1;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeHandler mHomeHandler;
    String line;


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

        private String mWord;
        private String translationStr;
        private String explainsStr;
        LoadDataTask(String type) {
            mWord = type;
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
            try {
                URL url =  new URL("http://fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q=" + mWord);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                while (null != (line = bufferedReader.readLine())){
                    LogUtil.e("doInBackground, yangli:" + line);
                    JSONObject jsonObject = new JSONObject(line);
                    translationStr = jsonObject.getString("translation");
                    JSONObject jsonBasic = jsonObject.getJSONObject("basic");
                    explainsStr = jsonBasic.getString("explains");
                    LogUtil.e("doInBackground, str:" + translationStr);
                    LogUtil.e("doInBackground, str:" + explainsStr);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            /*if (mLoadType == LOAD_MORE) {
                if (mFirstLoad) {
                    // trigger SwipeRefreshLayout to show or wait onMeasure called.
//                    mSwipeRefreshLayout.setProgressViewOffset(false, -26 * 3, 64 * 3);
                    mSwipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshing(true);
                        }
                    }, 100);
                } else {
                    setRefreshing(true);
                }
            }*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*if (mNewPrograms != null && mNewPrograms.size() > 0) {
                if (mLoadType == LOAD_NEW && mDatas.size() > 0) {
                    int oldFirstId = mDatas.get(0).id;
                    for (ProgramItem item : mNewPrograms) {
                        if (item.id < oldFirstId) {
                            // add new items at head
                            mDatas.add(0, item);
                            mLoadIndex++;
                        }
                    }
                } else {
                    mLoadIndex += mNewPrograms.size();
                    mDatas.addAll(mNewPrograms);
                }
                mNewPrograms.clear();

                //mAdapter.notifyDataSetChanged();
            }
            mHomeHandler.sendEmptyMessage(MSG_LOAD_DONE);*/
            mDeviceIdTx.setText(translationStr);
            mExplainTx.setText(explainsStr);
        }
    }

    private class PostDataTask extends AsyncTask<Integer, Void, Void> {

        private String mWord;
        private String postRTR;

        HttpResponse httpResponse;
        PostDataTask(String type) {
            mWord = type;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int index = 0;

            /*try {
                URL url =  new URL("http://fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q=" + mWord);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("POST");

                OutputStream outputStream = connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write("");
                bufferedWriter.flush();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            //Test
            String username="username";
            String password="password";
            String httpUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=testSmarBarchet&key=2117934058&type=data&doctype=json&version=1.1&q="+password;
            //创建httpRequest对象
            HttpGet httpRequest = new HttpGet(httpUrl);
            try
            {
                //取得HttpClient对象
                HttpClient httpclient = new DefaultHttpClient();
                //请求HttpClient，取得HttpResponse
                HttpResponse httpResponse = httpclient.execute(httpRequest);
                //请求成功
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    //取得返回的字符串
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                    postRTR = strResult;
                }
                else
                {
                    postRTR= "请求错误!";
                }
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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
     void onGetButtonClick (View view) {
        String inputStr = mEditText.getText().toString();
        mLoadTask = new LoadDataTask(inputStr);
        mLoadTask.execute(mLoadIndex);
    }

    @OnClick(R.id.set_trams_button)
    void onPostButtonClick (View view) {
        mPostDataTask = new PostDataTask("");
        mPostDataTask.execute(mLoadIndex);
    }

}
