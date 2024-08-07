package com.example.nasaimageoftheday;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetApiKeyActivity extends BaseActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_api_setting); // Overriden by BaseActivity
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the title of the Toolbar
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.apiTitle));

        // Declare Views
//        DatePicker datePicker = findViewById(R.id.datePicker);
        EditText editText = findViewById(R.id.api_key);
        Button defaultButton = findViewById(R.id.default_button);
        Button confirmButton = findViewById(R.id.confirm_button);

        String savedApiKey;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        savedApiKey = prefs.getString("api_key", "");
        editText.setText(savedApiKey);
        defaultButton.setOnClickListener(click -> {
            editText.setText("DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d");
        });

        confirmButton.setOnClickListener(click -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("api_key", String.valueOf(editText.getText()));
            editor.apply();
            Toast.makeText(this, getString(R.string.savedAPIKey),Toast.LENGTH_SHORT).show();
        });

    }
}