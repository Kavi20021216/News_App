package com.example.news_app;

public class CardItem {
    private String headline;     // Matches Firebase "headline"
    private String image;        // Matches Firebase "image"
    private String description;
    private String date;
    private String category;

    public CardItem() {}

    public CardItem(String headline, String image, String description, String date, String category) {
        this.headline = headline;
        this.image = image;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
