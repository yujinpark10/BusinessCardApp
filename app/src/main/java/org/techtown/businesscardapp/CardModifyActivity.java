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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CardModifyActivity extends AppCompatActivity {

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
    private static final String TAG_MEMO="memo";
    private EditText et_name, et_company, et_team, et_position, et_conumber, et_pnumber, et_email, et_fnumber, et_address, et_memo;
    private Button btn_modifySave, btn_modifyCancel;
    private AlertDialog dialog;
    String mJsonString;

    private static final int CAMERA_CODE = 10;
    private static final int GALLERY_CODE = 20;
    private String mCurrentPhotoPath;
    private String cardImage;
    private boolean checkImage = false;
    private TextView imageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_modify);
        getSupportActionBar().hide();

        Intent intent = new Intent(this.getIntent());
        final int cardNum = intent.getIntExtra("cardNum", 0);
        final String userID = getIntent().getStringExtra("userID");
        final String address = getIntent().getStringExtra("address");
        final int mine1 = getIntent().getIntExtra("mine1",0);

        //php 연동 회원 정보 받아오기
        GetData task = new GetData();
        task.execute("http://yujinpark10.dothome.co.kr/cardModify_select.php", Integer.toString(cardNum));

        //카메라 권한 요청
        requirePermission();

        //기본값 등록
        et_name = (EditText)findViewById(R.id.et_name);
        et_company = (EditText)findViewById(R.id.et_company);
        et_team = (EditText)findViewById(R.id.et_team);
        et_position = (EditText)findViewById(R.id.et_position);
        et_conumber = (EditText)findViewById(R.id.et_conumber);
        et_pnumber = (EditText)findViewById(R.id.et_pnumber);
        et_email = (EditText)findViewById(R.id.et_email);
        et_fnumber = (EditText)findViewById(R.id.et_fnumber);
        et_address = (EditText)findViewById(R.id.et_address);
        imageText = (TextView) findViewById(R.id.imageText);
        et_memo = (EditText) findViewById(R.id.et_memo);

        // 전화번호 형식으로 변환하기
        et_conumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        et_pnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        et_fnumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


//        //카메라 버튼 클릭
//        Button button = (Button) findViewById(R.id.camera2);
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
//                    Toast.makeText(CardModifyActivity.this, "카메라 권한 및 쓰기 권한을 주지 않았습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        //갤러리 버튼 클릭
//        Button button1 = (Button)findViewById(R.id.gallery2);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent,GALLERY_CODE);
//            }
//        });
//
//        //이미지 삭제 버튼 클릭
//        Button button3 = (Button)findViewById(R.id.imagedelete2);
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView imageView = (ImageView)findViewById(R.id.card2);
//                cardImage = "null";
//                imageView.setImageResource(android.R.color.transparent);
//            }
//        });

        //다이얼 로그 버튼 클릭
        ImageView img_dialog = (ImageView)findViewById(R.id.btn_addimage);
        img_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowimageDialog();
            }
        });


        //취소 버튼 클릭시 // 취소 확인하기 기능 추가하면 좋을듯
        btn_modifyCancel = (Button)findViewById(R.id.btn_modifyCancel);
        btn_modifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardModifyActivity.this, CardClicked.class);
                intent.putExtra("userID", userID);
                intent.putExtra("cardNum", cardNum);
                intent.putExtra("address", address);
                intent.putExtra("mine1",mine1);
                startActivity(intent);
                finish();
            }
        });

        //저장 버튼 클릭시
        btn_modifySave = (Button)findViewById(R.id.btn_modifySave);
        btn_modifySave.setOnClickListener(new View.OnClickListener() {
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
                final String address = et_address.getText().toString();
                String memo = et_memo.getText().toString();

                //빈칸 없이 입력 확인
                if (name.equals("") || company.equals("") || team.equals("") || position.equals("") || coNum.equals("") || num.equals("") || e_mail.equals("") || faxNum.equals("") || address.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardModifyActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardModifyActivity.this);
                                dialog = builder.setMessage("명함 수정에 성공했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                //finish();
                            } else {//카드 등록 실패한경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardModifyActivity.this);
                                dialog = builder.setMessage("명함 수정에 실패했습니다.")
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
                CardModify CardModify = new CardModify(Integer.toString(cardNum), name, company, team, position, coNum, num, e_mail, faxNum, address, userID, cardImage, memo, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardModifyActivity.this);
                queue.add(CardModify);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CardModifyActivity.this, CardClicked.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("cardNum", cardNum);
                        intent.putExtra("address", address);
                        startActivity(intent);
                        finish();
                    }
                },1000);
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

    // 내 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        ProgressDialog dialog = new ProgressDialog(CardModifyActivity.this);

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

    //카메라 겔러리 사진삭제 다이얼로그 불러오기
    private void ShowimageDialog() {
        LayoutInflater imageDialog = LayoutInflater.from(this);
        final View imageDialogLayout = imageDialog.inflate(R.layout.image_change_dialog, null);
        final Dialog Image_Dialog = new Dialog(this);

        Image_Dialog.setTitle("사진 등록");
        Image_Dialog.setContentView(imageDialogLayout);
        Image_Dialog.show();

        ImageView img_camera = (ImageView) imageDialogLayout.findViewById(R.id.img_camara);
        ImageView img_gallery = (ImageView) imageDialogLayout.findViewById(R.id.img_gallery);
        ImageView img_delete = (ImageView) imageDialogLayout.findViewById(R.id.img_delete);
        Button btn_cancel = (Button) imageDialogLayout.findViewById(R.id.btn_cancel);

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
                    Toast.makeText(CardModifyActivity.this, "카메라 권한 및 쓰기 권한을 주지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //갤러리 클릭시
        img_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_CODE);
                Image_Dialog.cancel();
            }
        });

        //삭제 클릭시
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)findViewById(R.id.card2);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK){

            //이미지 비트맵
            Bitmap bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ImageView imageView = (ImageView)findViewById(R.id.card2);
            imageText.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
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
            if(data == null)
            {
                return;
            }


            Uri source = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(source));
                ImageView imageView = (ImageView)findViewById(R.id.card2);
                imageText.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
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
                String memo = item.getString(TAG_MEMO);

                ImageView imageView = (ImageView)findViewById(R.id.card2);
                et_name.setText(name);
                et_company.setText(company);
                et_team.setText(team);
                et_position.setText(position);
                et_conumber.setText(coNum);
                et_pnumber.setText(num);
                et_email.setText(e_mail);
                et_fnumber.setText(faxNum);
                et_address.setText(address);
                cardImage = cardimage;

                // 메모 있나 없나 확인
                if(!memo.equals("null")) {
                    et_memo.setText(memo);
                }


                // 텍스트 or 이미지 뷰 선택
                if (!cardimage.equals("null")) {
                    imageText.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageText.setVisibility(View.VISIBLE);
//                imageText.setText("사진이 없습니다.\n등록 하시려면 + 버튼을 눌러주세요.");
                    imageView.setVisibility(View.GONE);
                }

                    try {
                        String bitmap1 = URLDecoder.decode(cardimage, "utf-8");
                        byte[] decodedByteArray = Base64.decode(bitmap1, Base64.NO_WRAP);
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedByteArray,0, decodedByteArray.length);
                        imageView.setImageBitmap(bitmap2);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }catch (JSONException e) {


        }

    }
}
