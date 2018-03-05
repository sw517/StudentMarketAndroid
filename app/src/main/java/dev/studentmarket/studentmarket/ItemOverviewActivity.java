package dev.studentmarket.studentmarket;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemOverviewActivity extends AppCompatActivity {
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_overview);
        String apiToken = ""; // CREATE INSTANCE TO ASSIGN FROM FILE

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

        // OPEN FILE TO GET LOCALLY STORED API TOKEN
        apiToken = getAPIToken();

        getItems("https://student-market.co.uk/api/items?api_token=" + apiToken, "items");
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
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();

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
                titles.add(itemData.getJSONObject(i).getString("name"));
                descriptions.add(itemData.getJSONObject(i).getString("description"));
                String itemId = Integer.toString(itemData.getJSONObject(i).getInt("id"));
                ids.add(itemId);
                String userId = Integer.toString(itemData.getJSONObject(i).getInt("user_id"));
                userIds.add(userId);
                images.add("https://student-market.co.uk/storage/item/1/K1Om0n1q36lv0WsHINfIzYbRwMWEE6bP8pbb4H2g.jpeg");
            }

            // ADD ITEMS TO LIST VIEW ON SCREEN
            listView = findViewById(R.id.listview);
            CustomAdapter adapter = new CustomAdapter(this, titles, descriptions, ids, userIds, images);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                    Intent details = new Intent(ItemOverviewActivity.this, ItemDetailsActivity.class);
                    String itemId = ((TextView) view.findViewById(R.id.textviewid)).getText().toString();
                    String userId = ((TextView) view.findViewById(R.id.textviewuserid)).getText().toString();
                    Log.d("ItemID", itemId);
//                    startActivityForResult(details, Integer.parseInt(itemId));
                    details.putExtra("id", itemId);
                    details.putExtra("userId", userId);
                    startActivity(details);

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

/**
 * This class goes through the view rows for the adapter and applies the data to the XML Elements
 */
class CustomAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> titleArray;
    ArrayList<String> descriptionArray;
    ArrayList<String> idArray;
    ArrayList<String> imageArray;
    ArrayList<String> userIdArray;

    CustomAdapter(Context c, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<String> id, ArrayList<String> userId, ArrayList<String> images) {

        super(c, R.layout.single_row, R.id.textviewtitle, titles);
        this.context = c;
        this.titleArray = titles;
        this.descriptionArray = descriptions;
        this.idArray = id;
        this.imageArray = images;
        this.userIdArray = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.single_row, parent, false);
        ImageView itemImage = row.findViewById(R.id.imageView);
        TextView itemTitle = row.findViewById(R.id.textviewtitle);
        TextView itemDescription = row.findViewById(R.id.textviewdescription);
        TextView itemId = row.findViewById(R.id.textviewid);
        TextView userId = row.findViewById(R.id.textviewuserid);

//        itemImage.setImageResource();
        itemTitle.setText(titleArray.get(position));
        itemDescription.setText(descriptionArray.get(position));
        itemId.setText(idArray.get(position));
        userId.setText(userIdArray.get(position));

        return row;
    }
}