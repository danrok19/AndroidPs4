package com.example.androidps3;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private boolean subtitleVisible;

    public static final String KEY_EXTRA_TASK_ID = "KEY_EXTRA_TASK_ID";
    public static final String KEY_EXTRA_VISIBLE = "KEY_EXTRA_VISIBLE";


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_task_menu,menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(subtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
            updateSubtitle();
        }
        else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }


    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    private void updateView(){
        TaskStorage taskStorage = TaskStorage.getInstance();
        List<Task> tasks = taskStorage.getTasks();
        if(adapter == null) {
            adapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(adapter);
        }
        else{
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_task:
                Task task = new Task();
                TaskStorage.getInstance().addTask(task);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(TaskListFragment.KEY_EXTRA_TASK_ID,task.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                getActivity().invalidateOptionsMenu(); // rekonstrukcja przyciskow akcji menu
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView dateTextView;
        private ImageView iconImageView;
        private CheckBox dummyCheckBox;
        Task task;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);

            nameTextView = itemView.findViewById((R.id.task_item_name));
            dateTextView = itemView.findViewById((R.id.task_item_date));
            iconImageView = itemView.findViewById((R.id.task_item_image));

            dummyCheckBox = itemView.findViewById((R.id.dummyCheckBox));

        }

        public void bind(Task task){
            this.task = task;
            nameTextView.setText(task.getName());
            dateTextView.setText(task.getDate().toString());
            if(task.getCategory().equals(Category.HOME)){
                iconImageView.setImageResource(R.drawable.ic_house);
            }
            else{
                iconImageView.setImageResource(R.drawable.ic_studies);
            }
            dummyCheckBox.setChecked(task.isDone());
            dummyCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));
            dummyCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> dummyCheckBox.setPaintFlags(dummyCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG));
            if(task.isDone()){
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        @Override
        public void onClick(View view){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(KEY_EXTRA_TASK_ID, task.getId());
            startActivity(intent);
        }

        public CheckBox getCheckBox() {
            return this.dummyCheckBox;
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder>{
        private List<Task> tasks;
        public TaskAdapter(List<Task> tasks){
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position){
            Task task =tasks.get(position);
            holder.bind(task);
            CheckBox checkBox = holder.getCheckBox();
            checkBox.setChecked(tasks.get(position).isDone());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> tasks.get(holder.getBindingAdapterPosition()).setDone(isChecked));

        }
        @Override
        public int getItemCount(){
            return tasks.size();
        }

    }
    private Boolean mSubtitleVisible;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(KEY_EXTRA_VISIBLE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_EXTRA_VISIBLE, subtitleVisible);
    }



    //funckja zliczajaca ile zadan jest niewykonana
    public void updateSubtitle(){
        TaskStorage taskStorage = TaskStorage.getInstance();
        List<Task> tasks = taskStorage.getTasks();
        int todoTasksCount = 0;
        for(Task task : tasks){
            if(!task.isDone()){
                todoTasksCount++;
            }
        }
        String subtitle = getString(R.string.subtitle_format,todoTasksCount);
        if(!subtitleVisible){
            subtitle = null;
        }

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
        updateView();//poprawne zaktualizowanie wyświetlanej w podtytule liczby zadań po dodaniu nowego zadania
    }
}
