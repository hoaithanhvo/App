package com.example.nidecsnipeit.utility;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class CustomDatePicker {
    public interface OnDateSelectedListener {
        void onDateSelected(int day, int month, int year);
    }

    public static void showDatePickerDialog(Context context, final OnDateSelectedListener listener) {
        // Lấy ngày hiện tại
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        // Tạo DatePickerDialog

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,style,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Gọi callback khi người dùng chọn ngày
                        listener.onDateSelected(dayOfMonth, monthOfYear + 1, year);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
    public static String getMonthFormat(int month) {
        Calendar cal = Calendar.getInstance();
        if(month == 1)
            return "01";
        if(month == 2)
            return "02";
        if(month == 3)
            return "03";
        if(month == 4)
            return "04";
        if(month == 5)
            return "05";
        if(month == 6)
            return "06";
        if(month == 7)
            return "07";
        if(month == 8)
            return "08";
        if(month == 9)
            return "09";
        if(month == 10)
            return "10";
        if(month == 11)
            return "11";
        if(month == 12)
            return "12";
        //default should never happen
        return String.valueOf(cal.get(Calendar.MONTH));
    }
    public static String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String resultDate = "";
        if(day<=9){
            resultDate =  "0"+day +"-"+ getMonthFormat(month)  + "-" + year;
        }
        else{
            resultDate=  day +"-"+ getMonthFormat(month)  + "-" + year;
        }
        return resultDate;
    }
}
