package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Fragment_DrinkSelectionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DrinkSelectionPrefs";
    private static final String TAG = "DrinkSelectionActivity";

    private TextView tvTotalPrice;
    private CheckBox checkboxSugar;
    private double foodTotal;

    private JuiceFragment juiceFragment;
    private LanmeiFragment lanmeiFragment;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_drink_selection); // 确保布局文件正确

        // 初始化视图
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DrinkSelection");

        tvTotalPrice = findViewById(R.id.tv_total_price);
        checkboxSugar = findViewById(R.id.checkbox_sugar);

        // 获取数据
        if (getIntent() != null) {
            foodTotal = getIntent().getDoubleExtra("food_total", 0.0);
            Log.d(TAG, "Received food total: " + foodTotal);
        }

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        checkboxSugar.setChecked(preferences.getBoolean("sugar_checked", false));

        checkboxSugar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Checkbox changed: " + isChecked);
            updateTotalPrice();
        });

        Button confirmButton = findViewById(R.id.btn_submit);
        confirmButton.setOnClickListener(v -> {
            double total = updateTotalPrice();
            Intent intent = new Intent(Fragment_DrinkSelectionActivity.this, OrderConfirmationActivity.class);
            intent.putExtra("total_price", total);
            startActivity(intent);
        });

        // 初始化 Fragment
        juiceFragment = new JuiceFragment();
        lanmeiFragment = new LanmeiFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragmentManager.findFragmentById(R.id.juice_fragment_container) == null) {
            Log.d("aaa", "Adding JuiceFragment");
            fragmentTransaction.replace(R.id.juice_fragment_container, new JuiceFragment(), "JUICE_FRAGMENT");
        }

        if (fragmentManager.findFragmentById(R.id.lanmei_fragment_container) == null) {
            Log.d("aaa", "Adding LanmeiFragment");
            fragmentTransaction.replace(R.id.lanmei_fragment_container, new LanmeiFragment(), "LANMEI_FRAGMENT");
        }

        fragmentTransaction.commit();
        Log.d(TAG, "FragmentTransaction committed");

        // 注册广播接收器
        IntentFilter filter = new IntentFilter("SmartoNetBroadcastReceiver");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("广播名字") != null) {
                    Log.d(TAG, intent.getStringExtra("广播名字"));
                    finish(); // 结束当前 Activity
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sugar_checked", checkboxSugar.isChecked());
        editor.apply();
        Log.d(TAG, "Preferences saved");
    }

    private double updateTotalPrice() {
        double juicePrice = juiceFragment != null ? juiceFragment.getJuicePrice() : 0.0;
        double lanmeiPrice = lanmeiFragment != null ? lanmeiFragment.getLanmeiPrice() : 0.0;
        double currentDrinkTotal = juicePrice + lanmeiPrice + (checkboxSugar.isChecked() ? 0.5 : 0.0);
        double newTotal = foodTotal + currentDrinkTotal;

        if (tvTotalPrice != null) {
            tvTotalPrice.setText(String.format("Total: $%.2f", newTotal));
            Log.d(TAG, "Total price updated: " + newTotal);
        }

        // 发送广播，通知数据加载完成
        Intent intent = new Intent("SmartoNetBroadcastReceiver");
        intent.putExtra("广播名字", "数据加载完成");
        sendBroadcast(intent);

        return newTotal;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}


