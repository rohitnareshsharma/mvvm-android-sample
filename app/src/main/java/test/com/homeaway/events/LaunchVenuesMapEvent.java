package test.com.homeaway.events;

import java.util.List;

import test.com.homeaway.models.Venue;

public class LaunchVenuesMapEvent {

    private List<Venue> mList;

    public LaunchVenuesMapEvent(List<Venue> list) {
        mList = list;
    }

    public List<Venue> getVenueList() {
        return mList;
    }
}
