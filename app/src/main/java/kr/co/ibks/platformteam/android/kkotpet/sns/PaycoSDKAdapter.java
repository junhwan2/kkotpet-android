package kr.co.ibks.platformteam.android.kkotpet.sns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.toast.android.paycologin.Errors;
import com.toast.android.paycologin.OnLoginListener;
import com.toast.android.paycologin.OnLogoutListener;
import com.toast.android.paycologin.PaycoLoginError;
import com.toast.android.paycologin.PaycoLoginExtraResult;
import com.toast.android.paycologin.PaycoLoginManager;
import com.toast.android.paycologin.PaycoLoginSdkVersion;
import com.toast.android.paycologin.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;

import kr.co.ibks.platformteam.android.kkotpet.R;
import kr.co.ibks.platformteam.android.kkotpet.util.AppLog;
import kr.co.ibks.platformteam.android.kkotpet.util.CommonUtil;

public class PaycoSDKAdapter extends SNSBaseAdapter {

    private CallbackManager callbackManager = null;

    public PaycoSDKAdapter(Context context, SNSInstance.SNSCallback snsCallback) {
        super(context, snsCallback);
    }

    @Override
    public void loginSNS() {
//        callJoin();
        callLogin();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PaycoLoginManager.getInstance().onActivityResult((Activity) context, requestCode, resultCode, data);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    // for login
    private void callLogin() {
        OnLoginListener onLoginListener = new OnLoginListener() {
            @Override
            public void onLogin(PaycoLoginExtraResult paycoLoginExtraResult) {
                AppLog.Debug("[PaycoLoginManager]login onLogin() call");

//                CommonUtil.showToast(context, R.string.login_msg);

                // TODO : 회원서버와 협의된 서비스에서 추가적으로 요구한 데이터 정보를 조회한다.
                // ex) servicePromotionReceiveYn : 서비스용 홍보성 정보 수신 동의(선택적)
                // 추가적인 정보 관련해서 안쓰는 서비스는 해당 정보를 파싱해서 사용하지 않는다.
                String servicePromotionReceiveYn = "";
                Hashtable<String, String> extraInfo = paycoLoginExtraResult.getExtraInfo();
                if (extraInfo.size() > 0) {
                    if (extraInfo.containsKey("servicePromotionReceiveYn")) {
                        servicePromotionReceiveYn = extraInfo.get("servicePromotionReceiveYn");
                    }
                }
//                CommonUtil.showToast(context, "servicePromotionReceiveYn:" + servicePromotionReceiveYn);

                processLoginSuccess();
            }

            @Override
            public void onCancel() {
                AppLog.Debug("[PaycoLoginManager]login onCancel()");

//                CommonUtil.showToast(context, R.string.cancel_msg);

                processLoginFail(context.getString(R.string.cancel_msg));
            }

            @Override
            public void onFail(PaycoLoginError error) {
                AppLog.Debug("[PaycoLoginManager]login onFail()");

//                CommonUtil.showToast(context, error.getDisplayMessage());

                processLoginFail(error.getDisplayMessage());
            }
        };

        PaycoLoginManager.getInstance().login((Activity) context, onLoginListener);
    }

    private void callJoin() {
        OnLoginListener onLoginListener = new OnLoginListener() {
            @Override
            public void onLogin(PaycoLoginExtraResult paycoLoginExtraResult) {
                AppLog.Debug("[PaycoLoginManager]join onLogin() call");

//                CommonUtil.showToast(context, R.string.login_msg);

                String servicePromotionReceiveYn = "";
                Hashtable<String, String> extraInfo = paycoLoginExtraResult.getExtraInfo();
                if (extraInfo.size() > 0) {
                    if (extraInfo.containsKey("servicePromotionReceiveYn")) {
                        servicePromotionReceiveYn = extraInfo.get("servicePromotionReceiveYn");
                    }
                }
//                CommonUtil.showToast(context, "servicePromotionReceiveYn:" + servicePromotionReceiveYn);

                processLoginSuccess();
            }

            @Override
            public void onCancel() {
                AppLog.Debug("[PaycoLoginManager]join onCancel()");

//                CommonUtil.showToast(context, R.string.cancel_msg);

                processLoginFail(context.getString(R.string.cancel_msg));
            }

            @Override
            public void onFail(PaycoLoginError error) {
                AppLog.Debug("[PaycoLoginManager]join onFail()");

//                CommonUtil.showToast(context, error.getDisplayMessage());

                processLoginFail(error.getDisplayMessage());
            }
        };

        PaycoLoginManager.getInstance().join((Activity) context, onLoginListener);
    }

    // for logout
    private void callLogout() {
        OnLogoutListener onLogoutListener = new OnLogoutListener() {
            @Override
            public void onLogout() {
                AppLog.Debug("[PaycoLoginManager]logout onLogout() call");

//                CommonUtil.showToast(context, R.string.logout_msg);

                processLoginSuccess();
            }

            @Override
            public void onFail(PaycoLoginError error) {
                AppLog.Debug("[PaycoLoginManager]logout onFail()");

                if (error.getErrorCode() == Errors.ERROR_NETWORK.getErrorCode()) {
//                    CommonUtil.showToast(context, error.getErrorMessage());
                    processLoginFail(error.getErrorMessage());
                } else {
//                    CommonUtil.showToast(context, error.getDisplayMessage());
                    processLoginFail(error.getDisplayMessage());
                }
            }
        };

        PaycoLoginManager.getInstance().logout(onLogoutListener);
    }

    /**
     * 로그인 성공 처리
     */
    private void processLoginSuccess() {
        AppLog.Debug("SDK Version:" + PaycoLoginSdkVersion.BUILD);

        if (isLogin()) {
            AppLog.Debug(PaycoLoginManager.getInstance().getLoginId());

            SNSUserInfoDTO userInfoDTO = new SNSUserInfoDTO();
            userInfoDTO.setId(PaycoLoginManager.getInstance().getLoginId());

            String token = PaycoLoginManager.getInstance().getAccessToken();
            String encToken = null;
            try {
                encToken = URLEncoder.encode(token, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            userInfoDTO.setAccessToken(encToken); // Access Token

            if (snsCallback != null) {
                snsCallback.SNSLoginSuccess(SNSInstance.SNS_TYPE_PAYCO, userInfoDTO);
            }
        } else {
            if (snsCallback != null) {
                snsCallback.SNSError(SNSInstance.SNS_TYPE_PAYCO, context.getString(R.string.cancel_msg));
            }
        }
    }

    /**
     * 로그인 실패 처리
     */
    private void processLoginFail(String errorMsg) {
        if (snsCallback != null) {
            snsCallback.SNSError(SNSInstance.SNS_TYPE_PAYCO, errorMsg);
        }
    }

    /**
     * 로그인 여부 확인
     *
     * @return
     */
    private boolean isLogin() {
        if (StringUtils.isNotBlank(PaycoLoginManager.getInstance().getAccessToken())) {
            return true;
        }

        return false;
    }
}
