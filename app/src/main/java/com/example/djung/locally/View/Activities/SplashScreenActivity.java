package com.example.djung.locally.View.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.R;

/**
 * Created by Angy Chung on 2016-12-05.
 */

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize the in season database the first time the app starts
        new InitializeData().execute();
    }

    private class InitializeData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new VendorItemDatabase(getApplicationContext()).initializeDb();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // done loading db so start MainActivity
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);

            // shut down this activity so we can't return to it
            finish();
        }
    }
}
