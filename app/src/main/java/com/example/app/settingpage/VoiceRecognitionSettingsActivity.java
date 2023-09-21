package com.example.app.settingpage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Map;

public class VoiceRecognitionSettingsActivity extends AppCompatActivity {
    // 定义变量
    EditText editTitle, editCommand, editAddress;
    Spinner spinnerType;
    Button btnSave;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition_settings);

        // Initialize views
        editTitle = findViewById(R.id.editTitle);
        editCommand = findViewById(R.id.editCommand);
        spinnerType = findViewById(R.id.spinnerType);
        editAddress = findViewById(R.id.editAddress);
        btnSave = findViewById(R.id.btnSave);

        btnBack = findViewById(R.id.btn_back);

        // Set click listeners or other initializations
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and return to the previous one
            }
        });

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.command_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RadioGroup radioGroupVideoSource = findViewById(R.id.radioGroupVideoSource);
                switch (position) {
                    case 0: // 打开网页
                        editAddress.setHint("输入网址");
                        radioGroupVideoSource.setVisibility(View.GONE);
                        break;
                    case 1: // 打开应用
                        editAddress.setHint("输入应用的包名");
                        radioGroupVideoSource.setVisibility(View.GONE);
                        break;
                    case 2: // 打开视频
                        radioGroupVideoSource.setVisibility(View.VISIBLE);
                        // Based on radio button selection, you can further update editAddress hint or input type.
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing here
            }
        });

        // Button save listener
        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String command = editCommand.getText().toString();
            int type = spinnerType.getSelectedItemPosition() + 1;
            String address = editAddress.getText().toString();

            // Save your custom command here
            // For example: saveCustomCommand(new CustomCommand(title, command, type, address));
        });
    }


}
