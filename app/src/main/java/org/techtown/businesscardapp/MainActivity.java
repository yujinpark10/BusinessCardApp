package org.techtown.businesscardapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private boolean checkKing;

    private LinearLayout kingLayout;
    private ImageView img_setting;
    private ImageView img_myCard;
    private ImageView img_cardEnroll;
    private ImageView img_cardChange;
    //private Button btn_cardEnroll;
    //private Button btn_cardChange;
    private ImageView kingCardImage;
    private TextView kingCardListName;
    private TextView kingCardListCompany;
    private TextView leftgalho, rightgalho;

    private static final String TAG_ME_JSON="responseme";
    private static final String TAG_JSON="responseyou";
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
    ListView cardList = null;
    String mJsonString;
    private static searchAdapter searchAdapter;
    String loginid, loginpassword;
    private int kingCardNum;
    private String kingCardAddress;


    //nfc 장치 사용 가능 확인 변수
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //아이디값 저장 변수
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginid = auto.getString("et_id",null);
        loginpassword = auto.getString("et_password", null);

        //nfc 장치 사용 하기전에 부름
         nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // 설정 버튼 클릭시
        img_setting = (ImageView)findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
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

                            case R.id.updateID:
                                /*
                                Intent modifyIntent = new Intent(MainActivity.this, MemberModifyActivity.class);
                                modifyIntent.putExtra("userID", loginid);
                                startActivity(modifyIntent);
                                 */
                                ShowMemberModifyDialog();
                                return true;

                            case R.id.dropID:
                                /*
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                };

                                MemberDelete MemberDelete = new MemberDelete(loginid, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                queue.add(MemberDelete);

                                Toast.makeText(getApplicationContext(), "drop", Toast.LENGTH_SHORT);
                                Intent dropIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(dropIntent);
                                SharedPreferences dropauto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor dropeditor = dropauto.edit();
                                dropeditor.clear();
                                dropeditor.commit();
                                Toast.makeText(MainActivity.this, "회원탈퇴.", Toast.LENGTH_SHORT).show();
                                finish();
                                 */
                                ShowMemberDeleteDialog();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                p.show();
            }
        });

        //아이디값 받아오기
        String userID = loginid;

        // 대표 명함
        kingCardImage = (ImageView)findViewById(R.id.kingCardImage);
        kingCardListName = (TextView)findViewById(R.id.kingCardListName);
        kingCardListCompany = (TextView)findViewById(R.id.kingCardListCompany);
        leftgalho = (TextView)findViewById(R.id.leftgalho);
        rightgalho = (TextView)findViewById(R.id.rightgalho);

        GetKing taskKing = new GetKing();
        taskKing.execute("http://yujinpark10.dothome.co.kr/kingCard.php", userID);

        // 대표 명함 클릭시
        kingLayout = (LinearLayout)findViewById(R.id.kingLayout);
        kingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkKing) {
                    Intent intent = new Intent(MainActivity.this, CardClicked.class);
                    intent.putExtra("userID", loginid);
                    intent.putExtra("cardNum", kingCardNum);
                    intent.putExtra("mine1",1);
                    intent.putExtra("address", kingCardAddress);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"대표 명함을 선택해주세요.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, myCardListActivity.class);
                    intent.putExtra("userID", loginid);
                    intent.putExtra("mine1",1);
                    startActivity(intent);
                }
            }
        });

        //명함 목록을 위한 리스트뷰
        cardList = (ListView)findViewById(R.id.cardList);

        searchAdapter = new searchAdapter();
        cardList.setAdapter(searchAdapter);

        GetData task = new GetData();
        task.execute("http://yujinpark10.dothome.co.kr/maincardlist.php", userID);//아이디값 받아온거  보내기

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, CardClicked.class);
                intent.putExtra("userID", loginid);
                cardListViewItem selected = (cardListViewItem) adapterView.getItemAtPosition(i);
                intent.putExtra("cardNum", selected.getCardNum());
                intent.putExtra("mine1",0);
                intent.putExtra("address", selected.getAddress());
                startActivity(intent);
            }
        });

        // 리스트뷰 검색
        final ClearEditText clearSearch = (ClearEditText)findViewById(R.id.clearSearch);
        clearSearch.addTextChangedListener(new TextWatcher() {
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
        img_myCard = (ImageView)findViewById(R.id.img_myCard);
        img_myCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, myCardListActivity.class);
                intent.putExtra("userID", loginid);
                intent.putExtra("mine1",1);
                startActivity(intent);
            }
        });

        // 명함등록 버튼
        img_cardEnroll = (ImageView) findViewById(R.id.img_cardEnroll);
        img_cardEnroll.setOnClickListener(new View.OnClickListener()
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
        img_cardChange = (ImageView) findViewById(R.id.img_cardChange);
        img_cardChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowChangeDialog();
            }
        });
    }

    // 회원 정보 수정 다이어로그
    private void ShowMemberModifyDialog(){
        LayoutInflater modifyDialog = LayoutInflater.from(this);
        final View modifyDialogLayout = modifyDialog.inflate(R.layout.modifydialog, null);
        final Dialog myModifyDialog = new Dialog(this);

        myModifyDialog.setTitle("비밀번호 확인");
        myModifyDialog.setContentView(modifyDialogLayout);
        myModifyDialog.show();

        final EditText check = (EditText)modifyDialogLayout.findViewById(R.id.check);
        Button btn_cancel = (Button)modifyDialogLayout.findViewById(R.id.btn_cancel);
        Button btn_check = (Button)modifyDialogLayout.findViewById(R.id.btn_check);

        btn_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String checkpass = check.getText().toString();
                String checklogin = loginpassword;

                if(!checkpass.equals(checklogin)) {
                    Toast.makeText(getApplicationContext(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    Intent modifyIntent = new Intent(MainActivity.this, MemberModifyActivity.class);
                    modifyIntent.putExtra("userID", loginid);
                    startActivity(modifyIntent);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myModifyDialog.cancel();
            }
        });
    }

    // 회원 탈퇴 다이어로그
    private void ShowMemberDeleteDialog(){
        LayoutInflater deleteDialog = LayoutInflater.from(this);
        final View deleteDialogLayout = deleteDialog.inflate(R.layout.deletedialog, null);
        final Dialog myDeleteDialog = new Dialog(this);

        myDeleteDialog.setTitle("비밀번호 확인");
        myDeleteDialog.setContentView(deleteDialogLayout);
        myDeleteDialog.show();

        final EditText check = (EditText)myDeleteDialog.findViewById(R.id.check);
        Button btn_cancel = (Button)deleteDialogLayout.findViewById(R.id.btn_cancel);
        Button btn_check = (Button)deleteDialogLayout.findViewById(R.id.btn_check);

        btn_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String checkpass = check.getText().toString();
                String checklogin = loginpassword;

                if(!checkpass.equals(checklogin)) {
                    Toast.makeText(getApplicationContext(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    };

                    MemberDelete MemberDelete = new MemberDelete(loginid, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(MemberDelete);

                    Toast.makeText(getApplicationContext(), "drop", Toast.LENGTH_SHORT);
                    Intent dropIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(dropIntent);
                    SharedPreferences dropauto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor dropeditor = dropauto.edit();
                    dropeditor.clear();
                    dropeditor.commit();
                    Toast.makeText(MainActivity.this, "회원탈퇴.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myDeleteDialog.cancel();
            }
        });
    }

    // 대표 명함 불러오기
    private class GetKing extends AsyncTask<String, Void, String>{
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
                showResultKing();
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

    // 대표 명함 검색결과
    private void showResultKing(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_ME_JSON);

            checkKing = false;

            for(int i=0;i<jsonArray.length();i++){

                checkKing = true;

                JSONObject item = jsonArray.getJSONObject(i);

                int cardNum = item.getInt(TAG_CARDNUM);
                String name = item.getString(TAG_NAME);
                String company = item.getString(TAG_COMPANY);
                String address = item.getString(TAG_ADDRESS);

                kingCardNum = cardNum;
                kingCardListName.setText(name);
                kingCardListCompany.setText(company);
                kingCardAddress = address;
            }

            if(checkKing){
                kingCardImage.setVisibility(View.VISIBLE);
                kingCardListName.setVisibility(View.VISIBLE);
                kingCardListCompany.setVisibility(View.VISIBLE);
                leftgalho.setVisibility(View.VISIBLE);
                rightgalho.setVisibility(View.VISIBLE);
            } else {
                kingCardImage.setVisibility(View.GONE);
                kingCardListName.setText("내 명함에서\n대표 명함을 설정해주세요.");
                kingCardListCompany.setVisibility(View.GONE);
                leftgalho.setVisibility(View.GONE);
                rightgalho.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

        }

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
                String team = item.getString(TAG_TEAM);
                String position = item.getString(TAG_POSITION);
                String coNum = item.getString(TAG_CONUM);
                String num = item.getString(TAG_NUM);
                String e_mail = item.getString(TAG_E_MAIL);
                String faxNum = item.getString(TAG_FAXNUM);
                String address = item.getString(TAG_ADDRESS);
                String cardimage = item.getString(TAG_CARDIMAGE);

                searchAdapter.addItem(cardNum, name, company, team, position, coNum, num, e_mail, faxNum, address, cardimage);
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
                if(checkKing){

                    if(nfcAdapter!=null && nfcAdapter.isEnabled()){
                        Intent intent = new Intent(MainActivity.this, nfcChangePage.class);
                        intent.putExtra("userID", loginid);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),"NFC가 꺼져있습니다.\n설정에서 NFC를 활성화 시켜주세요.",Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(),"대표 명함을 선택해주세요.",Toast.LENGTH_SHORT).show();
                    Intent kingintent = new Intent(MainActivity.this, myCardListActivity.class);
                    kingintent.putExtra("userID", loginid);
                    kingintent.putExtra("mine1",1);
                    startActivity(kingintent);
                }
            }
        });

        btn_QRCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkKing){
                    Intent intent = new Intent(MainActivity.this, QRchange_page.class);
                    intent.putExtra("userID", loginid);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"대표 명함을 선택해주세요.",Toast.LENGTH_SHORT).show();
                    Intent kingintent = new Intent(MainActivity.this, myCardListActivity.class);
                    kingintent.putExtra("userID", loginid);
                    kingintent.putExtra("mine1",1);
                    startActivity(kingintent);
                }
            }
        });

        //Button btn_image = (Button)changeDialogLayout.findViewById(R.id.btn_image);
        /*
        test용 버튼입니다.
        btn_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    Intent intent = new Intent(MainActivity.this, ImageChangeTest.class);
                    startActivity(intent);
            }
        });
        */

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
