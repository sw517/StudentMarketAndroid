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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests

    private String apiToken = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context className;

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
}
