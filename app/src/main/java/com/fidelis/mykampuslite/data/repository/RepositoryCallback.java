package com.fidelis.mykampuslite.data.repository;

/**
 * Callback hasil operasi tulis. Dipanggil dari BACKGROUND thread,
 * sehingga ViewModel harus memakai postValue() (bukan setValue()).
 */
public interface RepositoryCallback {
    void onSuccess();

    void onError(Exception e);
}
