package com.wang.powerfulloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mLoadBtn;
    private Button mLoadWithTextBtn;
    private Button mLoadInsideBtn;
    private EditText mInputEt;

    private Dialog mLoadingDialog = null;
    private PowerfulLoadingView mLoadingView = null;
    private PowerfulLoadingView mLoadingViewInside = null;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputEt = (EditText) findViewById(R.id.input);
        mLoadBtn = (Button) findViewById(R.id.load_btn);
        mLoadBtn.setOnClickListener(this);
        mLoadWithTextBtn = (Button) findViewById(R.id.load_btn_summary);
        mLoadWithTextBtn.setOnClickListener(this);
        mLoadingViewInside = (PowerfulLoadingView) findViewById(R.id.loading_view_inside);
        mLoadInsideBtn = (Button) findViewById(R.id.show_loading);
        mLoadInsideBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.load_btn:
                showLoadingDialog(MainActivity.this);
                break;

            case R.id.load_btn_summary:
                showLoadingDialog(MainActivity.this, R.string.loading_summary);
                break;

            case R.id.show_loading:
                if (mLoadingViewInside.getVisibility() != View.VISIBLE) {
                    mLoadingViewInside.setVisibility(View.VISIBLE);
                    mLoadingViewInside.startLoading();
                    mHandler.postDelayed(checkInput2, 2000);
                }

                break;
        }
    }

    private Runnable checkInput2 = new Runnable() {
        @Override
        public void run() {
            String input = mInputEt.getText().toString();
            finishLoading(input.equals("123"));
        }
    };

    private void showLoadingDialog(Context context) {
        showLoadingDialog(context, 0);
    }

    private void showLoadingDialog(Context context, int summaryResId) {
        View dialogView =
                getLayoutInflater().inflate(R.layout.dialog_loading_view, null, false);
        mLoadingDialog = new Dialog(context, ProgressDialog.STYLE_SPINNER);
        mLoadingDialog.setContentView(dialogView);
        mLoadingDialog.setCancelable(false);

        mLoadingView = (PowerfulLoadingView) dialogView.findViewById(R.id.loading_view);
        if (summaryResId != 0) {
            TextView textView = (TextView) dialogView.findViewById(R.id.loading_summary);
            textView.setText(summaryResId);
        }

        mLoadingDialog.show();
        mLoadingView.startLoading();

        //模拟加载任务，2秒后显示结果
        mHandler.postDelayed(checkInput, 2000);
    }


    private Runnable checkInput = new Runnable() {
        @Override
        public void run() {
            String input = mInputEt.getText().toString();
            dismissLoadingDialog(input.equals("123"), new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    //加载完成后的相关逻辑
                }
            });
        }
    };

    private void dismissLoadingDialog(final boolean succeed,
                                     @Nullable final Animator.AnimatorListener listener) {

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            if (succeed) {
                mLoadingView.loadSucceed(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mLoadingDialog.dismiss();
                        mLoadingView.clearAllAnimator();
                        if (listener != null) {
                            listener.onAnimationEnd(animator);
                        }
                    }
                });
            } else {
                mLoadingView.loadFailed(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingDialog.dismiss();
                        mLoadingView.clearAllAnimator();

                        if (listener != null) {
                            listener.onAnimationEnd(animation);
                        }

                    }
                });
            }
        }
    }

    private void finishLoading(boolean succeed) {
        if (succeed) {
            mLoadingViewInside.loadSucceed(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingViewInside.clearAllAnimator();
                    mLoadingViewInside.setVisibility(View.GONE);

                    //动画完成后的逻辑
                }
            });
        } else {
            mLoadingViewInside.loadFailed(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingViewInside.clearAllAnimator();
                    mLoadingViewInside.setVisibility(View.GONE);
                }
            });
        }
    }
}
