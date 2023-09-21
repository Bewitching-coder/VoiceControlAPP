package com.example.app.settingpage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VoiceRecognitionSettingsActivity extends AppCompatActivity {
    // 定义变量
    EditText editTitle, editCommand, editAddress;
    Spinner spinnerType;
    Button btnSave, btnBack;
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "voiceCommands";
    public static final String COMMANDS_LIST = "commands_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition_settings);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<CustomCommand> commands = loadCommandsFromPrefs();
        CommandsAdapter commandsAdapter = new CommandsAdapter(commands);
        recyclerView.setAdapter(commandsAdapter);

        editTitle = findViewById(R.id.editTitle);
        editCommand = findViewById(R.id.editCommand);
        spinnerType = findViewById(R.id.spinnerType);
        editAddress = findViewById(R.id.editAddress);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.command_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Here, you can handle the spinner item selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String command = editCommand.getText().toString();
            int type = getTypeFromSpinner();
            String keyWords = editAddress.getText().toString();

            CustomCommand newCommand = new CustomCommand(title, command, type, keyWords);
            commands.add(newCommand);
            saveCommandsToPrefs(commands);
            commandsAdapter.notifyDataSetChanged();

            Toast.makeText(this, "命令已保存", Toast.LENGTH_SHORT).show();
        });
    }

    public class CommandsAdapter extends RecyclerView.Adapter<CommandsAdapter.ViewHolder> {
        private ArrayList<CustomCommand> commands;

        public CommandsAdapter(ArrayList<CustomCommand> commands) {
            this.commands = commands;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_command, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.txtCommand.setText(commands.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return commands.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtCommand;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtCommand = itemView.findViewById(R.id.txt_command);
            }
        }
    }

    private ArrayList<CustomCommand> loadCommandsFromPrefs() {
        String jsonCommands = sharedPreferences.getString(COMMANDS_LIST, "");
        Type type = new TypeToken<ArrayList<CustomCommand>>(){}.getType();

        ArrayList<CustomCommand> savedCommands = new Gson().fromJson(jsonCommands, type);
        if (savedCommands == null) {
            savedCommands = new ArrayList<>();
        }
        return savedCommands;
    }

    private void saveCommandsToPrefs(ArrayList<CustomCommand> commands) {
        String jsonCommands = new Gson().toJson(commands);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COMMANDS_LIST, jsonCommands);
        editor.apply();
    }

    private int getTypeFromSpinner() {
        switch (spinnerType.getSelectedItemPosition()) {
            case 0:
                return CustomCommand.TYPE_WEB_PAGE;
            case 1:
                return CustomCommand.TYPE_APP;
            case 2:
                return CustomCommand.TYPE_VIDEO;
            default:
                return 0;
        }
    }
}
