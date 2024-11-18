package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {

    public static final String CHANNEL2_ID = "channel_02";
    public static final int NOTIFICATION1_ID = 2;

    private TextView tvOrderTotal;
    private TextView tvSeatNumber;
    private LinearLayout layoutRecommendations;
    private EditText etPhone;
    private CheckBox cbNeedCutlery;
    private EditText etRemarks;
    private MaterialButton btnSubmitOrder;

    private AppGlobals appGlobals;
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_order_confirmation);

        // 检查是否是“取消订单”的操作
        if ("CANCEL_ORDER".equals(getIntent().getAction())) {
            resetOrderState();
            Toast.makeText(this, "Order has been canceled", Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Order Confirmation");
        }

        circleView = findViewById(R.id.circle_view);
        circleView.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });

        appGlobals = (AppGlobals) getApplicationContext();

        tvSeatNumber = findViewById(R.id.tv_seat_number);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        layoutRecommendations = findViewById(R.id.layout_recommendations);
        etPhone = findViewById(R.id.et_phone);
        cbNeedCutlery = findViewById(R.id.cb_need_cutlery);
        etRemarks = findViewById(R.id.et_remarks);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);

        // 检查订单状态
        checkOrderState();

        List<String> recommendations = getRecommendations(appGlobals);

        if (recommendations.isEmpty()) {
            TextView noRecommendations = new TextView(this);
            noRecommendations.setText("All items selected!");
            layoutRecommendations.addView(noRecommendations);
        } else {
            for (String item : recommendations) {
                addRecommendation(item);
            }
        }

        btnSubmitOrder.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String remarks = etRemarks.getText().toString();

            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Please enter a valid 11-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (countWords(remarks) > 30) {
                Toast.makeText(this, "Remarks must not exceed 30 words", Toast.LENGTH_SHORT).show();
                return;
            }

            appGlobals.customerPhone = phone;
            appGlobals.customerRemarks = remarks;
            appGlobals.needCutlery = cbNeedCutlery.isChecked();

            showOrderSubmittedDialog();
            sendOrderSubmittedNotification();
        });
    }

    private void checkOrderState() {
        if (appGlobals.totalPrice == 0.0) {
            tvOrderTotal.setText("Total: $ 0.00");
            etPhone.setText("");
            etRemarks.setText("");
            cbNeedCutlery.setChecked(false);
        } else {
            tvOrderTotal.setText(String.format("Total: $ %.2f", appGlobals.totalPrice));
            etPhone.setText(appGlobals.customerPhone);
            etRemarks.setText(appGlobals.customerRemarks);
            cbNeedCutlery.setChecked(appGlobals.needCutlery);
        }

        String seatNumber = appGlobals.selectedSeatNumber;
        if (seatNumber != null) {
            tvSeatNumber.setText("Seat Number: " + seatNumber);
        } else {
            tvSeatNumber.setText("Seat Number: Not Selected");
        }
    }

    private List<String> getRecommendations(AppGlobals appGlobals) {
        List<String> recommendedItems = new ArrayList<>();

        if (appGlobals.juice1Count.isEmpty() || Integer.parseInt(appGlobals.juice1Count) == 0) {
            recommendedItems.add("Juice 1");
        }
        if (appGlobals.juice2Count.isEmpty() || Integer.parseInt(appGlobals.juice2Count) == 0) {
            recommendedItems.add("Juice 2");
        }
        if (appGlobals.gongbaoCount.isEmpty() || Integer.parseInt(appGlobals.gongbaoCount) == 0) {
            recommendedItems.add("Gongbao Chicken");
        }
        if (appGlobals.yuxiangCount.isEmpty() || Integer.parseInt(appGlobals.yuxiangCount) == 0) {
            recommendedItems.add("Yuxiang Eggplant");
        }

        return recommendedItems;
    }

    private void addRecommendation(String item) {
        LinearLayout recommendationItem = findViewById(R.id.layout_recommendations);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        loadRecommendationImage(item, imageView);
        recommendationItem.addView(imageView);

        TextView textView = new TextView(this);
        textView.setText(item);
        textView.setTextSize(16);
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        recommendationItem.addView(textView);
    }

    private void loadRecommendationImage(String item, ImageView imageView) {
        int imageResId;

        switch (item) {
            case "Juice 1":
                imageResId = R.drawable.juice_image;
                break;
            case "Juice 2":
                imageResId = R.drawable.lanmei_image;
                break;
            case "Gongbao Chicken":
                imageResId = R.drawable.gongbao_image;
                break;
            case "Yuxiang Eggplant":
                imageResId = R.drawable.yuxiang_image;
                break;
            default:
                imageResId = R.drawable.dark;
                break;
        }

        imageView.setImageResource(imageResId);
    }

    private void showOrderSubmittedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Order Submitted")
                .setMessage("Your order has been successfully placed.")
                .setPositiveButton("OK", (dialog, which) -> navigateToRatingActivity())
                .show();
    }

    private void sendOrderSubmittedNotification() {
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL2_ID,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notifications for order status updates");
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
    }

    // 修复 View Order，不重新创建页面
    Intent viewOrderIntent = new Intent(this, OrderConfirmationActivity.class);
    viewOrderIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent viewOrderPendingIntent = PendingIntent.getActivity(
            this,
            0,
            viewOrderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    // 修复 Cancel Order，触发正确的 Action
    Intent cancelOrderIntent = new Intent(this, OrderConfirmationActivity.class);
    cancelOrderIntent.setAction("CANCEL_ORDER");
    cancelOrderIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent cancelOrderPendingIntent = PendingIntent.getActivity(
            this,
            1,
            cancelOrderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL2_ID)
            .setSmallIcon(R.drawable.xiaoxi)
            .setContentTitle("Order Submitted")
            .setContentText("Your order has been successfully placed.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .addAction(R.drawable.dark, "View Order", viewOrderPendingIntent)
            .addAction(R.drawable.dark, "Cancel Order", cancelOrderPendingIntent);

    notificationManager.notify(NOTIFICATION1_ID, builder.build());
}



    private void resetOrderState() {
        appGlobals.totalPrice = 0.0;
        appGlobals.customerPhone = "";
        appGlobals.customerRemarks = "";
        appGlobals.needCutlery = false;
        appGlobals.juice1Count = "0";
        appGlobals.juice2Count = "0";
        appGlobals.gongbaoCount = "0";
        appGlobals.yuxiangCount = "0";

        checkOrderState();
    }

    private void navigateToRatingActivity() {
        Intent intent = new Intent(OrderConfirmationActivity.this, RatingActivity.class);
        startActivity(intent);
    }

    private int countWords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }
        return input.trim().split("\\s+").length;
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{11}");
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
        } else if (id == R.id.menu_night_mode) {
            toggleNightMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleNightMode() {
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        recreate();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Returning to Home", Toast.LENGTH_SHORT).show();
    }

    protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    // 更新 Intent
    setIntent(intent);

    // 检查是否是取消订单的操作
    if ("CANCEL_ORDER".equals(intent.getAction())) {
        resetOrderState();
        Toast.makeText(this, "Order has been canceled", Toast.LENGTH_SHORT).show();
    }
}

}
