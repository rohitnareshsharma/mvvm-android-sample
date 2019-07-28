package test.com.homeaway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import test.com.homeaway.events.LaunchVenuesMapEvent;
import test.com.homeaway.models.Venue;
import test.com.homeaway.viewmodels.VenueDetailViewModel;
import test.com.homeaway.viewmodels.VenueMapViewModel;

/**
 * A simple full screen activity displaying a list of venue.
 * Its a raw screen for now. If you will click on a map marker
 * it will launch {@link VenueDetailActivity} class for the selected
 * marker.
 */
public class VenuesMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = VenuesMapActivity.class.getSimpleName();

    // View model associated with this activity.
    private VenueMapViewModel venueMapViewModel;

    private GoogleMap mMap;

    private SupportMapFragment mapFragment;

    // Mapping displayed Venue with marker id.
    // This is done to launch detail activity once a marker is clicked
    private HashMap<String, Venue> markerVenueMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_map);

        venueMapViewModel = ViewModelProviders.of(this).get(VenueMapViewModel.class);

        // This will be null for orientation change cases as we have already removed it.
        // But we have saved it in our view model which will survive config changes.
        // I wanted to use EventBus for fast large data sharing across activities.
        // 50 to 100 parcellable or serialisable could have killed the performance.
        LaunchVenuesMapEvent event = EventBus.getDefault().removeStickyEvent(LaunchVenuesMapEvent.class);
        if(event != null) {
            venueMapViewModel.setListOfVenues(event.getVenueList());
        }

        // still check for valid model.
        // Android may recreate activities without data.
        // Safe check for preventing crash and gracefully handling this
        if(venueMapViewModel.getListOfVenues() == null) {
            Log.e(TAG, "This should not have happened. venueMapViewModel.getListOfVenues() is null");
            finish();
            return;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<Venue> list = venueMapViewModel.getListOfVenues();
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for(Venue venue : list) {
            LatLng venueLocation = new LatLng(venue.location.lat, venue.location.lng);
            Marker marker = mMap.addMarker(new MarkerOptions().position(venueLocation).title(venue.name));
            builder.include(venueLocation);
            markerVenueMap.put(marker.getId(), venue);
        }

        LatLngBounds bounds = builder.build();
        mapFragment.getView().post(() -> {

            int mapHeight = mapFragment.getView().getHeight();
            int mapWidth = mapFragment.getView().getWidth();

            int padding = Math.min(mapWidth, mapHeight)/3;

            // FIXME this padding logic needs testing across different devices.
            // Short on time. Leaving it in try catch
            try{
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });

        // Set the info window and launch Venue detail activity once it is clicked
        mMap.setOnInfoWindowClickListener(marker -> {
            if(marker != null) {
                Venue venue = markerVenueMap.get(marker.getId());
                if(venue != null) {
                    EventBus.getDefault().postSticky(venue);
                    Intent intent = new Intent(this, VenueDetailActivity.class);
                    this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

}
