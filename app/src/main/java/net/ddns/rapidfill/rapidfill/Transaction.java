package net.ddns.rapidfill.rapidfill;

/**
 * Created by Laurentiu on 3/27/2018.
 */

public class Transaction {
    private String UID;
    private String price;
    private String date;
    private String status;

    public Transaction() {
        UID = "Error";
        price = "Error";
        date = "Error";
        status = "Error";
    }

    public String getPrice() {
        return price;
    }
    public String getUID() {return UID;}
    public void setPrice(String price) {
        this.price = price;
    }
    public void setUID(String UID){ this.UID = UID;}
    public String getDate(){
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
}
