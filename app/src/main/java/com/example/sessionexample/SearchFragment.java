package com.example.sessionexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    private SearchData searchData;
    private String currentUserName,currentUserId;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance("https://instaworld-1f1af-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference();
        DatabaseReference usersRef = databaseReference.child("Users");

        currentUserId = mAuth.getUid();
        Query query = usersRef.child(currentUserId).child("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        TextInputLayout SearcUser = view.findViewById(R.id.searchUserInputLayout);
        EditText searchUser = SearcUser.getEditText();

        list = new ArrayList<>();
        searchData = new SearchData(getContext(),list);

        searchData.setOnItemClickListener(new SearchData.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String username = searchData.getNameAtPosition(position);

                Intent i = new Intent(getActivity(),viewUserProfile.class);
                i.putExtra("username",username);
                i.putExtra("currentUserName",currentUserName);
                startActivity(i);
            }
        });

        recyclerView = view.findViewById(R.id.userProfile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchData);

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = searchUser.getText().toString().toLowerCase();

                Query query = usersRef.orderByChild("name");

                if(temp.isEmpty() || temp.trim().isEmpty())
                {
                    searchData.clear();
                }
                else
                {
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String username = snapshot.child("name").getValue(String.class);
                                String imageUrl = snapshot.child("profileImage").getValue(String.class);

                                if(imageUrl != null && username.toLowerCase().startsWith(temp))
                                {
                                    Model model = new Model(imageUrl,username);
                                    list.add(model);
                                }
                            }

                            searchData.notifyDataSetChanged();

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }
}