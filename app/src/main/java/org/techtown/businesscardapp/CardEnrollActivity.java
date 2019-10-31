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

        //EditText 추가
        EditText edt_inputName = (EditText)findViewById(R.id.edt_inputName);
        EditText edt_inputCompany = (EditText)findViewById(R.id.edt_inputCompany);
        EditText edt_inputTeam = (EditText)findViewById(R.id.edt_inputTeam);
        EditText edt_inputPosition = (EditText)findViewById(R.id.edt_inputPosition);
        EditText edt_inputCoNumber = (EditText)findViewById(R.id.edt_inputCoNumber);
        EditText edt_inputPhone = (EditText)findViewById(R.id.edt_inputPhone);
        EditText edt_inputMail = (EditText)findViewById(R.id.edt_inputMail);
        EditText edt_inputFax = (EditText)findViewById(R.id.edt_inputFax);
        EditText edt_inputAddress = (EditText)findViewById(R.id.edt_inputAddress);

        //저장 버튼 클릭시 // 아직 데이터베이스에 저장 기능 구현 안됌
        Button btn_enrollSave = (Button)findViewById(R.id.btn_enrollSave);
        btn_enrollSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
