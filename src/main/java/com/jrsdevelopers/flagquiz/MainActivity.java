


package com.jrsdevelopers.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity {

    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";

    private boolean phoneDevice = true;
    private boolean preferencesChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(
                        preferencesChangeListener);


        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // if device is a tablet, sets phoneDevice to false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false;

        // if a phone portrait is the only orientation
        if (phoneDevice)
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (preferencesChanged) {
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(
                    R.id.quizFragment);
            quizFragment.updateGuessRows(
                    PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(
                    PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    private OnSharedPreferenceChangeListener preferencesChangeListener =
            new OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true;

                    MainActivityFragment quisFragment = (MainActivityFragment)
                            getSupportFragmentManager().findFragmentById(R.id.quizFragment);
                    if (key.equals(CHOICES)) {
                        quisFragment.updateGuessRows(sharedPreferences);
                        quisFragment.resetQuiz();
                    }
                    else if (key.equals(REGIONS)){
                        Set<String> regions =
                                sharedPreferences.getStringSet(REGIONS, null);
                        if (regions != null && regions.size()>0){
                            quisFragment.updateRegions(sharedPreferences);
                            quisFragment.resetQuiz();
                        }
                        else {
                            SharedPreferences.Editor editor =
                                    sharedPreferences.edit();
                            regions.add(getString(R.string.default_region));
                            editor.putStringSet(REGIONS, regions);
                            editor.apply();

                            Toast.makeText(MainActivity.this,
                                    R.string.default_region_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(MainActivity.this,
                            R.string.reset_quiz,
                            Toast.LENGTH_SHORT).show();
                }

            };
}