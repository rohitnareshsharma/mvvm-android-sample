package test.com.homeaway.viewmodels;


import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    // Main repository responsible for fetching data for this viewmodel
    private VenuesRepository repository = new VenuesRepository();

    // Current search query in the system. It's important to keep track of it to avoid
    // redundant calls
    public MutableLiveData<String> query = new MutableLiveData<>();

    // Main data set representing the list of venues against current query.
    public MutableLiveData<List<Venue>> venueList = new MutableLiveData<List<Venue>>(){{
        // Init it with null
        setValue(null);
    }};

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
            query.setValue(null);
            venueList.setValue(null);
            return;
        }

        if(query.getValue() != null &&
           query.getValue().equals(newQuery) &&
           venueList.getValue() != null) {

            Log.w(TAG, "We may have already published result for " + newQuery);
            return;
        }

        query.setValue(newQuery);
        repository.fetchVenues(query, venueList, errorMessage, progress);
    }

}
