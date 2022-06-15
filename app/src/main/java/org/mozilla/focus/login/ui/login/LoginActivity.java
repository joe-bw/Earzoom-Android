package org.mozilla.focus.login.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.mozilla.focus.FocusApplication;
import org.mozilla.focus.R;
import org.mozilla.focus.activity.MainActivity;
import org.mozilla.focus.login.data.AppApiClient;
import org.mozilla.focus.login.data.AppApiResponse;
import org.mozilla.focus.login.data.LoginDataVO;
import org.mozilla.focus.login.data.LoginManager;
import org.mozilla.focus.login.data.LoginMemberVO;
import org.mozilla.focus.login.data.LoginNewRequest;
import org.mozilla.focus.login.ui.notice.NoticeActivity;
import org.mozilla.focus.login.ui.privacy.PrivacyPolicyActivity;
import org.mozilla.focus.login.ui.signup.Signup2Activity;

import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements AddInfoDialog.AddInfoDialogListener {

    private final String TAG = "TEST";

    private OAuthLoginButton mNaverOAuthLoginButton;
    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar loadingProgressBar;
    private Button googleLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
//        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        mContext = this;

        if (getIntent().getExtras() != null) {
            boolean isNotice = getIntent().getExtras().getBoolean("Notice", false);

            if (isNotice) {
                gotoNoticeActivity();
                return;
            }
            boolean isReLogin = getIntent().getExtras().getBoolean("relogin", false);

            if (isReLogin) {
                setReLoginUI();
            }
        }

        final Button naverLoginButton = findViewById(R.id.btn_login_naver);
        final Button kakaoLoginButton = findViewById(R.id.btn_login_kakao);
        final Button facebookLoginButton = findViewById(R.id.btn_login_facebook);
        googleLoginButton = findViewById(R.id.btn_login_google);

        loadingProgressBar = findViewById(R.id.progress_loading);

        mNaverOAuthLoginButton = findViewById(R.id.btn_login_naver_org);

        // FIXME 네이버 로그인
        initNaverUI();
        initNaverData();

        mNaverOAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mOAuthLoginInstance.logoutAndDeleteToken(mContext);

                mOAuthLoginInstance.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });

        naverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNaverOAuthLoginButton.performClick();
            }
        });
        // FIXME 네이버 로그인

        // FIXME 카카오 로그인

        kakaoLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                callKakaoLogin();
            }
        });
        // FIXME 카카오 로그인


//        this.setTitle("OAuthLoginSample Ver." + OAuthLogin.getVersion());

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
//                if (isValid()) {
//                    showLoadingbar();
//                    login();
//                } else {
//                    errorInput();
//                }
//            }
//        });

//        signupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gotoSignupActivity();
//            }
//        });

        // FIXME 페이스북 로그인
        callbackManager = CallbackManager.Factory.create();

        LoginButton facebookLoginButtonOrg = (LoginButton) findViewById(R.id.btn_login_facebook_org);
        facebookLoginButtonOrg.setReadPermissions("email");
        // If using in a fragment
//        facebookLoginButton.setFragment(this);

        // Callback registration
        facebookLoginButtonOrg.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                setLoginInfo(LoginManager.SNS_TYPE_FACEBOOK, token);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(mContext, getString(R.string.txt_disable_facebook), Toast.LENGTH_SHORT).show();

                facebookLoginButtonOrg.performClick();
            }
        });
        // FIXME 페이스북 로그인


        // FIXME google
        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();

