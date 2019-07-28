package test.com.homeaway.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.List;

import test.com.homeaway.models.Venue;

public class VenueMapViewModel extends ViewModel {

    List<Venue> listOfVenues;

    public List<Venue> getListOfVenues() {
        return listOfVenues;
    }

    public void setListOfVenues(List<Venue> listOfVenues) {
        this.listOfVenues = listOfVenues;
    }

}
