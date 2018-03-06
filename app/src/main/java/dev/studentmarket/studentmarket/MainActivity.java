package dev.studentmarket.studentmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // USED TO CHECK IF BACK BUTTON WAS PRESSED IN LOGIN SCREEN, IF SO - CLOSE APP
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finishAffinity();
        }

        Button registerUser = (Button) findViewById(R.id.bRegisterForm);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });
    }

    /**
     *  CLOSE APP IF BACK BUTTON PRESSED ON LOGIN SCREEN
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    /**
     *  Called when the user presses login
     */
    public void attemptLogin(View view) {
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        String email = etEmail.getText().toString();

        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        String password = etPassword.getText().toString();

        parameters.clear();
        parameters.put("email", email);
        parameters.put("password", password);
        postRequest("https://student-market.co.uk/api/login", "login");
    }

    /**
     * Here we process the data our API provides us with
     */
    public void processLogin(Boolean success, String message, JSONObject data) {
        Intent intent = new Intent(this, ItemOverviewActivity.class);
        String varAPIKEY = "";
        String varUserId = "";
        String varUserImg = "";
        String varUserName = "";

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

        // GET API TOKEN
        try {
            varAPIKEY = data.getString("api_token");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // GET User ID
        try {
            varUserId = data.getString("id");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // GET Img URL
        try {
            if (data.getString("profile_picture") == null) {
                varUserImg = "null";
            } else {
                varUserImg = data.getString("profile_picture");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Profile Image", varUserImg);

        // GET USERS NAME
        try {
            varUserName = data.getString("first_name") + data.getString("last_name");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // CREATE LOCAL FILE FOR API TOKEN
        String filename = "localAPIToken";
        String fileContents = varAPIKEY;
        FileOutputStream outputStreamAPI;

        try {
            outputStreamAPI = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStreamAPI.write(fileContents.getBytes());
            outputStreamAPI.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // CREATE LOCAL FILE FOR USER ID
        filename = "localUserId";
        fileContents = varUserId;
        FileOutputStream outputStreamId;

        try {
            outputStreamId = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStreamId.write(fileContents.getBytes());
            outputStreamId.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // CREATE LOCAL FILE FOR PROFILE IMAGE URL
        filename = "localUserImg";
        fileContents = varUserImg;
        FileOutputStream outputStreamImg;

        try {
            outputStreamImg = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStreamImg.write(fileContents.getBytes());
            outputStreamImg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // CREATE LOCAL FILE FOR User's Name
        filename = "localUserName";
        fileContents = varUserName;
        FileOutputStream outputStreamUserName;

        try {
            outputStreamUserName = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStreamUserName.write(fileContents.getBytes());
            outputStreamUserName.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // LOAD NEXT PAGE IF LOGIN SUCCESSFUL
        if (success) {
            startActivity(intent);
        } else {
            final TextView loginStatus = (TextView) findViewById(R.id.loginStatusText);

            loginStatus.setText(message);

            // CLOSE KEYBOARD WHEN LOGIN BUTTON PRESSED SO LOGIN STATUS IS VISIBLE
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            // Fade out animation
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            loginStatus.startAnimation(fadeOut);
            fadeOut.setDuration(1200);
            fadeOut.setFillAfter(true);
            fadeOut.setStartOffset(4200);
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
                            if (type.equals("login")) {
                                if(!json_response.getBoolean("success")) {
                                    Integer test = json_response.getInt("data");
                                    JSONObject data = new JSONObject(("{\"test\":\""+test.toString()+"\"}"));

                                    processLogin(json_response.getBoolean("success"), json_response.getString("message"), data);
                                } else {
                                    processLogin(json_response.getBoolean("success"), json_response.getString("message"), json_response.getJSONObject("data"));
                                }
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
