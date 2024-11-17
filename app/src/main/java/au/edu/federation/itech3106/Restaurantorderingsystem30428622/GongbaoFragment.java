package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class GongbaoFragment extends Fragment {

    private static final double GONGBAO_PRICE = 4.0;

    private EditText etGongbaoCount;
    private TextView tvGongbaoTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gongbao, container, false);

        etGongbaoCount = view.findViewById(R.id.et_count_gongbao);


        etGongbaoCount.addTextChangedListener(new GenericTextWatcher());

        return view;
    }



    private int getCountFromEditText(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private class GenericTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
