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
import com.fidelis.mykampuslite.model.Dosen;
import com.fidelis.mykampuslite.model.Person;
import com.fidelis.mykampuslite.viewmodel.BaseAkademikViewModel;
import com.fidelis.mykampuslite.viewmodel.DosenViewModel;
import com.fidelis.mykampuslite.viewmodel.Event;
import com.fidelis.mykampuslite.viewmodel.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

/** Tab Dosen. Struktur sama dengan MahasiswaFragment; memakai adapter yang SAMA. */
public class DosenFragment extends Fragment {

    private DosenViewModel viewModel;

    private TextInputLayout tilNama, tilEmail, tilNidn, tilMatkul;
    private TextInputEditText etNama, etEmail, etNidn, etMatkul;
    private Button btnSimpan, btnBatal;
    private TextView tvKosong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dosen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tilNama = v.findViewById(R.id.tilNama);
        tilEmail = v.findViewById(R.id.tilEmail);
        tilNidn = v.findViewById(R.id.tilNidn);
        tilMatkul = v.findViewById(R.id.tilMatkul);
        etNama = v.findViewById(R.id.etNama);
        etEmail = v.findViewById(R.id.etEmail);
        etNidn = v.findViewById(R.id.etNidn);
        etMatkul = v.findViewById(R.id.etMatkul);
        btnSimpan = v.findViewById(R.id.btnSimpan);
        btnBatal = v.findViewById(R.id.btnBatal);
        tvKosong = v.findViewById(R.id.tvKosong);
        RecyclerView rv = v.findViewById(R.id.recyclerView);

        viewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(requireActivity().getApplication()))
                .get(DosenViewModel.class);

        AkademikAdapter adapter = new AkademikAdapter(new AkademikAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Person item) {
                Dosen d = (Dosen) item;
                viewModel.mulaiEdit(d);
                isiForm(d);
            }

            @Override
            public void onDelete(Person item) {
                konfirmasiHapus((Dosen) item);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);

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

        btnSimpan.setOnClickListener(x -> viewModel.simpan(
                teks(etNama), teks(etEmail), teks(etNidn), teks(etMatkul)));

        btnBatal.setOnClickListener(x -> {
            viewModel.batalEdit();
            kosongkanForm();
        });
    }

    private void tampilkanError(Map<String, String> e) {
        tilNama.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_NAMA));
        tilEmail.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_EMAIL));
        tilNidn.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_NIDN));
        tilMatkul.setError(e == null ? null : e.get(BaseAkademikViewModel.FIELD_MATKUL));
    }

    private void isiForm(Dosen d) {
        etNama.setText(d.getNama());
        etEmail.setText(d.getEmail());
        etNidn.setText(d.getNidn());
        etMatkul.setText(d.getMataKuliahDiampu());
    }

    private void kosongkanForm() {
        etNama.setText("");
        etEmail.setText("");
        etNidn.setText("");
        etMatkul.setText("");
        etNama.requestFocus();
    }

    private void konfirmasiHapus(Dosen d) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.hapus_judul)
                .setMessage(getString(R.string.hapus_pesan, d.getNama()))
                .setNegativeButton(R.string.batal, null)
                .setPositiveButton(R.string.hapus, (x, w) -> viewModel.hapus(d))
                .show();
    }

    private static String teks(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString();
    }
}
