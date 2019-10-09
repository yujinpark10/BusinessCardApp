package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyInfoActivity extends AppCompatActivity {
    private ListView listView;
    private  UserInfoAdapter adapter;
    private List<User> userCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        Intent intent = getIntent();
        // TextView userListTextView = (TextView)findViewById(R.id.userListTextView);
        // userListTextView.setText(intent.getStringExtra("userCardList"));

        listView = (ListView)findViewById(R.id.listView);
        userCardList = new ArrayList<User>();

        adapter = new UserInfoAdapter(getApplicationContext(), userCardList);
        listView.setAdapter(adapter);

       try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userCardList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0; // 해당 명함갯수를 관리
            String name, company, team, position, num, e_mail, address, userID;
            int cardNum, coNum, faxNum;
            while(count < jsonArray.length()){
                JSONObject object = jsonArray.getJSONObject(count);
                cardNum = object.getInt("cardNum");
                name = object.getString("name");
                company = object.getString("company");
                team = object.getString("team");
                position = object.getString("position");
                coNum = object.getInt("coNum");
                num = object.getString("num");
                e_mail = object.getString("e_mail");
                faxNum = object.getInt("faxNum");
                address = object.getString("address");
                userID = object.getString("userID");

                User user = new User(cardNum, name, company, team, position, coNum, num, e_mail,faxNum,address,userID);
                userCardList.add(user);
                count++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
