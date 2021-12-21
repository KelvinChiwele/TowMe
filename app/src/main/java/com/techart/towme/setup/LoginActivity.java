package com.techart.towme.setup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.techart.towme.MainActivity;
import com.techart.towme.R;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.utils.EditorUtils;

public class LoginActivity extends AppCompatActivity {
    // UI references.
    private ProgressDialog mProgress;

    private EditText etUsername;
    private EditText etPassWord;
    private String email;
    private boolean isAttached;

    // Firebase references.
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mDatabaseUsers = FireBaseUtils.mDatabaseUsers;
        etUsername = findViewById(R.id.loginUsername);
        etPassWord = findViewById(R.id.loginPassword);
        Button mLogin = findViewById(R.id.btLogin);
        Button mRegister = findViewById(R.id.btSignUp);

        mLogin.setOnClickListener(v -> {
            if (haveNetworkConnection()) {
                validUserCredentials();
            } else {
                noIntenet();
            }
        });

        mRegister.setOnClickListener(v -> {
            if (haveNetworkConnection()) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            } else {
                noIntenet();
            }
        });

    }


    private void validUserCredentials() {
        mProgress = new ProgressDialog(LoginActivity.this);
        email = etUsername.getText().toString().trim();
        String password = etPassWord.getText().toString().trim();
        if (validate(email, password)) {
            mProgress.setMessage("Logging in ...");
            mProgress.setCancelable(false);
            mProgress.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    validUsersExistance();
                } else {
                    closeDialog();
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        etUsername.setTextColor(Color.RED);
                        etUsername.setError("Unrecognized email...! Use the email you registered with");
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        etPassWord.setTextColor(Color.RED);
                        etPassWord.setError("Wrong password, enter the password you registered with");
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed!, ensure your internet is working and try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean validate(String email, String password) {
        return EditorUtils.editTextValidator(email, etUsername, "enter a valid email") &&
                EditorUtils.editTextValidator(password, etPassWord, "enter a valid password");
    }


    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    private void validUsersExistance() {
        final String userId = FireBaseUtils.getUiD();
        if (userId != null) {
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    closeDialog();
                    if (dataSnapshot.hasChild(userId)) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "You need to setup an Account", Toast.LENGTH_LONG).show();
                        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(registerIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            closeDialog();
            Toast.makeText(LoginActivity.this, "Error encountered, Try again later", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    private void closeDialog() {
        if (isAttached) {
            mProgress.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener =
                (dialog, button) -> {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
                        finishAffinity();
                    }
                    if (button == DialogInterface.BUTTON_NEGATIVE) {
                        dialog.dismiss();
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit application? ")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    private void noIntenet() {
        Toast.makeText(LoginActivity.this, "No internet...! Turn on Data or Wifi.", Toast.LENGTH_LONG).show();
    }

}