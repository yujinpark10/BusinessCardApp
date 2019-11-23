package org.techtown.businesscardapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private boolean checkKing, checkBack;

    private LinearLayout myTextLayout, yourTextLayout, kingLayout, cardLayout, buttonLayout;
    private ImageView img_icon, img_search, img_setting, img_back, img_myCard, img_cardEnroll, img_cardChange;
    private ImageView img_trash1, img_trash2, img_trash3, img_trash4;
    private ClearEditText clearSearch;
    //private Button btn_cardEnroll;
    //private Button btn_cardChange;
    private ImageView kingCardImage;
    private TextView kingText;

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
    private String kingCardName, kingCardCompay, kingCardTeam, kingCardPosition, kingCardCoNum, kingCardNumStr, kingCardE_mail, kingCardFaxNum, kingCardAddress, kingCardImageStr;

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

//        // 검색 버튼 클릭시
//        final ClearEditText clearSearch = (ClearEditText)findViewById(R.id.clearSearch);
//        clearSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, ListViewSearch.class);
//                startActivity(intent);
//            }
//        });

        // LinearLayout 설정 위한 변수 설정
        img_icon = (ImageView)findViewById(R.id.icon);
        myTextLayout = (LinearLayout)findViewById(R.id.myTextLayout);
        yourTextLayout = (LinearLayout)findViewById(R.id.yourTextLayout);
        kingLayout = (LinearLayout)findViewById(R.id.kingLayout);
        cardLayout = (LinearLayout)findViewById(R.id.cardLayout);
        buttonLayout = (LinearLayout)findViewById(R.id.buttonLayout);
        img_trash1 = (ImageView)findViewById(R.id.img_trash1);
        img_trash2 = (ImageView)findViewById(R.id.img_trash2);
        img_trash3 = (ImageView)findViewById(R.id.img_trash3);
        img_trash4 = (ImageView)findViewById(R.id.img_trash4);

        // 검색 버튼 클릭시
        clearSearch = (ClearEditText)findViewById(R.id.clearSearch);
        img_search = (ImageView)findViewById(R.id.img_search);
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBack = true;
                img_icon.setVisibility(View.GONE);
                img_search.setVisibility(View.GONE);
                img_setting.setVisibility(View.GONE);
                myTextLayout.setVisibility(View.GONE);
                yourTextLayout.setVisibility(View.GONE);
                kingLayout.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);
                clearSearch.setVisibility(View.VISIBLE);
                img_back.setVisibility(View.VISIBLE);
                img_trash1.setVisibility(View.GONE);
                img_trash2.setVisibility(View.GONE);
                img_trash3.setVisibility(View.GONE);
                img_trash4.setVisibility(View.GONE);
            }
        });

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

        // 뒤로 가기 이미지 버튼
        img_back = (ImageView)findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBack = false;
                clearSearch.setText(null);
                clearSearch.setVisibility(View.GONE);
                img_back.setVisibility(View.GONE);
                img_icon.setVisibility(View.VISIBLE);
                img_search.setVisibility(View.VISIBLE);
                img_setting.setVisibility(View.VISIBLE);
                myTextLayout.setVisibility(View.VISIBLE);
                yourTextLayout.setVisibility(View.VISIBLE);
                kingLayout.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.VISIBLE);
                img_trash1.setVisibility(View.VISIBLE);
                img_trash2.setVisibility(View.VISIBLE);
                img_trash3.setVisibility(View.VISIBLE);
                img_trash4.setVisibility(View.VISIBLE);
            }
        });

        //아이디값 받아오기
        String userID = loginid;

        // 대표 명함
        kingCardImage = (ImageView)findViewById(R.id.kingCardImage);
        kingText = (TextView)findViewById(R.id.kingText);

        GetKing taskKing = new GetKing();
        taskKing.execute("http://yujinpark10.dothome.co.kr/kingCard.php", userID);

        // 대표 명함 클릭시
        kingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkKing) {
                    Intent intent = new Intent(MainActivity.this, CardClicked.class);
                    intent.putExtra("userID", loginid);
                    intent.putExtra("cardNum", kingCardNum);
                    intent.putExtra("mine1",0);
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

        searchAdapter = new searchAdapter(this);
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

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

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
                String team = item.getString(TAG_TEAM);
                String position = item.getString(TAG_POSITION);
                String coNum = item.getString(TAG_CONUM);
                String num = item.getString(TAG_NUM);
                String e_mail = item.getString(TAG_E_MAIL);
                String faxNum = item.getString(TAG_FAXNUM);
                String address = item.getString(TAG_ADDRESS);
                String cardimage = item.getString(TAG_CARDIMAGE);

                kingCardNum = cardNum;
                kingCardAddress = address;

                kingCardName = name;
                kingCardCompay = company;
                kingCardTeam = team;
                kingCardPosition = position;
                kingCardCoNum = coNum;
                kingCardNumStr = num;
                kingCardE_mail = e_mail;
                kingCardFaxNum = faxNum;
                kingCardAddress = address;
                kingCardImageStr = cardimage;

                if(kingCardImageStr.equals("null")){
                    Bitmap processedBitmap = ProcessingBitmap();
                    if(processedBitmap != null){
                        kingCardImage.setImageBitmap(processedBitmap);
                    }else {
                        Toast.makeText(getApplicationContext(), "명함 이미지를 부르는데 문제가 발생했습니다.", Toast.LENGTH_LONG).show();
                    }
                }else{// 여기에 가져온 명함 이미지를 가져오면 됩니다.

                    try {
                        String bitmap1 = URLDecoder.decode(kingCardImageStr, "utf-8");
                        byte[] decodedByteArray = Base64.decode(bitmap1, Base64.NO_WRAP);
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedByteArray,0, decodedByteArray.length);
                        kingCardImage.setImageBitmap(bitmap2);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(checkKing){
                kingCardImage.setVisibility(View.VISIBLE);
            } else {
                kingCardImage.setVisibility(View.GONE);
                kingText.setText("내 명함에서\n대표 명함을 설정해주세요.");
            }

        } catch (JSONException e) {

        }

    }

    // 명함을 그리는 비트맵 함수
    private Bitmap ProcessingBitmap(){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        bm1 = BitmapFactory.decodeResource(getResources(),R.drawable.namecard_basic4);
        Bitmap.Config config = bm1.getConfig();
        if(config == null){
            config = Bitmap.Config.ARGB_8888;
        }


        newBitmap = Bitmap.createBitmap(bm1.getWidth(),bm1.getHeight(),config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(bm1,0,0,null);
        Paint paintText1 = setTextsize(100);
        Paint paintText2 = setTextsize(50);
        Paint paintText3 = setTextsize(90);
        Paint paintText4 = setTextsize(40);
        Paint paintText5 = setTextsize(50);
        Paint paintText6 = setTextsize(50);
        Paint paintText7 = setTextsize(40);
        Rect rectText = new Rect();
        paintText1.getTextBounds(kingCardName,0,kingCardName.length(),rectText);
        newCanvas.drawText(kingCardName,200,rectText.height()+200,paintText1);
        paintText2.getTextBounds(kingCardPosition,0,kingCardPosition.length(),rectText);
        newCanvas.drawText(kingCardPosition,220,rectText.height()+570,paintText2);
        paintText3.getTextBounds(kingCardCompay,0,kingCardCompay.length(),rectText);
        newCanvas.drawText(kingCardCompay,150,rectText.height()+1400,paintText3);
        paintText4.getTextBounds(kingCardAddress,0,kingCardAddress.length(),rectText);
        newCanvas.drawText(kingCardAddress,120,rectText.height()+1730,paintText4);
        paintText5.getTextBounds(kingCardNumStr,0,kingCardNumStr.length(),rectText);
        newCanvas.drawText(kingCardNumStr,1830,rectText.height()+370,paintText5);
        paintText6.getTextBounds(kingCardCoNum,0,kingCardCoNum.length(),rectText);
        newCanvas.drawText(kingCardCoNum,1830,rectText.height()+600,paintText6);
        int a = kingCardE_mail.indexOf("@");
        if(a != -1 && kingCardE_mail.length()>24)
        {
            Paint paintText8 = setTextsize(40);
            String use_email2;
            String use_email3;
            use_email3 = kingCardE_mail.substring(0,a);
            paintText7.getTextBounds(use_email3,0,use_email3.length(),rectText);
            newCanvas.drawText(use_email3,1830,rectText.height()+830,paintText7);
            use_email2 = kingCardE_mail.substring(a);
            paintText8.getTextBounds(use_email2,0,use_email2.length(),rectText);
            newCanvas.drawText(use_email2,2030,rectText.height()+980,paintText8);
        }
        else
        {
            paintText7.getTextBounds(kingCardE_mail,0,kingCardE_mail.length(),rectText);
            newCanvas.drawText(kingCardE_mail,1830,rectText.height()+830,paintText7);
        }

        return newBitmap;
    }

    private Paint setTextsize(int textsize){
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,textsize,getResources().getDisplayMetrics()));
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTypeface(ResourcesCompat.getFont(this,R.font.ongothic));


        return paintText;
    }

    // 상대 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String>{
        String errorString = null;

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

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
        ImageView img_sendNFC = (ImageView)changeDialogLayout.findViewById(R.id.img_sendgetNFC);
        ImageView img_QRCode = (ImageView)changeDialogLayout.findViewById(R.id.img_QRCode);

        img_sendNFC.setOnClickListener(new View.OnClickListener()
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

        img_QRCode.setOnClickListener(new View.OnClickListener()
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
        if(checkBack){
            checkBack = false;
            clearSearch.setText(null);
            clearSearch.setVisibility(View.GONE);
            img_back.setVisibility(View.GONE);
            img_icon.setVisibility(View.VISIBLE);
            img_search.setVisibility(View.VISIBLE);
            img_setting.setVisibility(View.VISIBLE);
            myTextLayout.setVisibility(View.VISIBLE);
            yourTextLayout.setVisibility(View.VISIBLE);
            kingLayout.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
            img_trash1.setVisibility(View.VISIBLE);
            img_trash2.setVisibility(View.VISIBLE);
            img_trash3.setVisibility(View.VISIBLE);
            img_trash4.setVisibility(View.VISIBLE);
        } else
        {
            if(System.currentTimeMillis()-time>=2000){
                time=System.currentTimeMillis();
                Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
            }else if(System.currentTimeMillis()-time<2000){
                ActivityCompat.finishAffinity(this);
            }
        }
    }
}
