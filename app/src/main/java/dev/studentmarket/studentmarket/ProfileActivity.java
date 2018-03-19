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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

public class ProfileActivity extends AppCompatActivity {

    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    private String apiToken = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;
    private String userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
                        } else if (navTitle.equals("Messages")) {
                            Intent intent = new Intent(className, MessagesActivity.class);
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

        // OPEN FILE TO GET LOCALLY STORED API TOKEN
        userId = getLocalUserId();

        Intent intent = getIntent();
        if(intent.hasExtra("userId")){
            userId = getIntent().getExtras().getString("userId", "0");
            Log.d("Found Extra", userId);
        }

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

        // HIDE WRITE REVIEW & MESSAGE BUTTONS IF VIEWING OWN PROFILE
        if (userId.equals(getLocalUserId())) {
            Button btnWriteReview = (Button) findViewById(R.id.btnWriteReview);
            btnWriteReview.setVisibility(View.GONE);
            Button btnMessage = (Button) findViewById(R.id.btnMessage);
            btnWriteReview.setVisibility(View.GONE);
            btnMessage.setVisibility(View.GONE);
        }


        getRequest("https://student-market.co.uk/api/view/" + userId + "?api_token=" + apiToken, "view");
    }

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
     * Find local file containing API TOKEN
     */
    public String getLocalUserId() {
        try {
            String fileString;
            FileInputStream fileInputStream = openFileInput("localUserId");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((fileString=bufferedReader.readLine()) != null) {
                stringBuffer.append(fileString);
                Log.d("LocalUserId", stringBuffer.toString());
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
            JSONObject userData = data.getJSONObject("viewUser");

            // GET NAME
            String firstNameStr = userData.getString("first_name");
            String lastNameStr = userData.getString("last_name");
            TextView nameET = (TextView) findViewById(R.id.profileName);
            username = firstNameStr + " " + lastNameStr;
            nameET.setText(username);

            // GET PROFILE PICTURE
            String profilePicture = userData.getString("profile_picture");
            ImageView ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);

            if (profilePicture != null) {
                String profilePicURL = "https://student-market.co.uk/storage/" + profilePicture;
                Picasso.with(getApplicationContext()).load(profilePicURL).into(ivProfilePicture);
            }

            // GET REVIEWS
            JSONArray userReviews = data.getJSONArray("userReviews");
            ArrayList<Integer> ratings = new ArrayList<>();
            for (int i = 0; i < userReviews.length(); i++) {
                JSONObject review = userReviews.getJSONObject(i);
                int rating = review.getInt("rating");
                ratings.add(rating);
            }

            // OLD CODE FOR WHEN API USED JSON OBJECT INSTEAD OF ARRAY FOR REVIEWS
//            JSONObject userReviews = data.getJSONObject("userReviews");
//            Iterator<?> keys = userReviews.keys();
//            while( keys.hasNext() ) {
//                String key = (String)keys.next();
//                if ( userReviews.get(key) instanceof JSONObject ) {
//                    int rating = userReviews.getJSONObject(key).getInt("rating");
//                    ratings.add(rating);
//                }
//            }

            int averageRating = 0;
            int totalRatings = ratings.size();
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            TextView numRatings = (TextView) findViewById(R.id.numReviews);
            for (int i = 0; i < ratings.size(); i++) {
                averageRating += ratings.get(i);
            }

            // ARITHMETIC EXCEPTION
            if (totalRatings != 0) {
                averageRating = (averageRating / totalRatings);
            }
            ratingBar.setRating(averageRating);
            numRatings.setText("(" + Integer.toString(totalRatings) + ")");

            // MAKE REVIEWS BUTTON VISIBLE IF THERE ARE REVIEWS
            if (userReviews.length() > 0) {
                Button reviewbtn = (Button) findViewById(R.id.btnViewReviews);
                reviewbtn.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  View Reviews of seller
     */
    public void viewReviews(View view) {
        Intent details = new Intent(ProfileActivity.this, ReviewsViewActivity.class);
        details.putExtra("userId", userId);
        startActivity(details);
    }

    /**
     *  Write review of seller
     */
    public void writeReview(View view) {
        Intent details = new Intent(ProfileActivity.this, WriteReviewActivity.class);
        details.putExtra("userId", userId);
        startActivity(details);
    }


    /**
     *  Write review of seller
     */
    public void messageSeller(View view) {
        Intent intent = new Intent(ProfileActivity.this, ViewMessageActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        Log.d("User ID", userId);
        Log.d("Username", username);
        startActivity(intent);
    }
}
