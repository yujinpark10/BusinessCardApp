package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CardClicked extends AppCompatActivity implements OnMapReadyCallback {

    private String use_company, use_conumber, use_pnumber, use_email, use_address;
    private TextView inp_name, inp_company, inp_team, inp_position, inp_conumber, inp_pnumber, inp_email, inp_fnumber, inp_address;
    private Button btn_modify, btn_delete, btn_cancel;
    private ImageView call_conumber, call_pnumber, message_pnumber, send_email;

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
    String mJsonString;

    private String mapResult;
    private double getLat, getLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_clicked);
        getSupportActionBar().hide();

        inp_name = (TextView)findViewById(R.id.inp_name);
        inp_company = (TextView)findViewById(R.id.inp_company);
        inp_team = (TextView)findViewById(R.id.inp_team);
        inp_position = (TextView)findViewById(R.id.inp_position);
        inp_conumber = (TextView)findViewById(R.id.inp_conumber);
        inp_pnumber = (TextView)findViewById(R.id.inp_pnumber);
        inp_email = (TextView)findViewById(R.id.inp_email);
        inp_fnumber = (TextView)findViewById(R.id.inp_fnumber);
        inp_address = (TextView)findViewById(R.id.inp_address);

        final int mine1 = getIntent().getIntExtra("mine1",0);
        final String userID = getIntent().getStringExtra("userID");
        final int cardNum = getIntent().getIntExtra("cardNum", 0);
        final String address = getIntent().getStringExtra("address");

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
        List<Address> mapList = null;

        try{
            mapList = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if(mapList != null) {
            if(mapList.size()==0) {
                //위치를 못찾을 경우 실패시 동아대학교 승학캠퍼스를 띄움
                mapText.setText("해당되는 주소 정보는 없습니다.\n주소를 다시 한번 확인해주세요.");
                mapText.setVisibility(View.VISIBLE);
            } else {
                mapResult = mapList.get(0).toString(); // 변환한 전체 정보 저장
                getLat = mapList.get(0).getLatitude();
                getLng = mapList.get(0).getLongitude();
                mapText.setVisibility(View.GONE);
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
                startActivity(intent);
            }
        });

        // 삭제 버튼
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        // 취소 버튼
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(whereCompany,16));

        map.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+use_address));
            startActivity(intent);
        }
    };
}
