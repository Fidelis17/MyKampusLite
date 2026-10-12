package com.fidelis.mykampuslite.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * [INHERITANCE - Level 2] Person extends BaseEntity.
 * Field: nama, email. Memiliki method abstract getRole().
 *
 * Person mengimplementasikan EvaluasiAkademik tetapi sengaja TIDAK
 * mengimplementasikan getStatusAkademik() -> diserahkan ke subclass
 * ([POLYMORPHISM]). Dengan begitu satu Adapter cukup memegang tipe Person.
 */
public abstract class Person extends BaseEntity implements EvaluasiAkademik {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private String nama = "";
    private String email = "";

    /** [ABSTRACTION] Wajib di-override Mahasiswa & Dosen. */
    public abstract String getRole();

    /** Ringkasan satu baris untuk ditampilkan di list (tiap subclass beda). */
    public abstract String getInfoDetail();

    public String getNama() {
        return nama;
    }

    /** Tidak boleh kosong dan minimal 3 karakter. */
    public void setNama(String nama) {
        String bersih = nama == null ? "" : nama.trim();
        if (bersih.isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong");
        }
        if (bersih.length() < 3) {
            throw new IllegalArgumentException("Nama minimal 3 karakter");
        }
        this.nama = bersih;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String bersih = email == null ? "" : email.trim();
        if (bersih.isEmpty()) {
            throw new IllegalArgumentException("Email tidak boleh kosong");
        }
        if (!EMAIL_PATTERN.matcher(bersih).matches()) {
            throw new IllegalArgumentException("Format email tidak valid");
        }
        this.email = bersih;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Person that = (Person) o;
        return nama.equals(that.nama) && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nama, email);
    }
}
