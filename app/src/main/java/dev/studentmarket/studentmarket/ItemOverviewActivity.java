package dev.studentmarket.studentmarket;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        String apiToken = ""; // CREATE INSTANCE TO ASSIGN FROM FILE

        /**
         * Navigation Menu
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // OPEN FILE TO GET LOCALLY STORED API TOKEN
        apiToken = getAPIToken();

        getItems("http://student-market.co.uk/api/items?api_token=" + apiToken, "items");
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
     * Here we process the data our API provides us with
     */
    public void processData(Boolean success, String message, JSONObject data) {
        ListView listView;
//        ArrayList<String> testData = new ArrayList<>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"));
        ArrayList<String> testData = new ArrayList<>();


        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

        // GET ITEM DATA
        try {
            JSONArray itemData = data.getJSONObject("items").getJSONArray("data");
            Log.d("ItemData", itemData.toString());
            JSONObject item1 = itemData.getJSONObject(0);
            Log.d("Item1", item1.toString());

            for(int i = 0; i < itemData.length(); i++) {
                testData.add(itemData.getJSONObject(i).getString("name"));
            }

            // ADD ITEMS TO LIST VIEW ON SCREEN
            listView = findViewById(R.id.listview);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testData);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
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
                                processData(json_response.getBoolean("success"), json_response.getString("message"), json_response.getJSONObject("data"));
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
