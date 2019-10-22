package org.techtown.businesscardapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class google_login extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

    }

    public class User{
        public String username;
        public String email;
        public String id;

        public User(){

        }
        public User(String username, String email, String id){
            this.username = username;
            this.email = email;
            this.id = id;
        }
    }

}
