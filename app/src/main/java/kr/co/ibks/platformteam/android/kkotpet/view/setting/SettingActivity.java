package kr.co.ibks.platformteam.android.kkotpet.view.setting;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ibks.platformteam.android.kkotpet.R;

/**
 * 접근권한 설정 Activity
 */
public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.locationSwitch)
    Switch locationSwitch;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();
    }

    /**
     * 뷰 초기화
     */
    private void initView() {
        // 위치정보 권한 체크
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            locationSwitch.setChecked(false);
        } else {
            locationSwitch.setChecked(true);
        }
    }

    @OnClick(R.id.prevButton)
    public void onPrevButtonClick() {
        finish();
        overridePendingTransition(0, 0);
    }

    @OnClick(R.id.locationSwitch)
    public void onLocationSwitchClick() {
        // Switch Click 시 바로 Check 값이 변경되지 않도록 설정
        if (locationSwitch.isChecked()) {
            locationSwitch.setChecked(false);
        } else {
            locationSwitch.setChecked(true);
        }

        // 앱 설정 화면으로 이동
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_notice)
                .setMessage(R.string.alert_setting_location)
                .setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startApplicationDetailSetting();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Application 세부 설정 화면으로 이동
     */
    private void startApplicationDetailSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}
