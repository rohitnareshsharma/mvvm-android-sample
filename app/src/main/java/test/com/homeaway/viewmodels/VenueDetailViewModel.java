package test.com.homeaway.viewmodels;

import androidx.lifecycle.ViewModel;

import test.com.homeaway.models.Venue;

public class VenueDetailViewModel extends ViewModel {

    private VenueViewModel mModel;

    public VenueViewModel getModel() {
        return mModel;
    }

    public void setVenue(Venue venue) {
        mModel = new VenueViewModel();
        mModel.setVenue(venue);
    }

}
