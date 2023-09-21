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
import com.example.app.AppConstants;


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

        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, MODE_PRIVATE);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Declare commands before using it
        ArrayList<CustomCommand> commands = loadCommandsFromPrefs();

        // Initialize CommandsAdapter once with the correct constructor
        CommandsAdapter commandsAdapter = new CommandsAdapter(this, commands);
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
            newCommand.saveToSharedPreferences(this); // 保存 command 到 SharedPreferences
            newCommand.saveTypeToSharedPreferences(this); // 保存类型到 SharedPreferences
            commands.add(newCommand);
            saveCommandsToPrefs(commands);
            commandsAdapter.notifyDataSetChanged();

            Toast.makeText(this, "命令已保存", Toast.LENGTH_SHORT).show();
        });
    }
    public class CommandsAdapter extends RecyclerView.Adapter<CommandsAdapter.ViewHolder> {
        private ArrayList<CustomCommand> commands;
        private Context context;

        public CommandsAdapter(Context context, ArrayList<CustomCommand> commands) {
            this.context = context;
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
            CustomCommand command = commands.get(position);
            holder.txtTitle.setText(command.getTitle());
            holder.txtCommand.setText(command.getCommand());
            holder.txtType.setText(getTypeString(command.getType()));  // You should define getTypeString method
            holder.txtKeywords.setText(command.getKeyWords());

            holder.btnDelete.setOnClickListener(v -> {
                commands.remove(position);
                saveCommandsToPrefs(commands);
                notifyItemRemoved(position);
                Toast.makeText(context, "命令已删除", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return commands.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtTitle, txtCommand, txtType, txtKeywords;
            Button btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txt_title);
                txtCommand = itemView.findViewById(R.id.txt_command);
                txtType = itemView.findViewById(R.id.txt_type);
                txtKeywords = itemView.findViewById(R.id.txt_keywords);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }

        private String getTypeString(int type) {
            switch (type) {
                case CustomCommand.TYPE_WEB_PAGE:
                    return "网页";
                case CustomCommand.TYPE_APP:
                    return "应用";
                case CustomCommand.TYPE_VIDEO:
                    return "视频";
                default:
                    return "未知";
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
