package org.techtown.businesscardapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    private EditText et_name, et_company, et_team, et_position, et_conumber, et_pnumber, et_email, et_fnumber, et_address, et_memo;
    private Button btn_enrollSave, btn_enrollCancel;
    private AlertDialog dialog;
    private ImageView imageView;
    private Uri photoUri;

    //카메라 변수 설정
    private static final int GALLERY_CROP_CODE = 10,
                             CAMERA_CROP_CODE = 20,
                             CAMERA_CROP_VIEW_CODE = 30,
                             GALLERY_CROP_VIEW_CODE = 40;
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
        et_memo = (EditText) findViewById(R.id.et_memo);

        // 이미지 띄어주는 부분 선언
        imageView = (ImageView)findViewById(R.id.card);
        imageText = (TextView)findViewById(R.id.imageText);

        //다이얼 로그 버튼 클릭
        ImageView btn_dialog = (ImageView)findViewById(R.id.getImage);
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowimageDialog();
            }
        });
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
        String memo = et_memo.getText().toString();

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
                        //finish();
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
        CardEnroll cardEnroll = new CardEnroll(name, company, team, position, coNum, num, e_mail, faxNum, address, userID, mine, cardImage, memo, responseListener);
        RequestQueue queue = Volley.newRequestQueue(CardEnrollActivity.this);
        queue.add(cardEnroll);
        if(mine1 == 1)
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                        @Override
                        public void run() { Intent intent = new Intent(CardEnrollActivity.this, myCardListActivity.class);
                        startActivity(intent);
                        finish();
                        }
            }, 1500);
         } else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                      Intent intent = new Intent(CardEnrollActivity.this, MainActivity.class);
                      startActivity(intent);
                      finish();
                        }
                    }, 1500);
                }
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

        ImageView img_camera = (ImageView)imageDialogLayout.findViewById(R.id.img_camara);
        ImageView img_gallery = (ImageView)imageDialogLayout.findViewById(R.id.img_gallery);
        ImageView img_delete = (ImageView)imageDialogLayout.findViewById(R.id.img_delete);
        Button btn_cancel = (Button)imageDialogLayout.findViewById(R.id.btn_cancel);

        //카메라 클릭시
        img_camera.setOnClickListener(new View.OnClickListener() {
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
        img_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_CROP_CODE);
                Image_Dialog.cancel();
            }
        });

        //삭제 클릭시
        img_delete.setOnClickListener(new View.OnClickListener() {
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
            photoUri = FileProvider.getUriForFile(this, "org.techtown.cam.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAMERA_CROP_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //사진 파일 만들기
    private File createImageFile() throws IOException {
        // Create an image file name
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

            //사진 가져 온거 크롭
            if (requestCode ==  CAMERA_CROP_CODE && resultCode == Activity.RESULT_OK) {

                //크롭 권한추가
                this.grantUriPermission("com.android.camera", photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                //이미지 크롭
                intent.setDataAndType(photoUri, "image/*");// crop한 이미지를 저장할때 200x200 크기로 저장
                intent.putExtra("aspectX", 5); // crop 박스의 x축 비율
                intent.putExtra("aspectY", 9); // crop 박스의 y축 비율
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                //이미지 뷰 이동
                startActivityForResult(intent, CAMERA_CROP_VIEW_CODE);

            }

            //갤러리
            else if (requestCode ==  GALLERY_CROP_CODE && resultCode == Activity.RESULT_OK) {
                Uri source = data.getData();

                //크롭 권한추가
                this.grantUriPermission("com.android.camera", source, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                //이미지 크롭하기
                intent.setDataAndType(source, "image/*");// crop한 이미지를 저장할때 200x200 크기로 저장
                intent.putExtra("aspectX", 5); // crop 박스의 x축 비율
                intent.putExtra("aspectY", 9); // crop 박스의 y축 비율
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, source);

                startActivityForResult(intent, GALLERY_CROP_VIEW_CODE);
            }

            //카메라 크롭 이미지 띄워주기
            else if(requestCode == CAMERA_CROP_VIEW_CODE &&resultCode == Activity.RESULT_OK) {

                //이미지 체킹
                checkImage = true;
                imageText.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                //uri to bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //비트맵 좌90도 회전
                int width1 = bitmap.getWidth();
                int height1 = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width1, height1, matrix, true);

                //이미지 띄우기
                imageView.setImageBitmap(bitmap);

                //DB 올릴 이미지 리사이징
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = width;
                int newHeight = height;
                float rate = 0.0f;
                int maxResolution = 700;
                if (width > height) {
                    if (maxResolution < width) {
                        rate = maxResolution / (float) width;
                        newHeight = (int) (height * rate);
                        newWidth = maxResolution;
                    }
                } else {
                    if (maxResolution < height) {
                        rate = maxResolution / (float) height;
                        newWidth = (int) (width * rate);
                        newHeight = maxResolution;
                    }
                }
                //리사이징 이미지 저장
                Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                //비트맵 -> 바이트 배열
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String bitmap1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);//NO_WRAP
                try {
                    //DB올릴 변수에 저장
                    cardImage = URLEncoder.encode(bitmap1, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            //갤러리 크롭 이미지 띄워주기
            else if(requestCode == GALLERY_CROP_VIEW_CODE && resultCode == Activity.RESULT_OK){

                checkImage = true;
                if (checkImage) {
                    imageText.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageText.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }

                Uri source = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), source);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //비트맵 좌90도 회전
                int width1 = bitmap.getWidth();
                int height1 = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width1, height1, matrix, true);

                //이미지 띄우기
                imageView.setImageBitmap(bitmap);

                //리사이징
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = width;
                int newHeight = height;
                float rate = 0.0f;
                int maxResolution = 700;

                if (width > height) {
                    if (maxResolution < width) {
                        rate = maxResolution / (float) width;
                        newHeight = (int) (height * rate);
                        newWidth = maxResolution;
                    }
                } else {
                    if (maxResolution < height) {
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
                String bitmap1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);//NO_WRAP
                try {
                    cardImage = URLEncoder.encode(bitmap1, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
    }
}