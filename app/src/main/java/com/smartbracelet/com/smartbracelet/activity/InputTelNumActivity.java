package com.smartbracelet.com.smartbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.network.PollingUtils;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;
import com.smartbracelet.com.smartbracelet.util.ToastHelper;
import com.smartbracelet.com.smartbracelet.util.Utils;
import com.smartbracelet.com.smartbracelet.view.AlertDialogCreator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputTelNumActivity extends AppCompatActivity implements ConstDefine{

    private Activity pThis;

    @Bind(R.id.input_confirm_bt)
    Button confirmButton;

    @Bind(R.id.input_tel_et)
    EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_tel_num);

        pThis = this;

        ButterKnife.bind(pThis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(pThis);
    }

    private AlertDialog mAlertDialog;

    @OnClick(R.id.input_confirm_bt)
    public void onConfirmBtClick(View view) {
        String str = inputEditText.getText().toString().trim();
        if (Utils.isMobileNO(str.trim())) {

            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
            if (null != sharedPreferencesHelper) {
                Utils.setTelNumber(str);
                sharedPreferencesHelper.putString(SP_PHONE_NUMBER, str);
            }
            Intent intent = new Intent(InputTelNumActivity.this, MainMenuActivity.class);
            startActivity(intent);
            InputTelNumActivity.this.finish();
        } else {
            AlertDialogCreator.getInstance().setmButtonOnClickListener(mDialogListener);
            mAlertDialog = AlertDialogCreator
                    .getInstance()
                    .createAlertDialog(
                            pThis,
                            getString(R.string.tip_title),
                            getString(R.string.input_error_warning));
            mAlertDialog.show();

        }

    }

    private AlertDialogCreator.ButtonOnClickListener mDialogListener = new AlertDialogCreator.ButtonOnClickListener() {
        @Override
        public void buttonTrue() {
        }

        @Override
        public void buttonTrue(int ring_dis) {

        }

        @Override
        public void buttonTrue(String value) {

        }

        @Override
        public void buttonTrue(String valuekey, String name) {

        }

        @Override
        public void buttonCancel() {

        }
    };
}
