package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class myCardListActivity extends AppCompatActivity {

    private Button btn_kingCard;
    private Button btn_cardEnroll;
    private Button btn_cancel;
    String loginid;

    private static final String TAG_JSON="responseme";
    private static final String TAG_CARDNUM = "cardNum";
    private static final String TAG_ID="userID";
    private static final String TAG_NAME = "name";
    private static final String TAG_COMPANY ="company";
    private static final String TAG_ADDRESS ="address";
    private static final String TAG_TEAM="team";
    private static final String TAG_POSITION="position";
    private static final String TAG_CONUM="coNum";
    private static final String TAG_NUM="num";
    private static final String TAG_E_MAIL="e_mail";
    private static final String TAG_FAXNUM="faxNum";
    private static final String TAG_CARDIMAGE="cardimage";
    ListView myCardList = null;
    private static myCardAdapter myCardAdapter;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card_list);
        getSupportActionBar().hide();

        //아이디값 저장 변수
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginid = auto.getString("et_id",null);

        //명함 목록을 위한 리스트뷰
        myCardList = (ListView)findViewById(R.id.myCardList);

        myCardAdapter = new myCardAdapter(this);
        myCardList.setAdapter(myCardAdapter);

        GetData task = new GetData();

        //아이디값 받아오기
        final String userID = loginid;
        task.execute("http://yujinpark10.dothome.co.kr/mycardlist.php", userID);//아이디값 받아온거  보내기

        myCardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(myCardListActivity.this, CardClicked.class);
                intent.putExtra("userID", userID);
                cardListViewItem selected = (cardListViewItem) adapterView.getItemAtPosition(i);
                intent.putExtra("cardNum", selected.getCardNum());
                intent.putExtra("mine1",1);
                intent.putExtra("address", selected.getAddress());
                startActivity(intent);
            }
        });

        // 대표 명함 버튼
        btn_kingCard = (Button)findViewById(R.id.btn_kingCard);
        btn_kingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myCardListActivity.this, KingCardActivity.class);
                intent.putExtra("userID", loginid);
                intent.putExtra("mine1",1);
                startActivity(intent);
            }
        });

        // 명함 등록 버튼
        btn_cardEnroll = (Button)findViewById(R.id.btn_cardEnroll);
        btn_cardEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myCardListActivity.this, CardEnrollActivity.class);
                intent.putExtra("userID", loginid);
                intent.putExtra("mine1",1);
                startActivity(intent);
            }
        });

        // 취소 버튼
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(myCardListActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // 내 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String>{
        String errorString = null;

        ProgressDialog dialog = new ProgressDialog(myCardListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("데이터 확인중");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.cancel();

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

                int cardNum = item.getInt(TAG_CARDNUM);
                String name = item.getString(TAG_NAME);
                String company = item.getString(TAG_COMPANY);
                String team = item.getString(TAG_TEAM);
                String position = item.getString(TAG_POSITION);
                String coNum = item.getString(TAG_CONUM);
                String num = item.getString(TAG_NUM);
                String e_mail = item.getString(TAG_E_MAIL);
                String faxNum = item.getString(TAG_FAXNUM);
                String address = item.getString(TAG_ADDRESS);
                String cardimage = item.getString(TAG_CARDIMAGE);

                myCardAdapter.addItem(cardNum, name, company, team, position, coNum, num, e_mail, faxNum, address, cardimage);
            }

            myCardAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

        }

    }

    //백버튼
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(myCardListActivity.this,MainActivity.class);
        startActivity(intent);


    }
}