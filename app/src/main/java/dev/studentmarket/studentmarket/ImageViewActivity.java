package dev.studentmarket.studentmarket;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    private String itemId;
    private String userId;
    private String url;

    /**
     *  CLASS IS USED TO SHOW IMAGES ENLARGED IN THEIR OWN PAGE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        itemId = getIntent().getExtras().getString("id", "0");
        url = getIntent().getExtras().getString("url", "0");
        userId = getIntent().getExtras().getString("userId", "0");
        ImageView image = (ImageView) findViewById(R.id.zoomImage);
        Picasso.with(getApplicationContext()).load(url).into(image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
