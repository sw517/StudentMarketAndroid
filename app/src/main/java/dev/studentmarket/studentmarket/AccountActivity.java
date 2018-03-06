package dev.studentmarket.studentmarket;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    private EditText etEmail; // Email Field
    private EditText mDisplayDate; // D.O.B Field
    private DatePickerDialog.OnDateSetListener mDateSetListener; // D.O.B Listener

    private String apiToken = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // PREVENT KEYBOARD AUTOMATICALLY POPPING UP
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // NEEDED FOR NAVIGATION MENU
        className =  this.getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // SWITCH ACTIVITY ON ITEM CLICK
                        String navTitle = menuItem.getTitle().toString();
                        if (navTitle.equals("All Items")) {
                            Intent intent = new Intent(className, ItemOverviewActivity.class);
                            startActivity(intent);
                        } else if (navTitle.equals("Profile")) {
                            Intent intent = new Intent(className, ProfileActivity.class);
                            startActivity(intent);
                        } else if (navTitle.equals("Account")) {
                            Intent intent = new Intent(className, AccountActivity.class);
                            startActivity(intent);
                        } else if (navTitle.equals("Logout")) {
                            Intent intent = new Intent(className, MainActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // OPEN FILES TO GET LOCALLY STORED API TOKEN
        apiToken = getAPIToken();

        getRequest("https://student-market.co.uk/api/profile?api_token=" + apiToken, "profile");

        // Open date-picker for D.O.B field
        mDisplayDate = (EditText)findViewById(R.id.accountDOB);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AccountActivity.this,
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
        etEmail = (EditText)findViewById(R.id.accountEmail);
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String strEmail = etEmail.getText().toString();

                if (!hasFocus) isEmailValid(strEmail);
            }

            public boolean isEmailValid(CharSequence email) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Log.d("Valid Email", "isEmailValid: YES");
                } else {
                    Log.d("Valid Email", "isEmailValid: NO");
                }
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });

    }

    /**
     * Allows navigation button to be pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Find local file containing API TOKEN
     */
    public String getAPIToken() {
        try {
            String fileString;
            FileInputStream fileInputStream = openFileInput("localAPIToken");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((fileString=bufferedReader.readLine()) != null) {
                stringBuffer.append(fileString);
                String apiToken = stringBuffer.toString();
                return apiToken;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Submits a GET request to the API to get user details
     */
    public void getRequest(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject json_response = new JSONObject(response);

                            processData(json_response.getBoolean("success"), json_response.getString("message"), json_response.getJSONObject("data"));

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

    /**
     * Here we process the data our API provides us with
     */
    public void processData(Boolean success, String message, JSONObject data) {

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

//      GET USER DATA
        try {
            String firstNameStr = data.getString("first_name");
            String lastNameStr = data.getString("last_name");
            String emailStr = data.getString("email");
            String dateOfBirthStr = data.getString("date_of_birth");

            EditText firstNameEt = (EditText) findViewById(R.id.accountFirstName);
            EditText lastNameEt = (EditText) findViewById(R.id.accountLastName);
            EditText emailEt = (EditText) findViewById(R.id.accountEmail);
            EditText dateOfBirthEt = (EditText) findViewById(R.id.accountDOB);

            firstNameEt.setText(firstNameStr);
            lastNameEt.setText(lastNameStr);
            emailEt.setText(emailStr);
            dateOfBirthEt.setText(dateOfBirthStr);

        } catch (JSONException e) {
            e.printStackTrace();
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
                            if (json_response.getBoolean("success")) {

                                Toast.makeText(AccountActivity.this, "Successfully updated account details",
                                        Toast.LENGTH_LONG).show();
                            }

//                            processData(json_response.getBoolean("success"), json_response.getString("message"), json_response.getJSONObject("data"));

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

    /**
     *  Called when the user presses login
     */
    public void updateAccount(View view) {
        apiToken = getAPIToken();
        EditText etFirstName = (EditText) findViewById(R.id.accountFirstName);
        EditText etLastName = (EditText) findViewById(R.id.accountLastName);
        EditText etEmail = (EditText) findViewById(R.id.accountEmail);
        EditText etDOB = (EditText) findViewById(R.id.accountDOB);

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String dob= etDOB.getText().toString();

        parameters.clear();
        parameters.put("first_name", firstName);
        parameters.put("last_name", lastName);
        parameters.put("email", email);
        parameters.put("date_of_birth", dob);
        postRequest("https://student-market.co.uk/api/profile?api_token=" + apiToken, "profile");
    }
}
