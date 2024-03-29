package test.com.homeaway.viewmodels;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import test.com.homeaway.models.Venue;
import test.com.homeaway.repositories.VenuesRepository;

/**
 * ViewModel representing one venue related data only.
 * It is used in {@link test.com.homeaway.adapters.SearchListAdapter} class
 * for each of the row displayed
 */
public class VenueViewModel extends BaseObservable {

    private Venue mVenue;

    private VenuesRepository repository = new VenuesRepository();

    public void setVenue(Venue venue) {
        mVenue = venue;
        notifyChange();
    }

    public Venue getVenue() {
        return mVenue;
    }

    @Bindable
    public String getName() {
        return mVenue.name;
    }

    @Bindable
    public String getCategory() {

        if(mVenue.categories != null && mVenue.categories.size() > 0) {
            return mVenue.categories.get(0).name;
        }

        return "";
    }

    @Bindable
    public String getImageUrl() {
        if(mVenue.categories != null &&
                mVenue.categories.size() > 0 &&
                mVenue.categories.get(0).icon != null) {
            return mVenue.categories.get(0).icon.getUrl();
        }

        return "";
    }

    @Bindable
    public String getDistance() {
        if(mVenue.location != null) {
            return String.valueOf(mVenue.location.getDistanceInKm());
        }
        return "0";
    }

    @Bindable
    public String getWebsiteLink() {
        return TextUtils.isEmpty(mVenue.url) ? "" : mVenue.url;
    }

    @Bindable
    public String getFormattedAddress() {
        if(mVenue.location.formattedAddress != null) {
            StringBuilder builder = new StringBuilder();

            if(mVenue.location.formattedAddress.length > 0) {
                builder.append(mVenue.location.formattedAddress[0]);
            }

            for(int i=1; i<mVenue.location.formattedAddress.length; i++) {
                builder.append("\n");
                builder.append(mVenue.location.formattedAddress[i]);
            }

            return builder.toString();
        }
        return "";
    }

    @Bindable
    public boolean isFavourite() {
         return mVenue.favourite;
    }

    private void favourite() {
        mVenue.favourite = true;
        repository.insert(mVenue);
        notifyChange();
    }

    private void unFavourite() {
        mVenue.favourite = false;
        repository.delete(mVenue);
        notifyChange();
    }

    public void toggleFavourite() {
        if(isFavourite()) unFavourite();
        else favourite();
    }

}
