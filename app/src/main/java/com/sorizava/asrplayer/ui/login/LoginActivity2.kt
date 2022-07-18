///*
// * Create by jhong on 2022. 7. 7.
// * Copyright(c) 2022. Sorizava. All rights reserved.
// */
//package com.sorizava.asrplayer.ui.login
//
//import com.sorizava.asrplayer.config.LoginManager.Companion.instance
//import com.kakao.sdk.user.UserApiClient.isKakaoTalkLoginAvailable
//import com.kakao.sdk.user.UserApiClient.loginWithKakaoTalk
//import com.kakao.sdk.user.UserApiClient.loginWithKakaoAccount
//import com.kakao.sdk.user.UserApiClient.me
//import com.sorizava.asrplayer.network.AppApiClient.apiService
//import com.sorizava.asrplayer.ui.login.AddInfoDialog.AddInfoDialogListener
//import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
//import com.facebook.CallbackManager
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import org.mozilla.focus.R
//import com.facebook.login.widget.LoginButton
//import com.facebook.FacebookCallback
//import com.facebook.login.LoginResult
//import com.facebook.FacebookException
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.common.SignInButton
//import android.content.Intent
//import com.sorizava.asrplayer.ui.privacy.PrivacyPolicyActivity
//import com.kakao.sdk.user.UserApiClient
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import android.widget.Toast
//import org.mozilla.focus.FocusApplication
//import com.sorizava.asrplayer.data.vo.LoginNewRequest
//import com.sorizava.asrplayer.network.AppApiResponse
//import com.sorizava.asrplayer.data.vo.LoginDataVO
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.common.api.ApiException
//import com.nhn.android.naverlogin.OAuthLogin
//import android.annotation.SuppressLint
//import android.content.Context
//import com.nhn.android.naverlogin.OAuthLoginHandler
//import android.content.DialogInterface
//import android.os.Bundle
//import android.os.Handler
//import android.text.TextUtils
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.ProgressBar
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.DialogFragment
//import com.google.android.gms.tasks.Task
//import com.kakao.sdk.auth.model.OAuthToken
//import com.kakao.sdk.user.model.User
//import com.sorizava.asrplayer.config.LoginManager
//import com.sorizava.asrplayer.ui.notice.NoticeActivity
//import kotlinx.android.synthetic.main.activity_login2.*
//import org.mozilla.focus.activity.MainActivity
//import com.sorizava.asrplayer.ui.signup.Signup2Activity
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class LoginActivity2 : AppCompatActivity(), AddInfoDialogListener {
//    private val TAG = "TEST"
//    private var mNaverOAuthLoginButton: OAuthLoginButton? = null
//    private var callbackManager: CallbackManager? = null
//    private var mGoogleSignInClient: GoogleSignInClient? = null
//    private var loadingProgressBar: ProgressBar? = null
//    private var googleLoginButton: Button? = null
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login2)
//        //        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
//        mContext = this
//        if (intent.extras != null) {
//            val isNotice = intent.extras!!.getBoolean("Notice", false)
//            if (isNotice) {
//                gotoNoticeActivity()
//                return
//            }
//            val isReLogin = intent.extras!!.getBoolean("relogin", false)
//            if (isReLogin) {
//                setReLoginUI()
//            }
//        }
//        val naverLoginButton = findViewById<Button>(R.id.btn_login_naver)
//        val kakaoLoginButton = findViewById<Button>(R.id.btn_login_kakao)
//        val facebookLoginButton = findViewById<Button>(R.id.btn_login_facebook)
//        googleLoginButton = findViewById(R.id.btn_login_google)
//        loadingProgressBar = findViewById(R.id.progress_loading)
////        mNaverOAuthLoginButton = findViewById(R.id.btn_login_naver_org)
//
//        // FIXME 네이버 로그인
//        initNaverUI()
//        initNaverData()
//
//        mNaverOAuthLoginButton?.setOnClickListener(View.OnClickListener { //                mOAuthLoginInstance.logoutAndDeleteToken(mContext);
//            mOAuthLoginInstance!!.startOauthLoginActivity(this@LoginActivity2, mOAuthLoginHandler)
//        })
//        naverLoginButton.setOnClickListener { mNaverOAuthLoginButton?.performClick() }
//        // FIXME 네이버 로그인
//
//        // FIXME 카카오 로그인
//        kakaoLoginButton.setOnClickListener { callKakaoLogin() }
//        // FIXME 카카오 로그인
//
//
////        this.setTitle("OAuthLoginSample Ver." + OAuthLogin.getVersion());
//
////        loginButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
//////                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
////                if (isValid()) {
////                    showLoadingbar();
////                    login();
////                } else {
////                    errorInput();
////                }
////            }
////        });
//
////        signupButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                gotoSignupActivity();
////            }
////        });
//
//        // FIXME 페이스북 로그인
//        callbackManager = CallbackManager.Factory.create()
//        val facebookLoginButtonOrg = findViewById<View>(R.id.btn_login_facebook_org) as LoginButton
//        facebookLoginButtonOrg.setReadPermissions("email")
//        // If using in a fragment
////        facebookLoginButton.setFragment(this);
//
//        // Callback registration
//        facebookLoginButtonOrg.registerCallback(
//            callbackManager,
//            object : FacebookCallback<LoginResult> {
//                override fun onSuccess(loginResult: LoginResult) {
//                    val token = loginResult.accessToken.token
//                    setLoginInfo(LoginManager.SNS_TYPE_FACEBOOK, token)
//                }
//
//                override fun onCancel() {}
//                override fun onError(error: FacebookException) {}
//            })
//        facebookLoginButton.setOnClickListener { //                Toast.makeText(mContext, getString(R.string.txt_disable_facebook), Toast.LENGTH_SHORT).show();
//            facebookLoginButtonOrg.performClick()
//        }
//        // FIXME 페이스북 로그인
//
//
//        // FIXME google
//        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
//        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail() // email addresses도 요청함
//            .build()
//
//// 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
//        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity2, gso)
//        val googleLoginButtonOrg = findViewById<View>(R.id.btn_login_google_org) as SignInButton
//        googleLoginButtonOrg.setOnClickListener {
//            Log.d("jinwoo", "googleLoginButtonOrg click")
//            signIn()
//        }
//        googleLoginButton.setOnClickListener(View.OnClickListener {
//            Log.d("jinwoo", "googleLoginButton click")
//            //                googleLoginButtonOrg.performClick();
//            signIn()
//        })
//        // FIXME google
//        findViewById<View>(R.id.btn_info).setOnClickListener { v: View? -> showNoticeDialog() }
//    }
//
//    fun showNoticeDialog() {
//        // Create an instance of the dialog fragment and show it
//        val dialog: DialogFragment = AddInfoDialog()
//        dialog.show(supportFragmentManager, "AddInfoDialog")
//    }
//
//    override fun onDialogPositiveClick(dialog: DialogFragment?, birth: String?, phone: String?) {
//        Log.d("TEST", "onDialogPositiveClick: $birth")
//        Log.d("TEST", "onDialogPositiveClick: $phone")
//        instance!!.prefUserBirth = birth
//        instance!!.prefUserPhone = phone
//        dialog!!.dismiss()
//        /** 추가정보 확인후 회원 가입여부 확인  */
//        callMemberInfo()
//    }
//
//    override fun onDialogNegativeClick(dialog: DialogFragment?) {
//        dialog!!.dismiss()
//        Log.d("TEST", "onDialogNegativeClick")
//    }
//
//    override fun onDialogPrivacyClick(dialog: DialogFragment?) {
//        Log.d("TEST", "onDialogPrivacyClick")
//        startActivity(Intent(this@LoginActivity2, PrivacyPolicyActivity::class.java))
//    }
//
//    /** 추가 정보 등록 UI 처리  */
//    private fun setReLoginUI() {
//        findViewById<View>(R.id.layout_add_info).visibility = View.VISIBLE
//        findViewById<View>(R.id.btn_login_naver).isEnabled = false
//        findViewById<View>(R.id.btn_login_kakao).isEnabled = false
//        findViewById<View>(R.id.btn_login_facebook).isEnabled = false
//        findViewById<View>(R.id.btn_login_google).isEnabled = false
//        findViewById<View>(R.id.btn_add_info).setOnClickListener { checkPrivacy() }
//    }
//
//    /** 카카오 설치 여부 확인  */
//    private fun callKakaoLogin() {
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(mContext)) {
//            UserApiClient.instance
//                .loginWithKakaoTalk(mContext) { token: OAuthToken?, error: Throwable? ->
//                    kakaoLoginCallback(
//                        token,
//                        error
//                    )
//                }
//        } else {
//            UserApiClient.instance
//                .loginWithKakaoAccount(mContext) { token: OAuthToken?, error: Throwable? ->
//                    kakaoLoginCallback(
//                        token,
//                        error
//                    )
//                }
//        }
//    }
//
//    /**
//     * 카카오 로그인 callback
//     * @param token access token
//     * @param error error
//     * @return null
//     */
//    private fun kakaoLoginCallback(token: OAuthToken?, error: Throwable?) {
//        if (error != null) {
//            Log.e(TAG, "Kakao login failed", error)
//            FirebaseCrashlytics.getInstance().log("Kakao login failed: $error")
//            Toast.makeText(mContext, getString(R.string.acntion_cancel_signup), Toast.LENGTH_SHORT)
//                .show()
//        } else if (token != null) {
//            // 유저정보 획득
//            UserApiClient.instance.me { user: User?, e: Throwable? ->
//                if (e != null) {
//                    e.printStackTrace()
//                    FirebaseCrashlytics.getInstance().log("Kakao login failed2: $e")
//                } else if (user != null) {
//                    setLoginInfo(LoginManager.SNS_TYPE_KAKAO, "" + user.id)
//                }
//                null
//            }
//        }
//        return null
//    }
//
//    fun setLoginInfo(snsType: Int, id: String?) {
//        instance!!.putIsLoginAuto(true)
//        instance!!.putUserSNSType(snsType)
//        instance!!.putUserId(id)
//        /** FCM 구독 설정  */
//        val focusApplication = FocusApplication()
//        focusApplication.setFCMSubscribe()
//        checkPrivacy()
//    }
//
//    /** 2021.10.31 회원 가입 여부 확인, 가입이 되어 있다면 그대로 앱 사용, 가입이 되어 있지 않다면 회원 가입 웹뷰 요청  */
//    private fun callMemberInfo() {
//        showLoadingbar()
//
////        final String userID = LoginManager.getInstance().getUserId();
////        final String userSNSType = "" + LoginManager.getInstance().getUserSNSType();
////        final String token = "" + LoginManager.getInstance().getDeviceToken();
////
////        LoginRequest request = new LoginRequest(userID, userSNSType, token);
//        val birth = instance!!.prefUserBirth
//        val phone = instance!!.prefUserPhone
//        val request = LoginNewRequest(birth!!, phone!!)
//        val call = apiService.requestMemberInfo(request)
//        call!!.enqueue(object : Callback<AppApiResponse<LoginDataVO>> {
//            override fun onResponse(
//                call: Call<AppApiResponse<LoginDataVO>>,
//                response: Response<AppApiResponse<LoginDataVO>>
//            ) {
//                hideLoadingbar()
//                Log.d(TAG, "response.code(): " + response.code())
//                if (response.isSuccessful) {
//                    Log.d(TAG, "isSuccessful")
//                    val result: AppApiResponse<*> = response.body()!!
//                    Log.d(TAG, "onResponse - result: $result")
//                    if (result.status == 200) {
//                        val data = result.data as LoginDataVO
//                        val member = data!!.member
//                        instance!!.prefUserId = member!!.id
//                        gotoMainActivity()
//                    } else {
//                        gotoSignupActivity()
//                    }
//                } else {
//                    Log.d(TAG, "fail")
//                    if (response.code() == 404) {
//                        showSignupPopup()
//                    } else {
//                        showErrorPopup()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<AppApiResponse<LoginDataVO>>, t: Throwable) {
//                Log.d(TAG, "onFailure - result: " + t.message)
//                hideLoadingbar()
//                showErrorPopup()
//            }
//        })
//    }
//
//    /** 2021.10.31 개인정보 확인  */
//    private fun checkPrivacy() {
//        if (TextUtils.isEmpty(instance!!.prefUserBirth)) {
//            showNoticeDialog()
//        } else {
//            /** 회원 가입 여부 확인  */
//            callMemberInfo()
//        }
//    }
//
//    private fun signIn() {
//        val signInIntent = mGoogleSignInClient!!.signInIntent
//        startActivityForResult(signInIntent, 2000)
//    }
//
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val acct = completedTask.getResult(ApiException::class.java)
//            if (acct != null) {
//                val personName = acct.displayName
//                val personGivenName = acct.givenName
//                val personFamilyName = acct.familyName
//                val personEmail = acct.email
//                val personId = acct.id
//                val personPhoto = acct.photoUrl
//                Log.d(TAG, "handleSignInResult:personName $personName")
//                Log.d(TAG, "handleSignInResult:personGivenName $personGivenName")
//                Log.d(TAG, "handleSignInResult:personEmail $personEmail")
//                Log.d(TAG, "handleSignInResult:personId $personId")
//                Log.d(TAG, "handleSignInResult:personFamilyName $personFamilyName")
//                Log.d(TAG, "handleSignInResult:personPhoto $personPhoto")
//                setLoginInfo(LoginManager.SNS_TYPE_GOOGLE, personId)
//            }
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
////            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
//            Log.e(TAG, "signInResult:failed fillInStackTrace: " + e.fillInStackTrace())
//            FirebaseCrashlytics.getInstance().log("google - log: " + e.fillInStackTrace())
//            e.printStackTrace()
//        }
//    }
//
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d("jinwoo", "onActivityResult$requestCode")
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == 2000) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        } else {
//            callbackManager!!.onActivityResult(requestCode, resultCode, data)
//        }
//    }
//
//    private fun initNaverData() {
//        Handler().post {
//            mOAuthLoginInstance = OAuthLogin.getInstance()
//            mOAuthLoginInstance.showDevelopersLog(true)
//            mOAuthLoginInstance.init(
//                mContext,
//                getString(R.string.naver_client_id),
//                getString(R.string.naver_client_secret),
//                getString(R.string.naver_client_name)
//            )
//            mOAuthLoginInstance.logoutAndDeleteToken(mContext)
//        }
//    }
//
//    private fun initNaverUI() {
//        mNaverOAuthLoginButton!!.setOAuthLoginHandler(mOAuthLoginHandler)
//    }
//
//    @SuppressLint("HandlerLeak")
//    private val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
//        override fun run(success: Boolean) {
//            if (success) {
//                val accessToken = mOAuthLoginInstance!!.getAccessToken(
//                    mContext!!.applicationContext
//                )
//                //                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
////                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
////                String tokenType = mOAuthLoginInstance.getTokenType(mContext);
//
//
////                mOauthAT.setText(accessToken);
////                mOauthRT.setText(refreshToken);
////                mOauthExpires.setText(String.valueOf(expiresAt));
////                mOauthTokenType.setText(tokenType);
////                mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());
//                setLoginInfo(LoginManager.SNS_TYPE_NAVER, "" + accessToken)
//            } else {
//                Log.d("jinwoo", "OAuthLoginHandler - failed")
//                //                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
////                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
////                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private fun showLoadingbar() {
//        loadingProgressBar!!.visibility = View.VISIBLE
//    }
//
//    private fun hideLoadingbar() {
//        loadingProgressBar!!.visibility = View.GONE
//    }
//
//    /**   */
//    private fun login() {
//        showLoadingbar()
//
////        final String userID = LoginManager.getInstance().getUserId();
////        final String userSNSType = "" + LoginManager.getInstance().getUserSNSType();
////        final String token = "" + LoginManager.getInstance().getDeviceToken();
////
////        LoginRequest request = new LoginRequest(userID, userSNSType, token);
//        val birth = instance!!.prefUserBirth
//        val phone = instance!!.prefUserPhone
//        val request = LoginNewRequest(birth!!, phone!!)
//        val call = apiService.requestLogin(request)
//        call!!.enqueue(object : Callback<AppApiResponse<LoginDataVO?>> {
//            override fun onResponse(
//                call: Call<AppApiResponse<LoginDataVO?>>,
//                response: Response<AppApiResponse<LoginDataVO?>>
//            ) {
//                hideLoadingbar()
//                Log.d(TAG, "response.code(): " + response.code())
//                if (response.isSuccessful) {
//                    Log.d(TAG, "isSuccessful")
//                    val result: AppApiResponse<*> = response.body()!!
//                    Log.d(TAG, "onResponse - result: $result")
//                    if (result.status == 200) {
////                        LoginDataVO data = (LoginDataVO) result.data;
////                        LoginMemberVO member = data.member;
//                        /** FCM 구독 설정  */
////                        FirebaseMessaging.getInstance().subscribeToTopic(ZerothDefine.FCM_SUBSCRIBE_NAME);
//
////                    saveData(member);
//                        gotoMainActivity()
//                    } else {
//                        showErrorPopup()
//                    }
//                } else {
//                    Log.d(TAG, "fail")
//                    if (response.code() == 404) {
//                        showSignupPopup()
//                    } else {
//                        showErrorPopup()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<AppApiResponse<LoginDataVO?>>, t: Throwable) {
//                Log.d(TAG, "onFailure - result: " + t.message)
//                hideLoadingbar()
//                showErrorPopup()
//            }
//        })
//    }
//
//    private fun showSignupPopup() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle(getString(R.string.txt_notice))
//            .setCancelable(false)
//            .setMessage(getString(R.string.txt_signup_error))
//            .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, which: Int ->
//                dialog.dismiss()
//                gotoSignupActivity()
//            }
//            .setNegativeButton(getString(R.string.action_cancel)) { dialog: DialogInterface, which: Int ->
//                dialog.dismiss()
//                setReLoginUI()
//            }
//        val alertDialog = builder.create()
//        alertDialog.show()
//    }
//
//    private fun showErrorPopup() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle(getString(R.string.txt_fail_login))
//            .setMessage(getString(R.string.txt_login_error))
//            .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
//        val alertDialog = builder.create()
//        alertDialog.show()
//    }
//
//    private fun gotoNoticeActivity() {
//        startActivity(Intent(this@LoginActivity2, NoticeActivity::class.java))
//    }
//
//    private fun gotoMainActivity() {
//        startActivity(Intent(this@LoginActivity2, MainActivity::class.java))
//        finish()
//    }
//
//    private fun gotoSignupActivity() {
//        startActivity(Intent(this@LoginActivity2, Signup2Activity::class.java))
//        finish()
//    }
//
//    companion object {
//        private var mOAuthLoginInstance: OAuthLogin? = null
//        private var mContext: Context? = null
//    }
//}