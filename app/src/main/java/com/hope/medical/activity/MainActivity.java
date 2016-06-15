package com.hope.medical.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.hope.medical.R;
import com.hope.medical.fragment.MessageFragment;
import com.hope.medical.fragment.NoticeFragment;
import com.hope.medical.utils.FileUtils;
import com.hope.medical.web.WebAppInterface;
import com.hope.medical.widget.LoadFragment;
import com.hope.medical.widget.ProgressWebView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;


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


    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.index);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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
                if(mUploadMessage != null) {
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

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

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


}
