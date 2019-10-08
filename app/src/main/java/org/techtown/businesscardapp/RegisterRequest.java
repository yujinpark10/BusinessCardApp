package org.techtown.businesscardapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    // URL에 POST방식으로 파라미터들을 전송하는 역할을 수행
    //회원가입정보를 PHP서버에 보내서 데이터베이스에 저장

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
        //map.put("cardNum",cardNum);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
