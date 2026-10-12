package com.fidelis.mykampuslite.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fidelis.mykampuslite.R;
import com.fidelis.mykampuslite.model.Mahasiswa;
import com.fidelis.mykampuslite.model.Person;
import com.fidelis.mykampuslite.viewmodel.BaseAkademikViewModel;
import com.fidelis.mykampuslite.viewmodel.Event;
import com.fidelis.mykampuslite.viewmodel.MahasiswaViewModel;
import com.fidelis.mykampuslite.viewmodel.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Map;

/**
 * Tab Mahasiswa. Fragment ini HANYA: membaca input mentah -> kirim ke ViewModel,
 * dan menampilkan state dari ViewModel. Tidak ada validasi di sini.
 */
public class MahasiswaFragment extends Fragment {

    private MahasiswaViewModel viewModel;

    private TextInputLayout tilNama, tilEmail, tilNim, tilJurusan, tilIpk;
    private TextInputEditText etNama, etEmail, etNim, etJurusan, etIpk;
    private Button btnSimpan, btnBatal;
    private TextView tvKosong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mahasiswa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tilNama = v.findViewById(R.id.tilNama);
        tilEmail = v.findViewById(R.id.tilEmail);
        tilNim = v.findViewById(R.id.tilNim);
        tilJurusan = v.findViewById(R.id.tilJurusan);
        tilIpk = v.findViewById(R.id.tilIpk);
        etNama = v.findViewById(R.id.etNama);
        etEmail = v.findViewById(R.id.etEmail);
        etNim = v.findViewById(R.id.etNim);
        etJurusan = v.findViewById(R.id.etJurusan);
        etIpk = v.findViewById(R.id.etIpk);
        btnSimpan = v.findViewById(R.id.btnSimpan);
        btnBatal = v.findViewById(R.id.btnBatal);
        tvKosong = v.findViewById(R.id.tvKosong);
        RecyclerView rv = v.findViewById(R.id.recyclerView);

        // ViewModel dimiliki Activity -> TIDAK hilang saat layar diputar
        viewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(requireActivity().getApplication()))
                .get(MahasiswaViewModel.class);

        AkademikAdapter adapter = new AkademikAdapter(new AkademikAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Person item) {
                Mahasiswa m = (Mahasiswa) item;
                viewModel.mulaiEdit(m);
                isiForm(m);
            }

            @Override
            public void onDelete(Person item) {
                konfirmasiHapus((Mahasiswa) item);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);

        // ---- Observe state dari ViewModel ----
        viewModel.getDaftar().observe(getViewLifecycleOwner(), list -> {
            adapter.submit(list);
            tvKosong.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getSedangDiedit().observe(getViewLifecycleOwner(), item -> {
            boolean edit = item != null;
            btnSimpan.setText(edit ? R.string.perbarui : R.string.simpan);
            btnBatal.setVisibility(edit ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorField().observe(getViewLifecycleOwner(), this::tampilkanError);

        viewModel.getPesan().observe(getViewLifecycleOwner(), e -> {
            String teks = e == null ? null : e.getContentIfNotHandled();
            if (teks != null) Toast.makeText(requireContext(), teks, Toast.LENGTH_SHORT).show();
        });

        viewModel.getSimpanBerhasil().observe(getViewLifecycleOwner(), (Event<Boolean> e) -> {
            if (e != null && e.getContentIfNotHandled() != null) kosongkanForm();
        });

        // ---- Aksi tombol: kirim input mentah ke ViewModel ----
        btnSimpan.setOnClickListener(x -> viewModel.simpan(
                teks(etNama), teks(etEmail), teks(etNim), teks(etJurusan), teks(etIpk)));

        btnBatal.setOnClickListener(x -> {
            viewModel.batalEdit();
            kosongkanForm();
        });
    }

    private void tampilkanError(Map<String, String> errors) {
        Map<String, String> e = errors;
        tilNama.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_NAMA));
        tilEmail.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_EMAIL));
        tilNim.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_NIM));
        tilJurusan.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_JURUSAN));
        tilIpk.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_IPK));
    }

    private void isiForm(Mahasiswa m) {
        etNama.setText(m.getNama());
        etEmail.setText(m.getEmail());
        etNim.setText(m.getNim());
        etJurusan.setText(m.getJurusan());
        etIpk.setText(String.format(Locale.US, "%.2f", m.getIpk()));
    }

    private void kosongkanForm() {
        etNama.setText("");
        etEmail.setText("");
        etNim.setText("");
        etJurusan.setText("");
        etIpk.setText("");
        etNama.requestFocus();
    }

    private void konfirmasiHapus(Mahasiswa m) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.hapus_judul)
                .setMessage(getString(R.string.hapus_pesan, m.getNama()))
                .setNegativeButton(R.string.batal, null)
                .setPositiveButton(R.string.hapus, (d, w) -> viewModel.hapus(m))
                .show();
    }

    private static String teks(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString();
    }
}
