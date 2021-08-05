package com.opt.auth.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.opt.auth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Activity mAct = MainActivity.this;

    private ListView listView, urlList;

    private Button btnUpdate;
    private Button btnDelete;
    private Button verifyBtn;
    private TextView verifyMsg;

    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////////hooks///////////////////////////////////////////////////////
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        verifyBtn = findViewById(R.id.verifyBtn);
        verifyMsg = findViewById(R.id.verifyMsg);
        //Main Activity List view///////////////////////////////////////////////////////////////////
        listView = findViewById(R.id.listView);
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(arrayAdapter);
        //URL list VIew/////////////////////////////////////////////////////////////////////////////
        urlList = findViewById(R.id.urlList);
        ArrayList<String> url = new ArrayList<>();
        ArrayAdapter arrayAdapter1 = new ArrayAdapter<String>(this, R.layout.list_item, url);
        urlList.setAdapter(arrayAdapter1);

        //Email Verification code//////////////////////////////////////////////////////////////////
        if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);
            verifyBtn.setOnClickListener(v -> user.sendEmailVerification().addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Verification email have been sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d("TAG", "onFailure:Email not sent" + e.getMessage())));
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        //Main Activity VIew List//////////////////////////////////////////////////////////////////
        Query query = reference.orderByKey().limitToFirst(4);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    list.add(snapshot1.getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //URL list view/////////////////////////////////////////////////////////////////////////////
        DatabaseReference ref = reference.child("url");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                url.clear();
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    url.add(snapshot2.getValue().toString());
                }
                arrayAdapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //////////////////////////////////Buttons on Main activity//////////////////////////////////
        btnUpdate.setOnClickListener(v -> reference.child("phone").setValue("98765433"));
        btnDelete.setOnClickListener(v -> reference.child("phone").removeValue());
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}