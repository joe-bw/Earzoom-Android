package kr.co.sorizava.asrplayerKt.websocket.listener

open interface WsStatusListener {
    fun onConnectionStatusChanged(status: Int)
    fun onMessageSubtitle(text: String?, isFinal: Boolean)
    fun onMessageSubtitle(text: String?)
    fun resetSubtitleView()
}