package kr.co.ibks.platformteam.android.kkotpet.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @brief Preferencs 손쉽게 사용하기 위해 제공
 */
public class PreferencesUtil {

    private SharedPreferences setting = null;
    private SharedPreferences.Editor editor = null;

    private String preferencesName = "";

    public PreferencesUtil(Context context, String name) {
        preferencesName = name;
        setting = context.getSharedPreferences(preferencesName, 0);
        editor = setting.edit();
    }

    public String getValue(String key, String defVal) {
        return setting.getString(key, defVal);
    }

    public int getValue(String key, int defVal) {
        return setting.getInt(key, defVal);
    }

    public float getValue(String key, float defVal) {
        return setting.getFloat(key, defVal);
    }

    public boolean getValue(String key, boolean defVal) {
        return setting.getBoolean(key, defVal);
    }

    public boolean setValue(String key, String val) {
        return editor.putString(key, val).commit();
    }

    public boolean setValue(String key, int val) {
        return editor.putInt(key, val).commit();
    }

    public boolean setValue(String key, float val) {
        return editor.putFloat(key, val).commit();
    }

    public boolean setValue(String key, boolean val) {
        return editor.putBoolean(key, val).commit();
    }

    public void clearAllData() {
        editor.clear().commit();
    }

    public void clearData(String key) {
        editor.remove(key).commit();
    }

}
