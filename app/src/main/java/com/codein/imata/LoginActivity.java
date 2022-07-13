package com.codein.imata;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        Button button = (Button) findViewById(R.id.loginBtn);
        button.setOnClickListener(v -> {
            int user_id;
            String name_str = name;
            String email_str = email.getText().toString().trim();
            String password_str = password.getText().toString().trim();
            MyDatabaseHelper myDB = new MyDatabaseHelper(LoginActivity.this);
            String msg;
            Cursor verified_email = myDB.getUser(email_str);
            if (verified_email.getCount() == 0){
                try {
                    ConnectionHelper connectionHelper = new ConnectionHelper();
                    Connection connect = connectionHelper.connectionclass();
                    if (connect != null) {
                        String query = "Select * from users where email='"+email_str+"'" ;
                        Statement st = connect.createStatement();
                        ResultSet rs = st.executeQuery(query);
                        if (rs.first()){
                            char[] hash_pwd = rs.getString("password").toCharArray();
                            user_id = rs.getInt("id");
                            if (isValid(password_str.toCharArray(),hash_pwd).verified){
                                myDB.addUser(user_id, name_str, email_str, generateHashedPass(password_str));
                                Toast.makeText(this,"You just signup, Login now!",Toast.LENGTH_LONG).show();
                            }else {
                                msg="Wrong Password or Email";
                                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                            }
                        }else {
                            msg = "Contact Admin";
                            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                        }
                    } else {
                        msg = "Connection Problem";
                        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();

                    }
                }catch (SQLException se){
                    se.printStackTrace();
                }
            }else{
                if (verified_email.moveToFirst())
                {
                    do
                    {
                        String sql_pass = verified_email.getString(3);
                        Integer sql_id = verified_email.getInt(0);
                        BCrypt.Result checkPass = isValid(password_str.toCharArray(),sql_pass.toCharArray());

                        if (checkPass.verified){
                            MySingletonClass.getInstance().setValue(sql_id);
                            startActivity(new Intent(this,MainActivity.class));
                            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                        }else{
                            Toast.makeText(this,"Wrong Password Try Again",Toast.LENGTH_LONG).show();
                        }
                    } while (verified_email.moveToNext());
                }
            }

        });
    }
    private String generateHashedPass(String pass) {
        // hash a plaintext password using the typical log rounds (10)
//        return BCrypt.hashpw(pass, BCrypt.gensalt());
//        String bcryptChars;
        return BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(6, pass.toCharArray());

    }

    private BCrypt.Result isValid(char[] clearTextPassword, char[] hashedPass) {
        // returns true if password matches hash
        return BCrypt.verifyer(BCrypt.Version.VERSION_2Y).verifyStrict( clearTextPassword, hashedPass);
//        return BCrypt.checkpw(clearTextPassword, hashedPass);
    }
}