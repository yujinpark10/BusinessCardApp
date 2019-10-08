package org.techtown.businesscardapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class UserInfoAdapter extends BaseAdapter {
    private Context context;
    private List<User> userCardList;
    private static final String MSG = "adapter";

    public UserInfoAdapter(Context context, List<User> userCardList){
        this.context = context;
        this.userCardList = userCardList;

    }

    @Override
    public int getCount() {
        return userCardList.size();
    }

    @Override
    public Object getItem(int i) { // 특정한 명함정보를 반환
        return userCardList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.user, null);
        TextView cardNum = (TextView)v.findViewById(R.id.cardNum);
        TextView name = (TextView)v.findViewById(R.id.name);
        TextView company = (TextView)v.findViewById(R.id.company);
        TextView team = (TextView)v.findViewById(R.id.team);
        TextView position = (TextView)v.findViewById(R.id.position);
        TextView coNum = (TextView)v.findViewById(R.id.coNum);
        TextView num = (TextView)v.findViewById(R.id.num);
        TextView e_mail = (TextView)v.findViewById(R.id.e_mail);
        TextView faxNum = (TextView)v.findViewById(R.id.faxNum);
        TextView address = (TextView)v.findViewById(R.id.address);
        TextView userID = (TextView)v.findViewById(R.id.userID);
        String m = userCardList.get(i).getName();

        Log.d(m, "오케이1");
        cardNum.setText(userCardList.get(i).getCardNum());
        name.setText(userCardList.get(i).getName());
        company.setText(userCardList.get(i).getCompany());
        team.setText(userCardList.get(i).getTeam());
        position.setText(userCardList.get(i).getPosition());
        coNum.setText(userCardList.get(i).getCoNum());
        num.setText(userCardList.get(i).getNum());
        e_mail.setText(userCardList.get(i).getE_mail());
        faxNum.setText(userCardList.get(i).getFaxNum());
        address.setText(userCardList.get(i).getAddress());
        userID.setText(userCardList.get(i).getUserID());
        Log.d(MSG, "오케이2");

        v.setTag(userCardList.get(i).getUserID());
        return v;
    }
}
