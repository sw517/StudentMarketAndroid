package dev.studentmarket.studentmarket;

import android.content.Intent;
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

    /**
     *  Called when the user presses login
     */
    public void attemptLogin(View view) {
        parameters.clear();
        parameters.put("email", "da332@kent.ac.uk");
        parameters.put("password", "deniz123");
        postRequest("http://student-market.co.uk/api/login", "login");
    }

    /**
     * Here we process the data our API provides us with
     */
    public void processLogin(Boolean success, String message, Object data) {
        Intent intent = new Intent(this, ItemOverviewActivity.class);

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

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
                                processLogin(json_response.getBoolean("success"), json_response.getString("message"), json_response.get("data"));
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
