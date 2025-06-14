package com.example.schoolhub;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class AgendaActivity extends AppCompatActivity {

    private GridLayout gridDays;
    private Spinner spinnerMonth, spinnerYear;
    private ImageButton btnPrevMonth, btnNextMonth;
    private ImageView backButton;

    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        gridDays = findViewById(R.id.grid_days);
        spinnerMonth = findViewById(R.id.spinner_month);
        spinnerYear = findViewById(R.id.spinner_year);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        setupMonthSpinner();
        setupYearSpinner();

        btnPrevMonth.setOnClickListener(v -> {
            if (currentMonth == 0) {
                currentMonth = 11;
                currentYear--;
            } else {
                currentMonth--;
            }
            updateSpinners();
            generateCalendar(currentYear, currentMonth);
        });

        btnNextMonth.setOnClickListener(v -> {
            if (currentMonth == 11) {
                currentMonth = 0;
                currentYear++;
            } else {
                currentMonth++;
            }
            updateSpinners();
            generateCalendar(currentYear, currentMonth);
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentMonth = position;
                generateCalendar(currentYear, currentMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentYear = 2000 + position;
                generateCalendar(currentYear, currentMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        generateCalendar(currentYear, currentMonth);
    }

    private void setupMonthSpinner() {
        String[] months = new DateFormatSymbols(Locale.ENGLISH).getMonths();
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < 12; i++) monthAdapter.add(months[i]);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(currentMonth);
    }

    private void setupYearSpinner() {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i <= 50; i++) yearAdapter.add(String.valueOf(2000 + i));
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(currentYear - 2000);
    }

    private void updateSpinners() {
        spinnerMonth.setSelection(currentMonth);
        spinnerYear.setSelection(currentYear - 2000);
    }

    private void generateCalendar(int year, int month) {
        gridDays.removeAllViews();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday = 1
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeekOffset = firstDayOfWeek - Calendar.SUNDAY;

        int totalCells = dayOfWeekOffset + daysInMonth;
        int rows = (int) Math.ceil(totalCells / 7.0);
        int day = 1;

        for (int i = 0; i < rows * 7; i++) {
            TextView dayView = new TextView(this);
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(16);
            dayView.setTextColor(Color.BLACK);
            dayView.setBackgroundColor(Color.parseColor("#EFEFEF"));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 7, 1f);
            params.setMargins(4, 4, 4, 4);
            dayView.setLayoutParams(params);

            if (i >= dayOfWeekOffset && day <= daysInMonth) {
                dayView.setText(String.valueOf(day));
                day++;
            } else {
                dayView.setText("");
            }

            gridDays.addView(dayView);
        }
    }
}
