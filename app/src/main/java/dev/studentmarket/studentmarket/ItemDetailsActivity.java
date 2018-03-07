package dev.studentmarket.studentmarket;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailsActivity extends AppCompatActivity {
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    private String apiToken = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        final String itemId = getIntent().getExtras().getString("id", "0");
        userId = getIntent().getExtras().getString("userId", "0");

        // OPEN FILE TO GET LOCALLY STORED API TOKEN
        apiToken = getAPIToken();

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
                            postRequest("https://student-market.co.uk/api/logout?api_token=" + apiToken, "logout");

                            Intent intent = new Intent(className, MainActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SET PROFILE IMAGE IN NAV DRAWER
        View hView =  navigationView.getHeaderView(0);
        ImageView navheaderimage = (ImageView) hView.findViewById(R.id.navheaderimage);
        TextView navheadername = (TextView) hView.findViewById(R.id.nav_header_name);
        navheadername.setText(getUserName());

        String imgURL = getProfileImg();
        if (imgURL != null) {
            String absoluteURL = "https://student-market.co.uk/storage/" + imgURL;
            Picasso.with(getApplicationContext()).load(absoluteURL).into(navheaderimage);
        }

        postRequest("https://student-market.co.uk/api/items/1/" + itemId + "?api_token=" + apiToken, "items");
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
    public void postRequest(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject json_response = new JSONObject(response);

                            processData(json_response.getBoolean("success"), json_response.getString("message"), json_response.getJSONObject("data"), type);

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
     * Find local file containing User Profile Img URL
     */
    public String getProfileImg() {
        try {
            String fileString;
            FileInputStream fileInputStream = openFileInput("localUserImg");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((fileString=bufferedReader.readLine()) != null) {
                stringBuffer.append(fileString);
                String url = stringBuffer.toString();
                return url;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find local file containing User Name
     */
    public String getUserName() {
        try {
            String fileString;
            FileInputStream fileInputStream = openFileInput("localUserName");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((fileString=bufferedReader.readLine()) != null) {
                stringBuffer.append(fileString);
                String name = stringBuffer.toString();
                return name;
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
    public void processData(Boolean success, String message, JSONObject data, String type) {

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

//         GET ITEM DATA
        if (type.equals("items")) {
            try {
                JSONObject itemData = data.getJSONObject("item");
                Log.d("ItemData", itemData.toString());
                final String itemId = itemData.getString("id");
                // TITLE
                String title = itemData.getString("name");
                TextView titleText = (TextView)findViewById(R.id.itemDetailsTitle);
                titleText.setText(title);
                // SELL TYPE
                String dtype = itemData.getString("type");
                TextView typeText = (TextView)findViewById(R.id.itemDetailsType);
                // DESCRIPTION
                String description = itemData.getString("description");
                TextView descriptionText = (TextView)findViewById(R.id.itemDetailsDescription);
                descriptionText.setText(description);
                // PRICE
                String price = itemData.getString("price");
                // TRADE
                String trade = itemData.getString("trade");

                TextView costText = (TextView)findViewById(R.id.itemDetailsCost);


                // CHANGE LETTER CASING TO LOOK NEATER
                // AND FORMAT COST TEXT
                if (dtype.equals("sell")) {
                    String cost = "£" + price;
                    typeText.setText("Price:");
                    costText.setText(cost);
                } else if (dtype.equals("swap")) {
                    typeText.setText("Swap for:");
                    costText.setText(trade);
                } else if (dtype.equals("part-exchange")) {
                    String cost = "£" + price + " + " + trade;
                    typeText.setText("Part-Exchange for:");
                    costText.setText(cost);
                }

                // IMAGES
                // FIND IMAGE VIEWS FROM ACTIVITY
                ArrayList<ImageView> imageViews = new ArrayList<ImageView>(); // IMAGE VIEW ARRAY
                ImageView mainImage = (ImageView) findViewById(R.id.itemMainImage);
                ImageView subImage1 = (ImageView) findViewById(R.id.subImage1);
                ImageView subImage2 = (ImageView) findViewById(R.id.subImage2);
                ImageView subImage3 = (ImageView) findViewById(R.id.subImage3);
                ImageView subImage4 = (ImageView) findViewById(R.id.subImage4);
                ImageView subImage5 = (ImageView) findViewById(R.id.subImage5);
                // ADD IMAGE VIEWS TO ARRAY
                imageViews.add(mainImage);
                imageViews.add(subImage1);
                imageViews.add(subImage2);
                imageViews.add(subImage3);
                imageViews.add(subImage4);
                imageViews.add(subImage5);
                // ADD IMAGES TO IMAGE VIEWS
                if (itemData.getJSONArray("images").length() > 0) {
                    for (int i = 0; i < itemData.getJSONArray("images").length(); i++) {
                        // GET FILE PATH
                        String imageURL = itemData.getJSONArray("images").getJSONObject(i).getString("path");
                        final String abURL = "https://student-market.co.uk/storage/" + imageURL;
                        // ASSIGN IMAGE TO IMAGE VIEW
                        Picasso.with(getApplicationContext()).load(abURL).into(imageViews.get(i));
                        imageViews.get(i).setVisibility(View.VISIBLE); // SET IMAGE TO VISIBLE (IMAGE VIEWS ARE HIDDEN BY DEFAULT)
                        // ADD ABILITY TO ENLARGE IMAGE BY LOADING IN NEW PAGE
                        imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
                                intent.putExtra("url", abURL);
                                intent.putExtra("id", itemId);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            }
                        });
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equals("logout")) {

            Log.d("Logout", "test");
        }
    }

    /**
     *  View Seller profile page
     */
    public void viewSeller(View view) {
        Intent details = new Intent(ItemDetailsActivity.this, ProfileActivity.class);
        details.putExtra("userId", userId);
        startActivity(details);
    }
}
