package org.techtown.businesscardapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CardEnrollActivity extends AppCompatActivity {

    private static final String TAG = "CardEnrollActivity";
    private EditText et_name, et_company, et_team, et_position, et_conumber, et_pnumber, et_email, et_fnumber, et_address;
    private Button btn_enrollSave, btn_enrollCancel;
    private AlertDialog dialog;
    private ImageView imageView;

    //카메라 변수 설정
    private static final int CAMERA_CODE = 10;
    private static final int GALLERY_CODE = 20;
    private static final int IMAGEDELETE_CODE = 30;
    private String mCurrentPhotoPath;
    private String cardImage = "null";

    private boolean checkImage;
    private TextView imageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_enroll);
        getSupportActionBar().hide();

        //카메라 권한 요청
        requirePermission();

        Intent intent = new Intent(this.getIntent());
        final int mine1 = intent.getIntExtra("mine1",0);
        final String userID = getIntent().getStringExtra("userID");
        final String mine = String.valueOf(mine1);

        //EditText 추가
        et_name = (EditText)findViewById(R.id.et_name);
        et_company = (EditText)findViewById(R.id.et_company);
        et_team = (EditText)findViewById(R.id.et_team);
        et_position = (EditText)findViewById(R.id.et_position);
        et_conumber = (EditText)findViewById(R.id.et_conumber);
        et_pnumber = (EditText)findViewById(R.id.et_pnumber);
        et_email = (EditText)findViewById(R.id.et_email);
        et_fnumber = (EditText)findViewById(R.id.et_fnumber);
        et_address = (EditText) findViewById(R.id.et_address);

        // 이미지 띄어주는 부분 선언
        imageView = (ImageView)findViewById(R.id.card);
        imageText = (TextView)findViewById(R.id.imageText);



        //다이얼 로그 버튼 클릭
        Button btn_dialog = (Button)findViewById(R.id.button2);
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowimageDialog();
            }
        });
