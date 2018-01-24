package dev.studentmarket.studentmarket;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etEmail; // Email Field
    private EditText mDisplayDate; // D.O.B Field
    private DatePickerDialog.OnDateSetListener mDateSetListener; // D.O.B Listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Open date-picker for D.O.B field
        mDisplayDate = (EditText)findViewById(R.id.etDOB);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        // End D.O.B listener code

        // Verify email has correct format

        etEmail = (EditText)findViewById(R.id.etEmail);
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String strEmail = etEmail.getText().toString();

                if (!hasFocus) isEmailValid(strEmail);
            }

            public boolean isEmailValid(CharSequence email) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Log.d(TAG, "isEmailValid: YES");
                } else {
                    Log.d(TAG, "isEmailValid: NO");
                }
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
    }
}
