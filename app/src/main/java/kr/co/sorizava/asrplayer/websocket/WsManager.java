package kr.co.sorizava.asrplayer.websocket;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kr.co.sorizava.asrplayer.AppConfig;
import kr.co.sorizava.asrplayer.ZerothDefine;
import kr.co.sorizava.asrplayer.media.dsp.Resampler;
import kr.co.sorizava.asrplayer.network.ApiManager;
import kr.co.sorizava.asrplayer.network.ApiService;
import kr.co.sorizava.asrplayer.network.ErrorModel;
import kr.co.sorizava.asrplayer.network.GsonManager;
import kr.co.sorizava.asrplayer.network.OAuthToken;
import kr.co.sorizava.asrplayer.network.OnGetTokenListener;
import kr.co.sorizava.asrplayer.websocket.listener.WsStatusListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;


public class WsManager implements IWsManager {
    private static final String TAG = "WsManager";

    private final static int RECONNECT_INTERVAL = 3 * 1000;    //Reconnect interval
    private final static long RECONNECT_MAX_TIME = 50 * 1000;   //Reconnect max interval

    private static Context mActivity;

    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;

    private int mCurrentStatus = WsStatus.DISCONNECTED;     //websocket Connection Status
    private boolean isNeedReconnect;          //Whether to automatically reconnect after disconnection
    private boolean isManualClose = false;         //Whether to manually close the websocket connection
    private WsStatusListener wsStatusListener;
    private Lock mLock;
    private Handler wsMainHandler = new Handler(Looper.getMainLooper());
    private int reconnectCount = 0;   //Number of reconnections
    private static WsManager mWsManager;

    /**
     * AccessToken 을 얻기 위한 retrofit Service
     */
    private static ApiService mApiService;

    private String mWsServerUrl;

    private static String channelConfig = String.format(ZerothDefine.OPT_16_KHZ, ZerothDefine.ZEROTH_MONO);

    private int mSourceSampleRate;                        // source sample rate
    private int mSourceChannelCount = 2;                  // source channel count

    private int mTargetSampleRate = 16000;                // target sample rate
    private int mTargetChannelCount = 1;                  // target channel count : AudioFormat.CHANNEL_IN_MONO;

    private boolean isStoreAudio = false;

    Resampler mResampler = new Resampler();

