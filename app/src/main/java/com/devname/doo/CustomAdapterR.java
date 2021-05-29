package com.devname.doo;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devname.doo.databinding.TaskItemBinding;

import java.util.ArrayList;

public class CustomAdapterR extends RecyclerView.Adapter<CustomAdapterR.ViewHolder> {

    // Getting Data List
    private final ArrayList<TaskItem> taskItemArrayList;
    public CustomAdapterR(ArrayList<TaskItem> taskItemArrayList) {
        this.taskItemArrayList = taskItemArrayList;
    }

    // Creating Listener Interface
    private onItemClickListener listener;

    public interface onItemClickListener {
        void onDeleteClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }


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

        if (position + 1 == getItemCount()) {
            // It is the last item of the list
            // Set bottom margin to 72dp
            setBottomMargin(holder.itemView, (int) (72 * Resources.getSystem().getDisplayMetrics().density));
        } else {
            // Reset bottom margin back to zero
            setBottomMargin(holder.itemView, 4);
        }

    }

    private static void setBottomMargin(View view, int bottomMargin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin);
            view.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return taskItemArrayList.size();
    }


    // ViewHolder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskItemBinding itemBinding;

        public ViewHolder(TaskItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void setDetails(TaskItem taskItem){
            itemBinding.taskView.setText(taskItem.getTask());
            itemBinding.dateView.setText(taskItem.getDate());
            // Triggers the above method of customClickListener
            itemBinding.deleteTaskItem.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(itemView, position);
                    }
                }
            });
        }
    }
}
