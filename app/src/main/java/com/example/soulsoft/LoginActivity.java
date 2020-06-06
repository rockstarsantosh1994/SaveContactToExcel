package com.example.soulsoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername,etPassword;
    AppCompatButton btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //basic intialisaiton
        initViews();
    }

    private void initViews(){
        etUsername=findViewById(R.id.et_email);
        etPassword=findViewById(R.id.et_password);
        btnLogin=findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            if(isValidated()){
                if(etUsername.getText().toString().equals("admin") && etPassword.getText().toString().equals("soulsoft")){
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            }
        }
    }

    public boolean isValidated(){
        if(etUsername.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "UserName required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etPassword.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Password required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
