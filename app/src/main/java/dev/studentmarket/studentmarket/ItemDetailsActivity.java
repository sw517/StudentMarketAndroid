package dev.studentmarket.studentmarket;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailsActivity extends AppCompatActivity {
    private Map<String, String> parameters = new HashMap<>(); // Here we store all of our parameters that are used in API requests
    private DrawerLayout mDrawerLayout; // Needed for Navigation Menu
    private ActionBarDrawerToggle mToggle; // Needed for Navigation Menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        /**
         * Navigation Menu
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
