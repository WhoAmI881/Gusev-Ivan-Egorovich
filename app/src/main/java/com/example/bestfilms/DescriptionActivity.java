package com.example.bestfilms;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bestfilms.api.Request;
import com.example.bestfilms.classes.DescriptionFilm;

import org.json.JSONException;

import java.io.IOException;

public class DescriptionActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private TextView mDescription;
    private TextView mName;
    private TextView mGenres;
    private TextView mCountries;
    private TextView mTitleGenres;
    private TextView mTitleCountries;
    private LinearLayout mLayoutWifiError;
    private Button mRestartButton;
    private long mFilmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Bundle arguments = getIntent().getExtras();
        mFilmId = arguments.getLong("filmId");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = findViewById(R.id.pb);
        mImageView = findViewById(R.id.poster);
        mDescription = findViewById(R.id.description);
        mName = findViewById(R.id.nameRu);
        mGenres = findViewById(R.id.genres);
        mCountries = findViewById(R.id.countries);
        mTitleGenres = findViewById(R.id.genre);
        mTitleCountries = findViewById(R.id.country);
        mLayoutWifiError = findViewById(R.id.wifi_error);
        mRestartButton = findViewById(R.id.restart);

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutWifiError.setVisibility(View.INVISIBLE);
                new LoadDescriptionFilm().execute(mFilmId);
            }
        });
        new LoadDescriptionFilm().execute(mFilmId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadDescriptionFilm extends AsyncTask<Long, Void, DescriptionFilm> {

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected DescriptionFilm doInBackground(Long... filmId) {
            try {
                return Request.loadDescriptionFilmById(filmId[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(DescriptionFilm uploaded){
            mProgressBar.setVisibility(View.INVISIBLE);
            if(uploaded == null){
                mLayoutWifiError.setVisibility(View.VISIBLE);
                return;
            }
            mTitleGenres.setVisibility(View.VISIBLE);
            mTitleCountries.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(uploaded.getPoster());
            mDescription.setText(uploaded.getDescription());
            mName.setText(uploaded.getNameRu());
            mGenres.setText(uploaded.getGenres());
            mCountries.setText(uploaded.getCountries());
        }
    }
}