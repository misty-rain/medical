package com.hope.medical.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.friendtime.http.HttpUtility;
import com.friendtime.http.callback.StringCallback;
import com.friendtime.http.config.HttpMethod;
import com.hope.medical.R;
import com.hope.medical.utils.JpushUtil;
import com.hope.medical.web.WebAppInterface;
import com.hope.medical.widget.LoadFragment;
import com.hope.medical.widget.ProgressWebView;
import com.hope.medical.widget.TipsToast;
import com.mistyrian.library.event.BaseReceiveEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import me.nereo.multi_image_selector.MultiImageSelector;


public class MainActivity extends AppCompatActivity {


    private LinearLayout oneLinearLayout;
    private LinearLayout twoLinearLayout;
    private LinearLayout threeLinearLayout;
    private LinearLayout fourLinearLayout;
    private LoadFragment loadFragment = null;
    private ProgressWebView webView;
    private static final int REQUEST_IMAGE = 2;

    public static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback mUploadMessage;
    private String mCameraPhotoPath;

    private ArrayList<String> mSelectPath;
    EventBus eventBus = EventBus.getDefault();
    public static boolean isForeground = false;


    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.index);
        eventBus.register(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        registerMessageReceiver();
        setStyleCustom();
        setTag("1234567890");

        WebAppInterface webAppInterface = new WebAppInterface(this);
        webView = (ProgressWebView) findViewById(R.id.webviewtest);
        webView.getSettings().setJavaScriptEnabled(true);
        setUpWebViewDefaults(webView);
        webView.addJavascriptInterface(webAppInterface, "Android");
        webView.loadUrl("http://cwcpf.mhunsha.com/index.php/home/");
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    ProgressWebView.circleLoadingView.setVisibility(View.GONE);
                } else {
                    if (ProgressWebView.circleLoadingView.getVisibility() == View.GONE)
                        ProgressWebView.circleLoadingView.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }


            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg) {

                mUploadMessage = uploadMsg;
                MultiImageSelector.create(MainActivity.this).single()
                          .start(MainActivity.this, REQUEST_IMAGE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                MultiImageSelector.create(MainActivity.this).single()
                          .start(MainActivity.this, REQUEST_IMAGE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                MultiImageSelector.create(MainActivity.this).single()
                          .start(MainActivity.this, REQUEST_IMAGE);

            }

            //For Android 5.0 +
            public boolean onShowFileChooser(
                      WebView webView, ValueCallback<Uri[]> filePathCallback,
                      WebChromeClient.FileChooserParams fileChooserParams) {

                // Double check that we don't have any existing callbacks
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;

                MultiImageSelector.create(MainActivity.this).single()
                          .start(MainActivity.this, REQUEST_IMAGE);

                return true;
            }


        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_IMAGE) {
            if (null == mUploadMessage) return;
            if (resultCode == RESULT_OK) {
                mSelectPath = intent.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                if (TextUtils.isEmpty(mSelectPath.get(0))) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                    return;
                }

                Uri uri = Uri.fromFile(new File(mSelectPath.get(0)));
                Log.i("UPFILE", "onActivityResult after parser uri:" + uri.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mUploadMessage.onReceiveValue(new Uri[]{uri});
                } else {
                    mUploadMessage.onReceiveValue(uri);
                }
            } else {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = null;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        eventBus.unregister(this);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack())
                webView.goBack();
            else
                finish();
        }

        return false;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                  imageFileName,  /* prefix */
                  ".jpg",         /* suffix */
                  storageDir      /* directory */
        );
        return imageFile;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 根据eventbus post的事件作出相应处理
     *
     * @param revEvent 返回事件
     */
    @Subscribe
    public void onEventMainThread(BaseReceiveEvent revEvent) {
        if (revEvent.getFlag() == BaseReceiveEvent.Flag_Success) {
            String url = "http://cwcpf.mhunsha.com/home/index/newbinddoctor";
            Map<String, String> params = new HashMap<String, String>();
            params.put("uid", "121212121");
            params.put("did", "3242342");
            try {
                HttpUtility.getInstance().execute(HttpMethod.POST, url, params, new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.d(TAG, "..................." + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "..................." + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = (String) jsonObject.get("code");
                            String msg = (String) jsonObject.get("msg");
                            if (code.equals("1")) {
                                TipsToast.makeText(MainActivity.this, msg, R.mipmap.tips_success, Toast.LENGTH_SHORT).show();
                            } else {
                                TipsToast.makeText(MainActivity.this, msg, R.mipmap.tips_error, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setTag(String tag) {
        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(MainActivity.this, R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!JpushUtil.isValidTagAndAlias(sTagItme)) {
                Toast.makeText(MainActivity.this, R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            tagSet.add(sTagItme);
        }

        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

    }

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JpushUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            JpushUtil.showToast(logs, getApplicationContext());
        }

    };

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };


    /**
     * 设置通知栏样式 - 定义通知栏Layout
     */
    private void setStyleCustom() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(MainActivity.this, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.mipmap.app_icon;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.hope.medical_doctor.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!JpushUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }


}
