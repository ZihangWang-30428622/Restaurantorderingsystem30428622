package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class FoodSelectionActivity extends AppCompatActivity {
    private static final double GONGBAO_PRICE = 4.0;
    private static final double YUXIANG_PRICE = 5.0;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String COUNT_GONGBAO_KEY = "count_gongbao";
    private static final String COUNT_YUXIANG_KEY = "count_yuxiang";
    private static final String CHECKBOX_RICE_KEY = "checkbox_rice";
    private static final String TOTAL_PRICE_KEY = "total_price";

    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Food Selection");
        }

        EditText etCountGongbao = findViewById(R.id.et_count_gongbao);
        EditText etCountYuxiang = findViewById(R.id.et_count_yuxiang);
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);
        CheckBox checkboxRice = findViewById(R.id.checkbox_sugar);

        // 添加 CircleView 到布局中
        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.food_selection_layout);
        mainLayout.addView(circleView);

        // 恢复 SharedPreferences 中保存的数据
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etCountGongbao.setText(prefs.getString(COUNT_GONGBAO_KEY, ""));
        etCountYuxiang.setText(prefs.getString(COUNT_YUXIANG_KEY, ""));
        checkboxRice.setChecked(prefs.getBoolean(CHECKBOX_RICE_KEY, false));
        tvTotalPrice.setText(String.format("Total: $ %.2f", prefs.getFloat(TOTAL_PRICE_KEY, 0.0f)));

        calculateTotalPrice();

        // 添加触摸监听器来更新 CircleView 显示红圈
        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });

        // 设置 EditText 和 CheckBox 的监听事件
        etCountGongbao.addTextChangedListener(new GenericTextWatcher());
        etCountYuxiang.addTextChangedListener(new GenericTextWatcher());
        checkboxRice.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());

        // 确认按钮的点击事件，显示确认对话框
        MaterialButton confirmButton = findViewById(R.id.btn_submit);
        confirmButton.setOnClickListener(v -> {
            double currentTotal = calculateTotalPrice();

            // 显示确认对话框
            new AlertDialog.Builder(FoodSelectionActivity.this)
                    .setTitle("Confirm Order")
                    .setMessage(String.format("Your total is $ %.2f. Do you want to proceed?", currentTotal))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(FoodSelectionActivity.this, Fragment_DrinkSelectionActivity.class);
                        intent.putExtra("food_total", currentTotal);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        calculateTotalPrice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 保存用户输入和总价到 SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        EditText etCountGongbao = findViewById(R.id.et_count_gongbao);
        EditText etCountYuxiang = findViewById(R.id.et_count_yuxiang);
        CheckBox checkboxRice = findViewById(R.id.checkbox_sugar);

        editor.putString(COUNT_GONGBAO_KEY, etCountGongbao.getText().toString());
        editor.putString(COUNT_YUXIANG_KEY, etCountYuxiang.getText().toString());
        editor.putBoolean(CHECKBOX_RICE_KEY, checkboxRice.isChecked());
        editor.putFloat(TOTAL_PRICE_KEY, (float) calculateTotalPrice());
        editor.apply();
    }

    private double calculateTotalPrice() {
        EditText etCountGongbao = findViewById(R.id.et_count_gongbao);
        EditText etCountYuxiang = findViewById(R.id.et_count_yuxiang);
        CheckBox checkboxRice = findViewById(R.id.checkbox_sugar);
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);

        int gongbaoCount = getCountFromEditText(etCountGongbao);
        int yuxiangCount = getCountFromEditText(etCountYuxiang);
        double currentTotal = (gongbaoCount * GONGBAO_PRICE) + (yuxiangCount * YUXIANG_PRICE);

        if (checkboxRice.isChecked()) {
            currentTotal += 1.0;
        }

        tvTotalPrice.setText(String.format("Total: $ %.2f", currentTotal));
        return currentTotal;
    }

    private int getCountFromEditText(EditText editText) {
        String inputText = editText.getText().toString();
        if (inputText.isEmpty()) {
            return 0;
        }
        try {
            return Math.max(0, Integer.parseInt(inputText));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private class GenericTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            calculateTotalPrice();
        }

        @Override
        public void afterTextChanged(Editable s) {}
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FoodSelectionActivity.this, SeatSelectionActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Returning to Seat Selection", Toast.LENGTH_SHORT).show();
        finish();
    }
}
