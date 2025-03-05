package com.ryvk.drifthomeadmin;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CommonHeaderFragment extends Fragment {
    private DrawerLayout drawerLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_header, container, false);

        // Find the DrawerLayout from the parent activity
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        // Find menu button
        ImageView menuButton = view.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        return view;
    }
}