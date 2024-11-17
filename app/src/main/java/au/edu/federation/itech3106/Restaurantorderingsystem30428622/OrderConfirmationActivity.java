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
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {

    public static final String CHANNEL1_ID = "channel_01";
    public static final int NOTIFICATION1_ID = 1;

    private TextView tvOrderTotal;
    private TextView tvSeatNumber; // 显示座位号
    private LinearLayout layoutRecommendations;
    private EditText etPhone;
    private CheckBox cbNeedCutlery;
    private EditText etRemarks;
    private MaterialButton btnSubmitOrder;
    private CircleView circleView;

    private AppGlobals appGlobals; // 全局变量实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Order Confirmation");
        }


        appGlobals = (AppGlobals) getApplicationContext(); // 初始化 AppGlobals

        tvSeatNumber = findViewById(R.id.tv_seat_number); // 获取座位号 TextView
        tvOrderTotal = findViewById(R.id.tv_order_total);
        layoutRecommendations = findViewById(R.id.layout_recommendations);
        etPhone = findViewById(R.id.et_phone);
        cbNeedCutlery = findViewById(R.id.cb_need_cutlery);
        etRemarks = findViewById(R.id.et_remarks);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);

        // 接收从 Intent 中传递的总价和座位号
        double totalPrice = getIntent().getDoubleExtra("total_price", 0.0);
        String seatNumber = getIntent().getStringExtra("seat_number");

        // 保存到全局变量
        appGlobals.totalPrice = totalPrice;
        appGlobals.selectedSeatNumber = seatNumber;

        // 显示总价和座位号
        tvOrderTotal.setText(String.format("Total: $ %.2f", totalPrice));
        tvSeatNumber.setText("Seat Number: " + (seatNumber != null ? seatNumber : "Not Selected"));

        // 获取推荐列表
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

            // 验证电话号码
            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Please enter a valid 11-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查备注是否超过30个单词
            if (countWords(remarks) > 30) {
                Toast.makeText(this, "Remarks must not exceed 30 words", Toast.LENGTH_SHORT).show();
                return;
            }

            // 保存用户输入到全局变量
            appGlobals.customerPhone = phone;
            appGlobals.customerRemarks = remarks;
            appGlobals.needCutlery = cbNeedCutlery.isChecked(); // 保存是否需要餐具

            showOrderSubmittedDialog();
            sendOrderSubmittedNotification();
        });

        // 添加 CircleView 到布局中
       // 初始化 CircleView 并添加到布局中
        circleView = findViewById(R.id.circle_view); // 假设 activity_main_menu.xml 已定义 CircleView
        circleView.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event); // 更新 CircleView 的触摸位置
            return true; // 返回 true 表示触摸事件已处理
        });
    }

    private List<String> getRecommendations(AppGlobals appGlobals) {
        List<String> recommendedItems = new ArrayList<>();

        // 检查未选择的项目并加入推荐列表
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

        // 添加图片
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        loadRecommendationImage(item, imageView);
        recommendationItem.addView(imageView);

        // 添加文字
        TextView textView = new TextView(this);
        textView.setText(item);
        textView.setTextSize(16);
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        recommendationItem.addView(textView);

        // 添加推荐项到推荐区域

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
                imageResId = R.drawable.dark; // 默认图片
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
                CHANNEL1_ID,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notifications for order status updates");
        notificationManager.createNotificationChannel(channel);
    }

    // 添加“查看订单”的Intent
    Intent viewOrderIntent = new Intent(this, OrderConfirmationActivity.class); // 回到当前Activity
    PendingIntent viewOrderPendingIntent = PendingIntent.getActivity(
            this,
            0,
            viewOrderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    // 添加“取消订单”的广播Intent
    Intent cancelOrderIntent = new Intent(this, OrderConfirmationActivity.class);
    cancelOrderIntent.setAction("ACTION_CANCEL_ORDER");
    PendingIntent cancelOrderPendingIntent = PendingIntent.getActivity(
            this,
            1,
            cancelOrderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    // 构建通知
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL1_ID)
            .setSmallIcon(R.drawable.xiaoxi)
            .setContentTitle("Order Submitted")
            .setContentText("Your order has been successfully placed.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .addAction(R.drawable.dark, "View Order", viewOrderPendingIntent) // 添加查看订单动作
            .addAction(R.drawable.dark, "Cancel Order", cancelOrderPendingIntent); // 添加取消订单动作

    notificationManager.notify(NOTIFICATION1_ID, builder.build());
}

// 重写 onNewIntent 方法，用于处理取消订单的广播动作
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if ("ACTION_CANCEL_ORDER".equals(intent.getAction())) {
        handleCancelOrder();
    }
}

// 处理取消订单的逻辑
private void handleCancelOrder() {
    Toast.makeText(this, "Order canceled successfully!", Toast.LENGTH_SHORT).show();

    // 重置全局变量
    appGlobals.totalPrice = 0.0;
    appGlobals.selectedSeatNumber = null;
    appGlobals.customerPhone = null;
    appGlobals.customerRemarks = null;
    appGlobals.needCutlery = false;

    // 更新UI或其他相关逻辑
    tvOrderTotal.setText("Total: $ 0.00");
    tvSeatNumber.setText("Seat Number: Not Selected");
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
}
