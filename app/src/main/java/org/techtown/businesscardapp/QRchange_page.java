package org.techtown.businesscardapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QRchange_page extends AppCompatActivity {

    private TextView QRtext;
    private ImageView QRimage;
    private Button QRscanner;
    private Button go_back2;

    private String userID;
    private String yourID;

    private AlertDialog dialog;
    private static final String TAG_JSON="response";
    private static final String TAG_CHECK = "check";
    String mJsonString;

    // 위치
    private String provider, change_where;
    private double longitude, latitude, altitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_change);
        getSupportActionBar().hide();

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보 사용 권한 얻기
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRchange_page.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
        } else {
            // 위치 정보
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            provider = location.getProvider();
            latitude = location.getLatitude(); // 위도
            longitude = location.getLongitude(); // 경도
            altitude = location.getAltitude();

            Geocoder geocoder = new Geocoder(this);
            List<Address> list = null;

            try{
                list = geocoder.getFromLocation(latitude, longitude, 10);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("text", "입출력 오류");
            }

            if(list !=null) {
                change_where = list.get(0).getAddressLine(0);
            }
        }

        QRtext = (TextView)findViewById(R.id.QRtext);
        QRimage = (ImageView)findViewById(R.id.QRimage);
        QRscanner = (Button)findViewById(R.id.QRscanner);
        go_back2 = (Button)findViewById(R.id.go_back2);

        //나가기 버튼
        go_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRchange_page.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //이전 페이지에서 받아온 유저아이디

        userID = getIntent().getStringExtra("userID");

        //유저 아이디로 만든 QR code 포맷
        if(userID != null)
        {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(userID, BarcodeFormat.QR_CODE,500,500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                QRimage.setImageBitmap(bitmap);
            }catch(WriterException e)
            {
                e.printStackTrace();
            }


        }


        //QR 스캐너 버튼 누를시
        final Activity activity = this;

        QRscanner.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("상대방의 QR code를 인식시켜주세요");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });


    }

    // QR코드 스캐너에서 작동하는 방법
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result !=null){
            if(result.getContents()==null){
                Toast.makeText(this,"취소되었습니다.",Toast.LENGTH_LONG).show();
            }else{
                yourID=result.getContents();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                };

                // 시간
                long todayTime = System.currentTimeMillis(); // 현재 시간을 msec 으로 구한다.
                Date todayDate = new Date(todayTime); // 현재 시간을 date 변수에 저장한다.
                SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 시간을 나타낼 포맷을 정한다
                String change_time = todayFormat.format(todayDate);

                GetData task = new GetData();
                task.execute("http://yujinpark10.dothome.co.kr/cardExchange.php", userID, yourID, change_where, change_time);//아이디값 받아온거  보내기

//                Toast.makeText(this,"아이디 스캔 완료 되었습니다.",Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 상대 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        ProgressDialog dialog = new ProgressDialog(QRchange_page.this);

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
            String yourID = params[2];
            String change_where = params[3];
            String change_time = params[4];
            String postParameters = "userID=" + userID + "&" + "yourID=" + yourID + "&" + "change_where=" + change_where + "&" + "change_time=" + change_time;

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

                String check_back = item.getString(TAG_CHECK);

                if(check_back.equals("true")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(QRchange_page.this);
                    dialog = builder.setMessage("명함이 교환되었습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QRchange_page.this);
                    dialog = builder.setMessage("이미 교환 하셨습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                }
            }

        } catch (JSONException e) {

        }

    }

    //백버튼
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(QRchange_page.this, MainActivity.class);
        startActivity(intent);
    }
}
