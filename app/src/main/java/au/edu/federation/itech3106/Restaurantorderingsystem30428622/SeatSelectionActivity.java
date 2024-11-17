package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Intent;
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

        // 恢复全局变量中的座位号
        AppGlobals appGlobals = (AppGlobals) getApplicationContext();
        if (!appGlobals.selectedSeatNumber.isEmpty()) {
            seatNumberInput.setText(appGlobals.selectedSeatNumber);
        }

        MaterialButton confirmButton = findViewById(R.id.btn_confirm_seat);
        confirmButton.setOnClickListener(v -> {
            String seatNumberText = seatNumberInput.getText().toString();
            if (seatNumberText.isEmpty() || Integer.parseInt(seatNumberText) > 28) {
                Toast.makeText(SeatSelectionActivity.this, "Enter an error. The seat number must be no larger than 28", Toast.LENGTH_SHORT).show();
            } else {
                // 保存座位号到全局变量
                appGlobals.selectedSeatNumber = seatNumberText;

                showConfirmationDialog(seatNumberText);
            }
        });

        // Adding CircleView to the layout
        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.seat_selection_layout);
        mainLayout.addView(circleView);

        // Setting touch listener to update CircleView
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
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Returning to Home", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SeatSelectionActivity.this, MainMenuActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Returning to Menu", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Show confirmation dialog
    private void showConfirmationDialog(String seatNumber) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Seat Selection")
                .setMessage("Are you sure you want to select seat number " + seatNumber + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    Intent intent = new Intent(SeatSelectionActivity.this, Fragment_DrinkSelectionActivity.class);
                    intent.putExtra("seat_number", seatNumber);
                    startActivity(intent);
                    finish(); // End current activity after navigation
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
