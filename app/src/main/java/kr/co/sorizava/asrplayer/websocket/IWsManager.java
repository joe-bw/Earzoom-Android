package kr.co.sorizava.asrplayer.websocket;

import okhttp3.WebSocket;
import okio.ByteString;

interface IWsManager {

  WebSocket getWebSocket();

  WsManager startConnect();

  void stopConnect();

  boolean isWsConnected();

  int getCurrentStatus();

  void setCurrentStatus(int currentStatus);

  boolean sendMessage(String msg);

  boolean sendMessage(ByteString byteString);
}
