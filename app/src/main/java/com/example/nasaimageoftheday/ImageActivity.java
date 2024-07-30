package com.example.nasaimageoftheday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageActivity extends BaseActivity {
    private ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.todayImage);
        TextView todayDateText = findViewById(R.id.todayDate);
        TextView explanationText = findViewById(R.id.explanation);
        TextView titleText = findViewById(R.id.imageTitle);
        TextView hdUrlText = findViewById(R.id.sdImageUrl);
        TextView sdUrlText = findViewById(R.id.hdImageUrl);
        LocalDate date = LocalDate.now();

        todayDateText.setText(date.toString());

        // This will replace the AsyncTask (doInBackground) that we learned in Lab 6
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap currentPic;
            HttpURLConnection connection;
            InputStream response;
            String explanation, title, hdurl, sdurl, fileExtension;

            try {
                // Open a connection to the cataas.com website
                URL url = new URL("https://api.nasa.gov/planetary/apod?api_key="+BuildConfig.NASA_KEY+"&date="+date);
                connection = (HttpURLConnection) url.openConnection();
                response = connection.getInputStream();

                // Read the JSON result.
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
                Log.i("MainActivity", "JSON Result: " + jsonObject);

                // Get the different values from the JSON object
                explanation = jsonObject.getString("explanation");
                title = jsonObject.getString("title");
                hdurl = jsonObject.getString("hdurl");
                sdurl = jsonObject.getString("url");
                fileExtension = hdurl.substring(hdurl.lastIndexOf(".")+1);

                URL imageURL = new URL(hdurl);
                connection = (HttpURLConnection) imageURL.openConnection();
                response = connection.getInputStream();

                currentPic = BitmapFactory.decodeStream(response);

//                File file = new File(getFilesDir(), hdurl);

//                if (file.exists()) {
//                    currentPic = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    Log.i("MainActivity", "Retrieved File " + hdurl + " from device.");

//                    FileOutputStream outputStream = openFileOutput(hdurl, Context.MODE_PRIVATE);
//                    currentPic.compress(Bitmap.CompressFormat.valueOf(fileExtension.toUpperCase()), 80, outputStream);
//                    outputStream.flush();
//                    outputStream.close();



                connection.disconnect();
                response.close();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> {
                imageView.setImageBitmap(currentPic);
                explanationText.setText(explanation);
                titleText.setText(title);
                hdUrlText.setText(hdurl);
                sdUrlText.setText(sdurl);
            });
        });

    }


}
