package com.fidelis.mykampuslite.data.repository;

import androidx.lifecycle.LiveData;

import com.fidelis.mykampuslite.model.Person;

import java.util.List;

/**
 * [ABSTRACTION] Kontrak akses data akademik (interface, bukan class).
 * ViewModel HANYA boleh bergantung pada interface ini (Dependency Inversion),
 * sehingga sumber data bisa diganti (Room -> REST API) tanpa mengubah ViewModel.
 */
public interface AkademikRepository<T extends Person> {

    LiveData<List<T>> getAll();

    void insert(T item, RepositoryCallback callback);

    void update(T item, RepositoryCallback callback);

    void delete(T item, RepositoryCallback callback);
}
