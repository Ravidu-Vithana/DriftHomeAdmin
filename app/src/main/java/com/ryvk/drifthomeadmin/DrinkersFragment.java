package com.ryvk.drifthomeadmin;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DrinkersFragment extends Fragment {
    private static final String TAG = "DrinkersFragment";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText searchedEmailField;
    private ConstraintLayout noDrinkersContainer;
    private final List<UserCard> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drinkers, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noDrinkersContainer = view.findViewById(R.id.noDrinkersContainer);
        searchedEmailField = view.findViewById(R.id.editTextText);

        ImageButton searchButton = view.findViewById(R.id.imageButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDrinkersList();
                Utils.hideKeyboard(searchButton);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        loadDrinkersList();

        return view;
    }

    private void loadDrinkersList(){

        String searchedEmail = searchedEmailField.getText().toString().trim();

        userList.clear();
        progressBar.setVisibility(View.VISIBLE);
        new Thread(()->{
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Task<QuerySnapshot> queryTask = db.collection("drinker").get();
            if(!searchedEmail.isEmpty() && !searchedEmail.isBlank()){
                queryTask = db.collection("drinker").whereEqualTo("email", searchedEmail).get();
            }
            queryTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Drinker drinker = document.toObject(Drinker.class);
                                Log.d(TAG, "Drinker: " + drinker.getEmail());
                                UserCard userCard = new UserCard(true,drinker.getName(), drinker.getEmail(), drinker.isBlocked());
                                userList.add(userCard);
                            }
                            updateUI();
                        } else {
                            Log.e(TAG, "Error getting documents", task.getException());
                        }
                    });
        }).start();
    }

    private void updateUI(){
        progressBar.setVisibility(View.GONE);
        if(userList.isEmpty()){
            recyclerView.setVisibility(View.INVISIBLE);
            noDrinkersContainer.setVisibility(View.VISIBLE);
        }else{
            noDrinkersContainer.setVisibility(View.INVISIBLE);
            UserAdapter adapter = new UserAdapter(userList, getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}