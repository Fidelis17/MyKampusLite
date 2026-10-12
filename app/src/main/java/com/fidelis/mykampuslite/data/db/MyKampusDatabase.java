package com.fidelis.mykampuslite.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fidelis.mykampuslite.data.dao.DosenDao;
import com.fidelis.mykampuslite.data.dao.MahasiswaDao;
import com.fidelis.mykampuslite.model.Dosen;
import com.fidelis.mykampuslite.model.Mahasiswa;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database lokal MyKampus Lite (offline).
 * TIDAK memakai allowMainThreadQueries(): semua tulis/ubah/hapus dijalankan
 * lewat Executor (DB_EXECUTOR), sedangkan baca (LiveData) ditangani Room.
 */
@Database(entities = {Mahasiswa.class, Dosen.class}, version = 1, exportSchema = false)
public abstract class MyKampusDatabase extends RoomDatabase {

    public static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(2);

    private static volatile MyKampusDatabase INSTANCE;

    public abstract MahasiswaDao mahasiswaDao();

    public abstract DosenDao dosenDao();

    public static MyKampusDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MyKampusDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    MyKampusDatabase.class,
                                    "mykampus_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
