package kr.co.ibks.platformteam.android.kkotpet;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.CommerceDetailObject;
import com.kakao.message.template.CommerceTemplate;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ibks.platformteam.android.kkotpet.common.BaseActivity;
import kr.co.ibks.platformteam.android.kkotpet.config.AppConfig;
import kr.co.ibks.platformteam.android.kkotpet.info.AppActionInfo;
import kr.co.ibks.platformteam.android.kkotpet.info.UserInfo;
import kr.co.ibks.platformteam.android.kkotpet.popupbridge.PopupBridge;
import kr.co.ibks.platformteam.android.kkotpet.popupbridge.PopupBridgeListener;
import kr.co.ibks.platformteam.android.kkotpet.popupbridge.PopupBridgeNavigationListener;
import kr.co.ibks.platformteam.android.kkotpet.sns.SNSInstance;
import kr.co.ibks.platformteam.android.kkotpet.sns.SNSUserInfoDTO;
import kr.co.ibks.platformteam.android.kkotpet.util.AppLog;
import kr.co.ibks.platformteam.android.kkotpet.util.CommonUtil;
import kr.co.ibks.platformteam.android.kkotpet.util.WebViewUtil;
import kr.co.ibks.platformteam.android.kkotpet.view.popup.PopupActivity;
import kr.co.ibks.platformteam.android.kkotpet.view.setting.SettingActivity;
import kr.co.ibks.platformteam.android.kkotpet.view.walkthrough.WalkThroughActivity;

public class MainActivity extends BaseActivity implements PopupBridgeListener, PopupBridgeNavigationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.splashView)
    RelativeLayout splashView;

    @BindView(R.id.splashImage)
    ImageView splashImage;

    private AnimationDrawable animationDrawable;

    private PopupBridge popupBridge;

    private static boolean isInitialized;

    private long mPrevPressedTime = 0;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    ShareLinkContent linkContent;

    /**
     * @brief SNS 로그인 관리 객체
     */
    private SNSInstance snsIns = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        initializeView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isInitialized = false;

        if (snsIns != null) {
            snsIns.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.getChildCount() > 0) {
            webView.removeViewAt(webView.getChildCount() - 1);
        }

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            leaveApp();
        }
    }

    /**
     * View 초기화
     */
    private void initializeView() {
        progressBar.setVisibility(View.GONE);

        // Splash 보이기
        if (!isInitialized) {
            startSplashAnimation();
        }

        // Permission Check
        checkPermissions();
    }

    /**
     * 앱 종료
     */
    private void leaveApp() {
        // 3초 이내에 Back Button 이 다시 눌리면 앱 종료
        long currentPressedTime = System.currentTimeMillis();
        if (currentPressedTime - mPrevPressedTime < 3000) {
            isInitialized = false;
            finish();
        } else {
            mPrevPressedTime = currentPressedTime;
            Toast.makeText(this, R.string.app_finish_msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * App 에서 사용하는 권한이 부여됐는지 여부를 확인하고 사용자에게 권한을 요청한다.
     */
    private void checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(allPermissionsListener)
                .onSameThread()
                .check();
    }

    private MultiplePermissionsListener allPermissionsListener = new MultiplePermissionsListener() {

        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
            // 모든 권한 허용 시
            if (report.areAllPermissionsGranted()) {
                // 웹뷰 세팅
                setWebView();
                return;
            }

            // 권한 거부 및 앱 종료
            new AlertDialog.Builder(context)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.permission_common_app_exit_deny)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 승인 거부 시 앱 종료에 대한 초기화 처리
                                    finish();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            token.continuePermissionRequest();
        }
    };

    /**
     * 웹뷰 세팅
     */
    private void setWebView() {
        WebViewUtil.setWebviewSetting(webView, AppConfig.USER_AGENT);

        popupBridge = PopupBridge.newInstance(this, webView);
        popupBridge.setPopupBridgeListener(this);
        popupBridge.setNavigationListener(this);

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(AppConfig.DOMAIN_URL_MAIN);
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

            if (!isInitialized) {
                stopSplashAnimation();
                isInitialized = true;
            }
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

    @Override
    public void loginSns(String addParams) {
        Log.d(TAG, "loginSns addParams : " + addParams);
//        Toast.makeText(context, "loginSns addParams : " + addParams, Toast.LENGTH_SHORT).show();

        String loginType = CommonUtil.getValueFromJson(addParams, "login_type");

        switch (loginType) {
            case SNSInstance.SNS_PARAM_FACEBOOK:
                loginSNS(SNSInstance.SNS_TYPE_FACEBOOK);
                break;
            case SNSInstance.SNS_PARAM_KAKAO:
                loginSNS(SNSInstance.SNS_TYPE_KAKAO);
                break;
            case SNSInstance.SNS_PARAM_NAVER:
                loginSNS(SNSInstance.SNS_TYPE_NAVER);
                break;
            case SNSInstance.SNS_PARAM_PAYCO:
                loginSNS(SNSInstance.SNS_TYPE_PAYCO);
                break;
        }
    }

    @Override
    public void shareFacebook(String addParams) {
//        Toast.makeText(context, "shareFacebook addParams : " + addParams, Toast.LENGTH_SHORT).show();
//        shareFacebookWeb(addParams);
        shareFacebookSdk(addParams);
    }

    private void shareFacebookWeb(String addParams) {
        String product_id = CommonUtil.getValueFromJson(addParams, "product_id");
        String url = String.format("%s/%s", AppConfig.FACEBOOK_SHARE_URL, product_id);
//        startPopupActivity(url, getString(R.string.title_facebook_share));
        popupBridge.open(url);
    }

    private void shareFacebookSdk(String addParams) {
        String product_id = CommonUtil.getValueFromJson(addParams, "product_id");
        String webUrl = String.format("%s/%s", AppConfig.FACEBOOK_SHARE_URL_2, product_id);

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }

        if (shareDialog == null) {
            shareDialog = new ShareDialog(this);
        }

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                shareDialog.show(linkContent, ShareDialog.Mode.WEB);
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(webUrl))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    @Override
    public void shareKakao(String addParams) {
//        Toast.makeText(context, "shareKakao addParams : " + addParams, Toast.LENGTH_SHORT).show();

        // CommerceTemplate
//        shareKakaoCommerceTemplate(addParams);

        // DefaultFeedTemplate
        sendDefaultFeedTemplate(addParams);
    }

    private void sendDefaultFeedTemplate(String addParams) {
        final String product_name = CommonUtil.getValueFromJson(addParams, "product_name");
        final String representation_image = CommonUtil.getValueFromJson(addParams, "representation_image");
        final String product_id = CommonUtil.getValueFromJson(addParams, "product_id");
        final String standard_price = CommonUtil.getValueFromJson(addParams, "standard_price");
        final String selling_price = CommonUtil.getValueFromJson(addParams, "selling_price");
        final String discount = CommonUtil.getValueFromJson(addParams, "discount");
        final String discount_type = CommonUtil.getValueFromJson(addParams, "discount_type");

        final String imageUrl = String.format("%s/%s", AppConfig.DOMAIN_URL_S3, representation_image);
        final String webUrl = String.format("%s/productView/%s", AppConfig.DOMAIN_URL, product_id);

        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder(product_name,
                        imageUrl,
                        LinkObject.newBuilder()
                                .setWebUrl(webUrl)
                                .setMobileWebUrl(webUrl)
                                .build())
//                        .setDescrption("#케익 #딸기 #삼평동 #카페 #분위기 #소개팅")
                        .build())
                .addButton(new ButtonObject("웹에서 바로 보기", LinkObject.newBuilder()
                        .setWebUrl(webUrl)
                        .setMobileWebUrl(webUrl)
                        .build()))
