package com.fidelis.mykampuslite.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/** Auto-check pilar OOP pada Model (berjalan di JVM biasa, tanpa emulator). */
public class ModelOopTest {

    // ---------------- INHERITANCE ----------------
    @Test
    public void hierarki_3Level_danAbstract() {
        assertTrue(Modifier.isAbstract(BaseEntity.class.getModifiers()));
        assertTrue(Modifier.isAbstract(Person.class.getModifiers()));
        assertEquals(BaseEntity.class, Person.class.getSuperclass());
        assertEquals(Person.class, Mahasiswa.class.getSuperclass());
        assertEquals(Person.class, Dosen.class.getSuperclass());
    }

    @Test
    public void getRole_dioverrideSubclass() {
        assertEquals("Mahasiswa", new Mahasiswa().getRole());
        assertEquals("Dosen", new Dosen().getRole());
    }

    // ---------------- ENCAPSULATION ----------------
    @Test
    public void semuaFieldModel_private() {
        Class<?>[] kelas = {BaseEntity.class, Person.class, Mahasiswa.class, Dosen.class};
        for (Class<?> k : kelas) {
            for (Field f : k.getDeclaredFields()) {
                if (f.isSynthetic()) continue;
                assertTrue(k.getSimpleName() + "." + f.getName() + " harus private",
                        Modifier.isPrivate(f.getModifiers()));
            }
        }
    }

    @Test
    public void nama_kosongDanKurangDari3_ditolak() {
        Mahasiswa m = new Mahasiswa();
        assertThrows(() -> m.setNama(""));
        assertThrows(() -> m.setNama("   "));
        assertThrows(() -> m.setNama(null));
        assertThrows(() -> m.setNama("Al"));
        m.setNama("Ali");
        assertEquals("Ali", m.getNama());
    }

    @Test
    public void nim_harus10DigitAngka() {
        Mahasiswa m = new Mahasiswa();
        assertThrows(() -> m.setNim("123"));
        assertThrows(() -> m.setNim("12345678901"));
        assertThrows(() -> m.setNim("23410100O39")); // huruf O
        assertThrows(() -> m.setNim(""));
        m.setNim("2341010003");
        assertEquals("2341010003", m.getNim());
    }

    @Test
    public void ipk_range0sampai4() {
        Mahasiswa m = new Mahasiswa();
        assertThrows(() -> m.setIpk(-0.1));
        assertThrows(() -> m.setIpk(4.01));
        assertThrows(() -> m.setIpk(Double.NaN));
        m.setIpk(0.0);
        m.setIpk(4.0);
        assertEquals(4.0, m.getIpk(), 0.0);
    }

    @Test
    public void email_formatHarusValid() {
        Dosen d = new Dosen();
        assertThrows(() -> d.setEmail("bukan-email"));
        d.setEmail("budi@kampus.ac.id");
    }

    @Test
    public void constructorPraktis_melemparJikaDataSalah() {
        assertThrows(() -> new Mahasiswa("Al", "a@b.co", "2341010003", "SI", 3.0));
    }

    // ---------------- POLYMORPHISM ----------------
    @Test
    public void statusMahasiswa_berdasarkanIpk() {
        assertEquals("Cumlaude", mhs(3.51).getStatusAkademik());
        assertEquals("Memuaskan", mhs(3.50).getStatusAkademik());
        assertEquals("Memuaskan", mhs(3.01).getStatusAkademik());
        assertEquals("Cukup", mhs(3.00).getStatusAkademik());
        assertEquals("Cukup", mhs(2.00).getStatusAkademik());
        assertEquals("Peringatan", mhs(1.99).getStatusAkademik());
    }

    @Test
    public void statusDosen_berdasarkanJumlahMatkul() {
        assertEquals("Normal", dsn("A, B, C").getStatusAkademik());
        assertEquals("Beban Lebih", dsn("A, B, C, D").getStatusAkademik());
    }

    @Test
    public void satuTipeInduk_perilakuBerbeda() {
        EvaluasiAkademik[] semua = {mhs(3.9), dsn("A,B,C,D,E")};
        assertEquals("Cumlaude", semua[0].getStatusAkademik());
        assertEquals("Beban Lebih", semua[1].getStatusAkademik());
    }

    @Test
    public void matkulDosen_dinormalisasi() {
        Dosen d = dsn("  Pemrograman ,, Basis Data ,  ");
        assertEquals("Pemrograman, Basis Data", d.getMataKuliahDiampu());
        assertEquals(2, d.getJumlahMataKuliah());
        assertThrows(() -> new Dosen().setMataKuliahDiampu(" , "));
    }

    // ---------------- equals / hashCode (BONUS) ----------------
    @Test
    public void equalsHashCode_konsisten() {
        Mahasiswa a = mhs(3.2);
        Mahasiswa b = mhs(3.2);
        b.setCreatedAt(a.getCreatedAt());
        b.setUpdatedAt(a.getUpdatedAt());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setIpk(3.3);
        assertNotEquals(a, b);
        assertFalse(a.equals(dsn("A")));
    }

    // ---------------- util ----------------
    private static Mahasiswa mhs(double ipk) {
        return new Mahasiswa("Fidelis Farel", "fidelis@kampus.ac.id", "2341010003", "Sistem Informasi", ipk);
    }

    private static Dosen dsn(String matkul) {
        return new Dosen("Budi Santoso", "budi@kampus.ac.id", "0012345678", matkul);
    }

    private static void assertThrows(Runnable r) {
        try {
            r.run();
            fail("Seharusnya melempar IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // OK
        }
    }
}
