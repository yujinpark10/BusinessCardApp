package org.techtown.businesscardapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class makeKing extends StringRequest {

    final static private String URL = "http://yujinpark10.dothome.co.kr/makeKing.php";
    private Map<String, String> map;

    public makeKing(String cardNum, String userID, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("cardNum", cardNum);
        map.put("userID",userID);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
