package it.polimi.travlendarplus.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
public class Timestamp {

    @Column(name = "TIMESTAMP_DATE")
    private LocalDate date;

    @Column(name = "TIMESTAMP_TIME")
    private LocalTime time;

    protected Timestamp(){
        date = LocalDate.now();
        time = LocalTime.now();
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        date = LocalDate.now();
        time = LocalTime.now();
    }
}
