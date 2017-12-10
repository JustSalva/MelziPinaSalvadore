package com.shakk.travlendar.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;

public class UserViewModel extends AndroidViewModel {

    private AppDatabase database;

    private LiveData<User> user;

    public UserViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(getApplication().getApplicationContext());
    }

    public LiveData<User> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
            loadUser();
        }
        return user;
    }

    private void loadUser() {
        user = database.userDao().getUser();
    }
}
