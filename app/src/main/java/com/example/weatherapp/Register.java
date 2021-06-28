package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Register extends AppCompatActivity {
    FirebaseAuth FireAuth;
    Animation animation_fadeIn,animation_fadeOut;

    EditText email_txt,password_txt,confirm_password_txt;
    Button sign_up,ok;
    TextView sign_in,validation_msg,error_msg;
    String email,password,confirm_password;

    boolean isSuccess,validData;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Utils.blackIconStatusBar(Register.this,R.color.light_Background);

        FirebaseAuth.getInstance().signOut();
        FireAuth = FirebaseAuth.getInstance();

        email_txt = findViewById(R.id.reg_email);
        password_txt = findViewById(R.id.reg_password);
        confirm_password_txt = findViewById(R.id.reg_confirm_password);

        validation_msg = findViewById(R.id.validation_msg);
        error_msg = findViewById(R.id.error_msg);

        sign_up = findViewById(R.id.signup_button);
        sign_in = findViewById(R.id.signin_link);
        ok = findViewById(R.id.validation_dialog_ok);

        LinearLayout overBox = findViewById(R.id.overbox);
        LinearLayout validationDialog = findViewById(R.id.validation_dialog);
        LinearLayout layout_register = findViewById(R.id.linear_register_layout);

        animation_fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        animation_fadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

        new Handler().postDelayed(() -> {
                    layout_register.setVisibility(View.VISIBLE);
                    layout_register.setAnimation(animation_fadeIn);
                },
                800);

        sign_up.setOnClickListener(v -> {
            validData = true;
            email = email_txt.getText().toString().trim();
            password = password_txt.getText().toString().trim();
            confirm_password = confirm_password_txt.getText().toString().trim();

            if (email.isEmpty()) {
                email_txt.setError("Required Field!");
                validData=false;
            }
            if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                email_txt.setError("Invalid email Address!");
                validData=false;
            }
            if (password.isEmpty()) {
                password_txt.setError("Required Field!");
                validData=false;
            }
            if (confirm_password.isEmpty()) {
                confirm_password_txt.setError("Required Field!");
                validData=false;
            }
            if (!password.equals(confirm_password)) {
                confirm_password_txt.setError("Password do not match!");
                validData=false;
            }

            if (validData){
                FireAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                    isSuccess = true;
                    showValidationDialog(true, overBox, validationDialog);

                }).addOnFailureListener(e ->{
                    isSuccess = false;
                    error_msg.setText(e.getMessage());
                    showValidationDialog(false, overBox, validationDialog);
                });
            }

        });

        sign_in.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        ok.setOnClickListener(v -> {
            if(isSuccess) {
                Objects.requireNonNull(FireAuth.getCurrentUser()).sendEmailVerification().addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(),"Check your email inbox",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }).addOnFailureListener(e -> {
                    FireAuth.getCurrentUser().delete();
                    validation_msg.setText(e.getMessage());
                    ok.setText("OK");
                    isSuccess = false;
                });
            }else{
                new Handler().postDelayed(() -> {
                    validationDialog.setAnimation(animation_fadeOut);
                    validationDialog.setVisibility(View.INVISIBLE);
                    overBox.setAnimation(animation_fadeOut);
                    overBox.setVisibility(View.INVISIBLE);
                    error_msg.setVisibility(View.INVISIBLE);
                },
                500);
            }
        });
    }
    
    @SuppressLint("SetTextI18n")
    private void showValidationDialog(boolean successed, LinearLayout ob, LinearLayout vd){
        ok.setText("OK");
        if (successed) {
            validation_msg.setText("Congratulations, your account has ben successfully created. Verify Now!");
            ok.setText("Verify");
        }else{
            validation_msg.setText("Registered failed! try again.");
            error_msg.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(() -> {
            ob.setVisibility(View.VISIBLE);
            ob.setAnimation(animation_fadeIn);
            vd.setVisibility(View.VISIBLE);
            vd.setAnimation(animation_fadeIn);
        },
        500);
    }
}