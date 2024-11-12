package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



public class DrinkSelectionActivity extends AppCompatActivity {

    private static final double JUICE1_PRICE = 1.0;  // Drink 1 price
    private static final double JUICE2_PRICE = 2.0;  // Drink 2 price

    private EditText etCountJuice1;
    private EditText etCountJuice2;
    private TextView tvTotalPrice;
    private CheckBox checkboxSugar;
    private double foodTotal;

    private static final String PREFS_NAME = "DrinkSelectionPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Drink Selection");

        etCountJuice1 = findViewById(R.id.et_count_juice);
        etCountJuice2 = findViewById(R.id.et_count_lanmei);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        checkboxSugar = findViewById(R.id.checkbox_sugar);


        foodTotal = getIntent().getDoubleExtra("food_total", 0.0);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etCountJuice1.setText(preferences.getString("juice1_count", "0"));
        etCountJuice2.setText(preferences.getString("juice2_count", "0"));
        checkboxSugar.setChecked(preferences.getBoolean("sugar_checked", false));

        calculateTotalPrice();

        etCountJuice1.addTextChangedListener(new GenericTextWatcher());
        etCountJuice2.addTextChangedListener(new GenericTextWatcher());
        checkboxSugar.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());

        Button confirmButton = findViewById(R.id.btn_submit);
        confirmButton.setOnClickListener(v -> {
            double total = calculateTotalPrice();
            Intent intent = new Intent(DrinkSelectionActivity.this, OrderConfirmationActivity.class);
            intent.putExtra("total_price", total);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("juice1_count", etCountJuice1.getText().toString());
        editor.putString("juice2_count", etCountJuice2.getText().toString());
        editor.putBoolean("sugar_checked", checkboxSugar.isChecked());
        editor.putLong("food_total", Double.doubleToLongBits(foodTotal));
        editor.apply();
    }

    private double calculateTotalPrice() {
        int juice1Count = getCountFromEditText(etCountJuice1);
        int juice2Count = getCountFromEditText(etCountJuice2);

        double currentDrinkTotal = (juice1Count * JUICE1_PRICE) + (juice2Count * JUICE2_PRICE);

        if (checkboxSugar.isChecked()) {
            currentDrinkTotal += 0.5;
        }

        double newTotal = foodTotal + currentDrinkTotal;

        tvTotalPrice.setText(String.format("Total: $ %.2f", newTotal));
        return newTotal;
    }

    private int getCountFromEditText(EditText editText) {
        String inputText = editText.getText().toString();
        if (inputText.isEmpty()) {
            return 0;
        }
        try {
            int count = Integer.parseInt(inputText);
            return Math.max(0, count);
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
        Intent intent = new Intent(DrinkSelectionActivity.this, FoodSelectionActivity.class);
        startActivity(intent);
        finish();
    }
}