//
//        //카메라 버튼 클릭
//        Button button = (Button) findViewById(R.id.camera);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean camera = ContextCompat.checkSelfPermission
//                        (view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//                boolean write = ContextCompat.checkSelfPermission
//                        (view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//                if (camera && write) {
//                    //사진찍은 인텐트 코드 넣기
//                    takePicture();
//                } else {
//                    Toast.makeText(CardEnrollActivity.this, "카메라 권한 및 쓰기 권한을 주지 않았습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        //갤러리 버튼 클릭
//        Button button1 = (Button)findViewById(R.id.gallery);
//        button1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent,GALLERY_CODE);
//                }
//        });
//
//        //이미지 삭체 버튼 클릭
//        Button button3 = (Button)findViewById(R.id.imagedelete);
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView imageView = (ImageView)findViewById(R.id.card);
//                cardImage = "null";
//                imageView.setImageResource(android.R.color.transparent);
//            }
//        });

        // 전화번호 형식으로 변환하기
        et_conumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        et_pnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        et_fnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //취소 버튼 클릭시 // 취소 확인하기 기능 추가하면 좋을듯
        btn_enrollCancel = (Button)findViewById(R.id.btn_enrollCancel);
        btn_enrollCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //저장 버튼 클릭시
        btn_enrollSave = (Button)findViewById(R.id.btn_enrollSave);
        btn_enrollSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //EditText에 현재 입력되어있는 값을 get 해온다.
                String name = et_name.getText().toString();
                String company = et_company.getText().toString();
                String team = et_team.getText().toString();
                String position = et_position.getText().toString();
                String coNum = et_conumber.getText().toString();
                String num = et_pnumber.getText().toString();
                String e_mail = et_email.getText().toString();
                String faxNum = et_fnumber.getText().toString();
                String address = et_address.getText().toString();

                //빈칸 없이 입력 확인
                if (name.equals("") || company.equals("") || team.equals("") || position.equals("") || coNum.equals("") || num.equals("") || e_mail.equals("") || faxNum.equals("") || address.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardEnrollActivity.this);
                    dialog = builder.setMessage("빈칸없이 입력해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //카드 등록 시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//카드등록 성공한 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardEnrollActivity.this);
                                dialog = builder.setMessage("명함 등록에 성공했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();
                            } else {//카드 등록 실패한경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardEnrollActivity.this);
                                dialog = builder.setMessage("명함 등록에 실패했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 volley 이용해서 요청을 함.name, company, team, position, coNum, num, e_mail, faxNum, address
                CardEnroll cardEnroll = new CardEnroll(name, company, team, position, coNum, num, e_mail, faxNum, address, userID, mine, cardImage, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardEnrollActivity.this);
                queue.add(cardEnroll);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CardEnrollActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, 1500);
            }
        });
    }

    @Override
    protected  void onStop(){
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    //카메라 권한 요청
    void requirePermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                //권한이 허가가 안됬을 경우 요청할 권한을 모집하는 부분
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            //권한 요청 하는 부분
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    //카메라 겔러리 사진삭제 다이얼로그 불러오기
    private void ShowimageDialog(){
        LayoutInflater imageDialog = LayoutInflater.from(this);
        final View imageDialogLayout = imageDialog.inflate(R.layout.image_change_dialog, null);
        final Dialog Image_Dialog = new Dialog(this);

        Image_Dialog.setTitle("사진 등록");
        Image_Dialog.setContentView(imageDialogLayout);
        Image_Dialog.show();

        Button btn_camera = (Button)imageDialogLayout.findViewById(R.id.btn_camara);
        Button btn_gallery = (Button)imageDialogLayout.findViewById(R.id.btn_gallery);
        Button btn_delete = (Button)imageDialogLayout.findViewById(R.id.btn_delete);
        Button btn_cancel = (Button)imageDialogLayout.findViewById(R.id.btn_cancel);

        //카메라 클릭시
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean camera = ContextCompat.checkSelfPermission
                        (view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                boolean write = ContextCompat.checkSelfPermission
                        (view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (camera && write) {
                    //사진찍은 인텐트 코드 넣기
                    takePicture();
                    Image_Dialog.cancel();
                } else {
                    Toast.makeText(CardEnrollActivity.this, "카메라 권한 및 쓰기 권한을 주지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //갤러리 클릭시
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_CODE);
                Image_Dialog.cancel();
            }
        });

        //삭제 클릭시
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage = false;
                // 텍스트 or 이미지 뷰 선택
                if(checkImage) {
                    imageText.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageText.setVisibility(View.VISIBLE);
//                    imageText.setText("사진이 없습니다.\n등록 하시려면 + 버튼을 눌러주세요.");
                    imageView.setVisibility(View.GONE);
                }
                cardImage = "null";
                imageView.setImageResource(android.R.color.transparent);
                Image_Dialog.cancel();
            }
        });

        //취소 클릭시
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Image_Dialog.cancel();
            }
        });
    }


    //카메라 불러오기
    void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this, "org.techtown.cam.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAMERA_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //사진 파일 만들기
    private File createImageFile() throws IOException {
        // Create an image file namΩe
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_CODE){

            checkImage = true;

            // 텍스트 or 이미지 뷰 선택
            if(checkImage) {
                imageText.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageText.setVisibility(View.VISIBLE);
//                imageText.setText("사진이 없습니다.\n등록 하시려면 + 버튼을 눌러주세요.");
                imageView.setVisibility(View.GONE);
            }

            //이미지 비트맵
            Bitmap bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath);
           imageView.setImageBitmap(bitmap1);

            //비율 설정
            int width = bitmap1.getWidth();
            int height = bitmap1.getHeight();
            int newWidth = width;
            int newHeight = height;
            float rate = 0.0f;
            int maxResolution = 700;

            if(width > height)
            {
                if(maxResolution < width)
                {
                    rate = maxResolution / (float) width;
                    newHeight = (int) (height * rate);
                    newWidth = maxResolution;
                }
            }
            else
            {
                if(maxResolution < height)
                {
                    rate = maxResolution / (float) height;
                    newWidth = (int) (width * rate);
                    newHeight = maxResolution;
                }
            }
           Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap1, newWidth, newHeight, true);

            //비트맵 -> 바이트 배열
           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
           byte[] imageBytes = byteArrayOutputStream.toByteArray();
           String bitmap2 = Base64.encodeToString(imageBytes, Base64.DEFAULT);//NO_WRAP
            try {
                cardImage = URLEncoder.encode(bitmap2, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == GALLERY_CODE)
        {
            checkImage = true;

            // 텍스트 or 이미지 뷰 선택
            if(checkImage) {
                imageText.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageText.setVisibility(View.VISIBLE);
//                imageText.setText("사진이 없습니다.\n등록 하시려면 + 버튼을 눌러주세요.");
                imageView.setVisibility(View.GONE);
            }

            Uri source = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(source));
                imageView.setImageBitmap(bitmap);

                //비율 설정
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = width;
                int newHeight = height;
                float rate = 0.0f;
                int maxResolution = 700;

                if(width > height)
                {
                    if(maxResolution < width)
                    {
                        rate = maxResolution / (float) width;
                        newHeight = (int) (height * rate);
                        newWidth = maxResolution;
                    }
                }
                else
                {
                    if(maxResolution < height)
                    {
                        rate = maxResolution / (float) height;
                        newWidth = (int) (width * rate);
                        newHeight = maxResolution;
                    }
                }
                Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                //비트맵 -> 바이트 배열
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String bitmap2 = Base64.encodeToString(imageBytes, Base64.DEFAULT);//NO_WRAP
                try {
                    cardImage = URLEncoder.encode(bitmap2, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}