package com.example.nasaimageoftheday;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetApiKeyActivity extends BaseActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_api_setting); // Overridden by BaseActivity
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
        defaultButton.setOnClickListener(click -> editText.setText("DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d"));

        confirmButton.setOnClickListener(click -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("api_key", String.valueOf(editText.getText()));
            editor.apply();
            setResult(1, getIntent());
            finish();
        });

    }

    /* Override toolbar item selection to update the "Help" button
     * Since the Activity is different from the ImageActivity, the "today's image" button should still work.
     * Call the super() version of the method to access that.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navTodayImage) {
            super.onOptionsItemSelected(item);
        } else if (id == R.id.navHelp) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.apiKeyHelpTitle))
                    .setMessage(getString(R.string.apiKeyHelpMessage))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        }

        return true;
    }
}