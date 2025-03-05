package com.ryvk.drifthomeadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class BaseActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        Admin loggedAdmin = Admin.getSPAdmin(BaseActivity.this);

        findViewById(R.id.fab).setOnClickListener(view -> {
            AlertUtils.showConfirmDialog(BaseActivity.this, "Logout?", "Are you sure you want to logout?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loggedAdmin.removeSPAdmin(BaseActivity.this);
                    Intent logoutIntent = new Intent(BaseActivity.this, MainActivity.class);
                    startActivity(logoutIntent);
                    finish();
                }
            });
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        TextView navTitle = headerView.findViewById(R.id.nameView);
        TextView navSubtitle = headerView.findViewById(R.id.emailView);
        navTitle.setText(loggedAdmin.getName());
        navSubtitle.setText(loggedAdmin.getEmail());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_drinkers,
                R.id.nav_saviours,
                R.id.nav_kyc,
                R.id.nav_reports
        ).setOpenableLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_base);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_base);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
