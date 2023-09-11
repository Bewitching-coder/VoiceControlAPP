package com.example.app.mainpage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the top fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_top, new TopFragment())
                    .commit();
        }

        // Load the bottom fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_bottom, new BottomFragment())
                    .commit();
        }
    }
}
