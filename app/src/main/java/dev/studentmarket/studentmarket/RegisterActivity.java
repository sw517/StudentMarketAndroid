package dev.studentmarket.studentmarket;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etEmail; // Email Field
    private EditText mDisplayDate; // D.O.B Field
    private DatePickerDialog.OnDateSetListener mDateSetListener; // D.O.B Listener
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

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


    /**
     *  Called when the user presses register
     */
    public void attemptRegistration(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        parameters.clear();
        parameters.put("first_name", "Mr");
        parameters.put("last_name", "Denzil");
        parameters.put("email", "denzil@deniz.tr");
        parameters.put("date_of_birth", "1996-05-27");
        parameters.put("password", "sam_smells");
        parameters.put("password_confirmation", "sam_smells");

        postRequest("http://f872b8a3.ngrok.io/api/register", "register");

        startActivity(intent);
    }

    /**
     * Here we process the data our API provides us with
     */
    public void processRegistration(JSONObject data) {
        try {
            Log.d("Data", "Hey, " + data.getString("first_name") + "!!!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postRequest(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject json_response = new JSONObject(response);
                            if (type.equals("register")) {
                                processRegistration(new JSONObject(json_response.getString("data")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override // We have to override here so that our own parameters are used
            protected Map<String, String> getParams()
            {
                return parameters;
            }
        };
        queue.add(request);
    }
}
