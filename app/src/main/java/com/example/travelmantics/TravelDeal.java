package com.example.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDeal implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;

    public TravelDeal(){
    }

    public TravelDeal(String title, String description, String price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    protected TravelDeal(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        price = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<TravelDeal> CREATOR = new Creator<TravelDeal>() {
        @Override
        public TravelDeal createFromParcel(Parcel in) {
            return new TravelDeal(in);
        }

        @Override
        public TravelDeal[] newArray(int size) {
            return new TravelDeal[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(imageUrl);
    }
}
