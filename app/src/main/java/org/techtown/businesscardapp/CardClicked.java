package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

public class CardClicked extends AppCompatActivity implements OnMapReadyCallback {

    private boolean checkimage;

    private String use_company, use_conumber, use_pnumber, use_email, use_address, use_name, use_position;
    private TextView inp_name, inp_company, inp_team, inp_position, inp_conumber, inp_pnumber, inp_email, inp_fnumber, inp_address;
    private Button btn_modify, btn_delete, btn_cancel;
    private ImageView call_conumber, call_pnumber, message_pnumber, send_email,cardview;


    private String userID, address;
    private int cardNum;

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
    private static final String TAG_CARDIMAGE="cardimage";
    String mJsonString;

    private String mapResult;
    private double getLat, getLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_clicked);
        getSupportActionBar().hide();

        checkimage = false;

        inp_name = (TextView)findViewById(R.id.inp_name);
        inp_company = (TextView)findViewById(R.id.inp_company);
        inp_team = (TextView)findViewById(R.id.inp_team);
        inp_position = (TextView)findViewById(R.id.inp_position);
        inp_conumber = (TextView)findViewById(R.id.inp_conumber);
        inp_pnumber = (TextView)findViewById(R.id.inp_pnumber);
        inp_email = (TextView)findViewById(R.id.inp_email);
        inp_fnumber = (TextView)findViewById(R.id.inp_fnumber);
        inp_address = (TextView)findViewById(R.id.inp_address);

        userID = getIntent().getStringExtra("userID");
        cardNum = getIntent().getIntExtra("cardNum", 0);
        address = getIntent().getStringExtra("address");

        // 카드번호 넘기기 성공
        //inp_name.setText(Integer.toString(cardNum));

        GetData task = new GetData();
        task.execute("http://yujinpark10.dothome.co.kr/cardClicked.php", Integer.toString(cardNum));//아이디값 받아온거  보내기

        // 지도
        final Geocoder geocoder = new Geocoder(this);

        // 동아대학교 승학캠퍼스 위도 경도
        getLat = 35.1162735;
        getLng = 128.9682498;

        final TextView mapText = (TextView)findViewById(R.id.mapText);
        final LinearLayout mapTextLayout = (LinearLayout)findViewById(R.id.mapTextLayout);
        final LinearLayout mapLayout = (LinearLayout)findViewById(R.id.mapLayout);
        List<Address> mapList = null;

        try{
            mapList = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if(mapList != null) {
            if(mapList.size()==0) {
                mapText.setText("해당되는 주소 정보는 없습니다.\n주소를 다시 한번 확인해주세요.");
                mapTextLayout.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
            } else {
                mapResult = mapList.get(0).toString(); // 변환한 전체 정보 저장
                getLat = mapList.get(0).getLatitude();
                getLng = mapList.get(0).getLongitude();
                mapTextLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
            }
        }

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        // 전화 문자 이메일 연결 call_conumber, call_pnumber, message_pnumber, send_email
        call_conumber = (ImageView)findViewById(R.id.call_conumber);
        call_pnumber = (ImageView)findViewById(R.id.call_pnumber);
        message_pnumber = (ImageView)findViewById(R.id.message_pnumber);
        send_email = (ImageView)findViewById(R.id.send_email);

        call_conumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+use_conumber));
                startActivity(intent);
            }
        });

        call_pnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+use_pnumber));
                startActivity(intent);
            }
        });

        message_pnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"+use_pnumber));
                startActivity(intent);
            }
        });

        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{use_email});
                email.setType("message/rfc822");
                startActivity(email);
            }
        });

        // 수정 버튼
        btn_modify = (Button)findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardClicked.this, CardModifyActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("cardNum", cardNum);
                intent.putExtra("address", address);
                startActivity(intent);
                finish();
            }
        });

        // 삭제 버튼
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCardDeleteDialog();
            }
        });

        // 취소 버튼
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
            Paint paintText1 = setTextsize(140);
            Paint paintText2 = setTextsize(80);
            Paint paintText3 = setTextsize(120);
            Paint paintText4 = setTextsize(60);
            Paint paintText5 = setTextsize(80);
            Paint paintText6 = setTextsize(80);
            Paint paintText7 = setTextsize(45);
            Rect rectText = new Rect();
            paintText1.getTextBounds(use_name,0,use_name.length(),rectText);
            newCanvas.drawText(use_name,50,rectText.height()+50,paintText1);
            paintText2.getTextBounds(use_position,0,use_position.length(),rectText);
            newCanvas.drawText(use_position,70,rectText.height()+450,paintText2);
            paintText3.getTextBounds(use_company,0,use_company.length(),rectText);
            newCanvas.drawText(use_company,50,rectText.height()+1400,paintText3);
            paintText4.getTextBounds(use_address,0,use_address.length(),rectText);
            newCanvas.drawText(use_address,50,rectText.height()+1730,paintText4);
            paintText5.getTextBounds(use_pnumber,0,use_pnumber.length(),rectText);
            newCanvas.drawText(use_pnumber,2080,rectText.height()+410,paintText5);
            paintText6.getTextBounds(use_conumber,0,use_conumber.length(),rectText);
            newCanvas.drawText(use_conumber,2080,rectText.height()+640,paintText6);
            paintText7.getTextBounds(use_email,0,use_email.length(),rectText);
            newCanvas.drawText(use_email,2080,rectText.height()+850,paintText7);

        return newBitmap;
    }

    private Paint setTextsize(int textsize){
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,textsize,getResources().getDisplayMetrics()));
        paintText.setStyle(Paint.Style.FILL);

        return paintText;
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
                String cardimage = item.getString(TAG_CARDIMAGE);

                inp_name.setText(name);
                inp_company.setText(company);
                inp_team.setText(team);
                inp_position.setText(position);
                inp_conumber.setText(coNum);
                inp_pnumber.setText(num);
                inp_email.setText(e_mail);
                inp_fnumber.setText(faxNum);
                inp_address.setText(address);

                use_company=company;
                use_pnumber=num;
                use_conumber=coNum;
                use_email=e_mail;
                use_address=address;
                use_name=name;
                use_position=position;

                // 명함 이미지
                cardview = (ImageView)findViewById(R.id.cardview);

                if(cardimage.equals("null")){
                    Bitmap processedBitmap = ProcessingBitmap();
                    if(processedBitmap != null){
                        cardview.setImageBitmap(processedBitmap);
                    }else {
                        Toast.makeText(getApplicationContext(), "명함 이미지를 부르는데 문제가 발생했습니다.", Toast.LENGTH_LONG).show();
                    }
                }else{// 여기에 가져온 명함 이미지를 가져오면 됩니다.

                    try {
                        String bitmap1 = URLDecoder.decode(cardimage, "utf-8");
                        byte[] decodedByteArray = Base64.decode(bitmap1, Base64.NO_WRAP);
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedByteArray,0, decodedByteArray.length);
                        cardview.setImageBitmap(bitmap2);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {

        }

    }

    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng whereCompany = new LatLng(getLat, getLng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(whereCompany);
        markerOptions.title(use_company);
        markerOptions.snippet(use_address);
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(whereCompany,15));

        map.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+use_address));
            startActivity(intent);
        }
    };

    // 카드 삭제 확인 다이어로그
    private void ShowCardDeleteDialog(){
        LayoutInflater deleteDialog = LayoutInflater.from(this);
        final View deleteDialogLayout = deleteDialog.inflate(R.layout.deletecard, null);
        final Dialog myDeleteDialog = new Dialog(this);

        myDeleteDialog.setTitle("카드삭제");
        myDeleteDialog.setContentView(deleteDialogLayout);
        myDeleteDialog.show();

        Button btn_cancel = (Button)deleteDialogLayout.findViewById(R.id.btn_cancel);
        Button btn_check = (Button)deleteDialogLayout.findViewById(R.id.btn_check);

        btn_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                };

                CardDelete CardDelete = new CardDelete(Integer.toString(cardNum), userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardClicked.this);
                queue.add(CardDelete);
                Intent intent = new Intent(CardClicked.this, MainActivity.class);
                startActivity(intent);
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
}
