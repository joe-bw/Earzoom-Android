package kr.co.sorizava.asrplayer.network;

public interface OnGetTokenListener {
    void onGetToken(String token);
    void onGetToken(OAuthToken oAuthToken);
    void onFailed(ErrorModel error);
}
