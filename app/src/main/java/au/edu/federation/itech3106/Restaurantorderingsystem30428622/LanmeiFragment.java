package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LanmeiFragment extends Fragment {

    private static final double LANMEI_PRICE = 2.0;
    private EditText etCountLanmei;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lanmei, container, false);
        etCountLanmei = view.findViewById(R.id.et_count_lanmei);
        return view;
    }

    public double getLanmeiPrice() {
        if (etCountLanmei == null) {
            return 0.0;
        }

        String inputText = etCountLanmei.getText().toString().trim();
        if (inputText.isEmpty()) {
            return 0.0;
        }

        try {
            int count = Integer.parseInt(inputText);
            return Math.max(0, count) * LANMEI_PRICE;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

