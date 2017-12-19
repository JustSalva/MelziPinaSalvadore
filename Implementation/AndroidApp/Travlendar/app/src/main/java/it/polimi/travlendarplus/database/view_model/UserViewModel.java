package it.polimi.travlendarplus.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.User;

public class UserViewModel extends AndroidViewModel {

    private AppDatabase database;

    private LiveData<User> user;

    public UserViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
    }

    public LiveData<User> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
            user = database.userDao().getUser();
        }
        return user;
    }
}
