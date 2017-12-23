package it.polimi.travlendarplus.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;

/**
 * This Embeddable class is used to provide the record of the time when
 * the last update is performed to all JPA class that needs such functionality
 */
@Embeddable
public class Timestamp {

    @Column( name = "UNIX_TIMESTAMP" )
    private Instant timestamp;

    public Timestamp () {
        this.timestamp = Instant.now();
    }

    /**
     * Every time a class that contains the Timestamp attribute is either
     * updated or persisted for the first time in Database the timestamp is automatically updated
     */
    @PreUpdate
    @PrePersist
    public void updateTimeStamps () {
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp () {
        return timestamp;
    }

    public void setTimestamp ( Instant timestamp ) {
        this.timestamp = timestamp;
    }
}
