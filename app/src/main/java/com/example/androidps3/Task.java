package com.example.androidps3;

import java.util.Date;
import java.util.UUID;

public class Task {
    private UUID id;
    private String name;
    private Date date;
    private boolean done;
    private Category category;

    public Task(){
        id = UUID.randomUUID();
        date = new Date();
        category = Category.HOME;
    }

    public Date getDate(){
        return this.date;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDone(boolean done){
        this.done = done;
    }

    public boolean isDone(){
        if(this.done){
            return true;
        }
        return false;
    }
    public UUID getId(){
        return id;
    }
    public String getName() {
        return name;
    }

    public void setCategory(Category category){
        this.category = category;
    }
    public Category getCategory(){
        return this.category;
    }

    public void setDate(Date _date){
        this.date = _date;
    }


}
