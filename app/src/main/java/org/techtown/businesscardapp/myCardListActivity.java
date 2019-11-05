package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
    private static final String TAG_NAME = "name";
    private static final String TAG_COMPANY ="company";
    ListView cardList = null;
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
        cardList = (ListView)findViewById(R.id.cardList);

        //아이디값 받아오기
        String userID = loginid;

        TextView id123 = (TextView)findViewById(R.id.id123) ;
        id123.setText(userID);

        // 대표 명함 버튼
        btn_kingCard = (Button)findViewById(R.id.btn_kingCard);
        btn_kingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                Intent intent = new Intent(myCardListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}