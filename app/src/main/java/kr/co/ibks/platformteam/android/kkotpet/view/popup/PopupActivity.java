package kr.co.ibks.platformteam.android.kkotpet.view.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ibks.platformteam.android.kkotpet.MainActivity;
import kr.co.ibks.platformteam.android.kkotpet.R;
import kr.co.ibks.platformteam.android.kkotpet.config.AppConfig;
import kr.co.ibks.platformteam.android.kkotpet.popupbridge.PopupBridge;
import kr.co.ibks.platformteam.android.kkotpet.sns.SNSInstance;
import kr.co.ibks.platformteam.android.kkotpet.util.WebViewUtil;
import kr.co.ibks.platformteam.android.kkotpet.view.setting.SettingActivity;

public class PopupActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;

    @BindView(R.id.titleText)
    TextView titleText;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private PopupBridge popupBridge;

    /**
     * @brief SNS 로그인 관리 객체
     */
    private SNSInstance snsIns = null;

    private String title = "";
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        ButterKnife.bind(this);

        context = this;

        if (getIntent().hasExtra("TITLE")) {
            title = getIntent().getStringExtra("TITLE");
        }

        if (getIntent().hasExtra("URL")) {
            url = getIntent().getStringExtra("URL");
        }

        titleText.setText(title);

        initializeView();
    }

    @OnClick(R.id.prevButton)
    public void onPrevButtonClick() {
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * View 초기화
     */
    private void initializeView() {
        progressBar.setVisibility(View.GONE);

        // 웹뷰 세팅
        setWebView();
    }

    /**
     * 웹뷰 세팅
     */
    private void setWebView() {
        WebViewUtil.setWebviewSetting(webView, AppConfig.USER_AGENT);

        popupBridge = PopupBridge.newInstance(this, webView);
//        popupBridge.setPopupBridgeListener(this);

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(url);
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            //view.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
            Log.d(TAG, "onPageStarted url : " + url);

//            ProgressDlg.getInstance().showProgress(mContext, true);
            progressBar.setVisibility(View.VISIBLE);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            //view.setLayerType(WebView.LAYER_TYPE_NONE, null);
            Log.d(TAG, "onPageFinished url : " + url);

//            ProgressDlg.getInstance().stopProgress();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(TAG, "shouldOverrideUrlLoading url : " + url);

            if (url.startsWith("tel:")) {
                //tel:01000000000
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("mailto:")) {
                //mailto:ironnip@test.com
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);
                return true;
            } else if (url.startsWith("intent:")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (url.startsWith("kkotpet:")) {
                if (url.contains("appSetting")) {
                    Intent intent = new Intent(context, SettingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                return true;
            }

            try {
                String sHostname = new URL(url).getHost();

                if (url.indexOf("___target=_blank") > -1) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    };


    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public boolean onCreateWindow(final WebView view, boolean dialog, boolean userGesture, Message resultMsg) {

            //웹뷰 새창 열기시 호출

            Log.d(TAG, "onCreateWindow");

            view.removeAllViews();

            WebView childView = new WebView(context);
            childView.getSettings().setJavaScriptEnabled(true);
            childView.setWebChromeClient(this);
            //childView.setWebViewClient(new WebViewClient());
            childView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            WebViewUtil.setWebviewSetting(childView, "");

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childView);
            resultMsg.sendToTarget();

            childView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView childView, String url) {

                    //외부 브라우저로 연결
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(url));
                    startActivity(browserIntent);
                    return true;
                }
            });

            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.app_name)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
        }
    };
}
