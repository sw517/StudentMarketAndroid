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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class WriteReviewActivity extends AppCompatActivity {

    private String apiToken = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

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
//                            postRequest("https://student-market.co.uk/api/logout?api_token=" + apiToken, "logout");

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
}
