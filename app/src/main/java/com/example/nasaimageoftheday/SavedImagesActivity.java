package com.example.nasaimageoftheday;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;

public class SavedImagesActivity extends BaseActivity {
    private final ArrayList<Bitmap> pictureList = new ArrayList<>();
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_images);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Declare Views
        GridView gridView = findViewById(R.id.gridView);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.savedImagesActivityTitle));


        // Get images from internal storage
        String[] pictureFiles = getApplicationContext().fileList();
        for (int i = 1; i < pictureFiles.length; i++) {
            File file = new File(getFilesDir(), pictureFiles[i]);
            Bitmap picture = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (picture != null) {
                pictureList.add(picture);
            } else {
                Log.e("SavedImagesActivity", "Failed to decode image: " + file.getAbsolutePath());
            }
        }


        gridView.setAdapter(adapter = new MyListAdapter());
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {return pictureList.size();}

        @Override
        public Bitmap getItem(int position) {return pictureList.get(position);}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            if (newView == null) {
                newView = inflater.inflate(R.layout.picture_layout, parent, false);
            }

            ImageView imageView = newView.findViewById(R.id.imageView);
            imageView.setImageBitmap(getItem(position));

            return newView;
        }

    }
}