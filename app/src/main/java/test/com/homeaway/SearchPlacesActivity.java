package test.com.homeaway;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import org.greenrobot.eventbus.EventBus;

import test.com.homeaway.adapters.SearchListAdapter;
import test.com.homeaway.databinding.ActivitySearchPlacesBinding;
import test.com.homeaway.events.LaunchVenuesMapEvent;
import test.com.homeaway.viewmodels.SearchPlacesViewModel;

public class SearchPlacesActivity extends AppCompatActivity {

    private static final String TAG = SearchPlacesActivity.class.getSimpleName();

    private SearchPlacesViewModel viewModel;

    private SearchListAdapter adapter;

    private ActivitySearchPlacesBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewAndModel();
        initObservers();
    }

    private void initViewAndModel() {

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_places);
        setSupportActionBar(activityMainBinding.toolbar);

        LayoutInflater inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        viewModel = ViewModelProviders.of(this).get(SearchPlacesViewModel.class);
        adapter = new SearchListAdapter(inflator, viewModel);

        activityMainBinding.setViewModel(viewModel);
        activityMainBinding.setAdapter(adapter);
        activityMainBinding.setLifecycleOwner(this);
        activityMainBinding.executePendingBindings();


        activityMainBinding.fab.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new LaunchVenuesMapEvent(viewModel.getVenueList().getValue()));
            Intent intent = new Intent(this, VenuesMapActivity.class);
            startActivity(intent);
        });

    }

    private void initObservers() {
        viewModel.getVenueList().observe(this, venues -> {
            // Set the data to the list
            adapter.setData(venues);

            // Control the visibility of floating button on the basis of
            if(venues != null && venues.size() > 0 ) {
                activityMainBinding.fab.show();
            } else {
                activityMainBinding.fab.hide();
            }
        });

        viewModel.getErrorMessage().observe(this, s -> {
            Log.e(TAG, s + "");

            Snackbar.make(activityMainBinding.fab
                     , s +  "", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        });

        viewModel.getSelectedVenue().observe(this, venue -> {
            EventBus.getDefault().postSticky(venue);
            Intent intent = new Intent(this, VenueDetailActivity.class);
            this.startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Setup the search widget
        setupSearchWidget(menu);

        return true;
    }

    private void setupSearchWidget(Menu menu) {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // See if we have any previous query
        final String previousQuery = viewModel.getQuery().getValue();

        // Expand the view by default. This will going to clear viewModel.getQuery string
        item.expandActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return viewModel.onQueryTextSubmit(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return viewModel.onQueryTextChange(newText);
            }
        });

        // Set any previous query if we have
        searchView.setQuery(previousQuery,false);

        // Hide the keyboard by default
        searchView.clearFocus();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            viewModel.search(query);
        }
    }
}
