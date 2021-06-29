package com.masjidtrpl.masjidku_admin;

public class ModelsProfile {
    private String key;
    private String name;
    private String address;
    private String contact;
    private String desc;
    private String imgProfil;
    private String imgUrl1;
    private String imgUrl2;
    private String imgUrl3;
    private String imgUrl4;
    private String imgUrl5;

    public ModelsProfile(String name, String address, String contact, String desc, String imgProfil, String imgUrl1, String imgUrl2, String imgUrl3, String imgUrl4, String imgUrl5) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.desc = desc;
        this.imgProfil = imgProfil;
        this.imgUrl1 = imgUrl1;
        this.imgUrl2 = imgUrl2;
        this.imgUrl3 = imgUrl3;
        this.imgUrl4 = imgUrl4;
        this.imgUrl5 = imgUrl5;
    }

    public ModelsProfile() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgProfil() {
        return imgProfil;
    }

    public void setImgProfil(String imgProfil) {
        this.imgProfil = imgProfil;
    }

    public String getImgUrl1() {
        return imgUrl1;
    }

    public void setImgUrl1(String imgUrl1) {
        this.imgUrl1 = imgUrl1;
    }

    public String getImgUrl2() {
        return imgUrl2;
    }

    public void setImgUrl2(String imgUrl2) {
        this.imgUrl2 = imgUrl2;
    }

    public String getImgUrl3() {
        return imgUrl3;
    }

    public void setImgUrl3(String imgUrl3) {
        this.imgUrl3 = imgUrl3;
    }

    public String getImgUrl4() {
        return imgUrl4;
    }

    public void setImgUrl4(String imgUrl4) {
        this.imgUrl4 = imgUrl4;
    }

    public String getImgUrl5() {
        return imgUrl5;
    }

    public void setImgUrl5(String imgUrl5) {
        this.imgUrl5 = imgUrl5;
    }
}
