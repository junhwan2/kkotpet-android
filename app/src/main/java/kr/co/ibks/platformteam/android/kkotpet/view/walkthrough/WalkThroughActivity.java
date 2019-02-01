package kr.co.ibks.platformteam.android.kkotpet.view.walkthrough;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ibks.platformteam.android.kkotpet.R;
import kr.co.ibks.platformteam.android.kkotpet.common.BaseActivity;
import kr.co.ibks.platformteam.android.kkotpet.common.OnSingleClickListener;
import kr.co.ibks.platformteam.android.kkotpet.info.AppActionInfo;

public class WalkThroughActivity extends BaseActivity {

    @BindView(R.id.btn_Start)
    Button btnStart;

    @BindView(R.id.pager_WalkThrough)
    ViewPager pager;

    private WalkThroughPagerAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);
        ButterKnife.bind(this);

        adapter = new WalkThroughPagerAdapter(this);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnStart.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    @OnClick(R.id.btn_Start)
    public void finishActivity() {
        AppActionInfo.getInstance(getApplicationContext()).setShowWalkThrough(true);
        finish();
    }
}
