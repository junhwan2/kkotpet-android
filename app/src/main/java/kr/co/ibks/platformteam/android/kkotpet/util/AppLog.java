package kr.co.ibks.platformteam.android.kkotpet.util;

import android.util.Log;

/**
 * @brief 앱로그
 */
public class AppLog {

    private final static String LOG_TAG = "APP_LOG";

    public static void Debug(String sLog)
    {
//        if (CommonUtil.APP_SERVER != CommonUtil.SERVER_TYPE_REAL) {
            Log.d(LOG_TAG, sLog);
//        }
    }

}
