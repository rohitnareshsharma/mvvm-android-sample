package test.com.homeaway.viewmodels;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.List;
import test.com.homeaway.livedata.SingleEventMutableLiveData;
import test.com.homeaway.models.Venue;
import test.com.homeaway.repositories.VenuesRepository;

/**
 * ViewModel associated with {@link test.com.homeaway.SearchPlacesActivity} screen.
 */
public class SearchPlacesViewModel extends ViewModel {

    // For logging purpose
    private static final String TAG = SearchPlacesViewModel.class.getSimpleName();

    // Main mRepository responsible for fetching data for this viewmodel
    private VenuesRepository mRepository = new VenuesRepository();

    // Current search query in the system. It's important to keep track of it to avoid
    // redundant calls
    public MutableLiveData<String> mQuery = new MutableLiveData<>();

    // Main data set representing the list of venues against current query. UI should
    // not use this directly. It is not processed fully. For e.g merging favourite details
    private MutableLiveData<List<Venue>> mVenueListRaw = new MutableLiveData<List<Venue>>(){{
        // Init it with null
        setValue(null);
    }};

    // List of venues saved by user as favourite
    private LiveData<List<Venue>> mFavouriteVenues = mRepository.getAllFavouriteVenues();

    // Main data set representing processed Venues. UI should use this data set for UI display
    public MediatorLiveData<List<Venue>> mVenueList = new MediatorLiveData<>();

    // public constructor
    @SuppressWarnings("unchecked")
    public SearchPlacesViewModel() {

        // This will be triggered everytime when venue list received from web services changes
        // or whenever user saves or remove any venue entry from favourite collection.
        Observer<List<Venue>> venuesAndFavouriteVenuesObserver = changedList -> {
            new ProcessVenueListTask(mVenueList).execute(mFavouriteVenues, mVenueListRaw);
        };

        mVenueList.addSource(mVenueListRaw, venuesAndFavouriteVenuesObserver);
        mVenueList.addSource(mFavouriteVenues, venuesAndFavouriteVenuesObserver);
    }

    // AsyncTask managing the modification of venue list to carry favourite details for now.
    // We may add more processing later.
    private static class ProcessVenueListTask extends AsyncTask<LiveData<List<Venue>>, Void, List<Venue>> {

        private MediatorLiveData<List<Venue>> mData;

        ProcessVenueListTask(MediatorLiveData<List<Venue>> data) {
            mData = data;
        }

        @Override
        protected List<Venue> doInBackground(LiveData<List<Venue>>... params) {

            List<Venue> favourite = params[0].getValue();
            List<Venue> list = params[1].getValue();

            if(list == null || favourite == null) {
                // Nothing to filter. Set result as is
                return list;
            }

            // Prepare set for contains check.
            HashSet<String> set = new HashSet<>();
            for(Venue venue : favourite) {
                set.add(venue.id);
            }

            // See if given venue is favourite or not
            for(Venue venue : list) {
                venue.favourite = set.contains(venue.id);
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<Venue> list) {
            super.onPostExecute(list);
            mData.setValue(list);
        }
    }

    /**
     * Returns indicator whether progress is happening or not
     */
    public MutableLiveData<Boolean> progress = new MutableLiveData<Boolean>() {{
        setValue(false);
    }};


    // Current selected venue by the user. We do not want observers to trigger
    // again and again because of lifecycle event. Only to trigger when there is a new value
    // available. So we used SingleEventMutableLiveData. Its a custom implementation
    public SingleEventMutableLiveData<Venue> selectedVenue = new SingleEventMutableLiveData<>();

    // Represent latest error occurred for current query
    public SingleEventMutableLiveData<String> errorMessage = new SingleEventMutableLiveData<>();

    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit() called with " + query);
        return false;
    }

    public boolean onQueryTextChange(String newQuery) {
        Log.d(TAG, "onQueryTextChange() called with " + newQuery);
        search(newQuery);
        return false;
    }

    /**
     * Perform network search against the query submitted
     * @param newQuery
     */
    public void search(String newQuery) {

        if(newQuery == null || TextUtils.isEmpty(newQuery.trim())) {
            mQuery.setValue(null);
            mVenueListRaw.setValue(null);
            return;
        }

        if(mQuery.getValue() != null &&
           mQuery.getValue().equals(newQuery) &&
           mVenueListRaw.getValue() != null) {

            Log.w(TAG, "We may have already published result for " + newQuery);
            return;
        }

        mQuery.setValue(newQuery);
        mRepository.fetchVenues(mQuery, mVenueListRaw, errorMessage, progress);
    }

}
