package dev.studentmarket.studentmarket;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import java.util.Iterator;
import java.util.Map;

public class ReviewsViewActivity extends AppCompatActivity {
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_view);
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

        // SET PROFILE IMAGE AND NAME IN NAV DRAWER
        View hView =  navigationView.getHeaderView(0);
        ImageView navheaderimage = (ImageView) hView.findViewById(R.id.navheaderimage);
        TextView navheadername = (TextView) hView.findViewById(R.id.nav_header_name);
        navheadername.setText(getUserName());

        String imgURL = getProfileImg();
        Log.d("IMGURL", imgURL);
        if (!imgURL.equals("null")) {
            String absoluteURL = "https://student-market.co.uk/storage/" + imgURL;
            Picasso.with(getApplicationContext()).load(absoluteURL).into(navheaderimage);
        }

        // OPEN FILE TO GET LOCALLY STORED API TOKEN
        apiToken = getAPIToken();

        String userId = getIntent().getExtras().getString("userId", "0");
        getReviews("https://student-market.co.uk/api/view/" + userId + "?api_token=" + apiToken, "view");
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
     * Submits a POST request to the API
     */
    public void getReviews(String url, final String type) {
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
     * Here we process the data our API provides us with
     */
    public void processData(Boolean success, String message, JSONObject data) {
        ListView listView;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();
        ArrayList<Integer> ratings = new ArrayList<>();

        Log.d("Data", data.toString());
        Log.d("Success", success + "!");
        Log.d("Message", message);

        // GET REVIEW DATA
        try {
            // GET REVIEWS
            JSONObject userReviews = data.getJSONObject("userReviews");
            Log.d("Reviews", userReviews.toString());
            Iterator<?> keys = userReviews.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( userReviews.get(key) instanceof JSONObject ) {
                    // ID
                    String buyerId = userReviews.getJSONObject(key).getString("buyer_id");
                    userIds.add(buyerId);
                    // DESCRIPTION
                    String description = userReviews.getJSONObject(key).getString("review");
                    descriptions.add(buyerId);
                    // RATING
                    int rating = userReviews.getJSONObject(key).getInt("rating");
                    ratings.add(rating);
                }
            }

            // ADD ITEMS TO LIST VIEW ON SCREEN
            listView = findViewById(R.id.listview);
            ReviewAdapter adapter = new ReviewAdapter(this, names, descriptions, userIds, ratings);
            listView.setAdapter(adapter);

//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
//                    Intent details = new Intent(ItemOverviewActivity.this, ItemDetailsActivity.class);
//                    String itemId = ((TextView) view.findViewById(R.id.textviewid)).getText().toString();
//                    String userId = ((TextView) view.findViewById(R.id.textviewuserid)).getText().toString();
//                    details.putExtra("id", itemId);
//                    details.putExtra("userId", userId);
//                    startActivity(details);
//
//                }
//            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

/**
 * This class goes through the view rows for the adapter and applies the data to the XML Elements
 * Essentially this populates each list item
 */
class ReviewAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> titleArray;
    ArrayList<String> descriptionArray;
    ArrayList<String> userIdArray;
    ArrayList<Integer> ratingArray;

    ReviewAdapter(Context c, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<String> userId, ArrayList<Integer> ratings) {

        super(c, R.layout.single_row, R.id.textviewtitle, titles);
        this.context = c;
        this.titleArray = titles;
        this.descriptionArray = descriptions;
        this.userIdArray = userId;
        this.ratingArray = ratings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.single_row, parent, false);
        TextView reviewName = row.findViewById(R.id.reviewUserName);
        TextView reviewDescription = row.findViewById(R.id.reviewUserDescription);
        RatingBar ratingBar = row.findViewById(R.id.ratingBar);
//        TextView userId = row.findViewById(R.id.textviewuserid);

        reviewName.setText(titleArray.get(position));
        reviewDescription.setText(descriptionArray.get(position));
        ratingBar.setRating(ratingArray.get(position));
//        userId.setText(userIdArray.get(position));

        return row;
    }
}