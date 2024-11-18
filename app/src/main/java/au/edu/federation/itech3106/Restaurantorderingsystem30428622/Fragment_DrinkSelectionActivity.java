package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class Fragment_DrinkSelectionActivity extends AppCompatActivity {

    // Price constants
    private static final double JUICE1_PRICE = 1.0;
    private static final double JUICE2_PRICE = 2.0;
    private static final double GONGBAO_PRICE = 4.0;
    private static final double YUXIANG_PRICE = 5.0;
    private static final double SUGAR_PRICE = 0.5;

    // UI elements
    private EditText etCountJuice1;
    private EditText etCountJuice2;
    private EditText etCountGongbao;
    private EditText etCountYuxiang;
    private TextView tvTotalPrice;
    private CheckBox checkboxSugar;

    // Other variables
    private double foodTotal;
    private CircleView circleView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
}

        setContentView(R.layout.activity_seat_selection);
        setContentView(R.layout.activity_fragment_drink_selection);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Selection");
        }

          // 初始化 CircleView 并添加到布局中
        circleView = findViewById(R.id.circle_view); // 假设 activity_main_menu.xml 已定义 CircleView
        circleView.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event); // 更新 CircleView 的触摸位置
            return true; //03125641 返回 true 表示触摸事件已处理
        });

        // Initialize UI elements
        etCountJuice1 = findViewById(R.id.et_count_juice);
        etCountJuice2 = findViewById(R.id.et_count_lanmei);
        etCountGongbao = findViewById(R.id.et_count_gongbao);
        etCountYuxiang = findViewById(R.id.et_count_yuxiang);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        checkboxSugar = findViewById(R.id.checkbox_sugar);

        // Set input range (0-20) for EditTexts
        InputFilterMinMax inputFilter = new InputFilterMinMax(0, 20);
        etCountJuice1.setFilters(new InputFilter[]{inputFilter});
        etCountJuice2.setFilters(new InputFilter[]{inputFilter});
        etCountGongbao.setFilters(new InputFilter[]{inputFilter});
        etCountYuxiang.setFilters(new InputFilter[]{inputFilter});

        // Get food total from intent
        if (getIntent() != null) {
            foodTotal = getIntent().getDoubleExtra("food_total", 0.0);
        }

        // Add TextWatchers for real-time price updates
        addTextWatcher(etCountJuice1);
        addTextWatcher(etCountJuice2);
        addTextWatcher(etCountGongbao);
        addTextWatcher(etCountYuxiang);

        // Update price when sugar checkbox changes
        checkboxSugar.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());

        // Handle submit button click
        MaterialButton confirmButton = findViewById(R.id.btn_submit);
        confirmButton.setOnClickListener(v -> {
            double total = updateTotalPrice();

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

        updateTotalPrice();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save global state
        AppGlobals appGlobals = (AppGlobals) getApplicationContext();

        appGlobals.juice1Count = etCountJuice1.getText().toString();
        appGlobals.juice2Count = etCountJuice2.getText().toString();
        appGlobals.gongbaoCount = etCountGongbao.getText().toString();
        appGlobals.yuxiangCount = etCountYuxiang.getText().toString();
        appGlobals.sugarChecked = checkboxSugar.isChecked();

        appGlobals.totalPrice = updateTotalPrice();
    }

    private double updateTotalPrice() {
        int juice1Count = getCountFromEditText(etCountJuice1);
        int juice2Count = getCountFromEditText(etCountJuice2);
        int gongbaoCount = getCountFromEditText(etCountGongbao);
        int yuxiangCount = getCountFromEditText(etCountYuxiang);

        double currentDrinkTotal = (juice1Count * JUICE1_PRICE) + (juice2Count * JUICE2_PRICE);
        double currentFoodTotal = (gongbaoCount * GONGBAO_PRICE) + (yuxiangCount * YUXIANG_PRICE);

        if (checkboxSugar.isChecked()) {
            currentDrinkTotal += SUGAR_PRICE;
        }

        double newTotal = foodTotal + currentDrinkTotal + currentFoodTotal;

        tvTotalPrice.setText(String.format("Total: $ %.2f", newTotal));

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

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update total price in real-time
                updateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
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
        } else if (id == R.id.menu_night_mode) {
            toggleNightMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void navigateToHome() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Returning to Home", Toast.LENGTH_SHORT).show();
    }

    // InputFilter for limiting min and max values
    private static class InputFilterMinMax implements InputFilter {
        private final int min;
        private final int max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                String newVal = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
                int input = Integer.parseInt(newVal);
                if (isInRange(min, max, input)) {
                    return null;
                }
            } catch (NumberFormatException e) {
                // Do nothing
            }
            return "";
        }

        private boolean isInRange(int min, int max, int value) {
            return value >= min && value <= max;
        }


    }
}
