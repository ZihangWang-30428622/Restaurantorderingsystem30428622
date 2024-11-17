package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnSubmit;
    private Button btnReset; // Reset Button
    private EditText etFeedback;
    private TextView tvRatingDisplay;
    private TextView tvCharCount; // TextView to display remaining characters

    public static final String CHANNEL1_ID = "channel_01";
    public static final int NOTIFICATION1_ID = 1;
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rating");

        checkNotificationPermission();
        initializeUI();

        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.rating_layout);
        mainLayout.addView(circleView);

        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });

        ratingBar = findViewById(R.id.ratingBar);
        etFeedback = findViewById(R.id.et_feedback);
        btnSubmit = findViewById(R.id.btn_submit);
        btnReset = findViewById(R.id.btn_reset);
        tvRatingDisplay = findViewById(R.id.tv_rating_display);
        tvCharCount = findViewById(R.id.tv_char_count);

        AppGlobals appGlobals = (AppGlobals) getApplicationContext();

        etFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                int remainingChars = 50 - charSequence.length();
                tvCharCount.setText(remainingChars + " characters left");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSubmit.setOnClickListener(v -> {
            float ratingValue = ratingBar.getRating();
            String feedback = etFeedback.getText().toString();

            if (ratingValue > 0) {
                appGlobals.ratingValue = ratingValue;
                appGlobals.feedbackText = feedback;

                // 显示弹窗


                // 发送通知
                sendRatingSubmittedNotification();
            } else {
                Toast.makeText(RatingActivity.this, "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> handleResetRating());
    }

    private void sendRatingSubmittedNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL1_ID,
                    "Rating Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for rating submission");
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.BLUE);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setBypassDnd(true); // 确保可以绕过勿扰模式
            notificationManager.createNotificationChannel(channel);
        }

        Intent fullScreenIntent = new Intent(this, RatingActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL1_ID)
                .setSmallIcon(R.drawable.xiaoxi)
                .setContentTitle("Rating Submitted")
                .setContentText("Your rating has been successfully submitted.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true); // 强制悬浮通知

        notificationManager.notify(NOTIFICATION1_ID, builder.build());
    }

    private void showRatingSubmittedDialog(float rating, String feedback) {
        new AlertDialog.Builder(this)
                .setTitle("Rating Submitted")
                .setMessage("Thank you for your rating!\n\n" +
                        "Rating: " + rating + " stars\n" +
                        "Feedback: " + feedback)
                .setPositiveButton("OK", (dialog, which) -> {
                    handleResetRating();
                })
                .show();
    }

    private void handleResetRating() {
        ratingBar.setRating(0);
        etFeedback.setText("");
        tvRatingDisplay.setText("Your rating: 0");
        tvCharCount.setText("50 characters left");

        AppGlobals appGlobals = (AppGlobals) getApplicationContext();
        appGlobals.ratingValue = 0;
        appGlobals.feedbackText = "";

        Toast.makeText(this, "Rating has been reset.", Toast.LENGTH_SHORT).show();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void initializeUI() {
        ratingBar = findViewById(R.id.ratingBar);
        etFeedback = findViewById(R.id.et_feedback);
        tvRatingDisplay = findViewById(R.id.tv_rating_display);
        tvCharCount = findViewById(R.id.tv_char_count);

        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> sendRatingSubmittedNotification());

        Button btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> handleResetRating());
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
}
