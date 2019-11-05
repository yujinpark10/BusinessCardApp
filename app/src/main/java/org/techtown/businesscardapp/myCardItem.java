package org.techtown.businesscardapp;

public class myCardItem {
    private String nameStr ;
    private String companyStr ;

    public void setName(String name) {
        nameStr = name ;
    }
    public void setCompany(String company) {
        companyStr = company ;
    }

    public String getName() {
        return this.nameStr ;
    }
    public String getCompany() {
        return this.companyStr ;
    }
}