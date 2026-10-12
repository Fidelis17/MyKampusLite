# MyKampus Lite

**Tugas LAT 4 – Studi Kasus OOP: Aplikasi MyKampus Local Database**
Mata Kuliah: Pemrograman Mobile Lanjut – Java Intermediate

| | |
|---|---|
| **Nama** | Fidelis Kristian Farel |
| **NIM** | 23410100039 |

Aplikasi offline untuk manajemen data akademik Prodi (Mahasiswa & Dosen) dengan **Room + MVVM**.
Data buruk (NIM duplikat, nama kosong, IPK di luar range) dicegah dari level **Model**, bukan di Activity.

---

## 1. Arsitektur

```
UI (Activity/Fragment/Adapter)  ──observe──▶  ViewModel  ──▶  «interface» AkademikRepository
                                                                        ▲
                                                           MahasiswaRepository / DosenRepository
                                                                        │
                                                                   DAO ▶ Room (SQLite)
```

```
com.fidelis.mykampuslite
├── model/        BaseEntity, Person, Mahasiswa, Dosen, EvaluasiAkademik
├── data/
│   ├── dao/        MahasiswaDao, DosenDao
│   ├── db/         MyKampusDatabase
│   └── repository/ AkademikRepository (interface), MahasiswaRepository, DosenRepository,
│                   RepositoryCallback, DuplicateDataException
├── viewmodel/    BaseAkademikViewModel, MahasiswaViewModel, DosenViewModel, ViewModelFactory, Event
└── ui/           MainActivity, TabPagerAdapter, MahasiswaFragment, DosenFragment, AkademikAdapter
```

Class diagram: [`docs/class_diagram.png`](docs/class_diagram.png) (sumber: `docs/class_diagram.dot`).

## 2. Penerapan 4 Pilar OOP

### A. Inheritance (3 level)
```
BaseEntity (abstract)   : id, createdAt, updatedAt
  └── Person (abstract) : nama, email, abstract getRole()
        ├── Mahasiswa   : nim, jurusan, ipk
        └── Dosen       : nidn, mataKuliahDiampu
```
`getRole()` bersifat `abstract` di `Person` dan di-override oleh `Mahasiswa` ("Mahasiswa") dan `Dosen` ("Dosen").

### B. Encapsulation
- Semua field Model **private** (diverifikasi otomatis oleh unit test lewat reflection).
- Validasi ada di **setter** dan melempar `IllegalArgumentException`:
  - `setNama()` → tidak boleh kosong, minimal 3 karakter
  - `setNim()` → tepat 10 digit angka
  - `setIpk()` → 0.0 – 4.0
  - (tambahan) `setEmail()`, `setJurusan()`, `setNidn()`, `setMataKuliahDiampu()`
- `ViewModel` memanggil setter satu per satu, menangkap exception, lalu mengirim **pesan error per kolom** ke UI.
  Jika ada satu saja yang gagal, **tidak ada data yang masuk ke Room**.
- NIM/NIDN dijaga **UNIQUE** di database (`@Index(unique = true)`); pelanggaran diterjemahkan Repository menjadi `DuplicateDataException` → tampil sebagai error di kolom NIM/NIDN.

### C. Polymorphism
- Interface `EvaluasiAkademik { String getStatusAkademik(); }`
  - `Mahasiswa`: IPK > 3.5 → **Cumlaude**, > 3.0 → **Memuaskan**, < 2.0 → **Peringatan**, selainnya → **Cukup**
  - `Dosen`: mata kuliah > 3 → **Beban Lebih**, selainnya → **Normal**
- **Satu** `AkademikAdapter` (`ListAdapter<Person, …>`) menampilkan Mahasiswa **dan** Dosen; perilaku berbeda dipilih saat runtime tanpa `instanceof`.

### D. Abstraction
- `AkademikRepository<T extends Person>` adalah **interface**; `MahasiswaRepository` (dan `DosenRepository`) mengimplementasikannya.
- `BaseAkademikViewModel` hanya bergantung pada interface tersebut (Dependency Inversion) dan disuntikkan lewat constructor oleh `ViewModelFactory`.
- Terbukti dengan `MahasiswaViewModelTest` yang memakai **repository palsu** tanpa Room/Android.

