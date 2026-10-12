package com.fidelis.mykampuslite.viewmodel;

import com.fidelis.mykampuslite.data.repository.AkademikRepository;
import com.fidelis.mykampuslite.model.Mahasiswa;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MahasiswaViewModel extends BaseAkademikViewModel<Mahasiswa> {

    public MahasiswaViewModel(AkademikRepository<Mahasiswa> repository) {
        super(repository);
    }

    @Override
    protected String getFieldUnik() {
        return FIELD_NIM;
    }

    /**
     * Menerima input MENTAH (String) dari form. Seluruh validasi dilakukan oleh
     * setter di Model; ViewModel hanya mengumpulkan hasilnya.
     * Jika ada yang gagal -> tidak ada satu pun data yang masuk ke Room.
     */
    public void simpan(String nama, String email, String nim, String jurusan, String ipkTeks) {
        final Mahasiswa baru = new Mahasiswa();

        Mahasiswa lama = getItemDiedit();
        if (lama != null) {                       // mode edit: pertahankan identitas
            baru.setId(lama.getId());
            baru.setCreatedAt(lama.getCreatedAt());
        }

        Map<String, String> errors = new LinkedHashMap<>();
        validasi(errors, FIELD_NAMA, () -> baru.setNama(nama));
        validasi(errors, FIELD_EMAIL, () -> baru.setEmail(email));
        validasi(errors, FIELD_NIM, () -> baru.setNim(nim));
        validasi(errors, FIELD_JURUSAN, () -> baru.setJurusan(jurusan));
        validasi(errors, FIELD_IPK, () -> baru.setIpk(parseIpk(ipkTeks)));

        if (!errors.isEmpty()) {
            laporkanError(errors);
            return;
        }
        laporkanError(Collections.<String, String>emptyMap());
        simpanItem(baru);
    }

    static double parseIpk(String teks) {
        if (teks == null || teks.trim().isEmpty()) {
            throw new IllegalArgumentException("IPK tidak boleh kosong");
        }
        try {
            return Double.parseDouble(teks.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("IPK harus berupa angka");
        }
    }
}
