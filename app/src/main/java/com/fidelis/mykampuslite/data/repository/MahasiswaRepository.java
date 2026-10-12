package com.fidelis.mykampuslite.data.repository;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;

import com.fidelis.mykampuslite.data.dao.MahasiswaDao;
import com.fidelis.mykampuslite.model.Mahasiswa;

import java.util.List;
import java.util.concurrent.Executor;

/** Implementasi AkademikRepository berbasis Room untuk Mahasiswa. */
public class MahasiswaRepository implements AkademikRepository<Mahasiswa> {

    private final MahasiswaDao dao;
    private final Executor executor;

    public MahasiswaRepository(MahasiswaDao dao, Executor executor) {
        this.dao = dao;
        this.executor = executor;
    }

    @Override
    public LiveData<List<Mahasiswa>> getAll() {
        return dao.getAll();
    }

    @Override
    public void insert(Mahasiswa item, RepositoryCallback callback) {
        executor.execute(() -> {
            try {
                dao.insert(item);
                if (callback != null) callback.onSuccess();
            } catch (SQLiteConstraintException e) {
                if (callback != null) {
                    callback.onError(new DuplicateDataException("NIM sudah terdaftar", e));
                }
            } catch (Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }

    @Override
    public void update(Mahasiswa item, RepositoryCallback callback) {
        executor.execute(() -> {
            try {
                item.touch();
                dao.update(item);
                if (callback != null) callback.onSuccess();
            } catch (SQLiteConstraintException e) {
                if (callback != null) {
                    callback.onError(new DuplicateDataException("NIM sudah terdaftar", e));
                }
            } catch (Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }

    @Override
    public void delete(Mahasiswa item, RepositoryCallback callback) {
        executor.execute(() -> {
            try {
                dao.delete(item);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }
}