## 3. Integrasi Android (MVVM + Room)

| Syarat | Implementasi |
|---|---|
| 2 Entity Room | `Mahasiswa`, `Dosen` (turunan hierarki di atas) |
| DAO `LiveData<List<Mahasiswa>> getAll()` | `MahasiswaDao`, `DosenDao` |
| Database | `MyKampusDatabase` |
| ViewModel tahan rotasi | `ViewModel` dimiliki Activity (`ViewModelProvider(requireActivity(), …)`); state edit/error juga ada di ViewModel |
| 1 Activity + 2 Tab | `MainActivity` + `ViewPager2` + `TabLayout` (Tab Mahasiswa & Tab Dosen) |
| Form + Simpan | `fragment_mahasiswa.xml`, `fragment_dosen.xml` |
| RecyclerView auto-update | `LiveData` → `ListAdapter.submitList()` |
| Edit & Delete | Tombol di tiap item (Edit mengisi form → tombol berubah jadi *Perbarui*; Hapus pakai dialog konfirmasi) |

**Larangan yang dipatuhi**
- ❌ Logic validasi di Activity → ✅ semua di Model/ViewModel (Activity/Fragment hanya kirim teks mentah).
- ❌ Field `public` di Model → ✅ semua `private`.
- ❌ Query di Main Thread → ✅ tulis lewat `Executor` (`DB_EXECUTOR`), baca lewat `LiveData`; **tidak ada** `allowMainThreadQueries()`.

**Bonus +10:** `equals()`/`hashCode()` di `BaseEntity`, `Person`, `Mahasiswa`, `Dosen` dipakai oleh `DiffUtil.ItemCallback` pada `ListAdapter`.

## 4. Cara Menjalankan

1. Buka folder ini di **Android Studio** (Hedgehog / Iguana atau lebih baru) → tunggu *Gradle Sync*.
   (Jika diminta, setujui upgrade AGP/Gradle.)
2. Jalankan di emulator/perangkat (minSdk 24).
3. Build APK debug: **Build ▸ Build Bundle(s)/APK(s) ▸ Build APK(s)**
   → `app/build/outputs/apk/debug/app-debug.apk`.
4. Unit test: `./gradlew testDebugUnitTest` (atau klik kanan folder `app/src/test` ▸ *Run Tests*).
   Mencakup uji hierarki, `private`, semua validasi setter, status polimorfik, `equals/hashCode`, dan perilaku ViewModel.
5. Class Diagram dari Android Studio (alternatif): klik kanan package `model` ▸ **Diagrams ▸ Show Diagram**.

## 5. Skenario Demo Video (maks. 2 menit)

1. **Validasi error** – Simpan form kosong → error merah di tiap kolom. Isi NIM `123` → "NIM harus 10 digit angka". IPK `5` → "IPK harus di antara 0.0 - 4.0".
2. **Data valid** – Tab Mahasiswa: simpan `Fidelis Kristian Farel`, NIM `2341010003`, IPK `3.75` → muncul di list dengan status **Cumlaude**. Simpan NIM yang sama → error "NIM sudah terdaftar".
3. **Rotasi layar** – Putar layar di tengah pengisian form dan saat mode edit → data & isian tidak hilang.
4. **Polimorfisme** – Edit IPK menjadi `1.5` → status berubah **Peringatan**. Pindah ke Tab Dosen, isi 4 mata kuliah (`A, B, C, D`) → status **Beban Lebih**; hapus satu → **Normal**. Tunjukkan Hapus.

---

## 6. Pertanyaan Refleksi

### 1) Apa yang terjadi jika kalian menghapus constructor kosong di Entity Room?
Room perlu cara untuk **membuat objek** saat membaca baris dari database. Aturannya: Entity harus punya constructor kosong **atau** constructor yang parameternya cocok (nama dan tipe) dengan field/kolom.
Jika constructor kosong dihapus (misalnya hanya tersisa constructor `Mahasiswa(String nama, String email, String nim, String jurusan, double ipk)`):

