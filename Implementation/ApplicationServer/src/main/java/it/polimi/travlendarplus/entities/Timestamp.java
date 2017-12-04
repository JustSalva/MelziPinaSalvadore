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

    public Timestamp(){
        this.timestamp = Instant.now();
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
