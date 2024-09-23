package com.example.mynotes;

public class Note {
    private int id;
    private String title;
    private String description;
    private int priority;
    private String photo;

    public Note(int id, String title, String description, int priority, String photo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.photo = photo;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