- Karena parameter constructor itu **tidak cocok persis** dengan kolom tabel (ada kolom `id`, `createdAt`, `updatedAt` yang tidak ikut), *annotation processor* Room menolaknya dengan **error saat kompilasi**: *"Entities and POJOs must have a usable public constructor. You can have an empty constructor or a constructor whose parameters match the fields."* Aplikasi tidak bisa di-build.
- Perlu diingat juga: di Java, begitu kita menulis constructor apa pun, constructor *default* otomatis hilang. Karena itu di proyek ini constructor kosong ditulis **eksplisit**, dan constructor praktis diberi `@Ignore` supaya Room tidak bingung memilih.
- Dalam desain ini, constructor kosong + **setter** juga yang dipakai Room untuk mengisi field private satu per satu; data yang sudah tersimpan lolos validasi yang sama dengan input pengguna.

### 2) Mengapa ViewModel tidak boleh meng-import `android.widget.TextView`? Hubungkan dengan prinsip Abstraction.
1. **Siklus hidup berbeda.** ViewModel hidup lebih lama daripada Activity/Fragment (selamat dari rotasi). `TextView` memegang referensi ke `Context`/Activity, sehingga jika ViewModel menyimpannya terjadi **memory leak**: Activity lama tidak bisa dibuang dari memori.
2. **Coupling & testability.** Jika ViewModel tahu tentang widget, logic-nya terikat ke framework UI dan tidak bisa di-unit-test di JVM biasa. Di proyek ini `MahasiswaViewModelTest` jalan tanpa emulator justru karena ViewModel bebas dari `android.widget`.
3. **Abstraction.** Abstraksi berarti menyembunyikan detail *bagaimana* sesuatu dilakukan dan hanya mengekspos *apa*-nya. ViewModel hanya mengekspos **state abstrak** (`LiveData` berisi daftar, pesan error per kolom, status edit) dan tidak peduli apakah ditampilkan lewat `TextView`, Compose, atau notifikasi. Sebaliknya, ViewModel pun hanya mengenal `AkademikRepository` (interface), bukan Room. Arah ketergantungannya satu arah: **UI → ViewModel → interface Repository**, tidak pernah sebaliknya.

### 3) Berikan 1 contoh nyata di studi kasus ini di mana Polymorphism menghemat baris kode kalian.
**`AkademikAdapter`.** Karena `Mahasiswa` dan `Dosen` sama-sama `Person` dan sama-sama punya `getRole()`, `getInfoDetail()`, dan `getStatusAkademik()`, **satu** adapter dan **satu** layout item cukup untuk dua tab:

```java
holder.tvNama.setText(item.getNama());
holder.tvRole.setText(item.getRole());                // "Mahasiswa" atau "Dosen"
holder.tvDetail.setText(item.getInfoDetail());        // NIM/jurusan/IPK atau NIDN/mata kuliah
holder.tvStatus.setText(item.getStatusAkademik());    // aturan status berbeda per tipe
```

Tanpa polymorphism harus dibuat `MahasiswaAdapter` + `DosenAdapter` (dua ViewHolder, dua `DiffUtil`, dua layout item) atau satu adapter penuh `if (item instanceof Mahasiswa) … else if (item instanceof Dosen) …`.
Dengan polymorphism, `AkademikAdapter` hanya **111 baris** dan `item_akademik.xml` **85 baris** untuk **dua** tipe; tanpanya (perkiraan) hampir **dua kali lipat** — sekitar 200+ baris untuk dua adapter plus dua layout item — dan setiap menambah tipe baru (misalnya `Staf`) harus mengedit adapter lagi. Sekarang cukup membuat subclass `Person` baru tanpa menyentuh adapter sama sekali.

---

## 7. Catatan Teknis
- Form dan daftar berada dalam satu `NestedScrollView` supaya form tetap terjangkau di layar kecil; jumlah data aplikasi ini kecil sehingga tidak masalah.
- `exportSchema = false` karena belum ada migrasi; untuk produksi sebaiknya aktifkan dan simpan skema.
# MyKampusLite
"# MyKampusLite" 
