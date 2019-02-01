package kr.co.ibks.platformteam.android.kkotpet.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CommonUtil {
    /**
     * @brief 액티비티 활성화 상태인지
     */
    public static boolean isActivityContextAlive(Context context) {
        if (context == null
                || !(context instanceof Activity)
                || ((Activity) context).isFinishing()) {
            return false;
        }

        return true;
    }

    /**
     * @brief viewinflate를 통한 view 반환
     */
    public static View getViewInflater(Context context, int nResId, ViewGroup root) {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) context.getSystemService(infService);
        View v = li.inflate(nResId, root, false);

        return v;
    }

    /**
     * Toast 보이기
     *
     * @param context
     * @param resId
     */
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast 보이기
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    public static String getValueFromJson(String json, String key) {
        String value = null;
        try {
            JSONObject obj = new JSONObject(json);
            value = String.valueOf(obj.get(key));
        } catch (JSONException e) {
            Log.e("Exception", e.toString());
        }
        return value;
    }
}
