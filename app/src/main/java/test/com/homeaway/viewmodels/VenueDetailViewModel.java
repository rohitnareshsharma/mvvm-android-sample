package test.com.homeaway.viewmodels;

import androidx.lifecycle.ViewModel;

import test.com.homeaway.models.Venue;

/**
 * ViewModel associated with {@link test.com.homeaway.VenueDetailActivity} class.
 * It carries very minimum data for now
 */
public class VenueDetailViewModel extends ViewModel {

    private VenueViewModel mModel;

    public VenueViewModel getVenueModel() {
        return mModel;
    }

    public void setVenue(Venue venue) {
        mModel = new VenueViewModel();
        mModel.setVenue(venue);
    }

}
