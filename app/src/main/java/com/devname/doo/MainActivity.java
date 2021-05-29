package com.devname.doo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devname.doo.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {

    private CustomAdapterR customAdapter;
    private ActivityMainBinding mainBinding;
    private ActivityResultLauncher<Intent> taskActivityResultLauncher;
    private final ArrayList<TaskItem> taskItemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        TaskItem task = new TaskItem("This is Example Task","date will be whatever I want");
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);
        taskItemArrayList.add(task);

        mainBinding.fabBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            taskActivityResultLauncher.launch(intent);
            CustomIntent.customType(this, "bottom-to-up");
        });

        buildRecyclerView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        taskActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                addTask(result.getData());
                showSnackBar("Task Added Successfully");
            } else if (result.getResultCode() == RESULT_CANCELED) {
                mainBinding.recyclerView.scrollToPosition(0);
                showSnackBar("Task Canceled");
            }
        });
    }

    private void buildRecyclerView() {
        customAdapter = new CustomAdapterR(taskItemArrayList);

        customAdapter.setOnItemClickListener((view, position) -> removeTask(position));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mainBinding.recyclerView.setLayoutManager(layoutManager);
        mainBinding.recyclerView.setAdapter(customAdapter);
    }

    private void removeTask(int position) {
        taskItemArrayList.remove(position);
        customAdapter.notifyItemRemoved(position);
    }

    private void addTask(Intent data){
        TaskItem taskItem = data.getParcelableExtra("KEY");
        taskItemArrayList.add(0,taskItem);
        customAdapter.notifyItemInserted(0);
        mainBinding.recyclerView.scrollToPosition(0);
    }

    private void showSnackBar(String string){
        Snackbar snackbar = Snackbar.make(mainBinding.getRoot(), string, Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(mainBinding.fabBtn);
        snackbar.show();
    }
}