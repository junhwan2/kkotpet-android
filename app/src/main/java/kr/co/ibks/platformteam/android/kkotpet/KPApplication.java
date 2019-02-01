package kr.co.ibks.platformteam.android.kkotpet;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.toast.android.paycologin.EnvType;
import com.toast.android.paycologin.PaycoLoginManager;
import com.toast.android.paycologin.PaycoLoginManagerConfiguration;

public class KPApplication extends Application {

    public static Context context;

    private static final EnvType envType = EnvType.REAL;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();

        init();
    }

    private void init() {
        /* 카카오 */
        KakaoSDK.init(new KakaoSDKAdapter());

        /* 페이코 */
        PaycoLoginManagerConfiguration configuration = new PaycoLoginManagerConfiguration.Builder()
                .setServiceProviderCode(getString(R.string.payco_service_provider_code))
                .setClientId(getString(R.string.payco_client_id))
                .setClientSecret(getString(R.string.payco_client_secret))
                .setAppName(getResources().getString(R.string.app_name))
                .setEnvType(envType)
                //.setLangType(LangType.KOREAN)
                .setDebug(true)
                .build();

        PaycoLoginManager.getInstance().init(this, configuration);
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return context.getApplicationContext();
                }
            };
        }
    }
}
