package org.techtown.businesscardapp;

public class cardListViewItem {
    private int cardNumStr;
    private String nameStr;
    private String companyStr;
    private String teamStr;
    private String positionStr;
    private String coNumStr;
    private String numStr;
    private String e_mailStr;
    private String faxNumStr;
    private String addressStr;
    private String cardImageStr;

    public void setCardNum(int cardNum) {
        cardNumStr = cardNum;
    }
    public void setName(String name) {
        nameStr = name;
    }
    public void setCompany(String company) {
        companyStr = company;
    }
    public void setTeam(String team) {
        teamStr = team;
    }
    public void setPosition(String position) {
        positionStr = position;
    }
    public void setCoNum(String coNum) {
        coNumStr = coNum;
    }
    public void setNum(String num) {
        numStr = num;
    }
    public void setEmail(String e_mail) {
        e_mailStr = e_mail;
    }
    public void setFaxNum(String faxNum) {
        faxNumStr = faxNum;
    }
    public void setAddress(String address) {
        addressStr = address;
    }
    public void setCardImage(String cardimage) {
        cardImageStr = cardimage;
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
    public String getTeam() {
        return this.teamStr;
    }
    public String getPosition() {
        return this.positionStr;
    }
    public String getCoNum() {
        return this.coNumStr;
    }
    public String getNum() {
        return this.numStr;
    }
    public String getEmail() {
        return this.e_mailStr;
    }
    public String getFaxNum() {
        return this.faxNumStr;
    }
    public String getAddress() {
        return this.addressStr;
    }
    public String getCardImage() {
        return this.cardImageStr;
    }
}