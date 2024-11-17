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



public class YuxiangFragment extends Fragment {

    private static final double YUXIANG_PRICE = 5.0;

    private EditText etYuxiangCount;
    private TextView tvYuxiangTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yuxiang, container, false);

        etYuxiangCount = view.findViewById(R.id.et_count_yuxiang);


        etYuxiangCount.addTextChangedListener(new GenericTextWatcher());

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