// 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        com.google.android.gms.common.SignInButton googleLoginButtonOrg = (com.google.android.gms.common.SignInButton) findViewById(R.id.btn_login_google_org);
        googleLoginButtonOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("jinwoo", "googleLoginButtonOrg click");
                signIn();
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("jinwoo", "googleLoginButton click");
//                googleLoginButtonOrg.performClick();
                signIn();
            }
        });
        // FIXME google

        findViewById(R.id.btn_info).setOnClickListener(v -> {
            showNoticeDialog();
        });
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddInfoDialog();
        dialog.show(getSupportFragmentManager(), "AddInfoDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String birth, String phone) {
        Log.d("TEST", "onDialogPositiveClick: " + birth);
        Log.d("TEST", "onDialogPositiveClick: " + phone);

        LoginManager.getInstance().setPrefUserBirth(birth);
        LoginManager.getInstance().setPrefUserPhone(phone);

        dialog.dismiss();

        /** 추가정보 확인후 회원 가입여부 확인 */
        callMemberInfo();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
        Log.d("TEST", "onDialogNegativeClick");
    }

    @Override
    public void onDialogPrivacyClick(DialogFragment dialog) {
        Log.d("TEST", "onDialogPrivacyClick");
        startActivity(new Intent(LoginActivity.this, PrivacyPolicyActivity.class));
    }


    /** 추가 정보 등록 UI 처리 */
    private void setReLoginUI() {

        findViewById(R.id.layout_add_info).setVisibility(View.VISIBLE);

        findViewById(R.id.btn_login_naver).setEnabled(false);
        findViewById(R.id.btn_login_kakao).setEnabled(false);
        findViewById(R.id.btn_login_facebook).setEnabled(false);
        findViewById(R.id.btn_login_google).setEnabled(false);

        findViewById(R.id.btn_add_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPrivacy();
            }
        });

    }

    /** 카카오 설치 여부 확인 */
    private void callKakaoLogin() {

        if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
            UserApiClient.getInstance().loginWithKakaoTalk(mContext, this::kakaoLoginCallback);
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(mContext, this::kakaoLoginCallback);
        }
    }

    /**
     * 카카오 로그인 callback
     * @param token access token
     * @param error error
     * @return null
     */
    private Unit kakaoLoginCallback(OAuthToken token, Throwable error) {
        if(error != null) {
            Log.e(TAG, "Kakao login failed", error);
            FirebaseCrashlytics.getInstance().log("Kakao login failed: " + error);
            Toast.makeText(mContext, getString(R.string.acntion_cancel_signup), Toast.LENGTH_SHORT).show();

        } else if (token != null) {
            // 유저정보 획득
            UserApiClient.getInstance().me((user, e) -> {
                if(e != null) {
                    e.printStackTrace();

                    FirebaseCrashlytics.getInstance().log("Kakao login failed2: " + e);

                } else if(user != null) {
                    setLoginInfo(LoginManager.SNS_TYPE_KAKAO, "" + user.getId());
                }
                return null;
            });
        }
        return null;
    }

    public void setLoginInfo(int snsType, String id) {

        LoginManager.getInstance().putIsLoginAuto(true);
        LoginManager.getInstance().putUserSNSType(snsType);
        LoginManager.getInstance().putUserId(id);

        /** FCM 구독 설정 */
        FocusApplication focusApplication = new FocusApplication();
        focusApplication.setFCMSubscribe();

        checkPrivacy();
    }

    /** 2021.10.31 회원 가입 여부 확인, 가입이 되어 있다면 그대로 앱 사용, 가입이 되어 있지 않다면 회원 가입 웹뷰 요청 */
    private void callMemberInfo() {

        showLoadingbar();

//        final String userID = LoginManager.getInstance().getUserId();
//        final String userSNSType = "" + LoginManager.getInstance().getUserSNSType();
//        final String token = "" + LoginManager.getInstance().getDeviceToken();
//
//        LoginRequest request = new LoginRequest(userID, userSNSType, token);

        String birth = LoginManager.getInstance().getPrefUserBirth();
        String phone = LoginManager.getInstance().getPrefUserPhone();

        LoginNewRequest request = new LoginNewRequest(birth, phone);

        Call<AppApiResponse<LoginDataVO>> call = AppApiClient.getApiService().requestMemberInfo(request);
        call.enqueue(new Callback<AppApiResponse<LoginDataVO>>() {
            @Override
            public void onResponse(Call<AppApiResponse<LoginDataVO>> call, Response<AppApiResponse<LoginDataVO>> response) {

                hideLoadingbar();

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
                        gotoSignupActivity();
                    }
                } else {
                    Log.d(TAG, "fail");
                    if(response.code() == 404) {
                        showSignupPopup();
                    } else {
                        showErrorPopup();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppApiResponse<LoginDataVO>> call, Throwable t) {

                Log.d(TAG, "onFailure - result: " + t.getMessage());

                hideLoadingbar();
                showErrorPopup();
            }
        });

    }

    /** 2021.10.31 개인정보 확인 */
    private void checkPrivacy() {

        if (TextUtils.isEmpty(LoginManager.getInstance().getPrefUserBirth())) {
            showNoticeDialog();
        } else {

            /** 회원 가입 여부 확인 */
            callMemberInfo();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 2000);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);

                setLoginInfo(LoginManager.SNS_TYPE_GOOGLE, personId);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.e(TAG, "signInResult:failed fillInStackTrace: " + e.fillInStackTrace());
            FirebaseCrashlytics.getInstance().log("google - log: " + e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("jinwoo", "onActivityResult" + requestCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 2000) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initNaverData() {

        new Handler().post(() -> {
            mOAuthLoginInstance = OAuthLogin.getInstance();
            mOAuthLoginInstance.showDevelopersLog(true);
            mOAuthLoginInstance.init(mContext, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name));

            mOAuthLoginInstance.logoutAndDeleteToken(mContext);
        });
    }

    private void initNaverUI() {
        mNaverOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
    }

    @SuppressLint("HandlerLeak")
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {

                String accessToken = mOAuthLoginInstance.getAccessToken(mContext.getApplicationContext());
//                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
//                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
//                String tokenType = mOAuthLoginInstance.getTokenType(mContext);



//                mOauthAT.setText(accessToken);
//                mOauthRT.setText(refreshToken);
//                mOauthExpires.setText(String.valueOf(expiresAt));
//                mOauthTokenType.setText(tokenType);
//                mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());

                setLoginInfo(LoginManager.SNS_TYPE_NAVER, "" + accessToken);

            } else {
                Log.d("jinwoo", "OAuthLoginHandler - failed");
//                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
//                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
//                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showLoadingbar() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }
    private void hideLoadingbar() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    /**  */
    private void login() {

        showLoadingbar();

//        final String userID = LoginManager.getInstance().getUserId();
//        final String userSNSType = "" + LoginManager.getInstance().getUserSNSType();
//        final String token = "" + LoginManager.getInstance().getDeviceToken();
//
//        LoginRequest request = new LoginRequest(userID, userSNSType, token);

        String birth = LoginManager.getInstance().getPrefUserBirth();
        String phone = LoginManager.getInstance().getPrefUserPhone();

        LoginNewRequest request = new LoginNewRequest(birth, phone);

        Call<AppApiResponse<LoginDataVO>> call = AppApiClient.getApiService().requestLogin(request);
        call.enqueue(new Callback<AppApiResponse<LoginDataVO>>() {
            @Override
            public void onResponse(Call<AppApiResponse<LoginDataVO>> call, Response<AppApiResponse<LoginDataVO>> response) {

                hideLoadingbar();

                Log.d(TAG, "response.code(): " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "isSuccessful");
                    AppApiResponse result = response.body();

                    Log.d(TAG, "onResponse - result: " + result.toString());

                    if (result.status == 200) {
//                        LoginDataVO data = (LoginDataVO) result.data;
//                        LoginMemberVO member = data.member;

                        /** FCM 구독 설정 */
//                        FirebaseMessaging.getInstance().subscribeToTopic(ZerothDefine.FCM_SUBSCRIBE_NAME);

//                    saveData(member);
                        gotoMainActivity();

                    } else {
                        showErrorPopup();
                    }
                } else {
                    Log.d(TAG, "fail");
                    if(response.code() == 404) {
                        showSignupPopup();
                    } else {
                        showErrorPopup();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppApiResponse<LoginDataVO>> call, Throwable t) {

                Log.d(TAG, "onFailure - result: " + t.getMessage());

                hideLoadingbar();
                showErrorPopup();
            }
        });
    }

    private void showSignupPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.txt_notice))
                .setCancelable(false)
                .setMessage(getString(R.string.txt_signup_error))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                    gotoSignupActivity();
                })
                .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> {
                    dialog.dismiss();
                    setReLoginUI();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showErrorPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.txt_fail_login))
                .setMessage(getString(R.string.txt_login_error))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void gotoNoticeActivity() {
        startActivity(new Intent(LoginActivity.this, NoticeActivity.class));
    }

    private void gotoMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void gotoSignupActivity() {
        startActivity(new Intent(LoginActivity.this, Signup2Activity.class));
        finish();
    }
}