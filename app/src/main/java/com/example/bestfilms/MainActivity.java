package com.example.bestfilms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.bestfilms.classes.FilmAdapter;
import com.example.bestfilms.api.Request;
import com.example.bestfilms.db.DatabaseAdapter;
import com.example.bestfilms.classes.CardFilm;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FilmAdapter.ItemClickListener{

    private LinearLayout mLayoutWifiError;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mPopularButton;
    private Button mFavouritesButton;
    private Button mRestartButton;
    private MenuItem mMenuFilter;
    private SearchView mSearchView;

    private FilmAdapter mFilmAdapter;
    private DatabaseAdapter mDatabaseAdapter;
    private LoadBestFilms mTaskLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.popular);

        mRecyclerView = findViewById(R.id.rv);
        mProgressBar = findViewById(R.id.pb);
        mLayoutWifiError = findViewById(R.id.wifi_error);
        mRestartButton = findViewById(R.id.restart);
        mPopularButton = findViewById(R.id.popular);
        mFavouritesButton = findViewById(R.id.favourites);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFilmAdapter = new FilmAdapter(this, new ArrayList<>());
        mFilmAdapter.setClickListeners(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFilmAdapter);

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutWifiError.setVisibility(View.INVISIBLE);
                new LoadBestFilms().execute();
            }
        });
        mPopularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.popular);
                mFilmAdapter.clearListFilms();
                mTaskLoader = new LoadBestFilms();
                mTaskLoader.execute();
                mFavouritesButton.setEnabled(true);
            }
        });
        mFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.favourites);
                if(mTaskLoader != null && mTaskLoader.getStatus() == AsyncTask.Status.RUNNING){
                    mTaskLoader.cancel(false);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                if(mLayoutWifiError.getVisibility() == View.VISIBLE){
                    mLayoutWifiError.setVisibility(View.INVISIBLE);
                }
                mFilmAdapter.refreshListFilms(mDatabaseAdapter.getAllFilms());
                if(!mSearchView.isIconified()){
                    mSearchView.setQuery("", true);
                }
                mPopularButton.setEnabled(true);
            }
        });

        mDatabaseAdapter = new DatabaseAdapter(this);
        mDatabaseAdapter.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        mMenuFilter = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) mMenuFilter.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mFilmAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mFilmAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mTaskLoader = new LoadBestFilms();
        mTaskLoader.execute();
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        long filmId = mFilmAdapter.getItemByIndex(position).getFilmId();
        Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
        intent.putExtra("filmId", filmId);
        this.startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        CardFilm cardFilm = mFilmAdapter.longFilmPressEvent(position);
        if(cardFilm.isFavourites()){
            mDatabaseAdapter.insert(cardFilm);
        }else{
            mDatabaseAdapter.delete(cardFilm.getFilmId());
            if(!mFavouritesButton.isEnabled()) {
                mFilmAdapter.removeFilms(position);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mDatabaseAdapter.close();
        super.onDestroy();
    }

    private class LoadBestFilms extends AsyncTask<Void, Void, ArrayList<CardFilm>> {

        private void selectFavourites(ArrayList<CardFilm> uploaded, ArrayList<CardFilm> compared){
            for(CardFilm load : uploaded){
                for(CardFilm comp : compared){
                    if(load.getFilmId() == comp.getFilmId()){
                        load.setFavourites(true);
                        break;
                    }
                }
            }
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<CardFilm> doInBackground(Void... voids) {
            try {
                return Request.loadListPopularFilms();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<CardFilm> uploaded){
            mProgressBar.setVisibility(View.INVISIBLE);
            if(uploaded == null){
                mLayoutWifiError.setVisibility(View.VISIBLE);
                return;
            }
            selectFavourites(uploaded, mDatabaseAdapter.getAllFilms());
            mFilmAdapter.refreshListFilms(uploaded);
            if(!mSearchView.isIconified()){
                CharSequence query = mSearchView.getQuery();
                mSearchView.setQuery(query, query.length() > 0);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}