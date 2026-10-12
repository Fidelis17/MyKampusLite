package com.fidelis.mykampuslite.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Locale;
import java.util.Objects;

/**
 * [INHERITANCE - Level 3] Mahasiswa extends Person.
 * Field: nim, jurusan, ipk (semua private + validasi di setter).
 *
 * NIM di-index UNIQUE -> mencegah NIM duplikat dari level database
 * (masalah "Excel berantakan" pada studi kasus).
 */
@Entity(tableName = "mahasiswa", indices = {@Index(value = {"nim"}, unique = true)})
public class Mahasiswa extends Person {

    private String nim = "";
    private String jurusan = "";
    private double ipk;

    /** Constructor kosong -> dipakai Room saat membaca data dari database. */
    public Mahasiswa() {
    }

    /** Constructor praktis; @Ignore agar Room tidak bingung memilih constructor. */
    @Ignore
    public Mahasiswa(String nama, String email, String nim, String jurusan, double ipk) {
        setNama(nama);
        setEmail(email);
        setNim(nim);
        setJurusan(jurusan);
        setIpk(ipk);
    }

    @Override
    public String getRole() {
        return "Mahasiswa";
    }

    @Override
    public String getInfoDetail() {
        return String.format(Locale.US, "NIM %s  •  %s  •  IPK %.2f", nim, jurusan, ipk);
    }

    /** [POLYMORPHISM] Implementasi khusus Mahasiswa berdasarkan IPK. */
    @Override
    public String getStatusAkademik() {
        if (ipk > 3.5) return "Cumlaude";
        if (ipk > 3.0) return "Memuaskan";
        if (ipk < 2.0) return "Peringatan";
        return "Cukup";
    }

    public String getNim() {
        return nim;
    }

    /** Harus tepat 10 digit angka, jika tidak -> IllegalArgumentException. */
    public void setNim(String nim) {
        String bersih = nim == null ? "" : nim.trim();
        if (bersih.isEmpty()) {
            throw new IllegalArgumentException("NIM tidak boleh kosong");
        }
        if (!bersih.matches("\\d{10}")) {
            throw new IllegalArgumentException("NIM harus 10 digit angka");
        }
        this.nim = bersih;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        String bersih = jurusan == null ? "" : jurusan.trim();
        if (bersih.isEmpty()) {
            throw new IllegalArgumentException("Jurusan tidak boleh kosong");
        }
        this.jurusan = bersih;
    }

    public double getIpk() {
        return ipk;
    }

    /** Range 0.0 - 4.0. */
    public void setIpk(double ipk) {
        if (Double.isNaN(ipk) || ipk < 0.0 || ipk > 4.0) {
            throw new IllegalArgumentException("IPK harus di antara 0.0 - 4.0");
        }
        this.ipk = ipk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Mahasiswa that = (Mahasiswa) o;
        return Double.compare(ipk, that.ipk) == 0
                && nim.equals(that.nim)
                && jurusan.equals(that.jurusan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nim, jurusan, ipk);
    }
}
