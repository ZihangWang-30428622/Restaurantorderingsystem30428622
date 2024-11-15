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

public class Fragment_DrinkSelectionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DrinkSelectionPrefs";
    private static final double JUICE1_PRICE = 1.0;
    private static final double JUICE2_PRICE = 2.0;
    private static final double SUGAR_PRICE = 0.5;

    private EditText etCountJuice1;
    private EditText etCountJuice2;
    private TextView tvTotalPrice;
    private CheckBox checkboxSugar;
    private double foodTotal;

    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_drink_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Drink Selection");
        }

        // 初始化UI组件
        etCountJuice1 = findViewById(R.id.et_count_juice);
        etCountJuice2 = findViewById(R.id.et_count_lanmei);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        checkboxSugar = findViewById(R.id.checkbox_sugar);

        if (getIntent() != null) {
            foodTotal = getIntent().getDoubleExtra("food_total", 0.0);
        }

        // 恢复 SharedPreferences 中的保存数据
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etCountJuice1.setText(preferences.getString("juice1_count", ""));
        etCountJuice2.setText(preferences.getString("juice2_count", ""));
        checkboxSugar.setChecked(preferences.getBoolean("sugar_checked", false));

        // 设置 TextWatcher 监听输入变化
        etCountJuice1.addTextChangedListener(new GenericTextWatcher());
        etCountJuice2.addTextChangedListener(new GenericTextWatcher());
        checkboxSugar.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());

        // 确认按钮点击事件和确认对话框
        MaterialButton confirmButton = findViewById(R.id.btn_submit);
        confirmButton.setOnClickListener(v -> {
            double total = updateTotalPrice();

            // 显示确认对话框
            new AlertDialog.Builder(Fragment_DrinkSelectionActivity.this)
                    .setTitle("Confirm Order")
                    .setMessage(String.format("Your total is $ %.2f. Do you want to proceed?", total))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(Fragment_DrinkSelectionActivity.this, OrderConfirmationActivity.class);
                        intent.putExtra("total_price", total);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // 添加 CircleView 到布局中
        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.drink_selection_layout);
        mainLayout.addView(circleView);

        // 设置触摸监听器来更新 CircleView
        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });

        // 初次计算并显示总价
        updateTotalPrice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 保存用户选择到 SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("juice1_count", etCountJuice1.getText().toString());
        editor.putString("juice2_count", etCountJuice2.getText().toString());
        editor.putBoolean("sugar_checked", checkboxSugar.isChecked());
        editor.apply();
    }

    private double updateTotalPrice() {
        int juice1Count = getCountFromEditText(etCountJuice1);
        int juice2Count = getCountFromEditText(etCountJuice2);

        double currentDrinkTotal = (juice1Count * JUICE1_PRICE) + (juice2Count * JUICE2_PRICE);

        if (checkboxSugar.isChecked()) {
            currentDrinkTotal += SUGAR_PRICE;
        }

        double newTotal = foodTotal + currentDrinkTotal;

        if (tvTotalPrice != null) {
            tvTotalPrice.setText(String.format("Total: $ %.2f", newTotal));
        }

        return newTotal;
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
            updateTotalPrice();
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
        Intent intent = new Intent(Fragment_DrinkSelectionActivity.this, FoodSelectionActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Returning to Food Selection", Toast.LENGTH_SHORT).show();
    }
}