//                .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
//                        .setWebUrl("https://developers.kakao.com")
//                        .setMobileWebUrl("https://developers.kakao.com")
//                        .setAndroidExecutionParams("key1=value1")
//                        .setIosExecutionParams("key1=value1")
//                        .build()))
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, kakaoCallback);
    }

    private void shareKakaoCommerceTemplate(String addParams) {
        String product_name = CommonUtil.getValueFromJson(addParams, "product_name");
        String representation_image = CommonUtil.getValueFromJson(addParams, "representation_image");
        String product_id = CommonUtil.getValueFromJson(addParams, "product_id");
        String standard_price = CommonUtil.getValueFromJson(addParams, "standard_price");
        String selling_price = CommonUtil.getValueFromJson(addParams, "selling_price");
        String discount = CommonUtil.getValueFromJson(addParams, "discount");
        String discount_type = CommonUtil.getValueFromJson(addParams, "discount_type");

        int standard_price1 = Integer.parseInt(standard_price);
        int selling_price1 = Integer.parseInt(selling_price);
        int discount1 = Integer.parseInt(discount);
        float discount_rate = 0;

        if ("WON".equals(discount_type)) {
            discount_rate = (float) (standard_price1 - selling_price1) / standard_price1 * 100;
        }

        ContentObject contentObject = ContentObject.newBuilder(
                product_name,
                String.format("%s/%s", AppConfig.DOMAIN_URL_S3, representation_image),
                LinkObject.newBuilder()
                        .setWebUrl(String.format("%s/productView/%s", AppConfig.DOMAIN_URL, product_id))
                        .setMobileWebUrl(String.format("%s/productView/%s", AppConfig.DOMAIN_URL, product_id))
                        .build())
                .build();

        CommerceDetailObject commerceDetailObject = CommerceDetailObject.newBuilder(standard_price1)
                .setDiscountPrice(selling_price1)
                .setDiscountRate((int) discount_rate)
                .build();

        ButtonObject firstButtonObject = new ButtonObject("구매하기",
                LinkObject.newBuilder()
                        .setWebUrl(String.format("%s/productView/%s", AppConfig.DOMAIN_URL, product_id))
                        .setMobileWebUrl(String.format("%s/productView/%s", AppConfig.DOMAIN_URL, product_id))
                        .build());

//        ButtonObject secondButtobObject = new ButtonObject("공유하기",
//                LinkObject.newBuilder()
//                        .setWebUrl("https://style.kakao.com/main/women/contentId=100/share")
//                        .setMobileWebUrl("https://m.style.kakao.com/main/women/contentId=100/share")
//                        .setAndroidExecutionParams("contentId=100&share=true")
//                        .setIosExecutionParams("contentId=100&share=true")
//                        .build());

        CommerceTemplate params = CommerceTemplate.newBuilder(contentObject, commerceDetailObject)
                .addButton(firstButtonObject)
//                .addButton(secondButtobObject)
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, kakaoCallback);
    }

    private ResponseCallback<KakaoLinkResponse> kakaoCallback = new ResponseCallback<KakaoLinkResponse>() {
        @Override
        public void onFailure(ErrorResult errorResult) {
//            Toast.makeText(getApplicationContext(), errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(KakaoLinkResponse result) {
//            Toast.makeText(getApplicationContext(), "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
            // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
        }
    };

    private Map<String, String> serverCallbackArgs = getServerCallbackArgs();

    private Map<String, String> getServerCallbackArgs() {
        Map<String, String> callbackParameters = new HashMap<>();
        callbackParameters.put("user_id", "${current_user_id}");
        callbackParameters.put("product_id", "${shared_product_id}");
        return callbackParameters;
    }

    /**
     * @param nType SNS 타입
     * @brief SNS 로그인 요청
     */
    private void loginSNS(int nType) {
        snsIns = new SNSInstance(this, nType, snsCallback);
        snsIns.loginSNS();
    }

    /**
     * @brief SNS 로그인 결과 값 리스너
     */
    private SNSInstance.SNSCallback snsCallback = new SNSInstance.SNSCallback() {
        @Override
        public void SNSLoginSuccess(int nType, SNSUserInfoDTO userInfo) {

            AppLog.Debug("SNSLoginSuccess nType : " + nType + " userInfoDTO : " + userInfo.getId());
            Toast.makeText(context, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

            String sSns = UserInfo.getSNSType(nType);

            if (!TextUtils.isEmpty(sSns) && !TextUtils.isEmpty(userInfo.getAccessToken())) {
                String accessToken = userInfo.getAccessToken();
                loginOnSNS(accessToken, sSns);

//                typeSelectFragment.loginOnSNS(sSns, userInfo);
            }
        }

        @Override
        public void SNSError(int nType, String sErrorMsg) {
            Toast.makeText(context, sErrorMsg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void SNSError(int nType, int sErrorCode, String sErrorMsg) {
            Toast.makeText(context, sErrorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private void loginOnSNS(String accessToken, String sSns) {
        String code = accessToken;
        String state = sSns;
        final String url = String.format("%s/login?code=%s&state=%s",
                AppConfig.DOMAIN_URL,
                code,
                state
        );

        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

//    private void startSplashAnimation() {
//        try {
//            splashImage.setBackgroundResource(R.drawable.splash_animation_list);
//            animationDrawable = (AnimationDrawable) splashImage.getBackground();
//            animationDrawable.start();
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//        }
//        splashView.setVisibility(View.VISIBLE);
//    }

//    private void stopSplashAnimation() {
//        if (animationDrawable != null) {
//            if (animationDrawable.isRunning())
//                animationDrawable.stop();
//        }
//
//        splashView.setVisibility(View.GONE);
//
//        if (!AppActionInfo.getInstance(getApplicationContext()).isShowWalkThrough()) {
//            startWalkThroughActivity();
//        }
//    }

    /**
     * Splash 애니메이션 Start
     */
    private void startSplashAnimation() {
        splashView.setVisibility(View.VISIBLE);

        // GIF 이미지 Load
        Glide.with(this)
                .load(R.drawable.splash)
                .into(splashImage);
    }

    /**
     * Splash 애니메이션 Stop
     */
    private void stopSplashAnimation() {
        // WalkThrough Show 상태 체크
        if (!AppActionInfo.getInstance(getApplicationContext()).isShowWalkThrough()) {
            startWalkThroughActivity();
        }

        splashView.setVisibility(View.GONE);
    }

    /**
     * @brief WalkThroughActivity 이동
     */
    private void startWalkThroughActivity() {
        Intent intent = new Intent(this, WalkThroughActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (snsIns != null) {
            //sns 로그인 객체들에게 결과값 호출
            snsIns.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onUrlOpened(String url) {
        String title = "";

        if (url.contains("phone-auth")) {
            title = getString(R.string.title_phone_auth);
        } else if (url.contains("pay")) {
            title = getString(R.string.title_phone_auth);
        }

        startPopupActivity(url, title);
    }

    private void startPopupActivity(String url, String title) {
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
