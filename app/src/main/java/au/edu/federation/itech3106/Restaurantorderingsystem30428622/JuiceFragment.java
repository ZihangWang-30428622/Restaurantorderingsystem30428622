package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JuiceFragment extends Fragment {

    private static final double JUICE_PRICE = 1.0;
    private EditText etCountJuice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_juice, container, false);
        etCountJuice = view.findViewById(R.id.et_count_juice);
        return view;
    }

    public double getJuicePrice() {
        if (etCountJuice == null) {
            return 0.0;
        }

        String inputText = etCountJuice.getText().toString().trim();
        if (inputText.isEmpty()) {
            return 0.0;
        }

        try {
            int count = Integer.parseInt(inputText);
            return Math.max(0, count) * JUICE_PRICE;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
