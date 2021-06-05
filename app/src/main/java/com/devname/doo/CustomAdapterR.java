package com.devname.doo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devname.doo.databinding.TaskItemBinding;

import java.util.ArrayList;
import java.util.Collection;

public class CustomAdapterR extends RecyclerView.Adapter<CustomAdapterR.ViewHolder> implements Filterable {

    // Getting Data List
    private final ArrayList<TaskItem> taskItemArrayList;
    private final ArrayList<TaskItem> completeList;

    public void updateList(){
        completeList.clear();
        completeList.addAll(taskItemArrayList);
    }
    // Filtering
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TaskItem> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(completeList);
            } else {
                for (TaskItem taskItem :
                        completeList) {
                    if (taskItem.getTask().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredList.add(taskItem);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            taskItemArrayList.clear();
            taskItemArrayList.addAll((Collection<? extends TaskItem>) results.values);
            notifyDataSetChanged();
        }
    };

    public CustomAdapterR(ArrayList<TaskItem> taskItemArrayList) {
        this.taskItemArrayList = taskItemArrayList;
        this.completeList = new ArrayList<>(taskItemArrayList);
    }


    // Creating Listener Interface
/*
    private onItemClickListener listener;

    public interface onItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }
*/


    // Adapter Overridden Methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(TaskItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterR.ViewHolder holder, int position) {
        TaskItem currentData = taskItemArrayList.get(position);
        holder.setDetails(currentData);
    }

    @Override
    public int getItemCount() {
        return taskItemArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskItemBinding itemBinding;

        public ViewHolder(TaskItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void setDetails(TaskItem taskItem){
            itemBinding.taskText.setText(taskItem.getTask());
            if (taskItem.getDate() == null){
                itemBinding.dateView.setVisibility(View.GONE);
            } else {
                itemBinding.dateView.setVisibility(View.VISIBLE);
                itemBinding.dateText.setText(taskItem.getDate());
            }
        }
    }
}
