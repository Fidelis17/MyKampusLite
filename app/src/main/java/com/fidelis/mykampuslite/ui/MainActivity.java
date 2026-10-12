package com.fidelis.mykampuslite.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.fidelis.mykampuslite.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity ini sengaja "tipis": hanya memasang ViewPager2 + TabLayout.
 * TIDAK ada logic bisnis / validasi / query database di sini.
 */
public class MainActivity extends AppCompatActivity {

    private static final String[] JUDUL_TAB = {"Mahasiswa", "Dosen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 pager = findViewById(R.id.viewPager);
        TabLayout tabs = findViewById(R.id.tabLayout);

        pager.setAdapter(new TabPagerAdapter(this));
        new TabLayoutMediator(tabs, pager, (tab, position) -> tab.setText(JUDUL_TAB[position]))
                .attach();
    }
}
