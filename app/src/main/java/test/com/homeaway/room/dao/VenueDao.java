package test.com.homeaway.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import test.com.homeaway.models.Venue;

@Dao
public interface VenueDao {

    @Query("SELECT * FROM venue")
    LiveData<List<Venue>> getAll();

    @Insert
    void insert(Venue venues);

    @Delete
    void delete(Venue venue);
}
