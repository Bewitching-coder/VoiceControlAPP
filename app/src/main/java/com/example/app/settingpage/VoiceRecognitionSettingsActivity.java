package com.example.app.settingpage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import android.widget.EditText;
import android.content.SharedPreferences;
import java.util.Map;

public class VoiceRecognitionSettingsActivity extends AppCompatActivity {

    // Define your views and other class level variables here
    Button btnBack;
    EditText keywordEditText, urlEditText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition_settings);

        // Initialize your views
        btnBack = findViewById(R.id.btn_back);

        // Set click listeners or other initializations
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and return to the previous one
            }
        });
        keywordEditText = findViewById(R.id.keyword_edit_text);
        urlEditText = findViewById(R.id.url_edit_text);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordEditText.getText().toString().trim();
                String url = urlEditText.getText().toString().trim();

                if (!keyword.isEmpty() && !url.isEmpty()) {
                    saveKeywordUrlPair(keyword, url);
                }
            }
        });

    }
    private void saveKeywordUrlPair(String keyword, String url) {
        SharedPreferences sharedPref = getSharedPreferences("voice_commands", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyword, url);
        editor.apply();
    }

}
