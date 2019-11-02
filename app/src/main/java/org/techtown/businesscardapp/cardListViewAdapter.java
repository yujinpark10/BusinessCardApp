package org.techtown.businesscardapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class cardListViewAdapter extends BaseAdapter implements Filterable {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<cardListViewItem> cardListViewItemList = new ArrayList<cardListViewItem>();
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList
    private ArrayList<cardListViewItem> filteredItemList = cardListViewItemList;

    Filter listFilter;

    // listViewAdapter의 생성자
    public cardListViewAdapter(){

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
            convertView = inflater.inflate(R.layout.card_list, parent, false);
        }

        // 화면에 표시될 View로부터 위젯에 대한 참조 획득
        TextView nameTextView = (TextView) convertView.findViewById(R.id.cardListName) ;
        TextView companyTextView = (TextView) convertView.findViewById(R.id.cardListCompany) ;

        // Data set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        cardListViewItem cardlistViewItem = filteredItemList.get(position);

        // 아이템 내 각 위젯에 대한 반영
        nameTextView.setText(cardlistViewItem.getNmae());
        companyTextView.setText(cardlistViewItem.getCompany());

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
    public void addItem(String name, String company) {
        cardListViewItem item = new cardListViewItem();

        item.setName(name);
        item.setCompany(company);

        cardListViewItemList.add(item);
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }
        return listFilter ;
    }

    // 검색 부분
    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = cardListViewItemList;
                results.count = cardListViewItemList.size() ;
            } else {
                ArrayList<cardListViewItem> itemList = new ArrayList<cardListViewItem>() ;

                // 하나의 text 뷰에 대해서 할꺼면 조건을 하나만 걸기
                for (cardListViewItem item : cardListViewItemList) {
                    if (item.getNmae().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getCompany().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<cardListViewItem>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
