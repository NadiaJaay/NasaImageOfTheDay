package com.example.nasaimageoftheday;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavedDatesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_dates); // Overriden by BaseActivity
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* TODO: Create Activity for ListView. Inspire yourself from SavedImagesActivity (It's almost identical)
         * I've already created an activity_saved_dates.xml to start you off, along with a row_layout.xml for the individual ListView items
         * Need to create functionality to be able to select and view the content of the row.
         * Need to create functionality to delete the row from database and SavedPreferences
         *
         */

        // Declare Views
        ListView listView = findViewById(R.id.listView);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.savedImagesActivityTitle));

    }


    /* Override toolbar item selection to update the "Help" button
     * Since the Activity is different from the ImageActivity, the "today's image" button should still work.
     * Call the super() version of the method to access that.
     *
     * TODO: Change the Title and Message of the "Help" button for this Activity, in English and in French
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navTodayImage) {
            super.onOptionsItemSelected(item);
        } else if (id == R.id.navHelp) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.savedImagesDialogTitle))
                    .setMessage(getString(R.string.savedImagesDialogMessage))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        }

        return true;
    }
}