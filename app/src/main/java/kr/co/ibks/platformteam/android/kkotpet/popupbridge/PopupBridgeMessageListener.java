package kr.co.ibks.platformteam.android.kkotpet.popupbridge;

/**
 * @brief 웹뷰 브릿지 소스 (오픈소스)
 */
public interface PopupBridgeMessageListener {
    void onMessageReceived(String messageName, String data);
}
