package test.com.homeaway.viewmodels;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import test.com.homeaway.livedata.SingleEventMutableLiveData;
import test.com.homeaway.models.Venue;
import test.com.homeaway.repositories.PlacesRepository;

public class SearchPlacesViewModel extends ViewModel {

    private static final String TAG = SearchPlacesViewModel.class.getSimpleName();

    private PlacesRepository repository = new PlacesRepository();

    private MutableLiveData<String> query = new MutableLiveData<>();

    private MutableLiveData<List<Venue>> venueList = new MutableLiveData<List<Venue>>(){{
        // Init it with null
        setValue(null);
    }};

    private SingleEventMutableLiveData<Venue> selectedVenue = new SingleEventMutableLiveData<>();

    private SingleEventMutableLiveData<String> errorMessage = new SingleEventMutableLiveData<>();

    public MutableLiveData<String> getQuery() {
        return query;
    }

    public MutableLiveData<Venue> getSelectedVenue() {
        return selectedVenue;
    }

    public MutableLiveData<Integer> progress = new MutableLiveData<Integer>() {{
        setValue(View.INVISIBLE); // 4 for invisible
    }};

    public LiveData<Integer> listVisibility = Transformations.map(venueList, input ->
            (input == null || input.size() == 0) ? View.INVISIBLE : View.VISIBLE);

    public LiveData<Integer> emptyViewVisibility = Transformations.map(venueList,
            input -> (input == null || input.size() == 0) ? View.VISIBLE : View.INVISIBLE);

    public MutableLiveData<List<Venue>> getVenueList() {
        return venueList;
    }

    public SingleEventMutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit() called with " + query);
        return false;
    }

    public boolean onQueryTextChange(String newQuery) {
        Log.d(TAG, "onQueryTextChange() called with " + newQuery);
        search(newQuery);
        return false;
    }

    public void search(String newQuery) {

        if(newQuery == null || TextUtils.isEmpty(newQuery.trim())) {
            query.setValue(null);
            venueList.setValue(null);
            return;
        }

        query.setValue(newQuery);
        repository.fetchVenues(query, venueList, errorMessage, progress);
    }

}
