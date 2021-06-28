package com.example.weatherapp;
import androidx.appcompat.app.AppCompatActivity;
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

public class Login extends AppCompatActivity{
    FirebaseAuth FireAuth;
    Animation animation_fadeIn,animation_fadeOut;

    private LinearLayout layout_main ;

    Button sign_in,ok;
    TextView forgot_psw,sign_up,error_msg;
    EditText email_txt,password_txt;
    String email,password;

    LinearLayout loginOverBox,loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Utils.blackIconStatusBar(Login.this,R.color.light_Background);

        FireAuth = FirebaseAuth.getInstance();

        layout_main = findViewById(R.id.linear_login_layout);
        loginOverBox = findViewById(R.id.login_overbox);
        loginDialog = findViewById(R.id.login_dialog);

        sign_in = findViewById(R.id.signin_button);
        ok = findViewById(R.id.login_dialog_ok);

        forgot_psw = findViewById(R.id.forgotten_link);
        sign_up = findViewById(R.id.signup_link);
        email_txt = findViewById(R.id.email);
        password_txt = findViewById(R.id.password);
        error_msg = findViewById(R.id.login_error_msg);

        animation_fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        animation_fadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

        new Handler().postDelayed(() -> {
                layout_main.setVisibility(View.VISIBLE);
                layout_main.setAnimation(animation_fadeIn);
            },
            850);

        sign_in.setOnClickListener(v -> {
            email = email_txt.getText().toString().trim();
            password = password_txt.getText().toString().trim();

            if(email.isEmpty()){
                email_txt.setError("Required Field!");
                return;
            }
            if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                email_txt.setError("Invalid email Address!");
                return;
            }
            if(password.isEmpty()){
                password_txt.setError("Required Field!");
                return;
            }
            FireAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
                startActivity(new Intent(getApplicationContext(),MainWeather.class));
                Toast.makeText(getApplicationContext(),"Welcome Back",Toast.LENGTH_LONG).show();
                finish();
            }).addOnFailureListener(e -> {
                error_msg.setText(e.getMessage());
                new Handler().postDelayed(() -> {
                    loginOverBox.setVisibility(View.VISIBLE);
                    loginOverBox.setAnimation(animation_fadeIn);
                    loginDialog.setVisibility(View.VISIBLE);
                    loginDialog.setAnimation(animation_fadeIn);
                },
                500);
            });

        });

        sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),Register.class);
            startActivity(intent);
            finish();
        });
        ok.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                        loginDialog.setAnimation(animation_fadeOut);
                        loginDialog.setVisibility(View.INVISIBLE);
                        loginOverBox.setAnimation(animation_fadeOut);
                        loginOverBox.setVisibility(View.INVISIBLE);
                    },
                    500);
        });
        forgot_psw.setOnClickListener(v -> {
            email = email_txt.getText().toString().trim();
            if(email.isEmpty()){
                email_txt.setError("Please enter your email");
                return;
            }
            FireAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                Toast.makeText(Login.this,"Check your email inbox",Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e->{
                email_txt.setError(e.getMessage());
                //Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FireAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainWeather.class));
            finish();
        }
    }
}