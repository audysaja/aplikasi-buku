package com.example.inventorybuku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.example.inventorybuku.databinding.ActivityDaftarBukuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DaftarBuku extends AppCompatActivity {
    //view binding
    private ActivityDaftarBukuBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist to store category
    private ArrayList<ModelCategory> categoryArrayList;

    //adapter
private AdapterCategory adapterCategory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDaftarBukuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        loadCategories();

        //handle click, start category add screen
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DaftarBuku.this, CategoryAddActivity.class));
            }
        });


    }

    private void loadCategories() {
        //init arraylist
        categoryArrayList = new ArrayList<>();
        //get all categories from firebase > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    try{
                        String id = ds.child("id").getValue(String.class);
                        String category = ds.child("category").getValue(String.class);
                        String uid = ds.child("uid").getValue(String.class);
                        String timestamp = ds.child("timestamp").getValue(String.class);

                        ModelCategory model = new ModelCategory(id, category, uid, timestamp);

                        //add to arraylist
                        categoryArrayList.add(model);
                        Log.d("DataDebug", "Added category: " + model.getCategory());
                    }catch (Exception e){
                        Log.e("DataError", "Error converting dataSnapshot to ModelCategory: " + e.getMessage());
                    }

                }
                //setup adapter
                adapterCategory = new AdapterCategory(DaftarBuku.this, categoryArrayList);
                //set adapter to recyclerview
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}