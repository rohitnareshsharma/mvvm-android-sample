package test.com.homeaway.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import test.com.homeaway.models.Venue;

@Dao
public interface VenueDao {

    @Query("SELECT * FROM venues")
    LiveData<List<Venue>> getAll();

    @Query("SELECT * FROM venues where venues.favourite = 1")
    LiveData<List<Venue>> getAllFavouriteVenues();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Venue venues);

    @Delete
    void delete(Venue venue);
}
