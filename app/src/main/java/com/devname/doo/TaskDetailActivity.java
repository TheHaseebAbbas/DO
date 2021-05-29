package com.devname.doo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.devname.doo.databinding.ActivityTaskDetailBinding;

import java.text.DateFormat;
import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

public class TaskDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityTaskDetailBinding taskDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskDetailBinding = ActivityTaskDetailBinding.inflate(getLayoutInflater());
        setContentView(taskDetailBinding.getRoot());

        taskDetailBinding.taskInputField.getEditText().addTextChangedListener(new TextWatcher() {
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        if (v == taskDetailBinding.cancelBtn){
            setResult(RESULT_CANCELED,intent);
            finish();
        }else if (v == taskDetailBinding.addBtn){
            String editTextInput = taskDetailBinding.taskInputField.getEditText().getText().toString().trim();
            TaskItem data = new TaskItem(editTextInput, getDate());
            intent.putExtra("KEY",data);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(taskDetailBinding.datePickerField.getYear(),
                taskDetailBinding.datePickerField.getMonth(),
                taskDetailBinding.datePickerField.getDayOfMonth());
        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this,"up-to-bottom");
    }
}