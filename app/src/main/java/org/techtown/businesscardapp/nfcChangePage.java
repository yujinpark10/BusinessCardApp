package org.techtown.businesscardapp;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class nfcChangePage extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    TextView Text1;
    Button go_back;

    private String userID;
    private String yourID;

    private AlertDialog dialog;
    private static final String TAG_JSON="response";
    private static final String TAG_CHECK = "check";
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcchange);
        getSupportActionBar().hide();

        //아이디값 저장 변수
        userID = getIntent().getStringExtra("userID");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Text1 = (TextView) findViewById(R.id.Text1);
        go_back = (Button)findViewById(R.id.go_back);

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nfcChangePage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Text1.setText("교환할 디바이스와 접촉하여 주세요.\n교환이 끝나면 나가기를 눌러주세요.");

        //보내는 부분
        NdefMessage ndefMessage = createNdefMessage(userID);
        nfcAdapter.setNdefPushMessage(ndefMessage, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables != null && parcelables.length > 0)
                {
                    readTextFromMessage((NdefMessage)parcelables[0]);
                }else{
                    Toast.makeText(this,"ndef 메시지를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
                }

        }

    }

    private void readTextFromMessage(NdefMessage ndefMessage){
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagcontent = getTextFromNdefRecord(ndefRecord);

            // 여기가 읽은 메시지
            yourID = tagcontent;

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            };

            GetData task = new GetData();
            task.execute("http://yujinpark10.dothome.co.kr/cardExchange.php", userID, yourID);//아이디값 받아온거  보내기

        }else{
            Toast.makeText(this,"ndef 레코드를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,nfcChangePage.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);

    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    private NdefRecord createTextRecord(String content){
        try{
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            final byte[] text = (content).getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language,0,languageSize);
            payload.write(text,0,textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload.toByteArray());

        }catch (UnsupportedEncodingException e){
            Log.e("createTextRecord",e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content){

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord });

        return ndefMessage;
    }

    public String getTextFromNdefRecord (NdefRecord ndefRecord)
    {
        String tagContent = null;

        try{
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, 3,
                    payload.length-3,textEncoding);
        }catch (UnsupportedEncodingException e)
        {
            Log.e("getTextFromNdefRecord",e.getMessage(),e);
        }
        return tagContent;
    }

    // 상대 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        ProgressDialog dialog = new ProgressDialog(nfcChangePage.this);

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
            String postParameters = "userID=" + userID + "&" + "yourID=" + yourID;

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(nfcChangePage.this);
                    dialog = builder.setMessage("명함이 교환되었습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(nfcChangePage.this);
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
        Intent intent = new Intent(nfcChangePage.this, MainActivity.class);
        startActivity(intent);
    }
}
