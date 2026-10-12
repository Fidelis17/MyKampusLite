package com.fidelis.mykampuslite.model;

/**
 * [POLYMORPHISM] Kontrak evaluasi akademik.
 * Setiap tipe (Mahasiswa, Dosen) mengimplementasikan aturan statusnya sendiri.
 */
public interface EvaluasiAkademik {
    String getStatusAkademik();
}
