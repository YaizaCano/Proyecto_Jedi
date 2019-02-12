package com.yaizacano.taskeate;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //declaro variables
    public static final String KEY_USERNAME = "username";
    public static final String KEY_SETTINGS = "settings";
    EditText ETUser;
    EditText ETPass;
    CheckBox CBRemember;
    Button BLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //indispensable
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(KEY_SETTINGS, MODE_PRIVATE);
        if (sp.contains(KEY_USERNAME)) {
            Log.d("LoginActivity", "Already have username, skipping login");
            startContentActivity();
            return;
        }

        //inicializo variables conectandolas con el xml.
        ETUser = findViewById(R.id.usr);
        ETPass = findViewById(R.id.pass);
        CBRemember = findViewById(R.id.checkBox);
        BLogin = findViewById(R.id.button);

        BLogin.setOnClickListener(LoginActivity.this);

    }

    @Override
    public void onClick(View view) {
        Log.d("LoginActivity", "Login!");
        Log.d("LoginActivity", ETUser.getText().toString());
        Log.d("LoginActivity", ETPass.getText().toString());
        Log.d("LoginActivity", CBRemember.isChecked() ? "remember" : "don't remember");

        String username = ETUser.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (CBRemember.isChecked()) { //Ctrl + Alt + C para public static final
            SharedPreferences sp = getSharedPreferences(KEY_SETTINGS, MODE_PRIVATE);
            sp.edit().putString(KEY_USERNAME, username).apply();
        }

        startContentActivity();
    }

    private void startContentActivity() { //Ctrl + Alt + M para crear una funcion seleccionando el código ya hecho
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}