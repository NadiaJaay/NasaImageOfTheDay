package com.example.nasaimageoftheday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {
    static final int DATE_REQUEST = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Overriden by BaseActivity
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the title of the Toolbar
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.homeTitle));

        // Declare Views
        DatePicker datePicker = findViewById(R.id.datePicker);
        Button button = findViewById(R.id.btn);

        // Get Today's Date and update the calendar
        Calendar todayDate = Calendar.getInstance(TimeZone.getDefault());
        // This is the minimum date based on the API documentation: https://github.com/nasa/apod-api
        // max date is set to the current day because you cannot query for future dates.
        Calendar minDate = Calendar.getInstance();
        minDate.set(1995, 5, 16);
        // Set the default date on the DatePicker to today's date.
        datePicker.updateDate(todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH));
        // Set the min + max dates (needs to be in Long)
        datePicker.setMinDate(minDate.getTimeInMillis());
        datePicker.setMaxDate(todayDate.getTimeInMillis());

        /* If the person clicks confirm, then open an ImageActivity for that date.
         * The ImageActivity will check if an image exists. If not, then a resultCode will be given back.
         * See onActivityResult()
         */
        button.setOnClickListener(click -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            LocalDate selectedDate = LocalDate.of(year, month, day);
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra("date", String.valueOf(selectedDate));
            startActivityIfNeeded(intent, DATE_REQUEST);
        });
    }

    /* Check for a result code
     * If a result code is delivered back, then the API failed.
     * Display a toast to say that no image was found for that selected day.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DATE_REQUEST) {
            if (resultCode == 0) Toast.makeText(this, getString(R.string.noResultFound),Toast.LENGTH_SHORT).show();
        }
    }
}