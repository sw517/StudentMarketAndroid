package dev.studentmarket.studentmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
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

        Button registerUser = (Button) findViewById(R.id.bRegisterForm);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });
    }

    // CLOSE APP IF BACK BUTTON PRESSED ON LOGIN SCREEN
    @Override
    public void onBackPressed() {
        this.finishAndRemoveTask();
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
    public void processLogin(Boolean success, String message, Object data, JSONObject data2) {
        Intent intent = new Intent(this, ItemOverviewActivity.class);
        String varAPIKEY = "";

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

        // GET API TOKEN
        try {
            varAPIKEY = data2.getString("api_token");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // CREATE LOCAL FILE FOR API TOKEN
        String filename = "localAPIToken";
        String fileContents = varAPIKEY;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.d("FileCreation", "File Created");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            startActivity(intent);
        } else {
            final TextView loginStatus = (TextView) findViewById(R.id.loginStatusText);

            loginStatus.setText(message);

            // Fade out animation
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            loginStatus.startAnimation(fadeOut);
            fadeOut.setDuration(1200);
            fadeOut.setFillAfter(true);
            fadeOut.setStartOffset(4200);
        }
    }

    // COPY FUNCTION INTO NEW ACTIVITIES TO GET API TOKEN
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
                Log.d("APITOKENREADING", stringBuffer.toString());
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
                                processLogin(json_response.getBoolean("success"), json_response.getString("message"), json_response.get("data"), json_response.getJSONObject("data"));
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
