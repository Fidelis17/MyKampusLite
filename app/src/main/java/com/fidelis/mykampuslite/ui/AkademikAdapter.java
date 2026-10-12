package com.fidelis.mykampuslite.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fidelis.mykampuslite.R;
import com.fidelis.mykampuslite.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * [POLYMORPHISM] SATU adapter untuk DUA tipe objek (Mahasiswa & Dosen).
 * Adapter hanya mengenal tipe induk Person; perilaku berbeda (role, detail,
 * status akademik) dipilih otomatis saat runtime lewat method override,
 * tanpa satu pun "if (x instanceof Mahasiswa) ... else if (x instanceof Dosen)".
 *
 * Memakai ListAdapter + DiffUtil (BONUS +10) yang bergantung pada equals()
 * di Model.
 */
public class AkademikAdapter extends ListAdapter<Person, AkademikAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onEdit(Person item);

        void onDelete(Person item);
    }

    private static final DiffUtil.ItemCallback<Person> DIFF = new DiffUtil.ItemCallback<Person>() {
        @Override
        public boolean areItemsTheSame(@NonNull Person lama, @NonNull Person baru) {
            return lama.getClass() == baru.getClass() && lama.getId() == baru.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Person lama, @NonNull Person baru) {
            return lama.equals(baru);
        }
    };

    private final OnItemActionListener listener;

    public AkademikAdapter(OnItemActionListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    /** Menerima List<Mahasiswa> maupun List<Dosen> (keduanya turunan Person). */
    public void submit(List<? extends Person> data) {
        submitList(data == null ? null : new ArrayList<Person>(data));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_akademik, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Person item = getItem(position);

        // Semua pemanggilan di bawah bersifat polimorfik:
        holder.tvNama.setText(item.getNama());
        holder.tvRole.setText(item.getRole());
        holder.tvDetail.setText(item.getInfoDetail());
        String status = item.getStatusAkademik();
        holder.tvStatus.setText(status);
        holder.tvStatus.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), warnaStatus(status)));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnHapus.setOnClickListener(v -> listener.onDelete(item));
    }

    private static int warnaStatus(String status) {
        switch (status) {
            case "Peringatan":
            case "Beban Lebih":
                return R.color.status_buruk;
            case "Cumlaude":
                return R.color.status_baik;
            default:
                return R.color.status_netral;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNama, tvRole, tvDetail, tvStatus, btnEdit, btnHapus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }
}
