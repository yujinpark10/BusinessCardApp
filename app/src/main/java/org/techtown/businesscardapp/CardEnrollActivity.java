package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CardEnrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_enroll);
        getSupportActionBar().hide();
        this.SetListener();//버튼클릭시 이벤트 모음

        //EditText 추가
        final EditText et_name = (EditText)findViewById(R.id.et_name);
        EditText edt_inputCompany = (EditText)findViewById(R.id.et_company);
        EditText edt_inputTeam = (EditText)findViewById(R.id.et_team);
        EditText edt_inputPosition = (EditText)findViewById(R.id.et_position);
        EditText edt_inputCoNumber = (EditText)findViewById(R.id.et_conumber);
        EditText edt_inputPhone = (EditText)findViewById(R.id.et_pnumber);
        EditText edt_inputMail = (EditText)findViewById(R.id.et_email);
        EditText edt_inputFax = (EditText)findViewById(R.id.et_fnumber);
        EditText edt_inputAddress = (EditText)findViewById(R.id.et_address);



        //저장 버튼 클릭시 // 아직 데이터베이스에 저장 기능 구현 안됌
        Button btn_enrollSave = (Button)findViewById(R.id.btn_enrollSave)
        btn_enrollSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = et_name.getText().toString();

                Intent intent = new Intent(CardEnrollActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



        //취소 버튼 클릭시 // 취소 확인하기 기능 추가하면 좋을듯
        Button btn_enrollCancel = (Button)findViewById(R.id.btn_enrollCancel);
        btn_enrollCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardEnrollActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SetListener() {

        btn_enrollSave






    }
}
