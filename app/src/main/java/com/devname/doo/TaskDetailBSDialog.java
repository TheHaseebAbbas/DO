package com.devname.doo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.devname.doo.databinding.BottomSheetFragmentLayoutBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class TaskDetailBSDialog extends BottomSheetDialogFragment {

    private BottomSheetFragmentLayoutBinding taskDetailBinding;
    private TaskDetailBSListener listener;
    private DatePickerFragment datePickerDialog;

    public TaskDetailBSDialog(TaskDetailBSListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable  Bundle savedInstanceState) {
        taskDetailBinding = BottomSheetFragmentLayoutBinding.inflate(inflater, container, false);
        return taskDetailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleAddButton();
        showDatePickerDialog();

        taskDetailBinding.cancelBtn.setOnClickListener(v -> dismiss());
        taskDetailBinding.addBtn.setOnClickListener(v -> {
            listener.onAddClickListener(getTaskItem());
            dismiss();
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private TaskItem getTaskItem() {
        String task = Objects.requireNonNull(taskDetailBinding.taskInputField.getEditText()).getText().toString();
        String date = taskDetailBinding.datePicker.getText().toString();

        return new TaskItem(task,
                (date.equals(getString(R.string.select_date))) ? null : date);
    }

    private void showDatePickerDialog() {
        datePickerDialog = new DatePickerFragment((year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            CharSequence date = DateFormat.format("EEEE, MMM d, yyyy", calendar);

            taskDetailBinding.datePicker.setText(date);
        });
        taskDetailBinding.datePicker.setOnClickListener(v -> datePickerDialog.show(getChildFragmentManager(), null));
    }

    private void toggleAddButton() {
        Objects.requireNonNull(taskDetailBinding.taskInputField.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskDetailBinding.addBtn.setEnabled(!taskDetailBinding.taskInputField.getEditText()
                        .getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface TaskDetailBSListener{
        void onAddClickListener(TaskItem taskItem);
    }
}
