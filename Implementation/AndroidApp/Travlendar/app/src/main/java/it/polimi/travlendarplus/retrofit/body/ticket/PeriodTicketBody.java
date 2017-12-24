package it.polimi.travlendarplus.retrofit.body.ticket;


import java.util.List;

import it.polimi.travlendarplus.TravelMeanUsed;

public class PeriodTicketBody extends TicketBody {

    private String name;
    private String startingDate;
    private String endingDate;
    private DistanceTicketBody distanceDecorator;
    private GenericTicketBody genericDecorator;
    private PathTicketBody pathDecorator;

    public PeriodTicketBody(float cost, List<TravelMeanUsed> relatedTo, String name, String startingDate, String endingDate) {
        super(cost, relatedTo);
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }

    public void setDistanceDecorator(DistanceTicketBody distanceDecorator) {
        this.distanceDecorator = distanceDecorator;
    }

    public void setGenericDecorator(GenericTicketBody genericDecorator) {
        this.genericDecorator = genericDecorator;
    }

    public void setPathDecorator(PathTicketBody pathDecorator) {
        this.pathDecorator = pathDecorator;
    }
}
