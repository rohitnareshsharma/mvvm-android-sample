package test.com.homeaway.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyError;
import com.easyvolley.EasyVolleyResponse;
import com.easyvolley.NetworkClient;
import com.easyvolley.NetworkRequest;

import java.util.List;

import test.com.homeaway.models.SearchVenueResponseWrapper;
import test.com.homeaway.models.Venue;

/**
 * Main repository preparing the Venue list data.
 */
public class PlacesRepository {

    private static final String TAG = PlacesRepository.class.getSimpleName();

    private static final String FOURSQUARE_BASE_URL = "https://api.foursquare.com";

    private static final String VENUE_SEARCH_ENDPOINT = "/v2/venues/search?";

    private static final String VENUUE_SEARCH_URL = FOURSQUARE_BASE_URL + VENUE_SEARCH_ENDPOINT;

    private static final String CLIENT_ID = "MXI2G1KJTRC1NJVZCTRLTYIYSGPNVG2HVN131KKGHPLG1410";

    private static final String CLIENT_SECRET = "I04VUPUP1R3KFLPQ3HPGX53DAETAV2UWJFLKETVEZLCZYOH3";

    private NetworkRequest currentSearchRequest;

    /**
     * Fetches venue details from Foursquare.
     *
     * Sample curl is like below
     * curl -X GET -G 'https://api.foursquare.com/v2/venues/search' \
     *     -d client_id="CLIENT_ID"  \
     *     -d client_secret="CLIENT_SECRET" \
     *     -d near="Seattle,+WA" \
     *     -d query="coffee" \
     *     -d v="20180401" \
     *     -d limit=20
     *
     */
    public void fetchVenues(final MutableLiveData<String> query,
                            final MutableLiveData<List<Venue>> list,
                            final MutableLiveData<String> errorMessage,
                            final MutableLiveData<Boolean> progress) {

        // Cancel any previous request if requested.
        // This be important for saving extra calls
        // It do not guarantee to cancel the request if already established
        if(currentSearchRequest != null ) {
            Log.d(TAG, "Cancelling request");
            currentSearchRequest.cancelRequest();
        }

        // Get the query string. It be required later to verify we are publishing
        // results of requested query only.
        final String queryString = query.getValue();

        progress.setValue(true);
        currentSearchRequest = NetworkClient.get(VENUUE_SEARCH_URL)
                .addQueryParam("client_id", CLIENT_ID)
                .addQueryParam("client_secret", CLIENT_SECRET)
                .addQueryParam("ll", "47.6062,-122.3321")
                .addQueryParam("query", query.getValue())
                .addQueryParam("v", "20180401")
                .addQueryParam("limit", "50")
                .setCallback(new Callback<SearchVenueResponseWrapper>() {
                    @Override
                    public void onSuccess(SearchVenueResponseWrapper v, EasyVolleyResponse response) {
                        Log.d(TAG, "Received results for query " + queryString);

                        // Check if the query is not changed. If changed do not publish results
                        if(queryString.equals(query.getValue())) {
                            list.setValue(v.response.venues);
                        } else {
                            Log.d(TAG, "Received results are no longer required as query string has changed");
                        }

                        progress.setValue(false);
                        currentSearchRequest = null;
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        Log.e(TAG, "Something went wrong " + error.mMessage);
                        errorMessage.setValue(error.mMessage == null ?
                                              "Something went wrong in fetchVenues function":
                                              error.mMessage);
                        progress.setValue(false);
                        currentSearchRequest = null;
                    }
                }).execute();
    }

}
