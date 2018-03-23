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
import android.widget.Toast;

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
//                String date = day + "/" + month + "/" + year;
                String date = year + "-" + month + "-" + day; // MUST BE THIS FORMAT OTHERWISE API DOESNT WORK
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

        EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
        String firstName = etFirstName.getText().toString();
        Log.d("FirstName", firstName);

        if (firstName.matches("")) {
            Toast.makeText(this, "You did not enter a first name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etLastName = (EditText) findViewById(R.id.etLastName);
        String lastName = etLastName.getText().toString();
        Log.d("LastName", lastName);

        if (lastName.matches("")) {
            Toast.makeText(this, "You did not enter a last name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        String email = etEmail.getText().toString();
        Log.d("Email", email);

        if (email.matches("")) {
            Toast.makeText(this, "You did not enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etDOB = (EditText) findViewById(R.id.etDOB);
        String DOB = etDOB.getText().toString();
        Log.d("DOB", DOB);

        if (DOB.matches("")) {
            Toast.makeText(this, "You did not enter a date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        String password = etPassword.getText().toString();
        Log.d("Password", password);

        if (password.matches("")) {
            Toast.makeText(this, "You did not enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        String confirmPassword = etConfirmPassword.getText().toString();
        Log.d("PasswordConfirm", confirmPassword);

        if (confirmPassword.matches("")) {
            Toast.makeText(this, "You did not confirm the password", Toast.LENGTH_SHORT).show();
            return;
        }

        parameters.clear();
        parameters.put("first_name", firstName);
        parameters.put("last_name", lastName);
        parameters.put("email", email);
        parameters.put("date_of_birth", DOB);
        parameters.put("password", password);
        parameters.put("password_confirmation", confirmPassword);

        postRequest("https://student-market.co.uk/api/register", "register");

    }

    /**
     * Here we process the data our API provides us with
     */
    public void processRegistration(Boolean success, String message, JSONObject data) {

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

        if (success) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Account successfully registered, please login", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Registration unsuccessful, email may be in use", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Submits a POST request to the API
     */
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
                                processRegistration(json_response.getBoolean("success"), json_response.getString("message"), json_response.getJSONObject("data"));

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
                        showErrorToast();
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

    public void showErrorToast() {
        Toast.makeText(this, "Unsuccessful: email may be in use", Toast.LENGTH_LONG).show();
    }
}
