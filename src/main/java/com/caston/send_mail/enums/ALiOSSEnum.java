package com.caston.send_mail.enums;

public enum ALiOSSEnum {
    ENDPOINT(),ACCESSKEYID(),ACCESSKEYSECRET();
    private String aliField;
    public String getAliField(){
        return aliField;
    }
    public void setAliField(String aliField){
        this.aliField = aliField;
    }
}
