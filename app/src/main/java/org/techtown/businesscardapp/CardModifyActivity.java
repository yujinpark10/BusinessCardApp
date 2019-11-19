package org.techtown.businesscardapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class CardModifyActivity extends AppCompatActivity {

    private static final String TAG_JSON="response";
    private static final String TAG_CARDNUM = "cardNum";
    private static final String TAG_NAME = "name";
    private static final String TAG_COMPANY ="company";
    private static final String TAG_TEAM="team";
    private static final String TAG_POSITION="position";
    private static final String TAG_CONUM="coNum";
    private static final String TAG_NUM="num";
    private static final String TAG_E_MAIL="e_mail";
    private static final String TAG_FAXNUM="faxNum";
    private static final String TAG_ADDRESS="address";
    private static final String TAG_ID="userID";
    private static final String TAG_MINE="mine";
    private static final String TAG_KING="king";
    private ClearEditText et_name, et_company, et_team, et_position, et_conumber, et_pnumber, et_email, et_fnumber, et_address;
    private Button btn_modifySave, btn_modifyCancel;
    private AlertDialog dialog;
    private boolean validate = false;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_modify);
        getSupportActionBar().hide();

        Intent intent = new Intent(this.getIntent());
        final int cardNum = intent.getIntExtra("cardNum", 0);
        final String userID = getIntent().getStringExtra("userID");

        et_name = (ClearEditText)findViewById(R.id.et_name);
        et_company = (ClearEditText)findViewById(R.id.et_company);
        et_team = (ClearEditText)findViewById(R.id.et_team);
        et_position = (ClearEditText)findViewById(R.id.et_position);
        et_conumber = (ClearEditText)findViewById(R.id.et_conumber);
        et_pnumber = (ClearEditText)findViewById(R.id.et_pnumber);
        et_email = (ClearEditText)findViewById(R.id.et_email);
        et_fnumber = (ClearEditText)findViewById(R.id.et_fnumber);
        et_address = (ClearEditText)findViewById(R.id.et_address);

        // 전화번호 형식으로 변환하기
        et_conumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        et_pnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        et_fnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        GetData task = new GetData();
        task.execute("http://yujinpark10.dothome.co.kr/cardModify_select.php", Integer.toString(cardNum));

        //취소 버튼 클릭시 // 취소 확인하기 기능 추가하면 좋을듯
        btn_modifyCancel = (Button)findViewById(R.id.btn_modifyCancel);
        btn_modifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //저장 버튼 클릭시
        btn_modifySave = (Button)findViewById(R.id.btn_modifySave);
        btn_modifySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //EditText에 현재 입력되어있는 값을 get 해온다.
                String name = et_name.getText().toString();
                String company = et_company.getText().toString();
                String team = et_team.getText().toString();
                String position = et_position.getText().toString();
                String coNum = et_conumber.getText().toString();
                String num = et_pnumber.getText().toString();
                String e_mail = et_email.getText().toString();
                String faxNum = et_fnumber.getText().toString();
                String address = et_address.getText().toString();

                //빈칸 없이 입력 확인
                if (name.equals("") || company.equals("") || team.equals("") || position.equals("") || coNum.equals("") || num.equals("") || e_mail.equals("") || faxNum.equals("") || address.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardModifyActivity.this);
                    dialog = builder.setMessage("빈칸없이 입력해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //카드 등록 시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//카드등록 성공한 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardModifyActivity.this);
                                dialog = builder.setMessage("명함 수정에 성공했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();
                            } else {//카드 등록 실패한경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardModifyActivity.this);
                                dialog = builder.setMessage("명함 수정에 실패했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 volley 이용해서 요청을 함.name, company, team, position, coNum, num, e_mail, faxNum, address
                CardModify CardModify = new CardModify(Integer.toString(cardNum), name, company, team, position, coNum, num, e_mail, faxNum, address, userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardModifyActivity.this);
                queue.add(CardModify);
                Intent intent = new Intent(CardModifyActivity.this, CardClicked.class);
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
            String cardNum = params[1];
            String postParameters = "cardNum=" + cardNum;

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

                String name = item.getString(TAG_NAME);
                String company = item.getString(TAG_COMPANY);
                String team = item.getString(TAG_TEAM);
                String position = item.getString(TAG_POSITION);
                String coNum = item.getString(TAG_CONUM);
                String num = item.getString(TAG_NUM);
                String e_mail = item.getString(TAG_E_MAIL);
                String faxNum = item.getString(TAG_FAXNUM);
                String address = item.getString(TAG_ADDRESS);

                et_name.setText(name);
                et_company.setText(company);
                et_team.setText(team);
                et_position.setText(position);
                et_conumber.setText(coNum);
                et_pnumber.setText(num);
                et_email.setText(e_mail);
                et_fnumber.setText(faxNum);
                et_address.setText(address);
            }

        } catch (JSONException e) {

        }

    }
}
