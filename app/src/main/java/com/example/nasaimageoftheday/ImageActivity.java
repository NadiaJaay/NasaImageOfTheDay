package com.example.nasaimageoftheday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageActivity extends BaseActivity {
    private ImageView imageView;
    private Bitmap currentPic;
    private String hdUrl;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Since we're performing an ASYNC task, set the loading to true.
        setLoading(true);

        // Declare different views
        imageView = findViewById(R.id.todayImage);
        TextView dateText = findViewById(R.id.todayDate);
        TextView explanationText = findViewById(R.id.explanation);
        TextView titleText = findViewById(R.id.imageTitle);
        TextView hdUrlText = findViewById(R.id.viewHDBrowserLink);
        TextView sdUrlText = findViewById(R.id.viewSDBrowserLink);
        ImageButton saveBtn = findViewById(R.id.saveBtn);
        ImageButton downloadBtn = findViewById(R.id.downloadBtn);

        // Set links as clickable and interactable
        hdUrlText.setClickable(true);
        hdUrlText.setMovementMethod(LinkMovementMethod.getInstance());
        sdUrlText.setClickable(true);
        sdUrlText.setMovementMethod(LinkMovementMethod.getInstance());

        // If a date parameter was passed in the Intent, set the date to the parameter. Otherwise, retrieve today's date.
        // Change the title of the action bar depending on date.
        LocalDate date;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            date = LocalDate.parse(bundle.getString("date"));
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.imageActivityDateTitle)+" "+date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        } else {
            date = LocalDate.now();
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.navTodayImage));
        }


        // If Long Clicked, display a toast with a description of what it does
        saveBtn.setOnLongClickListener(click -> {
            Toast.makeText(this, getResources().getString(R.string.saveDate), Toast.LENGTH_SHORT).show();
            return false;
        });
        downloadBtn.setOnLongClickListener(click -> {
            Toast.makeText(this, getResources().getString(R.string.downloadImage), Toast.LENGTH_SHORT).show();
            return false;
        });

        saveBtn.setOnClickListener(click -> {

        });

        // If download button is clicked...
        downloadBtn.setOnClickListener(c -> {
            String fileName = hdUrl.substring(hdUrl.lastIndexOf("/")+1);
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);

            // Create an EditText view and set its name to the name of the file from the NASA JSON object.
            final EditText editText = new EditText(this);
            editText.setText(fileName);

            // Create an alert dialog with an EditText field. Ask the user to name the file to save.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.downloadImageDialogTitle))
                    .setView(editText)

                    // If the user clicks "Save"...
                    .setPositiveButton(R.string.dialogPosSaveBtn, (dialog, btn) -> {

                        // Get the name in the EditText
                        String inputFileName = editText.getText().toString();

                        // Find if file exists...
                        File file = new File(getFilesDir(), inputFileName);

                        // If file does not exist, then save to internal storage.
                        if (!file.exists()) {
                            String temp = (fileExtension.equalsIgnoreCase("JPG")) ? "JPEG" : fileExtension.toUpperCase();
                            try {
                                FileOutputStream outputStream = openFileOutput(inputFileName, Context.MODE_PRIVATE);
                                currentPic.compress(Bitmap.CompressFormat.valueOf(temp), 80, outputStream);

                                outputStream.flush();
                                outputStream.close();

                                // Show a SnackBar. If they click "Undo", delete the file we just created.
                                Snackbar.make(getActivityContainer(), getResources().getString(R.string.fileDownloaded), Snackbar.LENGTH_LONG)
                                        .setAction("Undo", click -> file.delete())
                                        .show();
                            } catch (IOException e) {
                                Toast.makeText(ImageActivity.this, getResources().getString(R.string.fileExists), Toast.LENGTH_LONG).show();
                                throw new RuntimeException(e);
                            }

                        } else {
                            Toast.makeText(ImageActivity.this, getResources().getString(R.string.fileExists), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(R.string.dialogNegBtn, (click, arg) -> {})
                    .create().show();


        });


        // This will replace the AsyncTask (doInBackground) that we learned in Lab 6
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection connection;
            InputStream response;
            String explanation, title, sdUrl;

            try {
                // Open a connection to the NASA API
                URL url = new URL("https://api.nasa.gov/planetary/apod?api_key="+BuildConfig.NASA_KEY+"&date="+date);
                connection = (HttpURLConnection) url.openConnection();
                response = connection.getInputStream();

                // Read the JSON result and save to a StringBuilder
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                // Close the initial connection
                reader.close();
                response.close();
                connection.disconnect();

                // Retrieve the JSON results
                String result = sb.toString();
                JSONObject jsonObject = new JSONObject(result);

                // Get the different values from the JSON object
                explanation = jsonObject.getString("explanation");
                title = jsonObject.getString("title");
                hdUrl = jsonObject.getString("hdurl");
                sdUrl = jsonObject.getString("url");

                // Decide the image URL to display on the Activity
                URL imageURL = new URL(hdUrl);
                connection = (HttpURLConnection) imageURL.openConnection();
                response = connection.getInputStream();

                currentPic = BitmapFactory.decodeStream(response);

                // Close connections
                connection.disconnect();
                response.close();

            } catch (Exception e) {
                setResult(0, getIntent());
                finish();
                return;
            }

            // Once the API info is done being downloaded, display everything
            handler.post(() -> {
                downloadBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                dateText.setText(date.toString());
                imageView.setImageBitmap(currentPic);
                explanationText.setText(explanation);
                titleText.setText(title);

                // Set the link URLs. Has to be done from here as the URLs are retrieved from the JSON.
                // Add some text from the String XML file for easy translation for later
                hdUrlText.setText(Html.fromHtml("<a href='"+hdUrl+"'>"+this.getResources().getString(R.string.viewHDInBrowser)+"</a>", Html.FROM_HTML_MODE_COMPACT));
                sdUrlText.setText(Html.fromHtml(this.getResources().getString(R.string.troubleViewingText)+" <a href='"+sdUrl+"'>"+this.getResources().getString(R.string.viewSDInBrowser)+"</a>", Html.FROM_HTML_MODE_COMPACT));
                // Set loading to false
                setLoading(false);
            });
        });

    }

    // Set the visibility of the progress bar to true or false based on the state of the image being loaded.
    private void setLoading(boolean status) {
        ProgressBar loading = findViewById(R.id.loading);

        if (status) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navHelp) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.helpImageDialogTitle))
                    .setMessage(getString(R.string.helpImageDialogMessage))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        }

        return true;
    }


}
