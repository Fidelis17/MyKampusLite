package com.fidelis.mykampuslite.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fidelis.mykampuslite.model.Mahasiswa;

import java.util.List;

@Dao
public interface MahasiswaDao {

    /** LiveData -> Room otomatis query di background thread & auto-update UI. */
    @Query("SELECT * FROM mahasiswa ORDER BY nama COLLATE NOCASE ASC")
    LiveData<List<Mahasiswa>> getAll();

    /** Gagal (SQLiteConstraintException) jika NIM sudah ada (unique index). */
    @Insert
    long insert(Mahasiswa mahasiswa);

    @Update
    int update(Mahasiswa mahasiswa);

    @Delete
    int delete(Mahasiswa mahasiswa);
}
