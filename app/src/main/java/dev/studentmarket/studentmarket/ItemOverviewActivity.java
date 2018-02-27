package dev.studentmarket.studentmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
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

public class ItemOverviewActivity extends AppCompatActivity {
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests
    private DrawerLayout mDrawerLayout; // Needed for Navigation Menu
    private ActionBarDrawerToggle mToggle; // Needed for Navigation Menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_overview);

        /**
         * Navigation Menu
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getItems("http://student-market.co.uk/api/items", "items");
    }

    /**
     * Allow Navigation Button to be pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Here we process the data our API provides us with
     */
    public void processData(Boolean success, String message, Object data) {
        Intent intent = new Intent(this, ItemOverviewActivity.class);

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

        if (success) {
//            startActivity(intent);
        } else {
//            final TextView loginStatus = (TextView) findViewById(R.id.loginStatusText);

//            loginStatus.setText(message);

            // Fade out animation
//            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
//            loginStatus.startAnimation(fadeOut);
//            fadeOut.setDuration(1200);
//            fadeOut.setFillAfter(true);
//            fadeOut.setStartOffset(4200);
        }
    }


    /**
     * Submits a POST request to the API
     */
    public void getItems(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject json_response = new JSONObject(response);
                            if (type.equals("items")) {
                                processData(json_response.getBoolean("success"), json_response.getString("message"), json_response.get("data"));
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
