package kr.co.sorizava.asrplayer.network;

import com.google.gson.annotations.Expose;

/**
 * {"access_token":"abcd","token_type":"bearer","not-before-policy":0,"session_state":"a00967bb-3da1-4a07-8f18-e9d884705821","scope":"email profile"}
 */
public class OAuthToken {

    @Expose public String access_token;
    @Expose public String token_type;
    @Expose public Integer not_before_policy;
    @Expose public String session_state;
    @Expose public String scope;
   // @Expose public Integer expiration_time;
}
