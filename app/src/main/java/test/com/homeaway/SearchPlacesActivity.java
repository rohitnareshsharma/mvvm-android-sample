package test.com.homeaway;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import org.greenrobot.eventbus.EventBus;

import test.com.homeaway.adapters.SearchListAdapter;
import test.com.homeaway.databinding.ActivitySearchPlacesBinding;
import test.com.homeaway.events.LaunchVenuesMapEvent;
import test.com.homeaway.provider.MySuggestionProvider;
import test.com.homeaway.viewmodels.SearchPlacesViewModel;

/**
 * Main dashboard for allowing user to search for places around
 * center of seattle. Its a searchable activity with single top
 * launch mode
 */
public class SearchPlacesActivity extends AppCompatActivity {

    // For logging purpose
    private static final String TAG = SearchPlacesActivity.class.getSimpleName();

    // Main viewmodel associated with this activity
    private SearchPlacesViewModel viewModel;

    // Adapter for the search results list
    private SearchListAdapter adapter;

    // Binding carrying all the views for this screen
    private ActivitySearchPlacesBinding activityMainBinding;

    // Search Bar View
    private SearchView searchView;

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


        // If we have results display them in a map
        activityMainBinding.fab.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new LaunchVenuesMapEvent(viewModel.venueList.getValue()));
            Intent intent = new Intent(this, VenuesMapActivity.class);
            startActivity(intent);
        });

    }

    private void initObservers() {
        viewModel.venueList.observe(this, venues -> {
            // Set the data to the list
            adapter.setData(venues);

            // Control the visibility of floating button on the basis of venue list size
            if(venues != null && venues.size() > 0 ) {
                activityMainBinding.fab.show();
            } else {
                activityMainBinding.fab.hide();
            }
        });

        // Do monitor error cases
        viewModel.errorMessage.observe(this, s -> {
            Log.e(TAG, s + "");

            Snackbar.make(activityMainBinding.fab
                     , s +  "", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        });

        // Launch venue detail activity once user select one from the list
        viewModel.selectedVenue.observe(this, venue -> {
            EventBus.getDefault().postSticky(venue);
            Intent intent = new Intent(this, VenueDetailActivity.class);
            this.startActivity(intent);
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_suggestion : {
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        MySuggestionProvider.AUTHORITY,
                        MySuggestionProvider.MODE);
                suggestions.clearHistory();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSearchWidget(Menu menu) {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // See if we have any previous query
        final String previousQuery = viewModel.query.getValue();

        // Expand the view by default. This will going to clear viewModel.getQuery string
        item.expandActionView();

        // place of setting this text change listener is important
        // Comment this line below if we want to enable search only when
        // go is pressed
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

        // Set the suggestion listener.
        // default one was not setting the text right and was using intent approach
        // we are overriding this behaviour.
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor= searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                // 2 is the index of col containing suggestion name.
                // This is a dangerous assumption. FIXME
                String suggestion = cursor.getString(2);
                searchView.setQuery(suggestion,true);
                return true;
            }
        });

        // Set any previous query if we have
        searchView.setQuery(previousQuery,false);

        // Hide the keyboard by default
        searchView.clearFocus();
    }


    // As this is a searchable activity with singleTop Launch mode
    // we need to handle incoming intent.
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    // Check for search action
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Save the query in db for later use.
            // Currently this is saving suggestion only when ok is pressed on query
            // As we are using a text change listener we should save all words results.
            // That may bombard suggestions. Need to think around on this functionality.
            // FIXME
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY,
                    MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            // Ask repository to fetch venue list for given query
            viewModel.search(query);
        }
    }
}
