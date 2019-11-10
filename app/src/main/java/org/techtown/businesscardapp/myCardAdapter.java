package org.techtown.businesscardapp;

        import android.content.Context;
        import android.content.Intent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

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
        TextView cardNum = (TextView) convertView.findViewById(R.id.cardNum);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.cardListName) ;
        TextView companyTextView = (TextView) convertView.findViewById(R.id.cardListCompany) ;

        // Data set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        cardListViewItem cardListViewItem = filteredItemList.get(position);

        // 아이템 내 각 위젯에 대한 반영
        //cardNum.setText(cardListViewItem.getCardNum()); // 여기 정수를 문자로 해야할듯
        nameTextView.setText(cardListViewItem.getName());
        companyTextView.setText(cardListViewItem.getCompany());

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
    public void addItem(int cardNum, String name, String company) {
        cardListViewItem item = new cardListViewItem();

        item.setCardNum(cardNum);
        item.setName(name);
        item.setCompany(company);

        myCardItem.add(item);
    }
}
