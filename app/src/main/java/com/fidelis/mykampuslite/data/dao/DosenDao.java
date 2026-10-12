package com.fidelis.mykampuslite.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fidelis.mykampuslite.model.Dosen;

import java.util.List;

@Dao
public interface DosenDao {

    @Query("SELECT * FROM dosen ORDER BY nama COLLATE NOCASE ASC")
    LiveData<List<Dosen>> getAll();

    @Insert
    long insert(Dosen dosen);

    @Update
    int update(Dosen dosen);

    @Delete
    int delete(Dosen dosen);
}
