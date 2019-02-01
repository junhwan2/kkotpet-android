package kr.co.ibks.platformteam.android.kkotpet.config;

import kr.co.ibks.platformteam.android.kkotpet.BuildConfig;

public class AppConfig {
    public static boolean REAL_SETTING = BuildConfig.BUILD_TYPE.equals("debug") ? false : true;

    public final static String DOMAIN_URL_DEV = "https://dev.kkotpet.com";
    public final static String DOMAIN_URL_MAIN_DEV = "https://dev.kkotpet.com/main";

    public final static String DOMAIN_URL_REAL = "https://www.kkotpet.com";
    public final static String DOMAIN_URL_MAIN_REAL = "https://www.kkotpet.com/main";

    public final static String DOMAIN_URL = REAL_SETTING ? DOMAIN_URL_REAL : DOMAIN_URL_DEV;
    public final static String DOMAIN_URL_MAIN = REAL_SETTING ? DOMAIN_URL_MAIN_REAL : DOMAIN_URL_MAIN_DEV;

    public final static String DOMAIN_URL_S3 = "https://s3.ap-northeast-2.amazonaws.com/devfile.kkotpet.com";
    public final static String FACEBOOK_SHARE_URL = "https://m.facebook.com/sharer/sharer.php?u=https://devapi.kkotpet.com/web/share";
    public final static String FACEBOOK_SHARE_URL_2 = "https://devapi.kkotpet.com/web/share";

    public final static String USER_AGENT = "KKOTPETAPP";
}
