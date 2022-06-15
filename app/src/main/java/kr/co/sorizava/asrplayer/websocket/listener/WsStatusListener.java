package kr.co.sorizava.asrplayer.websocket.listener;

import okhttp3.Response;
import okio.ByteString;

public interface WsStatusListener {
  public void onConnectionStatusChanged(int status);
  public void onMessageSubtitle(String text, boolean isFinal);
  public void onMessageSubtitle(String text);
  public void resetSubtitleView();
}
