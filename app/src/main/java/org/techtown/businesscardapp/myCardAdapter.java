package org.techtown.businesscardapp;

import android.content.Context;
        import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class myCardAdapter extends BaseAdapter{
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<cardListViewItem> myCardItem = new ArrayList<cardListViewItem>();
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList
    private ArrayList<cardListViewItem> filteredItemList = myCardItem;

    // listViewAdapter의 생성자
    public myCardAdapter(){

    }

    // Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount(){
        return filteredItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // Layout을 inflate하여 convertView 참조 획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.my_card_list, parent, false);
        }

        // 화면에 표시될 View로부터 위젯에 대한 참조 획득
        ImageView cardview = (ImageView) convertView.findViewById(R.id.cardListImage) ;

        // Data set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        cardListViewItem cardListViewItem = filteredItemList.get(position);

        String cardimage = cardListViewItem.getCardImage();

        if(cardimage.equals("null")){
//            Bitmap processedBitmap = ProcessingBitmap();
//            if(processedBitmap != null){
//                cardview.setImageBitmap(processedBitmap);
//            }
        }else{// 여기에 가져온 명함 이미지를 가져오면 됩니다.

            try {
                String bitmap1 = URLDecoder.decode(cardimage, "utf-8");
                byte[] decodedByteArray = Base64.decode(bitmap1, Base64.NO_WRAP);
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedByteArray,0, decodedByteArray.length);
                cardview.setImageBitmap(bitmap2);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(int cardNum, String name, String company, String team, String position, String coNum, String num, String e_mail, String faxNum, String address, String cardimage) {
        cardListViewItem item = new cardListViewItem();

        item.setCardNum(cardNum);
        item.setName(name);
        item.setCompany(company);
        item.setTeam(team);
        item.setPosition(position);
        item.setCoNum(coNum);
        item.setNum(num);
        item.setFaxNum(faxNum);
        item.setEmail(e_mail);
        item.setAddress(address);
        item.setCardImage(cardimage);

        myCardItem.add(item);
    }

    // 명함을 그리는 비트맵 함수
    private Bitmap ProcessingBitmap(){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        bm1 = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.namecard_basic4);
        Bitmap.Config config = bm1.getConfig();
        if(config == null){
            config = Bitmap.Config.ARGB_8888;
        }


        newBitmap = Bitmap.createBitmap(bm1.getWidth(),bm1.getHeight(),config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(bm1,0,0,null);
        Paint paintText1 = setTextsize(140);
        Paint paintText2 = setTextsize(80);
        Paint paintText3 = setTextsize(120);
        Paint paintText4 = setTextsize(60);
        Paint paintText5 = setTextsize(80);
        Paint paintText6 = setTextsize(80);
        Paint paintText7 = setTextsize(60);
        Rect rectText = new Rect();
        paintText1.getTextBounds("1",0,"1".length(),rectText);
        newCanvas.drawText("1",50,rectText.height()+50,paintText1);
        paintText2.getTextBounds("2222222222222222222222",0,"2222222222222222222222".length(),rectText);
        newCanvas.drawText("2222222222222222222222",50,rectText.height()+50,paintText2);
//        paintText1.getTextBounds(use_name,0,use_name.length(),rectText);
//        newCanvas.drawText(use_name,50,rectText.height()+50,paintText1);
//        paintText2.getTextBounds(use_position,0,use_position.length(),rectText);
//        newCanvas.drawText(use_position,70,rectText.height()+450,paintText2);
//        paintText3.getTextBounds(use_company,0,use_company.length(),rectText);
//        newCanvas.drawText(use_company,50,rectText.height()+1400,paintText3);
//        paintText4.getTextBounds(use_address,0,use_address.length(),rectText);
//        newCanvas.drawText(use_address,50,rectText.height()+1730,paintText4);
//        paintText5.getTextBounds(use_pnumber,0,use_pnumber.length(),rectText);
//        newCanvas.drawText(use_pnumber,2080,rectText.height()+410,paintText5);
//        paintText6.getTextBounds(use_conumber,0,use_conumber.length(),rectText);
//        newCanvas.drawText(use_conumber,2080,rectText.height()+640,paintText6);
//        paintText7.getTextBounds(use_email,0,use_email.length(),rectText);
//        newCanvas.drawText(use_email,2080,rectText.height()+850,paintText7);

        return newBitmap;
    }

    private Paint setTextsize(int textsize){
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,textsize,Resources.getSystem().getDisplayMetrics()));
        paintText.setStyle(Paint.Style.FILL);

        return paintText;
    }
}
