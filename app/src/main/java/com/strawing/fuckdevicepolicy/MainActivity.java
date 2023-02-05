package com.strawing.fuckdevicepolicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.strawing.fuckdevicepolicy.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private boolean isLSPosed = true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isLSPosed", isLSPosed);
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        TextView textview_first = (TextView) findViewById(R.id.textview_first);
        textview_first.setMovementMethod(LinkMovementMethod.getInstance());
        SharedPreferences loadSharedPreferences;
        if (null != savedInstanceState) {
            isLSPosed = savedInstanceState.getBoolean("isLSPosed", true);
        }
        try {
            loadSharedPreferences = createDeviceProtectedStorageContext().getSharedPreferences("policies", Context.MODE_WORLD_READABLE);

        } catch (SecurityException ignored) {
            loadSharedPreferences = createDeviceProtectedStorageContext().getSharedPreferences("policies", Context.MODE_PRIVATE);
            isLSPosed = false;
        }

        TextView textview_mode = (TextView) findViewById(R.id.textview_mode);
        if (isLSPosed) {
            textview_mode.setText(Html.fromHtml(getResources().getString(R.string.lsposed_mode)));
        } else {
            textview_mode.setText(Html.fromHtml(getResources().getString(R.string.compact_mode)));
        }
        SharedPreferences sharedPreferences = loadSharedPreferences;
        String policies = sharedPreferences.getString("policies_string", "no_install_unknown_sources_globally\nno_debugging_features");
        final EditText policiesTextEdit = (EditText) findViewById(R.id.editTextTextMultiLine);
        policiesTextEdit.setText(policies);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("policies_string", policiesTextEdit.getText().toString().trim().replaceAll("(?m)^[ \t]*\r?\n", ""));
                editor.apply();
                editor.commit();
                policiesTextEdit.setText(sharedPreferences.getString("policies_string", "no_install_unknown_sources_globally\nno_debugging_features"));


                File pkgFolder = createDeviceProtectedStorageContext().getDataDir();
                if (pkgFolder.exists()) {
                    pkgFolder.setReadable(true, false);
                    pkgFolder.setExecutable(true, false);
                    File sharedPrefsFolder = new File(pkgFolder.getAbsolutePath() + "/shared_prefs");

                    if (sharedPrefsFolder.exists()) {
                        sharedPrefsFolder.setExecutable(true, false);
                        sharedPrefsFolder.setReadable(true, false);
                        File f = new File(sharedPrefsFolder.getAbsolutePath() + "/" + "policies" + ".xml");
                        if (f.exists()) {
                            f.setExecutable(true, false);
                            f.setReadable(true, false);
                        }
                    }
                }

                Snackbar.make(view, "Saved. Please reboot your device to take effect.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}