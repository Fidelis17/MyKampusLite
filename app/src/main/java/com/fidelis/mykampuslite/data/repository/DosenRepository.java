package com.fidelis.mykampuslite.data.repository;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;

import com.fidelis.mykampuslite.data.dao.DosenDao;
import com.fidelis.mykampuslite.model.Dosen;

import java.util.List;
import java.util.concurrent.Executor;

/** Implementasi AkademikRepository berbasis Room untuk Dosen. */
public class DosenRepository implements AkademikRepository<Dosen> {

    private final DosenDao dao;
    private final Executor executor;

    public DosenRepository(DosenDao dao, Executor executor) {
        this.dao = dao;
        this.executor = executor;
    }

    @Override
    public LiveData<List<Dosen>> getAll() {
        return dao.getAll();
    }

    @Override
    public void insert(Dosen item, RepositoryCallback callback) {
        executor.execute(() -> {
            try {
                dao.insert(item);
                if (callback != null) callback.onSuccess();
            } catch (SQLiteConstraintException e) {
                if (callback != null) {
                    callback.onError(new DuplicateDataException("NIDN sudah terdaftar", e));
                }
            } catch (Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }

    @Override
    public void update(Dosen item, RepositoryCallback callback) {
        executor.execute(() -> {
            try {
                item.touch();
                dao.update(item);
                if (callback != null) callback.onSuccess();
            } catch (SQLiteConstraintException e) {
                if (callback != null) {
                    callback.onError(new DuplicateDataException("NIDN sudah terdaftar", e));
                }
            } catch (Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }

    @Override
    public void delete(Dosen item, RepositoryCallback callback) {
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
