package org.techtown.businesscardapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MemberModifyActivity extends AppCompatActivity {

    private static final String TAG_JSON="response";
    private static final String TAG_USERID="userID";
    private static final String TAG_PASSWORD="userPassword";
    private static final String TAG_NAME="userName";
    private static final String TAG_BIRTH="userBirth";
    private static final String TAG_NUM="userNum";
    private static final String TAG_E_MAIL="userEmail";
    String mJsonString;

    private EditText et_id, et_password, et_name, et_birth, et_pnumber, et_email;
    private Button btn_modify, btn_cancel;
    private AlertDialog dialog;
    private int TodayYear, TodayMonth, TodayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_modify);
        getSupportActionBar().hide();

        final String userID = getIntent().getStringExtra("userID");

        et_id = (EditText)findViewById(R.id.et_id);
        et_password = (EditText)findViewById(R.id.et_password);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(MemberModifyActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        et_birth.setText(year + ". " + (month+1) + ". " + dayOfMonth);

                        Toast.makeText(getApplicationContext(),"선택 날짜 : "+ year + "년 "+(month+1)+"월 "+dayOfMonth+"일", Toast.LENGTH_LONG).show();
                    }
                }, TodayYear, TodayMonth, TodayDate);

                datePickerDialog.show();
            }
        });

        GetData task = new GetData();
        task.execute("http://yujinpark10.dothome.co.kr/memberModify_select.php", userID);

        //취소 버튼 클릭시 수행
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberModifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 회원가입 버튼 클릭 시 수행
        btn_modify = (Button)findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 get 해온다.
                String userID = et_id.getText().toString();
                String userPassword = et_password.getText().toString();
                String userName = et_name.getText().toString();
                String userBirth = et_birth.getText().toString();
                String userNum = et_pnumber.getText().toString();
                String userEmail = et_email.getText().toString();
                //String cardNum = null;

                if(userID.equals("") || userPassword.equals("") || userName.equals("") || userBirth.equals("") || userNum.equals("") || userEmail.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MemberModifyActivity.this);
                    dialog = builder.setMessage("빈칸없이 입력해주세요.")
                            .setNegativeButton("획인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 회원 수정 시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success){ // 회원수정에 성공한 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(MemberModifyActivity.this);
                                dialog = builder.setMessage("회원정보 수정에 성공했습니다.")
                                        .setPositiveButton("획인", null)
                                        .create();
                                dialog.show();
                                finish();

                            } else { // 회원수정에 실패한 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(MemberModifyActivity.this);
                                dialog = builder.setMessage("회원정보 수정에 실패했습니다.")
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
                MemberModify memberModify = new MemberModify(userID, userPassword, userName, userBirth, userNum, userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MemberModifyActivity.this);
                queue.add(memberModify);
                Intent intent = new Intent(MemberModifyActivity.this, MainActivity.class);
                startActivity(intent);
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

    // 내 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null){

            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String userID = params[1];
            String postParameters = "userID=" + userID;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {
                errorString = e.toString();

                return null;
            }

        }
    }

    // 내 명함 리스트뷰 검색결과
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String userID = item.getString(TAG_USERID);
                String password = item.getString(TAG_PASSWORD);
                String name = item.getString(TAG_NAME);
                String birth = item.getString(TAG_BIRTH);
                String num = item.getString(TAG_NUM);
                String e_mail = item.getString(TAG_E_MAIL);

                et_id.setText(userID);
                et_password.setText(password);
                et_name.setText(name);
                et_birth.setText(birth);
                et_pnumber.setText(num);
                et_email.setText(e_mail);
            }

        } catch (JSONException e) {

        }

    }
}
