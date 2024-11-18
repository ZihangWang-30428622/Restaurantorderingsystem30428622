package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnSubmit;
    private Button btnReset;
    private EditText etFeedback;
    private TextView tvRatingDisplay;
    private TextView tvCharCount;

    public static final String CHANNEL1_ID = "channel_01";
    public static final int RATING_NOTIFICATION1_ID = 1;
    public static final String ACTION_RESET_RATING = "ACTION_RESET_RATING";
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_rating);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rating");
        }

        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.rating_layout);
        mainLayout.addView(circleView);

        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });

        initializeUI();

        AppGlobals appGlobals = (AppGlobals) getApplicationContext();
        restoreState(appGlobals);

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

                tvRatingDisplay.setText("Your rating: " + ratingValue + "\nYour feedback: " + feedback);
                showRatingSubmittedDialog(ratingValue, feedback);
                 Toast.makeText(RatingActivity.this, "Your rating and your feedback has been sumbitted sucessfully " , Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(RatingActivity.this, "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> handleResetRating(appGlobals));

        // 注册广播接收器
        registerReceiver(resetReceiver, new IntentFilter(ACTION_RESET_RATING));
    }



    private final BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_RESET_RATING.equals(intent.getAction())) {
                AppGlobals appGlobals = (AppGlobals) getApplicationContext();
                handleResetRating(appGlobals);
            }
        }
    };

    private void showRatingSubmittedDialog(float rating, String feedback) {
        new AlertDialog.Builder(this)
                .setTitle("Rating Submitted")
                .setMessage("Thank you for your rating!\n\n" +
                        "Rating: " + rating + " stars\n" +
                        "Feedback: " + feedback)
                .setPositiveButton("OK", null)
                .show();
    }

    private void handleResetRating(AppGlobals appGlobals) {
        ratingBar.setRating(0);
        etFeedback.setText("");
        tvRatingDisplay.setText("Your rating: 0");
        tvCharCount.setText("50 characters left");

        appGlobals.ratingValue = 0;
        appGlobals.feedbackText = "";

        Toast.makeText(this, "Rating has been reset.", Toast.LENGTH_SHORT).show();
    }

    private void initializeUI() {
        ratingBar = findViewById(R.id.ratingBar);
        etFeedback = findViewById(R.id.et_feedback);
        tvRatingDisplay = findViewById(R.id.tv_rating_display);
        tvCharCount = findViewById(R.id.tv_char_count);

        btnSubmit = findViewById(R.id.btn_submit);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void restoreState(AppGlobals appGlobals) {
        ratingBar.setRating(appGlobals.ratingValue);
        etFeedback.setText(appGlobals.feedbackText);

        tvRatingDisplay.setText("Your rating: " + appGlobals.ratingValue +
                                "\nYour feedback: " + appGlobals.feedbackText);

        int remainingChars = 50 - appGlobals.feedbackText.length();
        tvCharCount.setText(remainingChars + " characters left");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(resetReceiver);
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
        } else if (id == R.id.menu_night_mode) {
            toggleNightMode();
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

     private void toggleNightMode() {
    // 不再依赖 SharedPreferences，直接切换模式
    int currentMode = AppCompatDelegate.getDefaultNightMode();
    if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // 切换到日间模式
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // 切换到夜间模式
    }
    recreate(); // 重新创建活动以应用主题变化
}
}