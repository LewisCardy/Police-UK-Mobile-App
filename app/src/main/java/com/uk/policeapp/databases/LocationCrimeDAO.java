package com.uk.policeapp.databases;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.uk.policeapp.data.LocationCrime;

import java.util.List;

/**
*   DAO for {@link LocationCrime} entities
*/

@Dao
public interface LocationCrimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(LocationCrime locationCrime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<LocationCrime> locationCrime);

    @Delete
    public void delete(LocationCrime locationCrime);

    @Delete
    public void delete(List<LocationCrime> locationCrime);

    @Query("SELECT * FROM LocationCrime WHERE date = :date AND searchedLongitude = :searchedLongitude AND searchedLatitude = :searchedLatitude")
    public List<LocationCrime> findBySearchValues(String date, Float searchedLongitude, Float searchedLatitude);

    @Query("DELETE FROM LocationCrime WHERE date = :date AND searchedLongitude = :searchedLongitude AND searchedLatitude = :searchedLatitude")
    public void DeleteBySearchValues(String date, Float searchedLongitude, Float searchedLatitude);



}
