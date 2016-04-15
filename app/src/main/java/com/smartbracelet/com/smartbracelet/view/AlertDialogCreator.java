package com.smartbracelet.com.smartbracelet.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.smartbracelet.com.smartbracelet.R;

/**
 * Created by leo.yang on 2016/4/15.
 */
public class AlertDialogCreator {
    private static AlertDialogCreator instance;
    public AlertDialogCreator() {

    }

    public static AlertDialogCreator getInstance () {

        if (null == instance) {
            instance = new AlertDialogCreator();
        }
        return  instance;
    }

    private ButtonOnClickListener mButtonOnClickListener;

    public ButtonOnClickListener getmButtonOnClickListener() {
        return mButtonOnClickListener;
    }

    public void setmButtonOnClickListener(ButtonOnClickListener mLister) {
        this.mButtonOnClickListener = mLister;
    }

    public AlertDialog createAlertDialog(final Context mContext, String title, String msg) {
        final AlertDialog localDialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.button_true, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked Yes so do some stuff */
                        getmButtonOnClickListener().buttonTrue();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked No so do some stuff */
                        getmButtonOnClickListener().buttonCancel();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getmButtonOnClickListener().buttonCancel();
                    }
                }).create();

        return localDialog;
    }

    public static abstract interface ButtonOnClickListener {
        public abstract void buttonTrue();

        public abstract void buttonTrue(int ring_dis);

        public abstract void buttonTrue(String valuekey, String name);

        public abstract void buttonCancel();
    }
}
