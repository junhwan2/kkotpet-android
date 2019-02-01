package kr.co.ibks.platformteam.android.kkotpet.popupbridge;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import kr.co.ibks.platformteam.android.kkotpet.util.CommonUtil;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;


/**
 * @brief 웹뷰 브릿지 소스 (오픈소스)
 */
public class PopupBridge extends BrowserSwitchFragment {

    public static final String POPUP_BRIDGE_NAME = "popupBridge";
    public static final String POPUP_BRIDGE_URL_HOST = "popupbridgev1";

    private static final String TAG = "kr.co.ibks.platformteam.android.kkotpet";

    private WebView mWebView;
    private PopupBridgeNavigationListener mNavigationListener;
    private PopupBridgeMessageListener mMessageListener;

    private PopupBridgeListener popupBridgeListener = null;

    public PopupBridge() {
    }

    /**
     * Create a new instance of {@link PopupBridge} and add it to the {@link Activity}'s {@link FragmentManager}.
     * <p>
     * This will enable JavaScript in your WebView.
     *
     * @param activity The {@link Activity} to add the {@link Fragment} to.
     * @param webView  The {@link WebView} to enable for PopupBridge.
     * @return {@link PopupBridge}
     * @throws IllegalArgumentException If the activity is not valid or the fragment cannot be added.
     */
    public static PopupBridge newInstance(Activity activity, WebView webView) throws IllegalArgumentException {
        if (activity == null) {
            throw new IllegalArgumentException("Activity is null");
        }

        if (webView == null) {
            throw new IllegalArgumentException("WebView is null");
        }

        FragmentManager fm = activity.getFragmentManager();
        PopupBridge popupBridge = (PopupBridge) fm.findFragmentByTag(TAG);
        if (popupBridge == null) {
            popupBridge = new PopupBridge();
            Bundle bundle = new Bundle();

            popupBridge.setArguments(bundle);

            try {
                if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                    try {
                        fm.beginTransaction().add(popupBridge, TAG).commitNow();
                    } catch (IllegalStateException | NullPointerException e) {
                        fm.beginTransaction().add(popupBridge, TAG).commit();
                        try {
                            fm.executePendingTransactions();
                        } catch (IllegalStateException ignored) {}
                    }
                } else {
                    fm.beginTransaction().add(popupBridge, TAG).commit();
                    try {
                        fm.executePendingTransactions();
                    } catch (IllegalStateException ignored) {}
                }
            } catch (IllegalStateException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        webView.getSettings().setJavaScriptEnabled(true);

        popupBridge.mContext = activity.getApplicationContext();
        popupBridge.mWebView = webView;
        popupBridge.mWebView.addJavascriptInterface(popupBridge, POPUP_BRIDGE_NAME);

        return popupBridge;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void runJavaScriptInWebView(final String script) {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.evaluateJavascript(script, null);
            }
        });
    }

    @Override
    public void onBrowserSwitchResult(int requestCode, BrowserSwitchResult result, Uri returnUri) {
        String error = null;
        String payload = null;

        if (result == BrowserSwitchResult.OK) {
            if (returnUri == null || !returnUri.getScheme().equals(getReturnUrlScheme()) ||
                    !returnUri.getHost().equals(POPUP_BRIDGE_URL_HOST)) {
                return;
            }

            JSONObject json = new JSONObject();
            JSONObject queryItems = new JSONObject();

            Set<String> queryParams = returnUri.getQueryParameterNames();
            if (queryParams != null && !queryParams.isEmpty()) {
                for (String queryParam : queryParams) {
                    try {
                        queryItems.put(queryParam, returnUri.getQueryParameter(queryParam));
                    } catch (JSONException e) {
                        error = "new Error('Failed to parse query items from return URL. " +
                                e.getLocalizedMessage() + "')";
                    }
                }
            }

            try {
                json.put("path", returnUri.getPath());
                json.put("queryItems", queryItems);
//                json.put("hash", returnUri.getFragment());
            } catch (JSONException ignored) {}

            payload = json.toString();
        } else if (result == BrowserSwitchResult.ERROR) {
            error = "new Error('" + result.getErrorMessage() + "')";
        }

        callComplete(error, payload);
    }

    private void callComplete(final String error, final String payload) {
        if (!CommonUtil.isActivityContextAlive(getActivity())) {
            return;
        }

        sendAppMesage(String.format("popupBridge.onComplete(%s, %s);", error, payload));

//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (SDK_INT >= KITKAT) {
//                    mWebView.evaluateJavascript(String.format("popupBridge.onComplete(%s, %s);", error,
//                            payload), null);
//                }
//                else
//                {
//                    String sCallFnc = String.format("javascript:popupBridge.onComplete(%s, %s);", error, payload);
//
//                    mWebView.loadUrl(sCallFnc);
//                }
//
//
//            }
//        });
    }

    public void sendAppMesage(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (SDK_INT >= KITKAT) {
                    mWebView.evaluateJavascript(msg, null);
                } else {
                    String sCallFnc = String.format("javascript:%s", msg);

                    mWebView.loadUrl(sCallFnc);
                }
            }
        });
    }

    @Override
    public String getReturnUrlScheme() {
        return mContext.getPackageName().toLowerCase().replace("_", "") + ".popupbridge";
        //return "com.braintreepayments.popupbridge" + ".popupbridge ";
    }

    @JavascriptInterface
    public String getReturnUrlPrefix() {
        return String.format("%s://%s/", getReturnUrlScheme(), POPUP_BRIDGE_URL_HOST);
    }

    @JavascriptInterface
    public void open(String url) {
        browserSwitch(1, url);
 
//        if (mNavigationListener != null) {
//            mNavigationListener.onUrlOpened(url);
//        }
    }

    @JavascriptInterface
    public void sendMessage(String messageName) {
        if (mMessageListener != null) {
            mMessageListener.onMessageReceived(messageName, null);
        }
    }

    @JavascriptInterface
    public void sendMessage(String messageName, String data) {
        if (mMessageListener != null) {
            mMessageListener.onMessageReceived(messageName, data);
        }
    }

    /**
     * SNS 로그인 (naver, facebook, kakao, payco)
     * @param addParams
     */
    @JavascriptInterface
    public void loginSns(String addParams) {
        if (popupBridgeListener != null) {
            popupBridgeListener.loginSns(addParams);
        }
    }

    /**
     * Facebook 공유하기
     *
     * @param addParams
     */
    @JavascriptInterface
    public void shareFacebook(String addParams) {
        if (popupBridgeListener != null) {
            popupBridgeListener.shareFacebook(addParams);
        }
    }

    /**
     * Kakao 공유하기
     *
     * @param addParams
     */
    @JavascriptInterface
    public void shareKakao(String addParams) {
        if (popupBridgeListener != null) {
            popupBridgeListener.shareKakao(addParams);
        }
    }

    public void setPopupBridgeListener(PopupBridgeListener popupBridgeListener) {
        this.popupBridgeListener = popupBridgeListener;
    }

    public void setNavigationListener(PopupBridgeNavigationListener listener) {
        mNavigationListener = listener;
    }

    public void setMessageListener(PopupBridgeMessageListener listener) {
        mMessageListener = listener;
    }
}
