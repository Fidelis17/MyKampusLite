package com.fidelis.mykampuslite.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fidelis.mykampuslite.data.db.MyKampusDatabase;
import com.fidelis.mykampuslite.data.repository.DosenRepository;
import com.fidelis.mykampuslite.data.repository.MahasiswaRepository;

/**
 * Titik "perakitan" (wiring): satu-satunya tempat implementasi konkret
 * repository dipilih lalu disuntikkan ke ViewModel lewat constructor.
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public ViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        MyKampusDatabase db = MyKampusDatabase.getInstance(application);

        if (modelClass.isAssignableFrom(MahasiswaViewModel.class)) {
            return (T) new MahasiswaViewModel(
                    new MahasiswaRepository(db.mahasiswaDao(), MyKampusDatabase.DB_EXECUTOR));
        }
        if (modelClass.isAssignableFrom(DosenViewModel.class)) {
            return (T) new DosenViewModel(
                    new DosenRepository(db.dosenDao(), MyKampusDatabase.DB_EXECUTOR));
        }
        throw new IllegalArgumentException("ViewModel tidak dikenal: " + modelClass.getName());
    }
}
