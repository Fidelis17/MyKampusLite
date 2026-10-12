package com.fidelis.mykampuslite.viewmodel;

import com.fidelis.mykampuslite.data.repository.AkademikRepository;
import com.fidelis.mykampuslite.model.Dosen;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DosenViewModel extends BaseAkademikViewModel<Dosen> {

    public DosenViewModel(AkademikRepository<Dosen> repository) {
        super(repository);
    }

    @Override
    protected String getFieldUnik() {
        return FIELD_NIDN;
    }

    public void simpan(String nama, String email, String nidn, String mataKuliah) {
        final Dosen baru = new Dosen();

        Dosen lama = getItemDiedit();
        if (lama != null) {
            baru.setId(lama.getId());
            baru.setCreatedAt(lama.getCreatedAt());
        }

        Map<String, String> errors = new LinkedHashMap<>();
        validasi(errors, FIELD_NAMA, () -> baru.setNama(nama));
        validasi(errors, FIELD_EMAIL, () -> baru.setEmail(email));
        validasi(errors, FIELD_NIDN, () -> baru.setNidn(nidn));
        validasi(errors, FIELD_MATKUL, () -> baru.setMataKuliahDiampu(mataKuliah));

        if (!errors.isEmpty()) {
            laporkanError(errors);
            return;
        }
        laporkanError(Collections.<String, String>emptyMap());
        simpanItem(baru);
    }
}
