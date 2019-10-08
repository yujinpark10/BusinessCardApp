package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id, et_password;
    private Button btn_register, btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText)findViewById(R.id.et_id);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);

        // 회원가입 버튼을 클릭 시 수행
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 get 해온다.
                String userID = et_id.getText().toString();
                String userPassword = et_password.getText().toString();

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

                                Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPassword", userPassword);
                                intent.putExtra("userName", userName);
                                intent.putExtra("userBirth", userBirth);
                                intent.putExtra("userNum", userNum);
                                intent.putExtra("userEmail", userEmail);

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
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
}

