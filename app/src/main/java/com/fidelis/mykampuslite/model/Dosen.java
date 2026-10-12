package com.fidelis.mykampuslite.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

/**
 * [INHERITANCE - Level 3] Dosen extends Person.
 * Field: nidn, mataKuliahDiampu (disimpan sebagai teks dipisah koma).
 */
@Entity(tableName = "dosen", indices = {@Index(value = {"nidn"}, unique = true)})
public class Dosen extends Person {

    private String nidn = "";
    private String mataKuliahDiampu = "";

    public Dosen() {
    }

    @Ignore
    public Dosen(String nama, String email, String nidn, String mataKuliahDiampu) {
        setNama(nama);
        setEmail(email);
        setNidn(nidn);
        setMataKuliahDiampu(mataKuliahDiampu);
    }

    @Override
    public String getRole() {
        return "Dosen";
    }

    @Override
    public String getInfoDetail() {
        return "NIDN " + nidn + "  •  " + getJumlahMataKuliah() + " MK: " + mataKuliahDiampu;
    }

    /** [POLYMORPHISM] Implementasi khusus Dosen berdasarkan jumlah mata kuliah. */
    @Override
    public String getStatusAkademik() {
        return getJumlahMataKuliah() > 3 ? "Beban Lebih" : "Normal";
    }

    public int getJumlahMataKuliah() {
        if (mataKuliahDiampu.isEmpty()) return 0;
        return mataKuliahDiampu.split(",").length;
    }

    public String getNidn() {
        return nidn;
    }

    /** NIDN: 10 digit angka. */
    public void setNidn(String nidn) {
        String bersih = nidn == null ? "" : nidn.trim();
        if (bersih.isEmpty()) {
            throw new IllegalArgumentException("NIDN tidak boleh kosong");
        }
        if (!bersih.matches("\\d{10}")) {
            throw new IllegalArgumentException("NIDN harus 10 digit angka");
        }
        this.nidn = bersih;
    }

    public String getMataKuliahDiampu() {
        return mataKuliahDiampu;
    }

    /**
     * Daftar mata kuliah dipisah koma, minimal 1. Disimpan dalam bentuk
     * ternormalisasi ("A, B, C"): spasi dirapikan & entri kosong dibuang.
     */
    public void setMataKuliahDiampu(String mataKuliahDiampu) {
        String mentah = mataKuliahDiampu == null ? "" : mataKuliahDiampu;
        StringBuilder hasil = new StringBuilder();
        for (String bagian : mentah.split(",")) {
            String mk = bagian.trim();
            if (mk.isEmpty()) continue;
            if (hasil.length() > 0) hasil.append(", ");
            hasil.append(mk);
        }
        if (hasil.length() == 0) {
            throw new IllegalArgumentException("Mata kuliah diampu minimal 1");
        }
        this.mataKuliahDiampu = hasil.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Dosen that = (Dosen) o;
        return nidn.equals(that.nidn) && mataKuliahDiampu.equals(that.mataKuliahDiampu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nidn, mataKuliahDiampu);
    }
}
