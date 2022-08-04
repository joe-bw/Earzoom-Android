package kr.co.sorizava.asrplayerKt.websocket

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import kr.co.sorizava.asrplayerKt.AppConfig
import kr.co.sorizava.asrplayerKt.ZerothDefine
import kr.co.sorizava.asrplayerKt.media.Resampler
import kr.co.sorizava.asrplayerKt.network.*
import kr.co.sorizava.asrplayerKt.websocket.listener.WsStatusListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class WsManager : IWsManager {

    private val TAG = "WsManager"
    private val RECONNECT_INTERVAL = 3 * 1000 //Reconnect interval
    private val RECONNECT_MAX_TIME = (50 * 1000).toLong() //Reconnect max interval

    private var mActivity: Context? = null
    private var mWebSocket: WebSocket? = null
    private var mOkHttpClient: OkHttpClient? = null

    private var mCurrentStatus = WsStatus.DISCONNECTED //websocket Connection Status
    private var isNeedReconnect = false //Whether to automatically reconnect after disconnection
    private var isManualClose = false //Whether to manually close the websocket connection

    private var wsStatusListener: WsStatusListener? = null
    private var mLock: Lock? = null
    private val wsMainHandler = Handler(Looper.getMainLooper())
    private var reconnectCount = 0 //Number of reconnections

    //lateinit var mWsManager: WsManager
    //private var mWsManager: WsManager? = null

    /**
     * AccessToken 을 얻기 위한 retrofit Service
     */
    //lateinit var mApiService: ApiService
    //private var mApiService: ApiService? = null

    //private val mApiService: ApiService? = null
    private var mWsServerUrl: String? = null
    private val channelConfig = String.format(ZerothDefine.OPT_16_KHZ, ZerothDefine.ZEROTH_MONO)

    private var mSourceSampleRate = 0 // source sample rate
    private var mSourceChannelCount = 2 // source channel count

    private val mTargetSampleRate = 16000 // target sample rate
    private val mTargetChannelCount = 1 // target channel count : AudioFormat.CHANNEL_IN_MONO;
    private var isStoreAudio = false
    var mResampler = Resampler()


    private val reconnectRunnable = Runnable { buildConnect() }


    constructor(builder : Builder)
    {
        this.isNeedReconnect = builder.needReconnect
        this.mLock = ReentrantLock()
    }

    companion object
    {
        var mWsManager: WsManager? = null
        var mApiService: ApiService? = null

        fun getInstance(): WsManager? {
            if (mWsManager == null) {
                mWsManager = Builder()
                    .needReconnect(true)
                    .build()
                mApiService = ApiManager.createServer()
            }
            return mWsManager
        }



        class Builder {
            internal var needReconnect = true
            fun needReconnect(`val`: Boolean): Builder {
                needReconnect = `val`
                return this
            }

            /*

            public WsManager build() {
                return new WsManager(this);
            }
             */
            fun build(): WsManager {
                return WsManager(this)
            }
        }
    }




    private val mWebSocketListener: WebSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            //Log.i(TAG, "WebSocket>>onMessage>>onOpen:" + response.code())
            Log.i(TAG, "WebSocket>>onMessage>>onOpen:" + response.code)
            mWebSocket = webSocket
            setCurrentStatus(WsStatus.CONNECTED)
            connected()
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            //Log.i(TAG, "WebSocket>>onMessage>>ByteString:" + String.valueOf(bytes));
            Log.i(TAG, "WebSocket>>onMessage>>ByteString:");
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            //Log.i(TAG, "WebSocket>>onMessage>>String:" + text);
            if (wsStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    wsMainHandler.post {
                        Log.e(TAG, "DPDPDP 11받은문장1:$text")
                        wsStatusListener!!.onMessageSubtitle(text)

                        Log.e(TAG, "DPDPDP 11받은문장2:$text")
                    }
                } else {
                    wsStatusListener!!.onMessageSubtitle(text)
                    Log.e(TAG, "DPDPDP 22받은문장:$text")
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(
                TAG,
                "WebSocket>>onClosing: $code / $reason"
            )
            tryReconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(
                TAG,
                "WebSocket>>onClosed : $code / $reason"
            )
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            //val code = response?.code() ?: WsStatus.ERROR_SOCKET_FAIL
            val code = response?.code ?: WsStatus.ERROR_SOCKET_FAIL
            Log.e(
                TAG,
                "WebSocket>>Fail: " + GsonManager.toJson(
                    ErrorModel(
                        code,
                        t.message,
                        t.localizedMessage
                    )
                )
            )
            tryReconnect()
        }
    }

    /*
    private fun WsManager(builder: kr.co.sorizava.asrplayerKt.websocket.WsManager.Builder) {
    //private fun WsManager(builder: kr.co.sorizava.asrplayerKt.websocket.WsManager.Builder) {
        isNeedReconnect = builder.needReconnect
        mLock = ReentrantLock()
    }
    */

    private fun initWebSocket() {
        if (AppConfig.getInstance()?.getPrefAsrAuthConnect()!!) {
            getToken(object : OnGetTokenListener {

                override fun onGetToken(token: String?) {
                    mWsServerUrl = createAuthWWSUrl(
                        false,
                        token!!,
                        AppConfig.getInstance()?.getPrefAsrModel()!! ,
                        channelConfig
                    )
                    Log.d(
                        TAG,
                        "createAuthWWSUrl: $mWsServerUrl"
                    )
                    if (mOkHttpClient == null) {
                        mOkHttpClient = OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .build()
                    }
                    //mOkHttpClient!!.dispatcher().cancelAll()
                    mOkHttpClient!!.dispatcher.cancelAll()
                    try {
                        mLock!!.lockInterruptibly()
                        try {
                            val request = Request.Builder().url(mWsServerUrl!!).build()
                            mOkHttpClient!!.newWebSocket(request, mWebSocketListener)
                        } finally {
                            mLock!!.unlock()
                        }
                    } catch (e: InterruptedException) {
                    }
                }

                override fun onGetToken(oAuthToken: kr.co.sorizava.asrplayerKt.network.OAuthToken?) {
                    TODO("Not yet implemented")
                }

                override fun onFailed(error: kr.co.sorizava.asrplayerKt.network.ErrorModel?) {
                    TODO("Not yet implemented")
                }

                /*
                override fun onGetToken(oAuthToken: OAuthToken) {}
                override fun onFailed(error: ErrorModel) {
                    Log.e(TAG, "fail...=" + GsonManager.toJson(error))
                }
                */

            })
        } else {
            mWsServerUrl = if (AppConfig.getInstance()?.getPrefAsrUseParamProject()!!) {
                createWWSUrl(
                    false,
                    AppConfig.getInstance()?.getPrefAppKey()!! ,
                    AppConfig.getInstance()?.getPrefAsrModel()!!,
                    channelConfig
                )
            } else {
                createWWSUrl(false, AppConfig.getInstance()?.getPrefAsrModel()!!, channelConfig)
            }
            Log.d(
                TAG,
                "createWWSUrl: $mWsServerUrl"
            )
            if (mOkHttpClient == null) {
                mOkHttpClient = OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build()
            }
            //mOkHttpClient!!.dispatcher().cancelAll()
            mOkHttpClient!!.dispatcher.cancelAll()
            try {
                mLock!!.lockInterruptibly()
                try {
                    val request = Request.Builder().url(mWsServerUrl!!).build()
                    mOkHttpClient!!.newWebSocket(request, mWebSocketListener)
                } finally {
                    mLock!!.unlock()
                }
            } catch (e: InterruptedException) {
            }
        }
    }

    override fun getWebSocket(): WebSocket? {
        return mWebSocket
    }


    fun setListener(activity: Activity?, wsStatusListener: WsStatusListener?): kr.co.sorizava.asrplayerKt.websocket.WsManager {
        this.mActivity = activity
        this.wsStatusListener = wsStatusListener
        return this
    }

    @Synchronized
    override fun isWsConnected(): Boolean {
        return mCurrentStatus == WsStatus.CONNECTED
    }

    @Synchronized
    override fun getCurrentStatus(): Int {
        return mCurrentStatus
    }

    @Synchronized
    override fun setCurrentStatus(currentStatus: Int) {
        mCurrentStatus = currentStatus
    }


    override fun startConnect(): WsManager {
        Log.d(TAG, "startConnect()")
        isManualClose = false
        if (mCurrentStatus == WsStatus.CONNECTED) {
            Log.d(TAG, "startConnect() already connected")
        } else {
            buildConnect()
        }
        return this
    }

    override fun stopConnect() {
        Log.d(TAG, "stopConnect()")
        isManualClose = true
        disconnect()
    }

    fun stopEOS() {
        isStoreAudio = true
        sendMessage("\'EOS\'")
    }

    private fun tryReconnect() {
        Log.d(TAG, "tryReconnect()")
        if (!isNeedReconnect or isManualClose) {
            Log.d(TAG, "pass tryReconnect")
            return
        }

//        if (!isNetworkConnected(mContext)) {
//            setCurrentStatus(WsStatus.DISCONNECTED);
//            return;
//        }
        setCurrentStatus(WsStatus.RECONNECT)
        val delay = reconnectCount.toLong() * RECONNECT_INTERVAL
        wsMainHandler
            .postDelayed(reconnectRunnable, Math.min(delay, RECONNECT_MAX_TIME))
        reconnectCount++
    }

    private fun cancelReconnect() {
        Log.d(TAG, "cancelReconnect()")
        wsMainHandler.removeCallbacks(reconnectRunnable)
        reconnectCount = 0
    }

    private fun connected() {
        cancelReconnect()
    }

    private fun disconnect() {
        Log.d(TAG, "disconnect()")
        if (mCurrentStatus == WsStatus.DISCONNECTED) {
            return
        }
        cancelReconnect()
        /** 2021.12.23
         * EOS not request
         * 서버 개발자(?)의 요청에 따라 주석 처리
         */
//        stopEOS();
        if (mOkHttpClient != null) {
            //mOkHttpClient!!.dispatcher().cancelAll()
            mOkHttpClient!!.dispatcher.cancelAll()
        }
        if (mWebSocket != null) {
            val isClosed = mWebSocket!!.close( CODE.NORMAL_CLOSE, TIP.NORMAL_CLOSE)
            //Close connection abnormally
            if (!isClosed) {
                Log.d(TAG, "disconnect() " + TIP.ABNORMAL_CLOSE)
            }
        }
        setCurrentStatus(WsStatus.DISCONNECTED)
    }

    @Synchronized
    private fun buildConnect() {
        Log.d(TAG, "buildConnect()")
        if (!isNetworkConnected(mActivity)) {
            setCurrentStatus(WsStatus.DISCONNECTED)
            tryReconnect()
            return
        }
        when (getCurrentStatus()) {
            WsStatus.CONNECTED, WsStatus.CONNECTING -> {}
            else -> {
                setCurrentStatus(WsStatus.CONNECTING)
                initWebSocket()
            }
        }
    }


    //Send a message
    override fun sendMessage(msg: String?): Boolean {
        //return false
        return send(msg)
    }

    override fun sendMessage(byteString: ByteString?): Boolean {
        return send(byteString)
    }

    private fun send(msg: Any?): Boolean {
        var isSend = false
        if (mWebSocket != null && mCurrentStatus == WsStatus.CONNECTED) {
            if (msg is String) {
                isSend = mWebSocket!!.send(msg)
            } else if (msg is ByteString) {
                isSend = mWebSocket!!.send(msg)
            }
            //Failed to send message, try to reconnect
            if (!isSend) {
                tryReconnect()
            }
        }
        return isSend
    }

    //Check if the network is connected
    private fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager
                .activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    private fun createAuthWWSUrl(
        single: Boolean,
        accessToken: String,
        model: String,
        contentType: String,
    ): String {
        val zerothUrl = AppConfig.getInstance()?.getPrefAsrAuthUrl() + ZerothDefine.API_AUTH_WWS_PARAM
        return String.format(
            zerothUrl, single.toString(),
            accessToken,
            model,
            contentType
        )
    }

    private fun createWWSUrl(
        single: Boolean,
        appKey: String,
        model: String,
        contentType: String,
    ): String {
        val zerothUrl = AppConfig.getInstance()?.getPrefAsrUrl() + ZerothDefine.API_WWS_PARAM_WITH_PROJECT
        return String.format(
            zerothUrl, single.toString(),
            appKey,
            model,
            contentType
        )
    }

    private fun createWWSUrl(
        single: Boolean,
        model: String,
        contentType: String,
    ): String {
        val zerothUrl = AppConfig.getInstance()?.getPrefAsrUrl() + ZerothDefine.API_WWS_PARAM
        return String.format(
            zerothUrl, single.toString(),
            model,
            contentType
        )
    }

    /**
     * Asynchronously send the request
     */
    private fun getToken(onGetTokenListener: OnGetTokenListener) {
        Log.i(TAG, "getToken")

        //@HeaderMap headers: Map<String?, String?>?,
        val headerMap = HashMap<String?, String?>()
        val authorizationCredentials =
            AppConfig.getInstance()?.getPrefAppKey() + ":" + AppConfig.getInstance()?.getPrefAppSecret()
        val flags = Base64.NO_WRAP or Base64.URL_SAFE
        val encodedString = Base64.encode(authorizationCredentials.toByteArray(), flags)
        val basicAuth = "Basic " + String(encodedString)
        headerMap["Content-Type"] = "application/x-www-form-urlencoded"
        headerMap["Authorization"] = basicAuth

        /*
        fun getToken(
            @Url url: String?,
            @HeaderMap headers: Map<String?, String?>?,
            @Field("grant_type") grant_type: String?
        ): Call<OAuthToken?>?
         */
        mApiService!!.getToken(
            AppConfig.getInstance()?.getPrefAuthTokenUrl(),
            headerMap,
            ApiService.GRANT_TYPE
        )?.enqueue(object : Callback<OAuthToken?> {

            override fun onResponse(call: Call<OAuthToken?>, response: Response<OAuthToken?>) {
                if (response.isSuccessful) {
                    //Log.d(TAG,"response.raw :"+response.toString());
                    //Log.i(TAG, "response=" + new Gson().toJson(response.body()));
                    //Log.i(TAG, "getToken=" + response.body().access_token);
                    val accessToken = response.body()!!.access_token
                    onGetTokenListener.onGetToken(accessToken)
                } else {
                    onGetTokenListener.onFailed(
                        ErrorModel(
                            response.code(),
                            response.message(),
                            ""
                        )
                    )
                }
            }

            override fun onFailure(call: Call<OAuthToken?>, t: Throwable) {
                onGetTokenListener.onFailed(
                    ErrorModel(
                        ZerothDefine.ERROR_GET_TOKEN_FAIL,
                        t.message,
                        t.localizedMessage
                    )
                )
                Log.i(TAG, "getToken fail")
            }

        })
    }

    /**
     * method for configuration audio format
     *
     * @param sampleRate   sampling rate
     * @param channelCount channel count
     */
    fun configure(sampleRate: Int, channelCount: Int) {
        mSourceChannelCount = channelCount
        mSourceSampleRate = sampleRate
        Log.d(
            TAG,
            "configure sampleRate: $sampleRate channelCount: $channelCount"
        )
    }

    var asr_mono_buffer = ByteArray(8192) // 8192 max mono buffer size

    var asr_stored_buffer = ByteArray(8192 * 8 * 4) // 4sec

    var asr_stored_buffer_offset = 0
    var asr_stored_buffer_length = 0

    // ex : 44100 * 16 * 2
    // = 1,411,200 bits / sec
    // = 176,400 bytes / sec

    // ex : 44100 * 16 * 2
    // = 1,411,200 bits / sec
    // = 176,400 bytes / sec
    // 16000 * 16 * 1 = 16000
    var zeroPcmBuffer = ByteArray(8192) // 4번이면 32000


    /**
     * callback for audio decoder
     *
     * @param buffer                   pcm buffer
     * @param length                   pcm buffer length
     */
    fun writeBuffer(buffer: ByteArray, length: Int) {

        //Log.e("writeBuffer1:", ByteString.of(*buffer).toString())
        if (mWebSocket == null || mCurrentStatus != WsStatus.CONNECTED) {
            if (isStoreAudio) {
                asr_stored_buffer_length = length
                if (asr_stored_buffer_offset + length < 262144) {
                    System.arraycopy(buffer, 0, asr_stored_buffer, asr_stored_buffer_offset, length)
                    asr_stored_buffer_offset += length
                }
            }
            return
        } else {
            if (isStoreAudio) {
                //Log.d(TAG, "send  asr_stored_buffer: " + asr_stored_buffer_offset);
                sendStoredBuffer()
                isStoreAudio = false
                asr_stored_buffer_offset = 0
            }
        }
        // mono convert
        if (mSourceChannelCount == 2 && mTargetChannelCount == 1) {
            val mono_buffer_length = length / 2
            // mono convert
            var i = 0
            while (i < mono_buffer_length) {
                asr_mono_buffer[i] = buffer[2 * i]
                asr_mono_buffer[i + 1] = buffer[2 * i + 1]
                i += 2
            }

            //Log.e("writeBuffer2:", ByteString.of(*asr_mono_buffer).toString())
            // resampling : 16bit 다운 셈플링
            val resampledPcmData = mResampler.reSample(
                asr_mono_buffer,
                mono_buffer_length,
                16,
                mSourceSampleRate,
                mTargetSampleRate
            )

            //Log.e("writeBuffer3:", ByteString.of(*resampledPcmData!!).toString())
            try {
                sendMessage(ByteString.of(*resampledPcmData!!))
            } catch (e: Exception) {
                Log.e(TAG, "sendMessage Responding failed", e)
            }
        }
    }

    fun requsetFinalTranscript() {
        Arrays.fill(zeroPcmBuffer, 0.toByte())
        for (i in 0..2) {
            try {
                sendMessage(ByteString.of(*zeroPcmBuffer))
            } catch (e: Exception) {
                Log.e(TAG, "sendMessage Responding failed", e)
            }
        }
        wsStatusListener?.resetSubtitleView()
    }

    private fun sendStoredBuffer() {
        if (asr_stored_buffer_length == 0 || asr_stored_buffer_offset == 0) return
        val buffer_length = asr_stored_buffer_offset / asr_stored_buffer_length
        val length = asr_stored_buffer_length
        for (j in 0 until buffer_length) {
            if (mSourceChannelCount == 2 && mTargetChannelCount == 1) {
                val mono_buffer_length = length / 2
                // mono convert
                var i = 0
                while (i < mono_buffer_length) {
                    asr_mono_buffer[i] = asr_stored_buffer[asr_stored_buffer_length * j + 2 * i]
                    asr_mono_buffer[i + 1] =
                        asr_stored_buffer[asr_stored_buffer_length * j + 2 * i + 1]
                    i += 2
                }

                // resampling : 16bit 다운 셈플링
                val resampledPcmData : ByteArray? = mResampler.reSample(
                    asr_mono_buffer,
                    mono_buffer_length,
                    16,
                    mSourceSampleRate,
                    mTargetSampleRate
                )
                try {
                    //sendMessage(ByteString.of(resampledPcmData1))
                    sendMessage(ByteString.of(*resampledPcmData!!))
                } catch (e: Exception) {
                    Log.e(TAG, "sendMessage Responding failed", e)
                }
            }
        }
    }

}

