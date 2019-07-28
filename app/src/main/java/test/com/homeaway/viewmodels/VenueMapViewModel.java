package test.com.homeaway.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.List;

import test.com.homeaway.models.Venue;

/**
 * ViewModel associated with {@link test.com.homeaway.VenuesMapActivity} class.
 * It carries only list of venues for now.
 */
public class VenueMapViewModel extends ViewModel {

    private List<Venue> listOfVenues;

    public List<Venue> getListOfVenues() {
        return listOfVenues;
    }

    public void setListOfVenues(List<Venue> listOfVenues) {
        this.listOfVenues = listOfVenues;
    }

}
