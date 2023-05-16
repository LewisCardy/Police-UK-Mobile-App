package com.uk.policeapp.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.uk.policeapp.data.PoliceOfficer;

import java.util.List;

/**
 *   DAO for {@link PoliceOfficer} entities
 */

@Dao
public interface PoliceOfficerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert( PoliceOfficer policeOfficer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<PoliceOfficer> policeOfficer);

    @Delete
    public void delete(PoliceOfficer policeOfficer);

    @Delete
    public void delete(List<PoliceOfficer> policeOfficer);

    @Query("SELECT * FROM PoliceOfficer WHERE force = :force")
    public List<PoliceOfficer> findByForce(String force);

    @Query("DELETE FROM PoliceOfficer WHERE force = :force")
    public void deleteByForce(String force);
}
