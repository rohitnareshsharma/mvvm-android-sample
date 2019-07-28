package test.com.homeaway;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.PersistableBundle;

import org.greenrobot.eventbus.EventBus;

import test.com.homeaway.databinding.ActivityVenueDetailBinding;
import test.com.homeaway.models.Venue;
import test.com.homeaway.viewmodels.VenueDetailViewModel;

public class VenueDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private VenueDetailViewModel venueDetailViewModel;

    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityVenueDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_venue_detail);
        venueDetailViewModel = ViewModelProviders.of(this).get(VenueDetailViewModel.class);

        // This will be null for orientation change cases as we have already removed it.
        // But we have saved it in our view model which will survive config changes.
        Venue venue = EventBus.getDefault().removeStickyEvent(Venue.class);
        if(venue != null) {
            venueDetailViewModel.setVenue(venue);
        }

        binding.setVenueModel(venueDetailViewModel.getModel());

        // Setup title bar
        setSupportActionBar(binding.toolbar);
        setTitle(venueDetailViewModel.getModel().getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

        // Add a marker in Sydney and move the camera
        Venue venue = venueDetailViewModel.getModel().getVenue();
        LatLng venueLocation = new LatLng(venue.location.lat, venue.location.lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(venueLocation).title(venue.name));

        LatLng seatleCenter = new LatLng(47.6062,-122.3321);
        mMap.addMarker(new MarkerOptions().position(seatleCenter).title("Center of Seatle"));

        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(venueLocation);
        builder.include(seatleCenter);

        LatLngBounds bounds = builder.build();

        mapFragment.getView().post(() -> {

            int height = mapFragment.getView().getHeight();

            // FIXME this padding logic needs testing across different devices.
            // Short on time. Leaving it in try catch
            try{
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, height/3));
            } catch (Exception e) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLocation, 14));
            }

            marker.showInfoWindow();
        });

    }

}
