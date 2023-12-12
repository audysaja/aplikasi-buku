package com.example.inventorybuku;

public class ModelCategory {
    //make sure to use same spellings for model variables as in firebase

    String id, category, timestamp, uid;


    //constructor empty required for firebase

    public ModelCategory(String id) {

    }

    //parmetrized constructor


    public ModelCategory(String id, String category, String uid, String timestamp) {
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }
    /*--------Getter/Setters------------*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}






