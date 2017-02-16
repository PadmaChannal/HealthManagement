package com.along.android.healthmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.entities.User;
import com.along.android.healthmanagement.helpers.EntityManager;

import java.util.List;

import DBManager.DatabaseHelper;

public class LoginActivity extends BasicActivity {

    //DatabaseHelper helper = new DatabaseHelper(this);

    EditText etUsername, etPassword;
    Button login, register, googleSignIn, forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        login = (Button) findViewById(R.id.btn_login);
        register = (Button) findViewById(R.id.btn_register);
        googleSignIn = (Button) findViewById(R.id.btn_google_sign_in);
        forgetPassword = (Button) findViewById(R.id.btn_forget_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                List<User> users = EntityManager.find(User.class, "username = ?", username);

                if(users.size() > 0) {
                    User user = users.get(0);
                    if(user.getPassword().equals(password)) {
                        Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                        //intent.setClass(LoginActivity.this,MainActivity.class);
                        intent.putExtra("Username", username);
                        startActivity(intent);
                    } else {
                        Toast temp = Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT);
                        temp.show();
                    }
                }

                //String dbpassword = helper.searchPassword(username);
//                String dbpassword;
//                if (password.equals(dbpassword)) {
//                    Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
//                    //intent.setClass(LoginActivity.this,MainActivity.class);
//                    intent.putExtra("Username", username);
//                    startActivity(intent);
//                } else {
//                    Toast temp = Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT);
//                    temp.show();
//                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
