package com.example.nasaimageoftheday;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (useToolbar()) {
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navView = findViewById(R.id.navView);
            navView.setNavigationItemSelectedListener(this);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    protected boolean useToolbar() {return true;}

    // Navigation Menu Item selection handler
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navSavedImages) {
            if (!(this instanceof SavedImagesActivity)) startActivity(new Intent(this, SavedImagesActivity.class));
        } else if (id == R.id.navExit) {
            finishAffinity();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navTodayImage) {
                startActivity(new Intent(this, ImageActivity.class));
        } else if (id == R.id.navHelp) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.homeDialogTitle))
                    .setMessage(getString(R.string.homeDialogMessage))
                    .setNeutralButton(R.string.dialogNeutralBtn, (click, arg) -> {})
                    .create().show();
        }

        return true;
    }
}