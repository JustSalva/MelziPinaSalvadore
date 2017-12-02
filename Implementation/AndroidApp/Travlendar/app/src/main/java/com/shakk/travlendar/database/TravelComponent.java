package com.shakk.travlendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.lang.annotation.Inherited;

@Entity
public class TravelComponent {
    @PrimaryKey
    private String ID;
    private float length;

    @ColumnInfo(name = "travel_mean")
    private TravelMeanType travelMean;

    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    @ColumnInfo(name = "arrival_location")
    private String arrivalLocation;

    @ColumnInfo(name = "starting_time")
    private String startingTime;
    @ColumnInfo(name = "ending_time")
    private String endingTime;
}
