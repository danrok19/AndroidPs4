package com.example.androidps3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskStorage {
    private static TaskStorage instance;
    private static final TaskStorage taskStorage = new TaskStorage();
    private final List<Task> tasks;


    public static TaskStorage getInstance(){
        if(instance == null) {
            instance = new TaskStorage();
        }
        return instance;

    }

    public void addTask(Task task){
        tasks.add(task);
    }

    private TaskStorage(){
        tasks = new ArrayList<>();
        for( int i = 0; i <= 100; i++){
            Task task = new Task();
            task.setName("Pilne zadanie numer " + i );
            task.setDone(i % 3 == 0);
            if(i % 3 == 0){
                task.setCategory(Category.STUDIES);
            }
            else{
                task.setCategory(Category.HOME);
            }
            tasks.add(task);
        }
    }
    public List<Task> getTasks() {
        return tasks;
    }
    public Task getTask(UUID taskId) {
        for(Task task : tasks) {
            if(taskId.equals(task.getId())){
                return task;
            }
        }
        return null;
    }
}
