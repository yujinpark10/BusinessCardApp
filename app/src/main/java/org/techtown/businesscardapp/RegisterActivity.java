package org.techtown.businesscardapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_password, et_chkpassword, et_name, et_birth, et_pnumber, et_email;
    private Button btn_register, btn_cancel;
    private AlertDialog dialog;
    private  boolean validate = false;

    private int TodayYear, TodayMonth, TodayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        et_id = (EditText)findViewById(R.id.et_id);
        et_password = (EditText)findViewById(R.id.et_password);
        et_chkpassword = (EditText)findViewById(R.id.et_chkpassword);
        et_name = (EditText)findViewById(R.id.et_name);
        et_birth = (EditText)findViewById(R.id.et_birth);
        et_pnumber = (EditText)findViewById(R.id.et_pnumber);
        et_email = (EditText)findViewById(R.id.et_email);

        // 전화번호 형식으로 변환하기
        et_pnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 생일 달력 생성 및 설정
        final Calendar calendar = Calendar.getInstance();
        TodayYear = calendar.get(Calendar.YEAR);
        TodayMonth = calendar.get(Calendar.MONTH);
        TodayDate = calendar.get(Calendar.DATE);

        Button btn_calendar = (Button)findViewById(R.id.btn_calender);
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        if(month < 9){
                            if(dayOfMonth < 10) {
                                et_birth.setText(year + ". 0" + (month+1) + ". 0" + dayOfMonth);
                            } else {
                                et_birth.setText(year + ". 0" + (month+1) + ". " + dayOfMonth);
                            }
                        } else {
                            if(dayOfMonth < 10) {
                                et_birth.setText(year + ". " + (month+1) + ". 0" + dayOfMonth);
                            } else {
                                et_birth.setText(year + ". " + (month+1) + ". " + dayOfMonth);
                            }
                        }

                        Toast.makeText(getApplicationContext(),"선택 날짜 : "+ year + "년 "+(month+1)+"월 "+dayOfMonth+"일", Toast.LENGTH_LONG).show();
                    }
                }, TodayYear, TodayMonth, TodayDate);

                datePickerDialog.show();
            }
        });

        //회원가입시 아이디가 사용가능한지 검증하는 부분
        final Button validateButton = (Button)findViewById(R.id.btn_chkid);
        validateButton.setOnClickListener(new View.OnClickListener() {
            // 아이디 중복체크를 눌렀을때, 중복여부 체크
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                if(validate){
                    return;
                }
                if(userID.equals((""))){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디를 입력해주세요.")
                            .setPositiveButton("획인", null)
                            .create();
                    dialog.show();
                    return;
                }

                /*
                if(et_id.getText().toString().length()<4) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("4자리 이상의 아이디만 사용하실 수 있습니다.")
                            .setNegativeButton("획인", null)
                            .create();
                    dialog.show();
                    return;
                }
                 */

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    // 서버에 접속한 뒤, 응답을 받는 부분
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                et_id.setEnabled(false);  // 아이디값을 변경할 수 없도록 고정
                                validate = true;
                                et_id.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);

            }
        });

        //취소 버튼 클릭시 수행
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 회원가입 버튼 클릭 시 수행
        btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 get 해온다.
                String userID = et_id.getText().toString();
                String userPassword = et_password.getText().toString();
                String checkPassword = et_chkpassword.getText().toString();
                String userName = et_name.getText().toString();
                String userBirth = et_birth.getText().toString();
                String userNum = et_pnumber.getText().toString();
                String userEmail = et_email.getText().toString();
                //String cardNum = null;

                if(userID.equals("") || userPassword.equals("") || userName.equals("") || userBirth.equals("") || userNum.equals("") || userEmail.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈칸없이 입력해주세요.")
                            .setNegativeButton("획인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(!userPassword.equals(checkPassword)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호가 일치하지 않습니다.\n비밀번호를 확인해주세요.")
                            .setNegativeButton("획인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(!validate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디 중복체크를 해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 회원가입 시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                            public void onResponse(String response) {
                                try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success){ // 회원등록에 성공한 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원등록에 성공했습니다.")
                                        .setPositiveButton("획인", null)
                                        .create();
                                dialog.show();
                                finish();

                            } else { // 회원등록에 실패한 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원등록에 실패했습니다.")
                                        .setNegativeButton("획인", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 서버로 Volley 이용해서 요청을 함.
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userBirth, userNum, userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    @Override
    protected  void onStop(){
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}
