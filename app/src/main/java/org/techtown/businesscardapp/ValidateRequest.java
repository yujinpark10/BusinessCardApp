package org.techtown.businesscardapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {
    // 서버 URL 설정
    final static private  String URL = "http://yujinpark10.dothome.co.kr/UserValidate.php";
    private Map<String, String> map;

    public ValidateRequest(String userID, Response.Listener<String> listener){
        // userID을 URL로 보냄으로써 회원가입여부를 체크 해주는 부분
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("userID",userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}