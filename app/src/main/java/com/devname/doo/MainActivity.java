package com.devname.doo;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devname.doo.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<TaskItem> taskItemArrayList = new ArrayList<>();
    private CustomAdapterR customAdapter;
    private ActivityMainBinding mainBinding;
    private ActivityResultLauncher<Intent> taskActivityResultLauncher;
    private ItemTouchHelper itemTouchHelper;

    //Swipe gesture creation
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback( ItemTouchHelper.UP |
            ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(taskItemArrayList, fromPosition, toPosition);
            customAdapter.notifyItemMoved(fromPosition, toPosition);
            customAdapter.updateList();
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TaskItem taskItem = taskItemArrayList.get(position);
            removeTask(position);
            showUndoSnackBar(position, taskItem);
        }

        // snackBar Undo Creation
        private void showUndoSnackBar(int position, TaskItem taskItem) {
            Snackbar snackbar = Snackbar.make(mainBinding.getRoot(), "Task Deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", v -> addTask(position, taskItem))
                    .setActionTextColor(getResources().getColor(R.color.secondaryColor));
            snackbar.setAnchorView(mainBinding.fabBtn);
            snackbar.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.secondaryColor))
                    .addSwipeLeftLabel("Delete")
                    .addSwipeLeftActionIcon(R.drawable.ic_sharp_delete)
                    .setSwipeLeftActionIconTint(Color.WHITE)
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        setSupportActionBar(mainBinding.toolbar);
//      Dummy Data Initialization
        for (int i = 0; i < 30; i++) {
            taskItemArrayList.add(new TaskItem("This is Example Task" + i, i + "/05/2021"));
        }

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
                TaskItem taskItem = result.getData().getParcelableExtra("KEY");
                addTask(0, taskItem);
                showSnackBar(getString(R.string.task_added_successfully));
            } else if (result.getResultCode() == RESULT_CANCELED)
                showSnackBar(getString(R.string.task_canceled));
        });
    }

    // On Options creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.search_));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                mainBinding.fabBtn.hide();
                itemTouchHelper.attachToRecyclerView(null);
            } else {
                mainBinding.recyclerView.scrollToPosition(0);
                mainBinding.fabBtn.show();
                itemTouchHelper.attachToRecyclerView(mainBinding.recyclerView);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void buildRecyclerView() {
        customAdapter = new CustomAdapterR(taskItemArrayList);

//        customAdapter.setOnItemClickListener(this::removeTask);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mainBinding.recyclerView.setLayoutManager(layoutManager);
        mainBinding.recyclerView.setAdapter(customAdapter);

        // Swipe Delete
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mainBinding.recyclerView);
    }

    private void removeTask(int position) {
        taskItemArrayList.remove(position);
        customAdapter.notifyItemRemoved(position);
        customAdapter.updateList();
    }

    private void addTask(int position, TaskItem taskItem) {
        taskItemArrayList.add(position, taskItem);
        customAdapter.notifyItemInserted(position);
        if (position == 0 || position == customAdapter.getItemCount()-1)
            mainBinding.recyclerView.scrollToPosition(position);
        customAdapter.updateList();
    }

    private void showSnackBar(String string) {
        Snackbar snackbar = Snackbar.make(mainBinding.getRoot(), string, Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(mainBinding.fabBtn);
        snackbar.show();
    }

}