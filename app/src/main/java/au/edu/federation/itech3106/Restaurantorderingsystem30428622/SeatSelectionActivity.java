package au.edu.federation.itech3106.Restaurantorderingsystem30428622;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



public class SeatSelectionActivity extends AppCompatActivity {

    private EditText seatNumberInput;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String SEAT_SELECTION_KEY = "seat_selection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        getSupportActionBar().setTitle("SeatSelection");

        seatNumberInput = findViewById(R.id.input_seat_number);


        restoreSelection();

        Button confirmButton = findViewById(R.id.btn_confirm_seat);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seatNumberText = seatNumberInput.getText().toString();
                if (seatNumberText.isEmpty() || Integer.parseInt(seatNumberText) > 28) {
                    Toast.makeText(SeatSelectionActivity.this, "Enter an error. The seat number must be no larger than 28", Toast.LENGTH_SHORT).show();
                } else {
                    saveSelection(seatNumberText);
                    Intent intent = new Intent(SeatSelectionActivity.this, FoodSelectionActivity.class);
                    startActivity(intent);
                    finish(); // 结束当前 Activity A
                }
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
}























