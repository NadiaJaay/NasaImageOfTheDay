package com.example.nasaimageoftheday;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SavedDatesActivity extends BaseActivity {
    private final List<SavedDate> savedDates = new ArrayList<>();
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_dates);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Declare Views
        ListView listView = findViewById(R.id.listView);
        // Update title on Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(this.getResources().getString(R.string.navSavedDates));
        }

        // Load saved dates from SharedPreferences
        loadSavedDates();

        // Sort dates by date
        savedDates.sort(Comparator.comparing(SavedDate::getDate));

        // Set the adapter for the ListView
        listView.setAdapter(adapter = new MyListAdapter());

        // On Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            SavedDate selectedDate = savedDates.get(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(selectedDate.getTitle())
                    .setMessage(getString(R.string.savedImageDate) + " " + selectedDate.getDate())
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        });

        // On Long Click Listener
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.delete_date)
                    .setPositiveButton(R.string.delete, (click, arg) -> {
                        deleteDate(position);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
            return true;
        });
    }

    private void loadSavedDates() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedDatesString = prefs.getString("saved_dates", "");
        if (!savedDatesString.isEmpty()) {
            String[] datesArray = savedDatesString.split(",");
            for (String dateEntry : datesArray) {
                String[] parts = dateEntry.split(":");
                if (parts.length == 2) {
                    savedDates.add(new SavedDate(parts[0], parts[1]));
                }
            }
        }
    }

    private void deleteDate(int position) {
        savedDates.remove(position);
        saveDatesToPreferences();
        adapter.notifyDataSetChanged();
    }

    private void saveDatesToPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (SavedDate date : savedDates) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(date.getTitle()).append(":").append(date.getDate());
        }
        editor.putString("saved_dates", sb.toString());
        editor.apply();
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
            alertDialogBuilder.setTitle(getString(R.string.savedDateDialogTitle))
                    .setMessage(getString(R.string.savedDatesDialogMessage))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        }

        return true;
    }

    // Inner class for SavedDate
    public static class SavedDate {
        private final String title;
        private final String date;

        public SavedDate(String title, String date) {
            this.title = title;
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }
    }

    // Adapter for ListView
    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return savedDates.size();
        }

        @Override
        public SavedDate getItem(int position) {
            return savedDates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = convertView;
            LayoutInflater inflater = getLayoutInflater();
            if (newView == null) {
                newView = inflater.inflate(R.layout.row_layout, parent, false);
            }
            TextView titleTextView = newView.findViewById(R.id.imageTitle);
            TextView dateTextView = newView.findViewById(R.id.imageDate);
            SavedDate date = getItem(position);
            titleTextView.setText(date.getTitle());
            dateTextView.setText(date.getDate());
            return newView;
        }
    }
}
