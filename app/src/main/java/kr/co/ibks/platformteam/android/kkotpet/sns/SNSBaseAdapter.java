package kr.co.ibks.platformteam.android.kkotpet.sns;

import android.content.Context;
import android.content.Intent;

/**
 * @brief SNS Adapter 추상 클래스
 */
abstract public class SNSBaseAdapter {
    protected Context context = null;
    /**
     * @brief SNS 결과값 콜백 객체
     */
    protected SNSInstance.SNSCallback snsCallback = null;

    public SNSBaseAdapter(Context context, SNSInstance.SNSCallback snsCallback) {
        this.context = context;
        this.snsCallback = snsCallback;
    }

    /**
     * @brief SNS 로그인 요청
     */
    public abstract void loginSNS();

    /**
     * @brief SNS 로그인 완료 후 결과값 받음
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * @brief 화면 시작
     */
    public abstract void onStart();

    /**
     * 화면 종료
     */
    public abstract void onDestroy();

}
