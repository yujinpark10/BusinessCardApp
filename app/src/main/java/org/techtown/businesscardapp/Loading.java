package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Loading extends AppCompatActivity {

    String loginid, loginpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getSupportActionBar().hide();

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginid = auto.getString("et_id",null);
        loginpw = auto.getString("et_password",null);

        if(loginid!=null && loginpw!=null)
        {
            String userID = loginid;
            String userPassword = loginpw;

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) { // 로그인에 성공한 경우
                            String userID = jsonObject.getString("userID");
                            String userPassword = jsonObject.getString("userPassword");
                            String userName = jsonObject.getString("userName");
                            String userBirth = jsonObject.getString("userBirth");
                            String userNum = jsonObject.getString("userNum");
                            String userEmail = jsonObject.getString("userEmail");
                            // String cardNum = jsonObject.getString("cardNum");

                            Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Loading.this, MainActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("userPassword", userPassword);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userBirth", userBirth);
                            intent.putExtra("userNum", userNum);
                            intent.putExtra("userEmail", userEmail);
                            //intent.putExtra("cardNum", cardNum);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        } else { // 로그인에 실패한 경우
                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
            RequestQueue queue = Volley.newRequestQueue(Loading.this);
            queue.add(loginRequest);
        }


        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Loading.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 1500);

             }
        }
    }


