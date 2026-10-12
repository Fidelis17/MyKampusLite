package com.fidelis.mykampuslite.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fidelis.mykampuslite.data.repository.AkademikRepository;
import com.fidelis.mykampuslite.data.repository.DuplicateDataException;
import com.fidelis.mykampuslite.data.repository.RepositoryCallback;
import com.fidelis.mykampuslite.model.Person;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ViewModel dasar (abstract) untuk Mahasiswa & Dosen.
 *
 * - Hanya bergantung pada INTERFACE AkademikRepository (bukan implementasi).
 * - TIDAK meng-import satu pun class android.widget / android.view.
 * - Menyimpan state (daftar, item yang sedang diedit, error) -> selamat dari rotasi layar.
 * - Menampung seluruh logic validasi/penyimpanan sehingga Activity/Fragment "bodoh".
 */
public abstract class BaseAkademikViewModel<T extends Person> extends ViewModel {

    // Kunci field error (dipakai UI untuk menaruh pesan di kolom yang tepat)
    public static final String FIELD_NAMA = "nama";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_NIM = "nim";
    public static final String FIELD_JURUSAN = "jurusan";
    public static final String FIELD_IPK = "ipk";
    public static final String FIELD_NIDN = "nidn";
    public static final String FIELD_MATKUL = "mataKuliah";

    private final AkademikRepository<T> repository;
    private final LiveData<List<T>> daftar;

    private final MutableLiveData<T> sedangDiedit = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> errorField = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> pesan = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> simpanBerhasil = new MutableLiveData<>();

    protected BaseAkademikViewModel(AkademikRepository<T> repository) {
        this.repository = repository;
        this.daftar = repository.getAll();
    }

    /** Nama field unik (NIM / NIDN) untuk menempatkan pesan "sudah terdaftar". */
    protected abstract String getFieldUnik();

    // ---------- State yang diamati UI ----------

    public LiveData<List<T>> getDaftar() {
        return daftar;
    }

    public LiveData<T> getSedangDiedit() {
        return sedangDiedit;
    }

    public LiveData<Map<String, String>> getErrorField() {
        return errorField;
    }

    public LiveData<Event<String>> getPesan() {
        return pesan;
    }

    public LiveData<Event<Boolean>> getSimpanBerhasil() {
        return simpanBerhasil;
    }

    // ---------- Aksi dari UI ----------

    public void mulaiEdit(T item) {
        sedangDiedit.setValue(item);
        errorField.setValue(Collections.<String, String>emptyMap());
    }

    public void batalEdit() {
        sedangDiedit.setValue(null);
        errorField.setValue(Collections.<String, String>emptyMap());
    }

    public void hapus(T item) {
        T diedit = sedangDiedit.getValue();
        if (diedit != null && diedit.getId() == item.getId()) {
            sedangDiedit.setValue(null);
        }
        repository.delete(item, new RepositoryCallback() {
            @Override
            public void onSuccess() {
                pesan.postValue(new Event<>("Data berhasil dihapus"));
            }

            @Override
            public void onError(Exception e) {
                pesan.postValue(new Event<>("Gagal menghapus: " + e.getMessage()));
            }
        });
    }

    // ---------- Util untuk subclass ----------

    protected T getItemDiedit() {
        return sedangDiedit.getValue();
    }

    protected void laporkanError(Map<String, String> errors) {
        errorField.setValue(errors);
        if (!errors.isEmpty()) {
            pesan.setValue(new Event<>("Periksa kembali isian yang bertanda merah"));
        }
    }

    /**
     * Jalankan satu langkah validasi (setter Model). Jika Model melempar
     * IllegalArgumentException, pesannya dicatat sebagai error untuk field itu.
     */
    protected void validasi(Map<String, String> errors, String field, Runnable langkah) {
        try {
            langkah.run();
        } catch (IllegalArgumentException e) {
            errors.put(field, e.getMessage());
        }
    }

    /** Simpan item yang SUDAH lolos validasi: insert (baru) atau update (mode edit). */
    protected void simpanItem(T item) {
        final boolean modeEdit = sedangDiedit.getValue() != null;
        RepositoryCallback callback = new RepositoryCallback() {
            @Override
            public void onSuccess() {
                sedangDiedit.postValue(null);
                pesan.postValue(new Event<>(
                        modeEdit ? "Data berhasil diperbarui" : "Data berhasil disimpan"));
                simpanBerhasil.postValue(new Event<>(Boolean.TRUE));
            }

            @Override
            public void onError(Exception e) {
                if (e instanceof DuplicateDataException) {
                    errorField.postValue(
                            Collections.singletonMap(getFieldUnik(), e.getMessage()));
                } else {
                    pesan.postValue(new Event<>("Gagal menyimpan: " + e.getMessage()));
                }
            }
        };

        if (modeEdit) {
            repository.update(item, callback);
        } else {
            repository.insert(item, callback);
        }
    }
}
