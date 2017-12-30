package it.polimi.travlendarplus.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import it.polimi.travlendarplus.database.entity.User;

/**
 * Dao interface containing methods to access DB user values.
 */
@Dao
public interface UserDao {
    /**
     * Inserts a user in the DB.
     * @param user User to be inserted.
     */
    @Insert
    void insert(User user);

    /**
     * Delete all the users from the DB.
     */
    @Query("DELETE FROM user")
    void delete();

    /**
     * Returns the user present in the DB.
     * @return The user present.
     */
    @Query("SELECT * FROM user")
    LiveData<User> getUser();

    /**
     * Sets a timestamp on the user.
     * @param timestamp Timestamp to be set.
     */
    @Query("UPDATE user SET timestamp = :timestamp")
    void setTimestamp(long timestamp);

    /**
     * Returns the number of users present in the DB.
     * @return The number of users.
     */
    @Query("SELECT COUNT(*) FROM user")
    int countUsers();
}