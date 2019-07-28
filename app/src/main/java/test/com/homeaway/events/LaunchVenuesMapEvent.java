package test.com.homeaway.events;

import java.util.List;

import test.com.homeaway.models.Venue;

/**
 * Used for EventBus message passing while all venue map list.
 * Its more efficient pass a large list this way as compare to
 * serializable or parcelable (Coca Cola)
 */
public class LaunchVenuesMapEvent {

    private List<Venue> mList;

    public LaunchVenuesMapEvent(List<Venue> list) {
        mList = list;
    }

    public List<Venue> getVenueList() {
        return mList;
    }
}
