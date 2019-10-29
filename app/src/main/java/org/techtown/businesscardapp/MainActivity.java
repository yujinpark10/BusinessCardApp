package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //private TextView tv_id, tv_pass;
    private Button btn_userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_UserName = (Button)findViewById(R.id.btn_UserName);
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("userName");
        final String userID = intent.getStringExtra("userID");
        btn_UserName.setText(userName);

        btn_UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final String userID = intent.getStringExtra("userID");
                Intent intent = new Intent(MainActivity.this,MyInfoActivity.class);
                intent.putExtra("userID", "park");
                startActivity(intent);
            }
        });


//        tv_id = (TextView)findViewById(R.id.tv_id);
//        tv_pass = (TextView)findViewById(R.id.tv_pass);
//
//        Intent intent = getIntent();
//        String userID = intent.getStringExtra("userID");
//        String userPassword = intent.getStringExtra("userPassword");
//
//        tv_id.setText(userID);
//        tv_pass.setText(userPassword);

        btn_UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
            }
        });


    }
    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target;
        @Override
        protected void onPreExecute(){
            target = "http://yujinpark10.dothome.co.kr/CardList.php"; // 서버에 존재하는 php파일 초기화
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");

                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(MainActivity.this, MyInfoActivity.class);
            intent.putExtra("userCardList", result );
            MainActivity.this.startActivity(intent);
        }
    }
}
