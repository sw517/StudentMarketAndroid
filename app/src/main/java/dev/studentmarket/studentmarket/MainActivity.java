package dev.studentmarket.studentmarket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerUser = (Button) findViewById(R.id.bRegisterForm);
        registerUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });
    }

    /** Called when the user presses login */
    public void attemptLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        Log.d("MyApp","I am here");


        startActivity(intent);
    }
}
