package com.fidelis.mykampuslite.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/** Adapter ViewPager2: posisi 0 = Mahasiswa, posisi 1 = Dosen. */
public class TabPagerAdapter extends FragmentStateAdapter {

    public TabPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new MahasiswaFragment() : new DosenFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
