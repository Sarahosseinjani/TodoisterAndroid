package com.bawp.todoister;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.bawp.todoister.adaptor.OnTodoClickListener;
import com.bawp.todoister.adaptor.RecyclerViewAdaptor;
import com.bawp.todoister.ir.hamsa.PersianActivity;
import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;

public class MainActivity extends AppCompatActivity implements OnTodoClickListener {
    private static final String TAG = "ITEM";
    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdaptor recyclerViewAdaptor;
    BottomSheetFragment bottomSheetFragment;
    private SharedViewModel sharedViewModel;
    private PersianDatePickerDialog picker;
    String date;
    DateConverter dateConverter = new DateConverter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);



        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
        .create(TaskViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        taskViewModel.getAllTasks().observe(this, tasks -> {
            recyclerViewAdaptor = new RecyclerViewAdaptor(tasks, this);
            recyclerView.setAdapter(recyclerViewAdaptor);


        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->  {
            showBottomSheetDialog();


        });
    }

    private void showBottomSheetDialog() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTodoClick(Task task) {
        sharedViewModel.selectItem(task);
        sharedViewModel.setIsEdit(true);
        showBottomSheetDialog();

    }

    @Override
    public void onTodoRadioButtonClicked(Task task) {
        TaskViewModel.delete(task);
        recyclerViewAdaptor.notifyDataSetChanged();
    }
    //get help from https://github.com/aliab/Persian-Date-Picker-Dialog
    public void showCal(View view) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Shabnam-Light-FD.ttf");
        picker = new PersianDatePickerDialog(this)
                .setPositiveButtonString("OK")
                .setNegativeButton("CANCLE")
                .setTodayButton("TODAY")
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setAllButtonsTextSize(12)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setInitDate(1370, 3, 13)
                .setActionTextColor(Color.GRAY)
                .setTypeFace(typeface)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(PersianPickerDate persianPickerDate)  {
                        Log.d(TAG, "onDateSelected: " + persianPickerDate.getTimestamp());//675930448000
                        Log.d(TAG, "onDateSelected: " + persianPickerDate.getGregorianDate());//Mon Jun 03 10:57:28 GMT+04:30 1991
                        Log.d(TAG, "onDateSelected: " + persianPickerDate.getPersianLongDate());// دوشنبه  13  خرداد  1370
                        Log.d(TAG, "onDateSelected: " + persianPickerDate.getPersianMonthName());//خرداد
                        Log.d(TAG, "onDateSelected: " + PersianCalendarUtils.isPersianLeapYear(persianPickerDate.getPersianYear()));//true
                        //Toast.makeText(MainActivity.this, persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay() +
                               " is equal to " + dateConverter.PersianToGregorian(persianPickerDate.getPersianYear() ,persianPickerDate.getPersianMonth(),persianPickerDate.getPersianDay()) +
                                "please choose it in calender and then click save button", Toast.LENGTH_LONG).show();

                    }



                    @Override
                    public void onDismissed() {
                        Toast.makeText(MainActivity.this, "Dismissed", Toast.LENGTH_SHORT).show();
                    }
                });

        picker.show();
    }


}