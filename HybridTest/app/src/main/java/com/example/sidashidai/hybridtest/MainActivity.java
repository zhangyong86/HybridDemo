package com.example.sidashidai.hybridtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private WebView native_web;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_main);
        native_web = findViewById(R.id.webView);
        initWeb();
    }

    /**
     * 初始化webview 允许js后要小心XSS攻击了
     *
     * @JavascriptInterface这个注解 在4.2以上修复了，但是需要注意兼容性
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        WebSettings webSettings = native_web.getSettings();
        //
        webSettings.setJavaScriptEnabled(true);

        // 第一个参数里面包含了需要从js调用的方法或者反向回馈给js的方法
        // 第二个参数调用的是和js里规定的方法一样，调用function：android中的方法showToast，前缀javascript可有可无
        // js代码：
        // function showToast(toast) {
        // javascript:android.showToast(toast);
        // }
        // 在js中，android.showToast(toast);是不能被执行的，可能在html中执行会报错。因为它是js和原生app协商后定义的方法，不是js原有方法
        native_web.addJavascriptInterface(new JsInterface(), "android");
        // 设置拦截js中的三种弹框
        native_web.setWebChromeClient(new MyWebChromeClien());
        // 监听点击的每一个url，做自己的特殊处理用
        native_web.setWebViewClient(new MyWebViewClient());
        // 加载项目asset文件夹下的本地html
//        native_web.loadUrl("file:///android_asset/h5/html/index.html");// 加载本地的html布局文件
        // 加载 asset 文件的内容，第二种加载本地html的方法
//        String tpl = getFromAssets("h5/html/index.html");
        // native_web.loadDataWithBaseURL(null, tpl, "text/html", "utf-8",null);
        native_web.loadUrl("https://www.baidu.com");//shop.guazi.com

    }

    /*
     * 获取html文件的内容
     */
    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "url===>" + url);
            try {
                // 如果url满足某种特定的格式，则进行特殊处理
                if (url.contains("http://")) {
                    view.loadUrl(url);
                } else {
                    view.loadUrl(url);
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        }
    }

    /**
     * 拦截js的提示框
     */
    private class MyWebChromeClien extends WebChromeClient {
        // 提示框
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  final JsPromptResult result) {
            new AlertDialog.Builder(MainActivity.this).setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 当弹出框点击的时候，手动用代码给js返回确认的信息
                            result.confirm("true");
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).show();
            // 返回false会弹出原声dialog，这里不让原声的弹出
            return true;
        }

        // 警告框
        @SuppressLint("NewApi")
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            // api 17以后才有onDismiss 接口，这里稍作处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                new AlertDialog.Builder(MainActivity.this).setMessage(message)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                // TODO Auto-generated method stub
                                result.cancel();
                            }
                        }).show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 当弹出框点击的时候，手动用代码给js返回确认的信息
                                result.cancel();
                            }
                        }).show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
            }
            return true;
        }

        // 确认框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(MainActivity.this).setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 当弹出框点击的时候，手动用代码给js返回确认的信息
                            result.confirm();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).show();
            return true;
        }
    }

    /**
     * 对应js中的调用方法
     */
    public class JsInterface {
        //这个方法就是js中带有android.前缀的方法名
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            // 当显示出toast的时候，就向js反向输出一条log
            log("show toast success");
        }

        //这里是将app产生的信息回馈给js
        public void log(final String msg) {
            native_web.post(new Runnable() {
                @Override
                public void run() {
                    // 调用js中指向info的方法
                    //参数 “javascript:”  +  js方法名
                    // 这个方法会将信息在浏览器的控制台的info中显示出来
                    native_web.loadUrl("javascript:info(" + "'" + msg + "'" + ")");
                }
            });
        }
    }

}
