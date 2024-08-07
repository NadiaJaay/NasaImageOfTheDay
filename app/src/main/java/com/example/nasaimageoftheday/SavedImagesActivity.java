package com.example.nasaimageoftheday;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SavedImagesActivity extends BaseActivity {
    private final Map<File, Bitmap> pictureList = new HashMap<>();
    private final ArrayList<Map.Entry<File, Bitmap>> data = new ArrayList<>();
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_images); // Overriden by BaseActivity
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Declare Views
        GridView gridView = findViewById(R.id.gridView);
        // Update title on Toolbar
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(this.getResources().getString(R.string.savedImagesActivityTitle));


        /* Get images from internal storage
         * For each image in list (start at item 1, the first item is the app logo?), decode the picture
         * Then, add the picture to a HashMap<File, Bitmap>
         */
        String[] pictureFiles = getApplicationContext().fileList();
        for (int i = 1; i < pictureFiles.length; i++) {
            File file = new File(getFilesDir(), pictureFiles[i]);
            Bitmap picture = BitmapFactory.decodeFile(file.getAbsolutePath());

            // Add picture if it is decoded
            if (picture != null) {
                pictureList.put(file, picture);
            }
        }
        // Add all HashMap entries to the ArrayList, then sort the array by last date modified (oldest image first).
        data.addAll(pictureList.entrySet());
        data.sort(Comparator.comparingLong(a -> a.getKey().lastModified()));

        // Set the adapter for the GridView
        gridView.setAdapter(adapter = new MyListAdapter());

        /* On Click Listener
         * Show and alert dialog with the date the image was saved, and the file size.
         *
         */
        gridView.setOnItemClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(data.get(pos).getKey().getName())
                    .setMessage(getString(R.string.savedImageDate)+" "+ new Date(data.get(pos).getKey().lastModified())+"\n"
                    +getString(R.string.savedImageSize)+" "+getFileSizeKilobytes(data.get(pos).getKey()))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        });

        /* On Long Click Listener
         * Create an alert dialog to delete the file.
         *
         */
        gridView.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.deleteImageTitle)
                    // If the user deletes an image, then delete the image from internal storage, then remove from arrayList and HashMap.
                    // Then notify the adapter
                    .setPositiveButton(R.string.dialogPosDelBtn, (click, arg) -> {
                        File file = data.get(pos).getKey();
                        file.delete();
                        pictureList.remove(file);
                        data.remove(pos);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.dialogNegBtn, (click, arg) -> {})
                    .create().show();
            return false;
        });
    }

    // Adapter for GridView
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {return data.size();}

        // Get the HashMap entry from the ArrayList
        @Override
        public Map.Entry<File, Bitmap> getItem(int position) {return data.get(position);}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            // Find the ImageView from picture_layout.xml and set it to the Bitmap entry value.
            if (newView == null) {
                newView = inflater.inflate(R.layout.picture_layout, parent, false);

                ImageView imageView = newView.findViewById(R.id.imageView);
                imageView.setImageBitmap(getItem(position).getValue());
            }

            return newView;
        }

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
            alertDialogBuilder.setTitle(getString(R.string.savedImagesDialogTitle))
                    .setMessage(getString(R.string.savedImagesDialogMessage))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        }

        return true;
    }

    // Small utility method to retrieve the file size in Kilobytes. Uses BigDecimal and rounds the number to 2 decimal places.
    private String getFileSizeKilobytes(File file) {
        return new BigDecimal(file.length() / 1024).setScale(2, RoundingMode.HALF_UP) + " kb";
    }
}