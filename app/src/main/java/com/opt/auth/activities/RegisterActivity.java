package com.opt.auth.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.opt.auth.R;
import com.opt.auth.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Activity mAct = RegisterActivity.this;
    private EditText mFullName, mEmail, mPassword;
    private Button mRegister;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.editFullName);
        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPassword);
        mRegister = findViewById(R.id.btnRegister);
        mLoginBtn = findViewById(R.id.AlreadyAccount);
        fAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        /////////////////////////////////////Functions//////////////////////////////////////////////
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        mRegister.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String fullName = mFullName.getText().toString().trim();
            //   String phone=mPhone.getText().toString().trim();
            UserModel signup = new UserModel(email, password, fullName);
            Log.e("tag", "onClick: " + signup.getUrl().get(0));
            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required");
                return;
            }
            if (password.length() < 6) {
                mPassword.setError("Password length should be greater or equal to 6 characters");
                return;
            }

            String mobileNo = mEmail.getText().toString().trim();

            if (mobileNo.isEmpty() || mobileNo.length() < 12) {
                mEmail.setError("Enter a valid mobile");
                mEmail.requestFocus();
                return;
            }

            Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
            intent.putExtra("mobile", mobileNo);
            startActivity(intent);
            // register the user
            fAuth.createUserWithEmailAndPassword(email + "@gmail.com", password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ///////////////////////////////////Email verification section////////////////////////////
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "Verification email have been sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d("TAG", "onFailure:Email not sent" + e.getMessage()));
                    ///////////////////////sending data to database///////////////////////////////////////////
                    databaseReference.child(fAuth.getUid()).setValue(signup);
                } else {
                    Toast.makeText(RegisterActivity.this, "Some error is occurred" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        //login button////////////////////////////////////////////////////////////////
        mLoginBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }
}