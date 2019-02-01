package kr.co.ibks.platformteam.android.kkotpet.popupbridge;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @brief 웹뷰 브릿지 소스 (오픈소스)
 */

public class BrowserSwitchActivity extends Activity {

    private static Uri sReturnUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sReturnUri = null;
        if (getIntent() != null && getIntent().getData() != null) {
            sReturnUri = getIntent().getData();
        }

        finish();
    }

    /**
     * @return the uri returned from the browser switch, or {@code null}.
     */
    @Nullable
    public static Uri getReturnUri() {
        return sReturnUri;
    }

    /**
     * Clears the return uri.
     */
    public static void clearReturnUri() {
        sReturnUri = null;
    }
}