    public static WsManager getInstance() {
        if (mWsManager == null) {
            mWsManager = new Builder()
                    .needReconnect(true)
                    .build();
            mApiService = ApiManager.createServer();
        }
        return mWsManager;
    }

    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            buildConnect();
        }
    };

    private WebSocketListener mWebSocketListener = new WebSocketListener() {

        @Override
        public void onOpen(WebSocket webSocket, final Response response) {
            Log.i(TAG, "WebSocket>>onMessage>>onOpen:" + response.code());
            mWebSocket = webSocket;
            setCurrentStatus(WsStatus.CONNECTED);
            connected();
        }

        @Override
        public void onMessage(WebSocket webSocket, final ByteString bytes) {
            //Log.i(TAG, "WebSocket>>onMessage>>ByteString:" + String.valueOf(bytes));
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            //Log.i(TAG, "WebSocket>>onMessage>>String:" + text);
            if (wsStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    wsMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            wsStatusListener.onMessageSubtitle(text);
                        }
                    });
                } else {
                    wsStatusListener.onMessageSubtitle(text);
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, final int code, final String reason) {
            Log.d(TAG, "WebSocket>>onClosing: " + code + " / " + reason);

            tryReconnect();
        }

        @Override
        public void onClosed(WebSocket webSocket, final int code, final String reason) {
            Log.d(TAG, "WebSocket>>onClosed : " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
            int code = response != null ? response.code() : WsStatus.ERROR_SOCKET_FAIL;
            Log.e(TAG, "WebSocket>>Fail: " + GsonManager.toJson(new ErrorModel(code, t.getMessage(), t.getLocalizedMessage())));

            tryReconnect();
        }
    };

    private WsManager(Builder builder) {
        isNeedReconnect = builder.needReconnect;
        this.mLock = new ReentrantLock();
    }

    private void initWebSocket() {
        if(AppConfig.getInstance().getPrefAsrAuthConnect()) {
            getToken(new OnGetTokenListener() {
                @Override
                public void onGetToken(String token) {
                    mWsServerUrl = createAuthWWSUrl(false, token,  AppConfig.getInstance().getPrefAsrModel(), channelConfig);
                    Log.d(TAG,"createAuthWWSUrl: " + mWsServerUrl);
                    if (mOkHttpClient == null) {
                        mOkHttpClient = new OkHttpClient.Builder()
                                .retryOnConnectionFailure(true)
                                .build();
                    }
                    mOkHttpClient.dispatcher().cancelAll();
                    try {
                        mLock.lockInterruptibly();
                        try {
                            Request request = new Request.Builder().url(mWsServerUrl).build();
                            mOkHttpClient.newWebSocket(request, mWebSocketListener);
                        } finally {
                            mLock.unlock();
                        }
                    } catch (InterruptedException e) {
                    }
                }

                @Override
                public void onGetToken(OAuthToken oAuthToken) {

                }

                @Override
                public void onFailed(ErrorModel error) {
                    Log.e(TAG, "fail...=" + GsonManager.toJson(error));
                }
            });
        }
        else {
            if(AppConfig.getInstance().getPrefAsrUseParamProject()) {
                mWsServerUrl = createWWSUrl(false, AppConfig.getInstance().getPrefAppKey(), AppConfig.getInstance().getPrefAsrModel(), channelConfig);
            }
            else {
                mWsServerUrl = createWWSUrl(false, AppConfig.getInstance().getPrefAsrModel(), channelConfig);
            }
            Log.d(TAG,"createWWSUrl: " + mWsServerUrl);
            if (mOkHttpClient == null) {
                mOkHttpClient = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .build();
            }
            mOkHttpClient.dispatcher().cancelAll();
            try {
                mLock.lockInterruptibly();
                try {
                    Request request = new Request.Builder().url(mWsServerUrl).build();
                    mOkHttpClient.newWebSocket(request, mWebSocketListener);
                } finally {
                    mLock.unlock();
                }
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public WebSocket getWebSocket() {
        return mWebSocket;
    }


    public WsManager setListener(Activity activity, WsStatusListener wsStatusListener) {
        this.mActivity = activity;
        this.wsStatusListener = wsStatusListener;
        return this;
    }

    @Override
    public synchronized boolean isWsConnected() {
        return mCurrentStatus == WsStatus.CONNECTED;
    }

    @Override
    public synchronized int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public synchronized void setCurrentStatus(int currentStatus) {
        this.mCurrentStatus = currentStatus;
    }

    @Override
    public WsManager startConnect() {
        Log.d(TAG, "startConnect()");
        isManualClose = false;
        if (mCurrentStatus == WsStatus.CONNECTED) {
            Log.d(TAG, "startConnect() already connected");
        } else {
            buildConnect();
        }
        return this;
    }

    @Override
    public void stopConnect() {
        Log.d(TAG, "stopConnect()");
        isManualClose = true;
        disconnect();
    }

    public void stopEOS() {
        isStoreAudio = true;
        sendMessage("\'EOS\'");
    }

    private void tryReconnect() {
        Log.d(TAG, "tryReconnect()");
        if (!isNeedReconnect | isManualClose) {
            Log.d(TAG, "pass tryReconnect");
            return;
        }

//        if (!isNetworkConnected(mContext)) {
//            setCurrentStatus(WsStatus.DISCONNECTED);
//            return;
//        }

        setCurrentStatus(WsStatus.RECONNECT);

        long delay = (long) reconnectCount * RECONNECT_INTERVAL;
        wsMainHandler
                .postDelayed(reconnectRunnable, Math.min(delay, RECONNECT_MAX_TIME));
        reconnectCount++;
    }

    private void cancelReconnect() {
        Log.d(TAG, "cancelReconnect()");
        wsMainHandler.removeCallbacks(reconnectRunnable);
        reconnectCount = 0;
    }

    private void connected() {
        cancelReconnect();
    }

    private void disconnect() {
        Log.d(TAG, "disconnect()");
        if (mCurrentStatus == WsStatus.DISCONNECTED) {
            return;
        }

        cancelReconnect();

        /** 2021.12.23
         * EOS not request 
         * 서버 개발자(?)의 요청에 따라 주석 처리
         */
//        stopEOS();

        if (mOkHttpClient != null) {
            mOkHttpClient.dispatcher().cancelAll();
        }
        if (mWebSocket != null) {
            boolean isClosed = mWebSocket.close(WsStatus.CODE.NORMAL_CLOSE, WsStatus.TIP.NORMAL_CLOSE);
            //Close connection abnormally
            if (!isClosed) {
                Log.d(TAG, "disconnect() " + WsStatus.TIP.ABNORMAL_CLOSE);
            }
        }
        setCurrentStatus(WsStatus.DISCONNECTED);
    }

    private synchronized void buildConnect() {
        Log.d(TAG, "buildConnect()");
        if (!isNetworkConnected(mActivity)) {
            setCurrentStatus(WsStatus.DISCONNECTED);
            tryReconnect();
            return;
        }
        switch (getCurrentStatus()) {
            case WsStatus.CONNECTED:
            case WsStatus.CONNECTING:
                break;
            default:
                setCurrentStatus(WsStatus.CONNECTING);
                initWebSocket();
        }
    }

    //Send a message
    @Override
    public boolean sendMessage(String msg) {
        return send(msg);
    }

    @Override
    public boolean sendMessage(ByteString byteString) {
        return send(byteString);
    }

    private boolean send(Object msg) {
        boolean isSend = false;
        if (mWebSocket != null && mCurrentStatus == WsStatus.CONNECTED) {
            if (msg instanceof String) {
                isSend = mWebSocket.send((String) msg);
            } else if (msg instanceof ByteString) {
                isSend = mWebSocket.send((ByteString) msg);
            }
            //Failed to send message, try to reconnect
            if (!isSend) {
                tryReconnect();
            }
        }
        return isSend;
    }

    //Check if the network is connected
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private static String createAuthWWSUrl(boolean single,
                                           String accessToken,
                                           String model,
                                           String contentType) {

        String zerothUrl = AppConfig.getInstance().getPrefAsrAuthUrl() + ZerothDefine.API_AUTH_WWS_PARAM;
        return String.format(zerothUrl,
                String.valueOf(single),
                accessToken,
                model,
                contentType);
    }

    private static String createWWSUrl(boolean single,
                                       String appKey,
                                       String model,
                                       String contentType) {

        String zerothUrl = AppConfig.getInstance().getPrefAsrUrl() + ZerothDefine.API_WWS_PARAM_WITH_PROJECT;

        return String.format(zerothUrl,
                String.valueOf(single),
                appKey,
                model,
                contentType);
    }

    private static String createWWSUrl(boolean single,
                                       String model,
                                       String contentType) {

        String zerothUrl = AppConfig.getInstance().getPrefAsrUrl() + ZerothDefine.API_WWS_PARAM;

        return String.format(zerothUrl,
                String.valueOf(single),
                model,
                contentType);
    }

    /**
     * Asynchronously send the request
     */
    private static void getToken(final OnGetTokenListener onGetTokenListener) {
        Log.i(TAG, "getToken");

        HashMap<String, String> headerMap = new HashMap<>();

        String authorizationCredentials = AppConfig.getInstance().getPrefAppKey() + ":" + AppConfig.getInstance().getPrefAppSecret();

        int flags = Base64.NO_WRAP | Base64.URL_SAFE;
        byte[] encodedString = Base64.encode(authorizationCredentials.getBytes(), flags);
        String basicAuth = "Basic " + new String(encodedString);

        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("Authorization", basicAuth);

        mApiService.getToken(AppConfig.getInstance().getPrefAuthTokenUrl(), headerMap, ApiService.GRANT_TYPE).enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, retrofit2.Response<OAuthToken> response) {
                if (response.isSuccessful()) {
                    //Log.d(TAG,"response.raw :"+response.toString());
                    //Log.i(TAG, "response=" + new Gson().toJson(response.body()));
                    //Log.i(TAG, "getToken=" + response.body().access_token);
                    String accessToken = response.body().access_token;
                    onGetTokenListener.onGetToken(accessToken);
                } else {
                    onGetTokenListener.onFailed(new ErrorModel(
                            response.code(),
                            response.message(),
                            ""));
                }
            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {
                onGetTokenListener.onFailed(new ErrorModel(
                        ZerothDefine.ERROR_GET_TOKEN_FAIL,
                        t.getMessage(),
                        t.getLocalizedMessage()));
                Log.i(TAG, "getToken fail");
            }
        });
    }

    /**
     * method for configuration audio format
     *
     * @param sampleRate   sampling rate
     * @param channelCount channel count
     */
    public void configure(int sampleRate, int channelCount) {
        mSourceChannelCount = channelCount;
        mSourceSampleRate = sampleRate;

        Log.d(TAG, "configure sampleRate: " + sampleRate + " channelCount: " + channelCount);
    }

    byte[] asr_mono_buffer = new byte[8192];  // 8192 max mono buffer size
    byte[] asr_stored_buffer = new byte[8192 * 8 * 4]; // 4sec
    int asr_stored_buffer_offset = 0;
    int asr_stored_buffer_length = 0;

    // ex : 44100 * 16 * 2
    // = 1,411,200 bits / sec
    // = 176,400 bytes / sec

    // 16000 * 16 * 1 = 16000
    byte[] zeroPcmBuffer = new byte[8192];   // 4번이면 32000

    /**
     * callback for audio decoder
     *
     * @param buffer                   pcm buffer
     * @param length                   pcm buffer length
     */
    public void writeBuffer(byte[] buffer, int length) {

        if (mWebSocket == null || mCurrentStatus != WsStatus.CONNECTED) {
            if(isStoreAudio) {
                asr_stored_buffer_length = length;
                if(asr_stored_buffer_offset + length < 262144) {
                    System.arraycopy(buffer, 0, asr_stored_buffer, asr_stored_buffer_offset, length);
                    asr_stored_buffer_offset += length;
                }
            }
            return;
        }
        else {
            if(isStoreAudio) {
                //Log.d(TAG, "send  asr_stored_buffer: " + asr_stored_buffer_offset);
                sendStoredBuffer();
                isStoreAudio = false;
                asr_stored_buffer_offset = 0;
            }
        }
        // mono convert
        if (mSourceChannelCount == 2 && mTargetChannelCount == 1) {
            int mono_buffer_length = length / 2;
            // mono convert
            for (int i = 0; i < mono_buffer_length; i += 2) {
                asr_mono_buffer[i] = buffer[2 * i];
                asr_mono_buffer[i + 1] = buffer[2 * i + 1];
            }

            // resampling : 16bit 다운 셈플링
            byte[] resampledPcmData = mResampler.reSample(asr_mono_buffer, mono_buffer_length, 16, mSourceSampleRate, mTargetSampleRate);

            try {
                sendMessage(ByteString.of(resampledPcmData));
            } catch (Exception e) {
                Log.e(TAG, "sendMessage Responding failed", e);
            }
        }
    }

    public void requsetFinalTranscript() {
        Arrays.fill(zeroPcmBuffer, (byte)0);
        for(int i = 0; i < 3; i++) {
            try {
                sendMessage(ByteString.of(zeroPcmBuffer));
            } catch (Exception e) {
                Log.e(TAG, "sendMessage Responding failed", e);
            }
        }
        if (wsStatusListener != null) {
            wsStatusListener.resetSubtitleView();
        }
    }

    private void sendStoredBuffer() {
        if(asr_stored_buffer_length == 0 || asr_stored_buffer_offset == 0) return;

        int buffer_length = asr_stored_buffer_offset / asr_stored_buffer_length;
        int length = asr_stored_buffer_length;
        for(int j = 0; j < buffer_length; j++) {
            if (mSourceChannelCount == 2 && mTargetChannelCount == 1) {
                int mono_buffer_length = length / 2;
                // mono convert
                for (int i = 0; i < mono_buffer_length; i += 2) {
                    asr_mono_buffer[i] = asr_stored_buffer[asr_stored_buffer_length * j + 2 * i];
                    asr_mono_buffer[i + 1] = asr_stored_buffer[asr_stored_buffer_length * j + 2 * i + 1];
                }

                // resampling : 16bit 다운 셈플링
                byte[] resampledPcmData = mResampler.reSample(asr_mono_buffer, mono_buffer_length, 16, mSourceSampleRate, mTargetSampleRate);

                try {
                    sendMessage(ByteString.of(resampledPcmData));
                } catch (Exception e) {
                    Log.e(TAG, "sendMessage Responding failed", e);
                }
            }
        }
    }

    public static final class Builder {

        private boolean needReconnect = true;

        public Builder() {
        }

        public Builder needReconnect(boolean val) {
            needReconnect = val;
            return this;
        }

        public WsManager build() {
            return new WsManager(this);
        }
    }
}
