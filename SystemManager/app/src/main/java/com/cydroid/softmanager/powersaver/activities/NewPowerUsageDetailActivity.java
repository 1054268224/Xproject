package com.cydroid.softmanager.powersaver.activities;

import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import com.cydroid.softmanager.R;
import com.cydroid.softmanager.utils.Log;
import com.cydroid.softmanager.BaseActivity;
import com.cydroid.softmanager.powersaver.fuelgauge.NewPowerUsageDetailFragment;

public class NewPowerUsageDetailActivity extends BaseActivity {
    private Bundle mBundle;

    public static final String ACTION_USAGE_DETAIL = "com.cydroid.softmanager.powersaver.notification.USAGE_DETAIL";
    public static final String TAG = "NewPowerUsageDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power_usage_details_layout);
        Intent intent = getIntent();
        mBundle = intent.getExtras();
        Log.d(TAG, "param = " + mBundle.getSerializable("drainType"));

        Fragment usageDetailFragment = new NewPowerUsageDetailFragment();
        usageDetailFragment.setArguments(mBundle);
        getFragmentManager().beginTransaction().replace(R.id.usage_detail, usageDetailFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}