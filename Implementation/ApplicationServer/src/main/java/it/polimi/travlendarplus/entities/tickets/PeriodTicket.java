package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;

@Entity(name="PERIOD_TICKET")
@DiscriminatorValue("PERIOD")
public class PeriodTicket extends Ticket {

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name="STARTING_DATE_OF_CALENDAR")
    private DateOfCalendar startingDate;

    @ManyToOne
    @JoinColumn(name="ENDING_DATE_OF_CALENDAR")
    private DateOfCalendar endingDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="PERIODICAL_TICKET")
    private Ticket decorator;

    public PeriodTicket() {
    }

    public PeriodTicket(float cost, ArrayList<PublicTravelMean> relatedTo, String name,
                        DateOfCalendar startingDate, DateOfCalendar endingDate, Ticket decorator) {
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

    public DateOfCalendar getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(DateOfCalendar startingDate) {
        this.startingDate = startingDate;
    }

    public DateOfCalendar getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(DateOfCalendar endingDate) {
        this.endingDate = endingDate;
    }

    @Override
    public PeriodTicket load(long key) throws EntityNotFoundException, NoResultException {
        return loadHelper(PeriodTicket.class, key);
    }
}
