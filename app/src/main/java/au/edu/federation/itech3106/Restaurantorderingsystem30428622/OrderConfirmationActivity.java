package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class OrderConfirmationActivity extends AppCompatActivity {
    public static final String CHANNEL1_ID = "channel_01";
    public static final int NOTIFICATION1_ID = 1;

    private static final int COUNTDOWN_TIME = 60 * 1000 * 45;
    private int remainingTime;
    private boolean isCountdownRunning = false;
    private Runnable countdownRunnable;

    private TextView tvOrderTotal;
    private TextView tvTimer;
    private MaterialButton btnSubmitOrder;
    private CircleView circleView;

    private Handler handler;
    private static final String PREFS_NAME = "OrderConfirmationPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Order Confirmation");
        }

        tvOrderTotal = findViewById(R.id.tv_order_total);
        tvTimer = findViewById(R.id.tv_timer);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);

        double totalPrice = getIntent().getDoubleExtra("total_price", 0.0);
        tvOrderTotal.setText(String.format("Total: $ %.2f", totalPrice));

        // 恢复 SharedPreferences 中的状态
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        remainingTime = preferences.getInt("remaining_time", COUNTDOWN_TIME / 1000);
        isCountdownRunning = preferences.getBoolean("is_countdown_running", false);

        handler = new Handler();
        initCountdown();

        btnSubmitOrder.setOnClickListener(v -> {
            startOrStopCountdown();
            showOrderSubmittedDialog();
        });

        if (isCountdownRunning) {
            handler.postDelayed(countdownRunnable, 1000);
        }

        // 添加 CircleView 到布局中
        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.order_confirmation_layout);
        mainLayout.addView(circleView);

        // 设置触摸监听器更新 CircleView
        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });
    }

    private void initCountdown() {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                int hours = remainingTime / 3600;
                int minutes = (remainingTime % 3600) / 60;
                int secs = remainingTime % 60;

                String time = String.format("%d:%02d:%02d", hours, minutes, secs);
                tvTimer.setText("Maximum waiting time: " + time);
                if (isCountdownRunning && remainingTime > 0) {
                    remainingTime--;
                }

                if (remainingTime <= 0) {
                    stopCountdown();
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void startOrStopCountdown() {
        if (!isCountdownRunning) {
            remainingTime = COUNTDOWN_TIME / 1000; // 重置剩余时间
            handler.postDelayed(countdownRunnable, 1000);
            isCountdownRunning = true;
        } else {
            stopCountdown();
        }
    }

    private void stopCountdown() {
        isCountdownRunning = false;
        handler.removeCallbacks(countdownRunnable);
    }

    private void showOrderSubmittedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Order Submitted")
                .setMessage("Your order has been successfully placed.")
                .setPositiveButton("OK", (dialog, which) -> sendOrderSubmittedNotification())
                .show();
    }

    private void sendOrderSubmittedNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 在Android 8.0及更高版本上创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL1_ID,
                    "Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for order status updates");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL1_ID)
                .setSmallIcon(R.drawable.xiaoxi)  // 确保此图标资源存在
                .setContentTitle("Order Submitted")
                .setContentText("Your order has been successfully placed.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true); // 用户点击通知后自动关闭通知

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION1_ID, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            navigateToHome();
            return true;
        } else if (id == R.id.menu_back) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Returning to Home", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 保存用户选择到 SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("remaining_time", remainingTime);
        editor.putBoolean("is_countdown_running", isCountdownRunning);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(countdownRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderConfirmationActivity.this, Fragment_DrinkSelectionActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Returning to Drink Selection", Toast.LENGTH_SHORT).show();
        finish();
    }
}
