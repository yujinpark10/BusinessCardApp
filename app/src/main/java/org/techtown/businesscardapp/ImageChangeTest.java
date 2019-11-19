package org.techtown.businesscardapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileNotFoundException;

public class ImageChangeTest extends AppCompatActivity {

    private TextView source_text;
    private ImageView changed_image;
    private EditText change_text;
    private Button change_image;
    private Button load_image;

    final int RQS_IMAGE1 = 1;
    Uri source1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_image);
        getSupportActionBar().hide();

        source_text = (TextView)findViewById(R.id.source_text);
        changed_image = (ImageView)findViewById(R.id.changed_image);
        change_image = (Button)findViewById(R.id.change_image);
        load_image = (Button)findViewById(R.id.load_image);
        change_text = (EditText)findViewById(R.id.change_text);

        //읽기 버튼
        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });

        //변환 버튼
        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(source1 != null || true){
                    Bitmap processedBitmap = ProcessingBitmap();
                    if(processedBitmap != null){
                        changed_image.setImageBitmap(processedBitmap);
                        Toast.makeText(getApplicationContext(),"변환완료!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"문제가 발생했네요.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"이미지를 불러오지 않았습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source1 = data.getData();
                    source_text.setText(source1.toString());
                    break;
            }
        }

    }

    private Bitmap ProcessingBitmap(){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;
        //try{
            bm1 = BitmapFactory.decodeResource(getResources(),R.drawable.namecard_basic4);
            //bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(source1));
            Bitmap.Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm1.getWidth(),bm1.getHeight(),config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1,0,0,null);

            String captionString = change_text.getText().toString();

            if(captionString != null){
                /*
                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLACK);
                paintText.setTextSize(350);
                paintText.setStyle(Paint.Style.FILL);
                */
                //paintText.setShadowLayer(10f,10f,10f,Color.BLACK);
                Paint paintText1 = setTextsize(350);
                Paint paintText2 = setTextsize(200);
                Paint paintText3 = setTextsize(300);
                Paint paintText4 = setTextsize(150);
                Paint paintText5 = setTextsize(200);
                Paint paintText6 = setTextsize(200);
                Paint paintText7 = setTextsize(150);
                Rect rectText = new Rect();
                paintText1.getTextBounds("김철수",0,"김철수".length(),rectText);
                newCanvas.drawText("김철수",150,rectText.height()+150,paintText1);
                paintText2.getTextBounds("작전과장",0,"작전과장".length(),rectText);
                newCanvas.drawText("돌격대장",170,rectText.height()+550,paintText2);
                paintText3.getTextBounds("동아컴퍼니",0,"동아컴퍼니".length(),rectText);
                newCanvas.drawText("동아컴퍼니",150,rectText.height()+1600,paintText3);
                paintText4.getTextBounds("부산광역시 사하구 낙동대로 550번길 37",0,"부산광역시 사하구 낙동대로 550번길 37".length(),rectText);
                newCanvas.drawText("부산광역시 사하구 낙동대로 550번길 37",150,rectText.height()+1930,paintText4);
                paintText5.getTextBounds("010 1234 5678",0,"010 1234 5678".length(),rectText);
                newCanvas.drawText("010 1234 5678",2380,rectText.height()+450,paintText5);
                paintText6.getTextBounds("051 1234 5678",0,"051 1234 5678".length(),rectText);
                newCanvas.drawText("051 1234 5678",2380,rectText.height()+720,paintText6);
                paintText7.getTextBounds("nancheonjaeya@naver.com",0,"nancheonjaeya@naver.com".length(),rectText);
                newCanvas.drawText("nancheonjaeya@naver.com",2380,rectText.height()+970,paintText7);


                Toast.makeText(getApplicationContext(),"drawText: " + captionString,Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getApplicationContext(),"캡션이 비어있음!", Toast.LENGTH_LONG).show();
            }
        //}catch(FileNotFoundException e){
         //   e.printStackTrace();

        //}


        return newBitmap;
    }

    private Paint setTextsize(int textsize){
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(textsize);
        paintText.setStyle(Paint.Style.FILL);

        return paintText;
    }

    //백버튼
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ImageChangeTest.this, MainActivity.class);
        startActivity(intent);
    }
}
