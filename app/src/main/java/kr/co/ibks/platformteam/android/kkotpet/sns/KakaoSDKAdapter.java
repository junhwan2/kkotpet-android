package kr.co.ibks.platformteam.android.kkotpet.sns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.ibks.platformteam.android.kkotpet.util.AppLog;

/**
 * @brief Kakao sdk 연동 클래스
 */
public class KakaoSDKAdapter extends SNSBaseAdapter {

    private SessionCallback mKakaocallback = null;

    private boolean reqLogin = false;

    public KakaoSDKAdapter(Context context, SNSInstance.SNSCallback snsCallback) {
        super(context, snsCallback);
    }

    @Override
    public void loginSNS() {
        reqLogin = false;

//        if (KakaoSDK.getAdapter() == null)
//            KakaoSDK.init(new KakaoAdapterBridge());

        if (mKakaocallback == null) {
            // 카카오 세션을 오픈한다
            mKakaocallback = new SessionCallback();
            com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
            com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
            com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, (Activity) context);
        }
    }

    /**
     * @brief onActivityResult를 통해 결과값 전달
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onStart() {
        AppLog.Debug("onStart");
    }

    @Override
    public void onDestroy() {
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            AppLog.Debug("세션 오픈됨");

            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            requestMe();
//            requestSignUp();
            com.kakao.auth.Session.getCurrentSession().removeCallback(mKakaocallback);
            mKakaocallback = null;
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                AppLog.Debug(exception.getMessage());
                if (snsCallback != null) {
                    snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, exception.getLocalizedMessage());
                }
            }

            com.kakao.auth.Session.getCurrentSession().removeCallback(mKakaocallback);
            mKakaocallback = null;
        }
    }

//    private void requestSignUp() {
//
//        //콜백이 두번 호출 되는 문제가 있어서 예외 처리
//        if (reqLogin)
//            return;
//
//        reqLogin = true;
//
//        List<String> propertyKeys = new ArrayList<String>();
//        propertyKeys.add("kaccount_email");
//        propertyKeys.add("nickname");
//        propertyKeys.add("profile_image");
//        propertyKeys.add("thumbnail_image");
//
//        UserManagement.getInstance().requestSignup(new ApiResponseCallback<Long>() {
//            @Override
//            public void onNotSignedUp() {
//            }
//
//            @Override
//            public void onSuccess(Long result) {
//                requestMe();
//            }
//
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                int ErrorCode = errorResult.getErrorCode();
//                int ClientErrorCode = -777;
//
//                if (ErrorCode == ClientErrorCode) {
//                    if (snsCallback != null)
//                        snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.");
//                } else {
//                    if (snsCallback != null)
//                        snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, ErrorCode, errorResult.getErrorMessage());
//                }
//            }
//
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//                if (snsCallback != null) {
//                    snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, errorResult.getErrorCode(), errorResult.getErrorMessage());
//                }
//            }
//        }, propertyKeys);
//    }

    /**
     * @brief 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {

        //콜백이 두번 호출 되는 문제가 있어서 예외 처리
        if (reqLogin) {
            return;
        }

        reqLogin = true;

        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kakao_account.email");
        propertyKeys.add("properties.nickname");
        propertyKeys.add("properties.profile_image");
        propertyKeys.add("properties.thumbnail_image");

        UserManagement.getInstance().me(propertyKeys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    if (snsCallback != null) {
                        snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.");
                    }
                } else {
                    if (snsCallback != null) {
                        snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, ErrorCode, errorResult.getErrorMessage());
                    }
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if (snsCallback != null) {
                    snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, errorResult.getErrorCode(), errorResult.getErrorMessage());
                }
            }

            @Override
            public void onSuccess(MeV2Response response) {

                String profileUrl = response.getProfileImagePath();
                String userId = String.valueOf(response.getId());
                String userName = response.getNickname();


                SNSUserInfoDTO userInfoDTO = new SNSUserInfoDTO();
                userInfoDTO.setNickname(userName);
                userInfoDTO.setId(userId);
                userInfoDTO.setImgurl(profileUrl);
                userInfoDTO.setSnstype(SNSInstance.SNS_TYPE_KAKAO);
                userInfoDTO.setAccessToken(com.kakao.auth.Session.getCurrentSession().getAccessToken()); // Access Token

                if (!TextUtils.isEmpty(response.getKakaoAccount().getEmail())) {
                    userInfoDTO.setEmail(response.getKakaoAccount().getEmail());
                }

                if (snsCallback != null) {
                    snsCallback.SNSLoginSuccess(SNSInstance.SNS_TYPE_KAKAO, userInfoDTO);
                }

                com.kakao.auth.Session.getCurrentSession().close();

            }

//            @Override
//            public void onNotSignedUp() {
//                // 자동가입이 아닐경우 동의창
//            }
        });


//          UserManagement.requestMe(new MeResponseCallback() {
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                int ErrorCode = errorResult.getErrorCode();
//                int ClientErrorCode = -777;
//
//                if (ErrorCode == ClientErrorCode) {
//                    if (snsCallback != null)
//                        snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.");
//                } else {
//                    if (snsCallback != null)
//                        snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, ErrorCode, errorResult.getErrorMessage());
//                }
//            }
//
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//                if (snsCallback != null)
//                    snsCallback.SNSError(SNSInstance.SNS_TYPE_KAKAO, errorResult.getErrorCode(), errorResult.getErrorMessage());
//            }
//
//            @Override
//            public void onSuccess(UserProfile userProfile) {
//
//                String profileUrl = userProfile.getProfileImagePath();
//                String userId = String.valueOf(userProfile.getId());
//                String userName = userProfile.getNickname();
//
//
//                SNSUserInfoDTO userInfoVO = new SNSUserInfoDTO();
//                userInfoVO.setNickname(userName);
//                userInfoVO.setId(userId);
//                userInfoVO.setImgurl(profileUrl);
//                userInfoVO.setSnstype(SNSInstance.SNS_TYPE_KAKAO);
//                userInfoVO.setAccessToken(com.kakao.auth.Session.getCurrentSession().getAccessToken()); // Access Token
//
//                if (!TextUtils.isEmpty(userProfile.getEmail())
//                        && userProfile.getEmailVerified()) {
//                    userInfoVO.setEmail(userProfile.getEmail());
//                }
//
//                if (snsCallback != null) {
//                    snsCallback.SNSLoginSuccess(SNSInstance.SNS_TYPE_KAKAO, userInfoVO);
//                }
//
//                com.kakao.auth.Session.getCurrentSession().close();
//
//            }
//
//            @Override
//            public void onNotSignedUp() {
//                // 자동가입이 아닐경우 동의창
//            }
//        }, propertyKeys, false);
    }

//    class KakaoAdapterBridge extends KakaoAdapter {
//        @Override
//        public ISessionConfig getSessionConfig() {
//            return new ISessionConfig() {
//
//                @Override
//                public AuthType[] getAuthTypes() {
//                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
//                }
//
//                @Override
//                public boolean isUsingWebviewTimer() {
//                    return false;
//                }
//
//                @Override
//                public boolean isSecureMode() {
//                    return false;
//                }
//
//                @Override
//                public ApprovalType getApprovalType() {
//                    return ApprovalType.INDIVIDUAL;
//                }
//
//                @Override
//                public boolean isSaveFormData() {
//                    return true;
//                }
//            };
//        }
//
//        @Override
//        public IApplicationConfig getApplicationConfig() {
//            return new IApplicationConfig() {
//                @Override
//                public Context getApplicationContext() {
//                    return context.getApplicationContext();
//                }
//            };
//        }
//    }
}
