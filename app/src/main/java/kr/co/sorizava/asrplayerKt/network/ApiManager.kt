package kr.co.sorizava.asrplayerKt.network

import kr.co.sorizava.asrplayerKt.ZerothDefine
import kr.co.sorizava.asrplayerKt.network.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



/*

    private static Retrofit builder;

    public static ApiService createServer() {
        builder = new Retrofit.Builder()
                .baseUrl(ZerothDefine.API_AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        return builder.create(ApiService.class);
    }

    public static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(getHttpLogginInterceptor())
                .connectTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .build();
    }

    private static Interceptor getHttpLogginInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
 */



class ApiManager {


    companion object
    {
        private var builder: Retrofit? = null

        fun createServer(): ApiService? {
            builder = Retrofit.Builder()
                .baseUrl(ZerothDefine.API_AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient()!!)
                .build()
            return builder?.create(ApiService::class.java)
        }

        fun getClient(): OkHttpClient? {
            return OkHttpClient.Builder()
                .addInterceptor(getHttpLogginInterceptor()!!)
                .connectTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .readTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .build()
        }

        private fun getHttpLogginInterceptor(): Interceptor? {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return interceptor
        }
    }

}