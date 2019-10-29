package org.techtown.businesscardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private TextView tv_id, tv_pass;
    private Button btn_userName;
    ListView cardList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //버튼 로그아웃 클릭시 실행
        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(MainActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

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


//        tv_id = (TextView)findViewById(R.id.tv_id);
//        tv_pass = (TextView)findViewById(R.id.tv_pass);
//
//        Intent intent = getIntent();
//        String userID = intent.getStringExtra("userID");
//        String userPassword = intent.getStringExtra("userPassword");
//
//        tv_id.setText(userID);
//        tv_pass.setText(userPassword);

        btn_UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
            }
        });

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
        cardListViewAdapter cardlistAdapter;

        cardlistAdapter = new cardListViewAdapter();

        cardList = (ListView)findViewById(R.id.cardList);
        cardList.setAdapter(cardlistAdapter);

        // 임시 아이템 추가
        cardlistAdapter.addItem("구교동", "1번");
        cardlistAdapter.addItem("박유진", "2번");
        cardlistAdapter.addItem("빈준호", "3번");
        cardlistAdapter.addItem("임창섭", "4번");
        cardlistAdapter.addItem("이름", "5번");
        cardlistAdapter.addItem("이름", "6번");
        cardlistAdapter.addItem("이름", "7번");
        cardlistAdapter.addItem("이름", "8번");
        cardlistAdapter.addItem("이름", "9번");
        cardlistAdapter.addItem("이름", "10번");

        // 커스텀 리스트뷰 검색
        EditText editSearch = (EditText)findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                ((cardListViewAdapter)cardList.getAdapter()).getFilter().filter(filterText);
            }
        });
    }

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
