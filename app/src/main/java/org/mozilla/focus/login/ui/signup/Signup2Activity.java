package org.mozilla.focus.login.ui.signup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.focus.R;
import org.mozilla.focus.activity.MainActivity;
import org.mozilla.focus.login.data.AppApiClient;
import org.mozilla.focus.login.data.AppApiResponse;
import org.mozilla.focus.login.data.LoginDataVO;
import org.mozilla.focus.login.data.LoginManager;
import org.mozilla.focus.login.data.LoginMemberVO;
import org.mozilla.focus.login.data.LoginNewRequest;
import org.mozilla.focus.login.ui.login.LoginActivity;

import kr.co.sorizava.asrplayer.AppConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 소리자바 서버로의 고객 정보 신규 등록 화면
 * 신규등록 API로 ID, snsFlag, deviceToken 정보를 전달한다.
 * snsFlag 값 정의
 * 1 : naver, 2: kakao, 3: faceBook, 4: google
 */
public class Signup2Activity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        String birth = LoginManager.getInstance().getPrefUserBirth();
        String phone = LoginManager.getInstance().getPrefUserPhone();
        final String adFlag = "a";

        webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        //웹뷰세팅
        WebSettings mWebSettings = webView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        String TAG_JAVA_INTERFACE = "SORIJAVA";
        webView.addJavascriptInterface(new WebBridge(), TAG_JAVA_INTERFACE);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(final WebView view, boolean bDialog, boolean userGesture, Message resultMsg)
            {
                WebView newWebView = new WebView(Signup2Activity.this);
                WebSettings webSettings = newWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                final Dialog dialog = new Dialog(Signup2Activity.this);
                dialog.setContentView(newWebView);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                dialog.show();
                Window window = dialog.getWindow();
                window.setAttributes(lp);

                newWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onCloseWindow(WebView window) {
                        dialog.dismiss();
                    }
                });
                ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }
        });

        webView.loadUrl(AppConfig.getInstance().getPrefWebAddInfoUrl());
        long delayTime = 3000L;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:addInfo_script.parameter" +
                        "('" + birth + "', '" + phone + "', '" + adFlag + "')"
                );
            }
        }, delayTime);
    }

    class WebBridge{
        @JavascriptInterface
        public void callbackAndroid(final int result){
            runOnUiThread(() -> {

                if (result == 1) {
                    callMemberInfo();
                } else if (result == 2) {
                    reLogin();
                } else {
                    Toast.makeText(Signup2Activity.this, getString(R.string.web_callback_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.txt_notice)).setMessage(getString(R.string.txt_backpress_contents))
                .setPositiveButton(getString(R.string.txt_exit), (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(getString(R.string.txt_continue), (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void reLogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .putExtra("relogin", true));
        finish();
    }

    private final String TAG = "TEST";

    /** 2021.10.31 회원 가입 여부 확인, 가입이 되어 있다면 그대로 앱 사용, 가입이 되어 있지 않다면 회원 가입 웹뷰 요청 */
    private void callMemberInfo() {

        String birth = LoginManager.getInstance().getPrefUserBirth();
        String phone = LoginManager.getInstance().getPrefUserPhone();

        LoginNewRequest request = new LoginNewRequest(birth, phone);

        Call<AppApiResponse<LoginDataVO>> call = AppApiClient.getApiService().requestMemberInfo(request);
        call.enqueue(new Callback<AppApiResponse<LoginDataVO>>() {
            @Override
            public void onResponse(Call<AppApiResponse<LoginDataVO>> call, Response<AppApiResponse<LoginDataVO>> response) {

                Log.d(TAG, "response.code(): " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "isSuccessful");
                    AppApiResponse result = response.body();

                    Log.d(TAG, "onResponse - result: " + result.toString());

                    if (result.status == 200) {
                        LoginDataVO data = (LoginDataVO) result.data;
                        LoginMemberVO member = data.member;
                        LoginManager.getInstance().setPrefUserId(member.id);
                        gotoMainActivity();
                    } else {
                        reLogin();
                    }
                } else {
                    Log.d(TAG, "fail");
//                    if(response.code() == 404) {
//                        reLogin();
//                    }
                    reLogin();
                }
            }

            @Override
            public void onFailure(Call<AppApiResponse<LoginDataVO>> call, Throwable t) {
                Log.d(TAG, "onFailure - result: " + t.getMessage());
            }
        });
    }

    private void gotoMainActivity() {
        startActivity(new Intent(Signup2Activity.this, MainActivity.class));
        finish();
    }
}