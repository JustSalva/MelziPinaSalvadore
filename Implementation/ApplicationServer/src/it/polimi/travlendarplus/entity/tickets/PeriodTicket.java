package it.polimi.travlendarplus.entity.tickets;

import it.polimi.travlendarplus.entity.travelMeans.PublicTravelMean;

import java.time.LocalDate;
import java.util.ArrayList;

public class PeriodTicket extends Ticket {
    private String name;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private Ticket decorator;

    public PeriodTicket(float cost, ArrayList<PublicTravelMean> relatedTo, String name, LocalDate startingDate, LocalDate endingDate, Ticket decorator) {
        super(cost, relatedTo);
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.decorator = decorator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }
}
