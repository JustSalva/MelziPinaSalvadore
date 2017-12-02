package it.polimi.travlendarplus.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
public class Timestamp {

    @Column(name = "UNIX_TIMESTAMP")
    private Instant timestamp;

    protected Timestamp(){
        this.timestamp = Instant.now();
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        this.timestamp = Instant.now();
    }
}
