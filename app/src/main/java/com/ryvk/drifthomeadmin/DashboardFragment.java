package com.ryvk.drifthomeadmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    public static final int KYC_PENDING = 1;
    public static final int KYC_VERIYFIED = 2;
    public static final int KYC_DECLINED = 3;
    private FirebaseFirestore db;
    private TextView savedSoulsCountText;
    private TextView savioursOnDutyText;
    private TextView totalReportsText;
    private TextView pendingKycVerificationsText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        db = FirebaseFirestore.getInstance();
        savedSoulsCountText = view.findViewById(R.id.textView13);
        savioursOnDutyText = view.findViewById(R.id.textView10);
        totalReportsText = view.findViewById(R.id.textView8);
        pendingKycVerificationsText = view.findViewById(R.id.textView14);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        loadAllData();
    }

    private void loadAllData(){
        getSavedSoulsCount();
        getSavioursOnDuty();
        getTotalReports();
        getPendingKycVerifications();
    }

    private void getSavedSoulsCount() {
        db.collection("trip").count().get(AggregateSource.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long count = task.getResult().getCount();
                        Log.d(TAG, "Total trip documents: " + count);
                        savedSoulsCountText.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "Error getting document count", task.getException());
                    }
                });
    }
    private void getSavioursOnDuty() {
        db.collection("saviour").whereEqualTo("kyc", KYC_VERIYFIED)
                .count().get(AggregateSource.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long count = task.getResult().getCount();
                        Log.d(TAG, "Total saviour documents: " + count);
                        savioursOnDutyText.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "Error getting document count", task.getException());
                    }
                });
    }
    private void getTotalReports() {
        //
    }
    private void getPendingKycVerifications() {
        db.collection("saviour").whereEqualTo("kyc", KYC_PENDING)
                .count().get(AggregateSource.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long count = task.getResult().getCount();
                        Log.d(TAG, "Total pending kyc documents: " + count);
                        pendingKycVerificationsText.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "Error getting document count", task.getException());
                    }
                });
    }
}