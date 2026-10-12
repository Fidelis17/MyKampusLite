package com.fidelis.mykampuslite.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fidelis.mykampuslite.data.repository.AkademikRepository;
import com.fidelis.mykampuslite.data.repository.DuplicateDataException;
import com.fidelis.mykampuslite.data.repository.RepositoryCallback;
import com.fidelis.mykampuslite.model.Mahasiswa;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Membuktikan [ABSTRACTION]: ViewModel diuji dengan repository PALSU (fake)
 * tanpa Room/Android, karena ViewModel hanya bergantung pada interface.
 */
public class MahasiswaViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();

    private FakeRepository repo;
    private MahasiswaViewModel vm;

    @Before
    public void setUp() {
        repo = new FakeRepository();
        vm = new MahasiswaViewModel(repo);
    }

    @Test
    public void inputInvalid_tidakMasukRepository_danErrorPerField() {
        vm.simpan("Al", "salah", "123", "", "5.0");

        assertTrue(repo.inserted.isEmpty());
        Map<String, String> err = vm.getErrorField().getValue();
        assertNotNull(err);
        assertNotNull(err.get(BaseAkademikViewModel.FIELD_NAMA));
        assertNotNull(err.get(BaseAkademikViewModel.FIELD_EMAIL));
        assertNotNull(err.get(BaseAkademikViewModel.FIELD_NIM));
        assertNotNull(err.get(BaseAkademikViewModel.FIELD_JURUSAN));
        assertNotNull(err.get(BaseAkademikViewModel.FIELD_IPK));
    }

    @Test
    public void ipkBukanAngka_menjadiErrorIpk() {
        vm.simpan("Fidelis Farel", "f@k.ac.id", "2341010003", "SI", "abc");
        assertEquals("IPK harus berupa angka",
                vm.getErrorField().getValue().get(BaseAkademikViewModel.FIELD_IPK));
        assertTrue(repo.inserted.isEmpty());
    }

    @Test
    public void inputValid_masukRepository() {
        vm.simpan("Fidelis Farel", "f@k.ac.id", "2341010003", "SI", "3,75");

        assertEquals(1, repo.inserted.size());
        assertEquals(3.75, repo.inserted.get(0).getIpk(), 0.0);
        assertTrue(vm.getErrorField().getValue().isEmpty());
        assertNotNull(vm.getSimpanBerhasil().getValue());
    }

    @Test
    public void nimDuplikat_munculSebagaiErrorDiFieldNim() {
        repo.failWithDuplicate = true;
        vm.simpan("Fidelis Farel", "f@k.ac.id", "2341010003", "SI", "3.0");
        assertEquals("NIM sudah terdaftar",
                vm.getErrorField().getValue().get(BaseAkademikViewModel.FIELD_NIM));
    }

    @Test
    public void modeEdit_memanggilUpdate_danMempertahankanId() {
        Mahasiswa lama = new Mahasiswa("Fidelis Farel", "f@k.ac.id", "2341010003", "SI", 3.0);
        lama.setId(7);
        vm.mulaiEdit(lama);
        assertNotNull(vm.getSedangDiedit().getValue());

        vm.simpan("Fidelis Farel", "f@k.ac.id", "2341010003", "SI", "3.9");

        assertTrue(repo.inserted.isEmpty());
        assertEquals(1, repo.updated.size());
        assertEquals(7, repo.updated.get(0).getId());
        assertNull(vm.getSedangDiedit().getValue());
    }

    // ---- Repository palsu: implementasi interface tanpa database ----
    private static class FakeRepository implements AkademikRepository<Mahasiswa> {
        final List<Mahasiswa> inserted = new ArrayList<>();
        final List<Mahasiswa> updated = new ArrayList<>();
        boolean failWithDuplicate = false;

        @Override
        public LiveData<List<Mahasiswa>> getAll() {
            return new MutableLiveData<>(new ArrayList<Mahasiswa>());
        }

        @Override
        public void insert(Mahasiswa item, RepositoryCallback cb) {
            if (failWithDuplicate) {
                cb.onError(new DuplicateDataException("NIM sudah terdaftar"));
                return;
            }
            inserted.add(item);
            cb.onSuccess();
        }

        @Override
        public void update(Mahasiswa item, RepositoryCallback cb) {
            updated.add(item);
            cb.onSuccess();
        }

        @Override
        public void delete(Mahasiswa item, RepositoryCallback cb) {
            cb.onSuccess();
        }
    }
}
