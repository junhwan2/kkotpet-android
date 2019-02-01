package kr.co.ibks.platformteam.android.kkotpet.sns;

import android.content.Context;
import android.content.Intent;

import kr.co.ibks.platformteam.android.kkotpet.util.AppLog;

/**
 * @brief SNS SDK를 하나의 instance로 제공
 */
public class SNSInstance {
    /**
     * @brief SNS 결과값 인터페이스
     */
    public interface SNSCallback {
        /**
         * @brief 로그인 완료
         */
        void SNSLoginSuccess(int nType, SNSUserInfoDTO userInfo);

        /**
         * @brief 로그인 에러
         */
        void SNSError(int nType, String sErrorMsg);

        /**
         * @brief 로그인 에
         */
        void SNSError(int nType, int sErrorCode, String sErrorMsg);
    }

    public static final String SNS_PARAM_FACEBOOK = "facebook";
    public static final String SNS_PARAM_KAKAO = "kakao";
    public static final String SNS_PARAM_NAVER = "naver";
    public static final String SNS_PARAM_PAYCO = "payco";

    /**
     * @brief SNS 타입 - 페이스북
     */
    public static final int SNS_TYPE_FACEBOOK = 10000;
    /**
     * @brief SNS 타입 - 카카오
     */
    public static final int SNS_TYPE_KAKAO = 10001;
    /**
     * @brief SNS 타입 - 네이버
     */
    public static final int SNS_TYPE_NAVER = 10002;
    /**
     * @brief SNS 타입 - 네이버
     */
    public static final int SNS_TYPE_PAYCO = 10003;
//    /**
//     * @brief SNS 타입 - 구글플러스
//     */
//    public static final int SNS_TYPE_GOOGLEP = 10004;
//    public static final int SNS_TYPE_TWITTER = 10005;
//
//    /**
//     * SNS 타입 - Facebook Account Kit
//     */
//    public static final int SNS_TYPE_FB_ACCOUNT = 10006;


    private Context context = null;

    /**
     * @brief SNS 타입
     */
    private int nSnsType = -1;

    /**
     * @brief SNS 결과값 콜백
     */
    private SNSCallback snsCallback = null;

    /**
     * @brief 현재 SNS 처리 Adapter
     */
    private SNSBaseAdapter snsAdapter = null;

    /**
     * @param context     Context 객체
     * @param nSnsType    SNS타입
     * @param snsCallback SNS 결과 콜백
     * @brief 생성자
     */
    public SNSInstance(Context context, int nSnsType, SNSCallback snsCallback) {
        this.context = context;
        this.nSnsType = nSnsType;
        this.snsCallback = snsCallback;

        createIns();
    }

    /**
     * @brief SNS Adapter 객체 생성
     */
    private void createIns() {
        switch (nSnsType) {
            case SNS_TYPE_FACEBOOK:
                snsAdapter = new FacebookSDKAdapter(context, snsCallback);
                break;
            case SNS_TYPE_KAKAO:
                snsAdapter = new KakaoSDKAdapter(context, snsCallback);
                break;
            case SNS_TYPE_NAVER:
                snsAdapter = new NaverSDKAdapter(context, snsCallback);
                break;
            case SNS_TYPE_PAYCO:
                snsAdapter = new PaycoSDKAdapter(context, snsCallback);
                break;
        }
    }

    /**
     * @brief SNS 로그인 요청
     */
    public void loginSNS() {
        if (snsAdapter != null) {
            snsAdapter.loginSNS();
        }
    }

    /**
     * @brief 다른 Activity에서 전달 받은 SNS 결과값
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (snsAdapter != null) {
            snsAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * @brief Activity onStart상태 전달
     */
    public void onStart() {
        if (snsAdapter != null) {
            snsAdapter.onStart();
        }
    }

    /**
     * Activity onDestroy 상태 전달
     */
    public void onDestroy() {
        if (snsAdapter != null) {
            snsAdapter.onDestroy();
        }
    }
}
