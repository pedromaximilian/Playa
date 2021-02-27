package com.example.playa.ui.home;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();




            mText.setValue(formatter.format(date));
        }else{
            mText.setValue("Posible version antigua de Android");
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}