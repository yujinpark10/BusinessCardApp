package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private Button btn_setting;
    private Button btn_myCard;
    private Button btn_cardEnroll;
    private Button btn_cardChange;
    private static final String TAG_CARDNUM = "cardNum";
    private static final String TAG_ID="userID";
    private static final String TAG_JSON="responseyou";
    private static final String TAG_NAME = "name";
    private static final String TAG_COMPANY ="company";
    ListView cardList = null;
    String mJsonString;
    private static searchAdapter searchAdapter;
    String loginid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //아이디값 저장 변수
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginid = auto.getString("et_id",null);

        // 설정 버튼 클릭시
        btn_setting = (Button)findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu p = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.setting, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.logout:
                                Toast.makeText(getApplicationContext(), "logout", Toast.LENGTH_SHORT);
                                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(logoutIntent);
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = auto.edit();
                                editor.clear();
                                editor.commit();
                                Toast.makeText(MainActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
                                finish();
                                return true;

                            case R.id.dropID:
                                Toast.makeText(getApplicationContext(), "dropID", Toast.LENGTH_SHORT);
                                Intent dropIDIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(dropIDIntent);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                p.show();
            }
        });

        //명함 목록을 위한 리스트뷰
        cardList = (ListView)findViewById(R.id.cardList);

        searchAdapter = new searchAdapter();
        cardList.setAdapter(searchAdapter);

        GetData task = new GetData();

        //아이디값 받아오기
        String userID = loginid;
        task.execute("http://yujinpark10.dothome.co.kr/maincardlist.php", userID);//아이디값 받아온거  보내기

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, CardClicked.class);
                intent.putExtra("userID", loginid);
                cardListViewItem selected = (cardListViewItem) adapterView.getItemAtPosition(i);
                intent.putExtra("cardNum", selected.getCardNum());
                intent.putExtra("mine1",0);
                startActivity(intent);
            }
        });

        // 리스트뷰 검색
        final EditText editSearch = (EditText)findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString() ;

                ((searchAdapter)cardList.getAdapter()).getFilter().filter(filterText) ;
            }
        });

        // 내 명함 버튼
        btn_myCard = (Button)findViewById(R.id.btn_myCard);
        btn_myCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, myCardListActivity.class);
                intent.putExtra("userID", loginid);
                intent.putExtra("mine1",1);
                startActivity(intent);
            }
        });

        // 명함등록 버튼
        btn_cardEnroll = (Button)findViewById(R.id.btn_cardEnroll);
        btn_cardEnroll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,CardEnrollActivity.class);
                intent.putExtra("userID", loginid);
                intent.putExtra("mine1",0);
                startActivity(intent);
            }
        });

        // 명함교환 버튼
        btn_cardChange = (Button)findViewById(R.id.btn_cardChange);
        btn_cardChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowChangeDialog();
            }
        });
    }

    // 상대 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String>{
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

    // 상대 명함 리스트뷰 검색결과
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                int cardNum = item.getInt(TAG_CARDNUM);
                String name = item.getString(TAG_NAME);
                String company = item.getString(TAG_COMPANY);

                searchAdapter.addItem(cardNum, name, company);
            }

            searchAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

        }

    }

    // 명함 교환 다이어로그
    private void ShowChangeDialog(){
        LayoutInflater changeDialog = LayoutInflater.from(this);
        final View changeDialogLayout = changeDialog.inflate(R.layout.changedialog, null);
        final Dialog myEnrollDialog = new Dialog(this);

        myEnrollDialog.setTitle("명함 교환");
        myEnrollDialog.setContentView(changeDialogLayout);
        myEnrollDialog.show();

        Button btn_cancel = (Button)changeDialogLayout.findViewById(R.id.btn_cancel);
        Button btn_sendNFC = (Button)changeDialogLayout.findViewById(R.id.btn_sendgetNFC);
        Button btn_QRCode = (Button)changeDialogLayout.findViewById(R.id.btn_QRCode);

        btn_sendNFC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, nfcChangePage.class);
                intent.putExtra("userID", loginid);
                startActivity(intent);
            }
        });

        btn_QRCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myEnrollDialog.cancel();
            }
        });
    }

    //백버튼 두번 누를시종료
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            ActivityCompat.finishAffinity(this);
        }
    }
}
