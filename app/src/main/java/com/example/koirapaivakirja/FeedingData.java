package com.example.koirapaivakirja;

public class FeedingData {
    private String documentId;
    private String food_type;
    private String food_amount;
    private String current_time;
    private String current_date;

    public FeedingData() {

    }

    public String getDocumentId(){
        return documentId;
    }
    void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public FeedingData(String food_type, String food_amount, String current_date, String current_time) {
        this.food_type = food_type;
        this.food_amount = food_amount;
        this.current_date = current_date;
        this.current_time = current_time;
    }
    public String getFood_type() {
        return food_type;
    }
    public String getFood_amount() {
        return food_amount;
    }
    public String getCurrent_date() {
        return current_date;
    }
    public String getCurrent_time() {
        return current_time;
    }

}
