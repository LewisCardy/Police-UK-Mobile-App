package com.uk.policeapp.databases;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.uk.policeapp.data.ForceCrime;

import java.util.List;

/**
 *   DAO for {@link ForceCrime} entities
 */


@Dao
public interface ForceCrimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(ForceCrime forceCrime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<ForceCrime> forceCrime);

    @Delete
    public void delete(ForceCrime forceCrime);

    @Delete
    public void delete(List<ForceCrime> forceCrime);

    @Query("SELECT * FROM ForceCrime WHERE force = :force")
    public List<ForceCrime> findByForce(String force);

    @Query("DELETE FROM ForceCrime WHERE force = :force")
    public void deleteByForce(String force);
}
