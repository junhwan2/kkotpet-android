package kr.co.ibks.platformteam.android.kkotpet.common;

import android.os.SystemClock;
import android.view.View;

/**
 * @brief 연속 클릭 방지 클릭 리스너
 */

public abstract class OnSingleClickListener implements View.OnClickListener {
    // 중복 클릭 방지 시간 설정
    private static final long MIN_CLICK_INTERVAL = 500;

    private long mLastClickTime;

    private int nCurClickViewId = -1;

    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        // 중복 클릭인 경우
        if (nCurClickViewId == v.getId()
                && elapsedTime <= MIN_CLICK_INTERVAL) {
            return;
        }

        nCurClickViewId = v.getId();
        // 중복 클릭아 아니라면 추상함수 호출
        onSingleClick(v);
    }

}