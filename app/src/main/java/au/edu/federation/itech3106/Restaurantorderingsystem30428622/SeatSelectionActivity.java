package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class SeatSelectionActivity extends AppCompatActivity {

    private EditText seatNumberInput;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String SEAT_SELECTION_KEY = "seat_selection";
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seat Selection");
        }

        seatNumberInput = findViewById(R.id.input_seat_number);
        restoreSelection();

        MaterialButton confirmButton = findViewById(R.id.btn_confirm_seat);
        confirmButton.setOnClickListener(v -> {
            String seatNumberText = seatNumberInput.getText().toString();
            if (seatNumberText.isEmpty() || Integer.parseInt(seatNumberText) > 28) {
                Toast.makeText(SeatSelectionActivity.this, "Enter an error. The seat number must be no larger than 28", Toast.LENGTH_SHORT).show();
            } else {
                showConfirmationDialog(seatNumberText);
            }
        });

        // 添加 CircleView 到布局中
        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.seat_selection_layout);
        mainLayout.addView(circleView);

        // 设置主布局的触摸监听，更新 CircleView
        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });
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
        saveSelection(getCurrentSelection());
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Returning to Home", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        saveSelection(getCurrentSelection());
        super.onBackPressed();
        Intent intent = new Intent(SeatSelectionActivity.this, MainMenuActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Returning to Menu", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveSelection(String selection) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SEAT_SELECTION_KEY, selection);
        editor.apply();
    }

    private void restoreSelection() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedSelection = sharedPreferences.getString(SEAT_SELECTION_KEY, "");
        if (!savedSelection.isEmpty()) {
            seatNumberInput.setText(savedSelection);
        }
    }

    private String getCurrentSelection() {
        return seatNumberInput.getText().toString();
    }

    // 显示确认对话框
    private void showConfirmationDialog(String seatNumber) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Seat Selection")
                .setMessage("Are you sure you want to select seat number " + seatNumber + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    saveSelection(seatNumber);
                    Intent intent = new Intent(SeatSelectionActivity.this, FoodSelectionActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
