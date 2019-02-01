package kr.co.ibks.platformteam.android.kkotpet.info;

import android.content.Context;

import kr.co.ibks.platformteam.android.kkotpet.util.PreferencesUtil;

/**
 * @brief 앱내 활동 정보
 */

public class AppActionInfo {

    //public final static String COACH_MARK_TYPE_HOME = "COACH_MARK_TYPE_HOME";
    public final static String COACH_MARK_TYPE_NEW_HOME = "COACH_MARK_TYPE_NEW_HOME";
    public final static String COACH_MARK_TYPE_SUGGEST = "COACH_MARK_TYPE_SUGGEST";
    public final static String COACH_MARK_TYPE_DIARY = "COACH_MARK_TYPE_DIARY";
    public final static String COACH_MARK_TYPE_FEED = "COACH_MARK_TYPE_FEED";

    private static AppActionInfo gIns = null;

    private static Context mContext;

    public static AppActionInfo getInstance(Context context) {

        mContext = context;

        if (gIns == null) {
            gIns = new AppActionInfo();
        }

        return gIns;
    }

    private PreferencesUtil preferences = null;
    private final String PREFERENCES_APP_ACTION_INFO = "PREFERENCES_APP_ACTION_INFO";

    private final String KEY_SHOW_WALK_THROUGH = "KEY_SHOW_WALK_THROUGH";

    private AppActionInfo() {
        preferences = new PreferencesUtil(mContext, PREFERENCES_APP_ACTION_INFO);
    }

    public void setShowWalkThrough(boolean isShow) {
        preferences.setValue(KEY_SHOW_WALK_THROUGH, isShow);
    }

    public boolean isShowWalkThrough() {
        return preferences.getValue(KEY_SHOW_WALK_THROUGH, false);
    }

    public void setShowCoachMark(String sType, boolean isShow) {
        preferences.setValue(sType, isShow);
    }

    public boolean isShowCoachMark(String sType) {
        return preferences.getValue(sType, false);
    }

    public void clearAllTermInfo() {
        preferences.clearAllData();
    }


}
