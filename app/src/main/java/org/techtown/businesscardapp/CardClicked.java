package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CardClicked extends AppCompatActivity {

    private TextView inp_name, inp_company, inp_team, inp_position, inp_conumber, inp_pnumber, inp_email, inp_fnumber, inp_address;
    private Button btn_modify, btn_delete, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_clicked);
        getSupportActionBar().hide();

        inp_name = (TextView)findViewById(R.id.inp_name);
        inp_company = (TextView)findViewById(R.id.inp_company);
        inp_team = (TextView)findViewById(R.id.inp_team);
        inp_position = (TextView)findViewById(R.id.inp_position);
        inp_conumber = (TextView)findViewById(R.id.inp_conumber);
        inp_pnumber = (TextView)findViewById(R.id.inp_pnumber);
        inp_email = (TextView)findViewById(R.id.inp_email);
        inp_fnumber = (TextView)findViewById(R.id.inp_fnumber);
        inp_address = (TextView)findViewById(R.id.inp_address);

        final int mine1 = getIntent().getIntExtra("mine1",0);
        final String userID = getIntent().getStringExtra("userID");
    }
}
