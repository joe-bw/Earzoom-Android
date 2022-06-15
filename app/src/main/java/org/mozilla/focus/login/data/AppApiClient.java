package org.mozilla.focus.login.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import kr.co.sorizava.asrplayer.ZerothDefine;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppApiClient {

    /**
     * 웹서버 URL - 테스트 서버: http://211.248.153.107:1969/
     * 웹서버 URL - 운영 서버: http://45.115.152.130:8090/
     * https://api.earzoom.kr:8443
     *
     */
//    private static final String BASE_URL = "http://211.248.153.107:1969/";
//    private static final String BASE_URL = "http://45.115.152.130:8090/";

    private static final Gson gson = new GsonBuilder().setLenient().create();;

    public static AppApiService getApiService() {
        return getInstance().create(AppApiService.class);
    }

    private static Retrofit getInstance() {
        return new Retrofit.Builder()
                .baseUrl(ZerothDefine.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getProvideOkHttpClient(getHttpInterceptor()))
                .build();
    }

    private static Interceptor getHttpInterceptor() {
        return chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();
            Request request = builder.build();
            return chain.proceed(request);
        };
    }

    public static OkHttpClient getProvideOkHttpClient(Interceptor interceptor) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addNetworkInterceptor(interceptor);
        httpClientBuilder.connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .build();

        return httpClientBuilder.build();
    }
}
