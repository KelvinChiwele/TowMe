package com.techart.towmekiz.setup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ServerValue;
import com.techart.towmekiz.MainActivity;
import com.techart.towmekiz.R;
import com.techart.towmekiz.constants.Constants;
import com.techart.towmekiz.constants.FireBaseUtils;
import com.techart.towmekiz.model.Profile;
import com.techart.towmekiz.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etLogin;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private EditText etRepeatedPassword;
    private ChipGroup chipUseType;
    private String firstPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private ProgressDialog mProgress;
    private String registeredAs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etLogin = findViewById(R.id.et_login);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etRepeatedPassword = findViewById(R.id.et_repeatPassword);
        chipUseType = findViewById(R.id.chip_user_type);
        Button btRegister = findViewById(R.id.bt_register);
        ChipGroup userType = findViewById(R.id.chip_user_type);
        btRegister.setClickable(true);

        chipUseType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chipAnswer = findViewById(checkedId);
                registeredAs = chipAnswer.getText().toString();
            }
        });

        btRegister.setOnClickListener(v -> {
            if (haveNetworkConnection()) {
                if (validateCredentials()) {
                    startRegister();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Ensure that your internet is working", Toast.LENGTH_LONG).show();
            }
        });


        userType.setOnCheckedChangeListener((chipGroup, i) -> {
            Chip chip = chipGroup.findViewById(i);
            if (chip != null)
                registeredAs = chip.getText().toString();
        });
    }

    /**
     * implementation of the registration
     */
    private void startRegister() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing Up  ...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, firstPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> values = new HashMap<>();
                values.put(Constants.FIRST_NAME, firstName);
                values.put(Constants.LAST_NAME, lastName);
                values.put(Constants.EMAIL, email);
                values.put(Constants.PHONE_NUMBER, phoneNumber);
                values.put(Constants.USER_TYPE, registeredAs);
                values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);

                FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).setValue(values);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(firstName + " " + lastName)
                        .build();
                Log.i("REF", FireBaseUtils.mDatabaseUsers.toString());
                Toast.makeText(RegisterActivity.this, FireBaseUtils.mDatabaseUsers.toString(), Toast.LENGTH_LONG).show();
                if (user != null) {
                    mProgress.dismiss();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Profile profile = Profile.getInstance();
                                    profile.setUserType(registeredAs);
                                    profile.setLastName(firstName);
                                    profile.setPhoneNumber(lastName);
                                    profile.setUuid(FireBaseUtils.getUiD());

                                    Toast.makeText(RegisterActivity.this, "User profile updated.", Toast.LENGTH_LONG).show();
                                }
                            });

                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                } else {
                    mProgress.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error encountered, Please try again later", Toast.LENGTH_LONG).show();
                }
            } else {
                mProgress.dismiss();
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(RegisterActivity.this, "User already exits, use another email address", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error encountered, Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    /**
     * Validates the entries
     *
     * @return true if they all true
     */
    private boolean validateCredentials() {
        firstPassword = etPassword.getText().toString().trim();
        String repeatedPassword = etRepeatedPassword.getText().toString().trim();
        firstName = etFirstName.getText().toString().trim();
        lastName = etLastName.getText().toString().trim();
        email = etLogin.getText().toString().trim();
        phoneNumber = etPhoneNumber.getText().toString().trim();
        return EditorUtils.dropDownValidator(getApplicationContext(), registeredAs) &&
                EditorUtils.editTextValidator(firstName, etFirstName, "enter a valid first name") &&
                EditorUtils.editTextValidator(lastName, etLastName, "enter a valid last name") &&
                EditorUtils.editTextValidator(email, etLogin, "enter a valid email") &&
                EditorUtils.editTextValidator(phoneNumber, etPhoneNumber, "enter a valid phone number") &&
                EditorUtils.isEmailValid(email, etPassword) &&
                EditorUtils.editTextValidator(firstPassword, etPassword, "enter a valid password") &&
                EditorUtils.doPassWordsMatch(firstPassword, repeatedPassword, etRepeatedPassword);
    }

}