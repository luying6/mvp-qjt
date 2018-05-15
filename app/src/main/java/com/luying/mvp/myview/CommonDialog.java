package com.luying.mvp.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.luying.mvp.R;

/**
 * 创建人:luying
 * 创建时间:2018/5/15.
 */

public class CommonDialog extends Dialog implements View.OnClickListener {
    private TextView title, content, cancel, submit;
    private String titleText, contentText, cancelText = "取消", submitText = "确定";
    private OnDialogClickListener listener;
    private Context context;
    public CommonDialog(@NonNull Context context, OnDialogClickListener listener) {
        super(context, R.style.commonDialog);
        this.listener = listener;
        this.context = context;
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_common_dialog);
        initView();
    }

    private void initView() {
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        cancel = findViewById(R.id.cancel);
        submit = findViewById(R.id.submit);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
        if (!TextUtils.isEmpty(titleText)) {
            title.setText(titleText);
        }

        if (!TextUtils.isEmpty(contentText)) {
            content.setText(contentText);
        }

        if (!TextUtils.isEmpty(cancelText)) {
            cancel.setText(cancelText);
        }

        if (!TextUtils.isEmpty(submitText)) {
            submit.setText(submitText);
        }
    }

    public CommonDialog setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public CommonDialog setCannelBtnText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public CommonDialog setSubmitBtnText(String submitText) {
        this.submitText = submitText;
        return this;
    }

    public CommonDialog setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (listener != null) {
                    listener.onSubmit();
                }
                break;
            case R.id.cancel:
                if (listener != null) {
                    listener.onCancel();
                }
                this.dismiss();

                break;
        }
    }


    public interface OnDialogClickListener {
        void onSubmit();

        void onCancel();
    }
}
