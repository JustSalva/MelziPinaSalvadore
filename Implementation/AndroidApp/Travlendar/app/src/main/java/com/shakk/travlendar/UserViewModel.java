package com.shakk.travlendar;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;

import java.util.List;


public class UserViewModel extends AndroidViewModel {

    private LiveData<List<User>> users;
    private AppDatabase database = AppDatabase
            .getAppDatabase(getApplication()
                    .getApplicationContext());

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<List<User>>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        users = database.userDao().getUsers();
    }
}
