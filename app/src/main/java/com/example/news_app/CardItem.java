package com.example.news_app;

public class CardItem {
    private String title;
    private String base64Image;
    private String description;
    private String date;
    private String category;

    // Required empty constructor for Firebase deserialization
    public CardItem() {}

    // Constructor with title and image (used for quick display)
    public CardItem(String title, String base64Image) {
        this.title = title;
        this.base64Image = base64Image;
    }

    // Full constructor
    public CardItem(String title, String base64Image, String description, String date, String category) {
        this.title = title;
        this.base64Image = base64Image;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getBase64Image() {
        return base64Image;
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

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
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
