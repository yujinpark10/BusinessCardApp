package org.techtown.businesscardapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class QRchange_page extends AppCompatActivity {

    private TextView QRtext;
    private ImageView QRimage;
    private Button QRscanner;
    private Button go_back2;

    private String userID;
    private String yourID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_change);
        getSupportActionBar().hide();

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

                CardExchange cardExchange = new CardExchange(userID, yourID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(QRchange_page.this);
                queue.add(cardExchange);

                Toast.makeText(this,"아이디 스캔 완료 되었습니다.",Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //백버튼
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(QRchange_page.this, MainActivity.class);
        startActivity(intent);
    }
}
