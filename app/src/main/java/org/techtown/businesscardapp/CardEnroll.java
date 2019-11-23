package org.techtown.businesscardapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class CardEnroll extends StringRequest {
    // 서버 URL 설정
    final static private String URL = "http://yujinpark10.dothome.co.kr/cardEnroll.php";
    private Map<String, String> map;

    public CardEnroll(String name, String company, String team, String position, String coNum, String num, String e_mail, String faxNum, String address, String userID, String mine, String cardImage, String memo, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("name", name);
        map.put("company", company);
        map.put("team", team);
        map.put("position", position);
        map.put("coNum", coNum);
        map.put("num", num);
        map.put("e_mail", e_mail);
        map.put("faxNum", faxNum);
        map.put("address", address);
        map.put("userID",userID);
        map.put("mine", mine);
        map.put("cardImage", cardImage);
        map.put("memo", memo);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
