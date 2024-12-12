package org.example;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private String description;
    private double price;
    private String category;
    private LocalDateTime dateAndTime;
    private String place;
    private String imageUrl;

    public Event(int id, String title, String description, double price, String category, LocalDateTime dateAndTime, String place, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dateAndTime = dateAndTime;
        this.place = place;
        this.imageUrl = imageUrl;
    }

    public Event(String title, String description, double price, String place, LocalDateTime dateAndTime, String category, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.place = place;
        this.dateAndTime = dateAndTime;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public Event() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", dateAndTime=" + dateAndTime +
                ", imageUrl='" + imageUrl + '\'' +
                ", place='" + place + '\'' +
                '}';
    }
}
