package org.techtown.businesscardapp;

public class cardListViewItem {
    private int cardNumStr;
    private String nameStr;
    private String companyStr;
    private String addressStr;

    public void setCardNum(int cardNumStr) {
        this.cardNumStr = cardNumStr;
    }
    public void setName(String name) {
        nameStr = name;   }
    public void setCompany(String company) {
        companyStr = company;
    }
    public void setAddress(String addressStr) {
        this.addressStr = addressStr;
    }

    public int getCardNum() {
        return cardNumStr;
    }
    public String getName() {
        return this.nameStr;
    }
    public String getCompany() {
        return this.companyStr;
    }
    public String getAddress() {
        return this.addressStr;
    }
}