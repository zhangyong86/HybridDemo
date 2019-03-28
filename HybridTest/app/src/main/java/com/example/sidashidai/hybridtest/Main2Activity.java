package com.example.sidashidai.hybridtest;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private WebView mWebView;
    private Button mButton;
    private static final String TAG = Main2Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main2Activity.this.onClick(mButton);
            }
        });
        mWebView = findViewById(R.id.webView2);
        //load html
        mWebView.loadUrl("file:///android_asset/h5/html/test.html");
        WebSettings webSettings = mWebView.getSettings();
        //设置为可调用js方法
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInteration(), "android");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("file:///android_asset/test2.html")) {
                    Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                    startActivity(new Intent(Main2Activity.this,MainActivity.class));
                    return true;
                } else {
                    mWebView.loadUrl(url);
                    return false;
                }
            }
        });
    }

    //Android调用有返回值js方法(有返回值)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onClick(View v) {

        mWebView.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue value=" + value);
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
            }
        });
    }

    //定义本地Java方法--js调用(mWebView.addJavascriptInterface)
    public class JsInteration {
        @JavascriptInterface
        public String back() {
            return "hello world";
        }
    }
}
