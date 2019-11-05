package org.techtown.businesscardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btn_setting;
    private Button btn_cardEnroll;
    private Button btn_cardChange;
    private static final String TAG_ID="userID";
    private static final String TAG_JSON="responseyou";
    private static final String TAG_NAME = "name";
    private static final String TAG_COMPANY ="company";
    ArrayList<HashMap<String, String>> mArrayList;
    ListView cardList = null;
    String mJsonString;
    private static searchAdapter searchAdapter;
    String loginid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //아이디값 저장 변수
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginid = auto.getString("et_id",null);

     /*   //아이디값 넘어오는지 확인
        String userID = getIntent().getStringExtra("userID");
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText(userID);    */


        /*
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://yujinpark10.dothome.co.kr/maincardlist.php"); //요청 URL을 입력
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)

            conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
            conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

            conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)


            StringBuffer buffer = new StringBuffer();
            buffer.append("userID").append("=").append(loginid).append("&");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); //캐릭터셋 설정

            writer.write(buffer.toString());
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
*/







        // 설정 버튼 클릭시
        btn_setting = (Button)findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu p = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.setting, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.logout:
                                Toast.makeText(getApplicationContext(), "logout", Toast.LENGTH_SHORT);
                                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(logoutIntent);
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = auto.edit();
                                editor.clear();
                                editor.commit();
                                Toast.makeText(MainActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
                                finish();
                                return true;

                            case R.id.dropID:
                                Toast.makeText(getApplicationContext(), "dropID", Toast.LENGTH_SHORT);
                                Intent dropIDIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(dropIDIntent);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                p.show();
            }
        });

        // 임시 버튼 참고용
        /*
        Button btn_UserName = (Button)findViewById(R.id.btn_UserName);
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("userName");
        final String userID = intent.getStringExtra("userID");
        btn_UserName.setText(userName);

        btn_UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final String userID = intent.getStringExtra("userID");
                Intent intent = new Intent(MainActivity.this,MyInfoActivity.class);
                intent.putExtra("userID", "park");
                startActivity(intent);
            }
        });

        btn_UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
            }
        });
         */

        //내 정보에 들어갈 뷰페이저
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        pagerAdapter mycard_adapter = new pagerAdapter(getSupportFragmentManager());

        mycard_Fragment1 mycard_fragment1 = new mycard_Fragment1();
        mycard_adapter.addItem(mycard_fragment1);
        mycard_Fragment2 mycard_fragment2 = new mycard_Fragment2();
        mycard_adapter.addItem(mycard_fragment2);
        mycard_Fragment3 mycard_fragment3 = new mycard_Fragment3();
        mycard_adapter.addItem(mycard_fragment3);

        pager.setAdapter(mycard_adapter);

        //명함 목록을 위한 리스트뷰

        cardList = (ListView)findViewById(R.id.cardList);
        mArrayList = new ArrayList<>();

        searchAdapter = new searchAdapter();
        cardList.setAdapter(searchAdapter);


        GetData task = new GetData();
        //아이디값 받아오기
        String userID = loginid;

        task.execute("http://yujinpark10.dothome.co.kr/maincardlist.php", userID);//아이디값 받아온거  보내기

        // 리스트뷰 검색
        final EditText editSearch = (EditText)findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString() ;

                ((searchAdapter)cardList.getAdapter()).getFilter().filter(filterText) ;
            }
        });

        // 명함등록 버튼
        btn_cardEnroll = (Button)findViewById(R.id.btn_cardEnroll);
        btn_cardEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEnrollDialog();
            }
        });

        // 명함교환 버튼
        btn_cardChange = (Button)findViewById(R.id.btn_cardChange);
        btn_cardChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowChangeDialog();
            }
        });
    }

    // 내 정보 클릭시 정보 호출 참고용
    /*
    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target;
        @Override
        protected void onPreExecute(){
            target = "http://yujinpark10.dothome.co.kr/CardList.php"; // 서버에 존재하는 php파일 초기화
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");

                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(MainActivity.this, MyInfoActivity.class);
            intent.putExtra("userCardList", result );
            MainActivity.this.startActivity(intent);
        }
    }
     */

    // 뷰페이저 어댑터
    class pagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public pagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item){
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    // 상대 명함 리스트뷰
    private class GetData extends AsyncTask<String, Void, String>{
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
            String userID = params[1];
            String postParameters = "userID=" + userID;

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

                String name = item.getString(TAG_NAME);
                String company = item.getString(TAG_COMPANY);

                //HashMap<String,String> hashMap = new HashMap<>();

                //hashMap.put(TAG_NAME, name);
                //hashMap.put(TAG_COMPANY, company);

                searchAdapter.addItem(name, company);

                //mArrayList.add(hashMap);
            }

            searchAdapter.notifyDataSetChanged();

            /*
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, mArrayList, R.layout.card_list,
                    new String[]{TAG_NAME, TAG_COMPANY},
                    new int[]{R.id.cardListName, R.id.cardListCompany}
            );

            cardList.setAdapter(adapter);
            */
        } catch (JSONException e) {

        }

    }

    // 명함 등록 다이어로그
    private void ShowEnrollDialog(){
        LayoutInflater enrollDialog = LayoutInflater.from(this);
        final View enrollDialogLayout = enrollDialog.inflate(R.layout.enrolldialog, null);
        final Dialog myEnrollDialog = new Dialog(this);

        myEnrollDialog.setTitle("명함 등록");
        myEnrollDialog.setContentView(enrollDialogLayout);
        myEnrollDialog.show();

        Button btn_cancel = (Button)enrollDialogLayout.findViewById(R.id.btn_cancel);
        Button btn_myCardEnroll = (Button)enrollDialogLayout.findViewById(R.id.btn_myCardEnroll);
        Button btn_youCardEnroll = (Button)enrollDialogLayout.findViewById(R.id.btn_yourCardEnroll);

        btn_myCardEnroll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,CardEnrollActivity.class);
                startActivity(intent);
            }
        });

        btn_youCardEnroll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,CardEnrollActivity.class);
                startActivity(intent);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myEnrollDialog.cancel();
            }
        });
    }

    // 명함 교환 다이어로그
    private void ShowChangeDialog(){
        LayoutInflater changeDialog = LayoutInflater.from(this);
        final View changeDialogLayout = changeDialog.inflate(R.layout.changedialog, null);
        final Dialog myEnrollDialog = new Dialog(this);

        myEnrollDialog.setTitle("명함 교환");
        myEnrollDialog.setContentView(changeDialogLayout);
        myEnrollDialog.show();

        Button btn_cancel = (Button)changeDialogLayout.findViewById(R.id.btn_cancel);
        Button btn_sendNFC = (Button)changeDialogLayout.findViewById(R.id.btn_sendgetNFC);
        Button btn_QRCode = (Button)changeDialogLayout.findViewById(R.id.btn_QRCode);

        btn_sendNFC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, nfcChangePage.class);
                startActivity(intent);
            }
        });

        btn_QRCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myEnrollDialog.cancel();
            }
        });
    }

    //백버튼 두번 누를시종료
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            ActivityCompat.finishAffinity(this);
        }
    }
}
