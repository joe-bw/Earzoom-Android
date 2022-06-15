package kr.co.sorizava.asrplayer.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ApiService {

    //     @GET("/token")
    String GRANT_TYPE = "client_credentials";

   // @FormUrlEncoded
   // @POST("/auth/realms/zeroth/protocol/openid-connect/token")
   // Call<OAuthToken> getToken(@HeaderMap Map<String, String> headers, @Field("grant_type") String grant_type);

    @FormUrlEncoded
    @POST
    Call<OAuthToken> getToken(@Url String url, @HeaderMap Map<String, String> headers, @Field("grant_type") String grant_type);

}
