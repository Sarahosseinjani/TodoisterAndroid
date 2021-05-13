package com.bawp.todoister;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bawp.todoister.ir.hamsa.PersianActivity;
import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Date;


import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;

import static com.bawp.todoister.R.string.empty_field;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText enterTodo;
    private ImageButton calenderButton;
    private RadioGroup priorityRadioGroup;
    private RadioGroup selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calenderGroup;
    private Date dueDate;
    Calendar calender = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;
    private RadioButton high;
    private RadioButton med;
    private RadioButton low;
    private PersianDatePickerDialog picker;
    MainActivity mainActivity = new MainActivity();

    public BottomSheetFragment() {

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calenderGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calenderButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityRadioGroup = view.findViewById((R.id.radioGroup_priority));
        high = (RadioButton) view.findViewById(R.id.radioButton_high); // initiate a radio button
        med = (RadioButton) view.findViewById(R.id.radioButton_med); // initiate a radio button
        low = (RadioButton) view.findViewById(R.id.radioButton_low); // initiate a radio button

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);



        return view;
    }


    public void save(Date dueDate){
        String task = enterTodo.getText().toString().trim();
        if(dueDate == null){

            Log.i("date", String.valueOf(dueDate));
        }
            if (!TextUtils.isEmpty(task) && dueDate != null && priority != null ){

                Task myTask = new Task(task, priority, dueDate, Calendar.getInstance().getTime(), false);
                if(isEdit){
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();
                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueDate);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);


                }else {
                    TaskViewModel.insert(myTask);
                }
                enterTodo.setText("");
                if(this.isVisible()){
                    this.dismiss();
                }
            }else{
                Snackbar.make(saveButton, empty_field, Snackbar.LENGTH_LONG).show();
            }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(sharedViewModel.getSelectedItem().getValue() != null){
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);



        calenderButton.setOnClickListener(view12 -> {
            calenderGroup.setVisibility(calenderGroup.getVisibility() == View.GONE ? View.VISIBLE :View.GONE);
            Utils.hideSoftKeyBoard(view12);
        });


        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            calender.clear();
            calender.set(year, month, dayOfMonth);
            dueDate = calender.getTime();

        });
       // priorityButton.setOnClickListener(view13 -> {
          //  Log.i("Press", "Ok");
           // priorityRadioGroup.setVisibility(priorityRadioGroup.getWindowVisibility() == View.GONE ? View.VISIBLE : View.GONE);
           // Utils.hideSoftKeyBoard(view13);
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                if(priorityRadioGroup.getVisibility() == View.VISIBLE ){
                    //selectedButtonId = checkedId;
                    Boolean highButtonState = high.isChecked(); // check current state of a radio button (true or false).
                    Boolean medButtonState = med.isChecked(); // check current state of a radio button (true or false).
                    Boolean lowButtonState = low.isChecked(); // check current state of a radio button (true or false).
                    //selectedRadioButton = view.findViewById(selectedButtonId);
                    if(highButtonState){
                        priority = Priority.HIGH;
                        Log.i("Press", "high");
                    }else if(medButtonState){
                        priority = Priority.MEDIUM;
                    }else if(lowButtonState){
                        priority = Priority.LOW;
                    }else{
                        priority = Priority.LOW;
                    }
                }else{
                    priority = Priority.LOW;
                }

            });

        //});


        saveButton.setOnClickListener(view1 -> {

            save(dueDate);

        });

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if(id == R.id.today_chip){
            //set date for today
            calender.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calender.getTime();

        }else if(id == R.id.tomorrow_chip){
            calender.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calender.getTime();

        }else if(id == R.id.next_week_chip) {
            calender.add(Calendar.DAY_OF_YEAR, 7);
            dueDate = calender.getTime();
        }

    }


}