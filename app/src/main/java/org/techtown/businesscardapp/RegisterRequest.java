package org.techtown.businesscardapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    // 서버 URL 설정
    final static private  String URL = "http://yujinpark10.dothome.co.kr/Register.php";
    private Map<String, String> map;


    public RegisterRequest(String userID, String userPassword, String userName, String userBirth, String userNum, String userEmail, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("userPassword",userPassword);
        map.put("userName",userName);
        map.put("userBirth",userBirth);
        map.put("userNum",userNum);
        map.put("userEmail",userEmail);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
