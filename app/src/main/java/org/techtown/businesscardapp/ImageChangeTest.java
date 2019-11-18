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

        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(source1 != null){
                    Bitmap processedBitmap = ProcessingBitmap();
                    if(processedBitmap != null){
                        changed_image.setImageBitmap(processedBitmap);
                        Toast.makeText(getApplicationContext(),"변환완료!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"문제가 발생했네요.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"같은 이미지입니다.",Toast.LENGTH_LONG).show();
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
        try{
            bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(source1));
            Bitmap.Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm1.getWidth(),bm1.getHeight(),config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1,0,0,null);

            String captionString = change_text.getText().toString();

            if(captionString != null){
                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLUE);
                paintText.setTextSize(200);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(10f,10f,10f,Color.BLACK);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString,0,captionString.length(),rectText);

                newCanvas.drawText(captionString,0,rectText.height(),paintText);

                Toast.makeText(getApplicationContext(),"drawText: " + captionString,Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getApplicationContext(),"캡션이 비어있음!", Toast.LENGTH_LONG).show();
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();

        }
        return newBitmap;
    }

    //백버튼
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ImageChangeTest.this, MainActivity.class);
        startActivity(intent);
    }
}
