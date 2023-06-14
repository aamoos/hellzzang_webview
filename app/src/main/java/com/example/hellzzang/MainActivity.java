package com.example.hellzzang;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView; // 웹뷰 선언
    private WebSettings webSettings; //웹뷰세팅

    //김재성 url
    private String url = "http://192.168.0.39:8080/";

    //이향준 url
    //private String url = "http://192.168.0.38:8080/";

    private long backKeyPressedTime = 0;
    private Toast guideToast;

    private Activity activity;

    ValueCallback mFilePathCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                // 파일 n개 선택 가능하도록 처리
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(intent, 0);
                return true;
            }
        });
    }

    // 안드로이드 웹뷰에서 파일 첨부하기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode:: ", String.valueOf(resultCode));
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            // 파일 n개 선택한 경우
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && data != null && data.getClipData() != null && data.getClipData().getItemCount() > 0) {
                int count = data.getClipData().getItemCount();

                Uri[] uriArr = new Uri[count];
                for (int i = 0; i < count; i++) {
                    uriArr[i] = data.getClipData().getItemAt(i).getUri();
                }

                mFilePathCallback.onReceiveValue(uriArr);

            } else if (data != null && data.getData() != null) {
                // 파일 1개 선택한 경우
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                } else {
                    mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
                }
            }

            mFilePathCallback = null;

        } else {
            mFilePathCallback.onReceiveValue(null);
        }
    }


    //back버튼 처리
    @Override
    public void onBackPressed() {
        
        //뒤로가기 history가 있을경우 뒤로가기
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            //뒤로가기 1번 클릭할경우 guide 보여주기
            if(System.currentTimeMillis() > backKeyPressedTime + 2000){
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
            }

            //뒤로가기 2번 클릭할경우 끄기
            else if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
                activity.finish();
                guideToast.cancel();
            }
        }
    }

    public void showGuide(){
        Toast.makeText(this, "뒤로 가기를 한번더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
    }

}