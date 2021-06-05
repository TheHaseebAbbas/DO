package com.devname.doo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devname.doo.databinding.FragmentMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainFragment extends Fragment {

    private final ArrayList<TaskItem> taskItemArrayList = new ArrayList<>();
    private FragmentMainBinding fragmentMainBinding;
    private CustomAdapterR customAdapter;
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
            return true;
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
            Snackbar snackbar = Snackbar.make(fragmentMainBinding.getRoot(), "Task Deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", v -> addTask(position, taskItem))
                    .setActionTextColor(getResources().getColor(R.color.secondaryColor));
            snackbar.setAnchorView(fragmentMainBinding.fabBtn);
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

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMainBinding = FragmentMainBinding.inflate(getLayoutInflater(), container, false);

        buildRecyclerView();

//      Dummy Data Initialization
        for (int i = 0; i < 30; i++) {
            taskItemArrayList.add(new TaskItem("This is Example Task" + i, i + "/05/2021"));
        }

        fragmentMainBinding.fabBtn.setOnClickListener(v -> showBottomSheetDialog());

        return fragmentMainBinding.getRoot();
    }

    private void buildRecyclerView() {
        customAdapter = new CustomAdapterR(taskItemArrayList);

//        customAdapter.setOnItemClickListener(this::removeTask);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentMainBinding.recyclerView.setLayoutManager(layoutManager);
        fragmentMainBinding.recyclerView.setAdapter(customAdapter);

        // Swipe Delete
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(fragmentMainBinding.recyclerView);
    }

    private void showBottomSheetDialog() {
        TaskDetailBSDialog detailBSDialog = new TaskDetailBSDialog(taskItem -> addTask(0, taskItem));
        detailBSDialog.show(getChildFragmentManager(), null);
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
            fragmentMainBinding.recyclerView.scrollToPosition(position);
        customAdapter.updateList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_btn){
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
                    return true;
                }
            });
            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus){
                    fragmentMainBinding.fabBtn.hide();
                    itemTouchHelper.attachToRecyclerView(null);
                } else {
                    fragmentMainBinding.recyclerView.scrollToPosition(0);
                    fragmentMainBinding.fabBtn.show();
                    itemTouchHelper.attachToRecyclerView(fragmentMainBinding.recyclerView);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}