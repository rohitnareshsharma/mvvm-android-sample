package test.com.homeaway.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import test.com.homeaway.R;
import test.com.homeaway.databinding.SearchListItemBinding;
import test.com.homeaway.models.Venue;
import test.com.homeaway.viewmodels.SearchPlacesViewModel;
import test.com.homeaway.viewmodels.VenueViewModel;

/**
 * Adapter for list displaying venues. It is currently used in
 * {@link test.com.homeaway.SearchPlacesActivity}.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchResultViewHolder> {

    // For logging purpose
    private static String TAG = SearchListAdapter.class.getSimpleName();

    // Inflator service for creating views from xml
    private LayoutInflater mInflator;

    // Viewmodel associated with {@link test.com.homeaway.SearchPlacesActivity}.
    // Item selected event needs to be passed to the parent activity for further
    // decision
    private SearchPlacesViewModel mSearchPlacesViewModel;

    // Main data set
    private List<Venue> data = new ArrayList<>();

    /**
     * Sets the data shown by the adapter
     * @param data
     */
    public void setData(List<Venue> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public SearchListAdapter(LayoutInflater inflater, SearchPlacesViewModel searchPlacesViewModel) {
        mInflator = inflater;
        mSearchPlacesViewModel = searchPlacesViewModel;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchListItemBinding binding = DataBindingUtil.inflate(mInflator, R.layout.search_list_item, parent, false);
        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void onVenueSelected(VenueViewModel model) {
        mSearchPlacesViewModel.selectedVenue.setValue(model.getVenue());
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {

        private SearchListItemBinding mBinding;

        public SearchResultViewHolder(SearchListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setVenueModel(new VenueViewModel());
            mBinding.setAdapter(SearchListAdapter.this);
        }

        public void bind(Venue venue) {
            mBinding.getVenueModel().setVenue(venue);
            mBinding.executePendingBindings();
        }
    }

}
