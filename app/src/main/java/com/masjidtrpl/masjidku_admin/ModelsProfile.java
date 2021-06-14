package com.masjidtrpl.masjidku_admin;

public class ModelsProfile {
    private String name;
    private String address;
    private String contact;
    private String desc;

    public ModelsProfile(String name, String address, String contact, String desc) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.desc = desc;
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
}
