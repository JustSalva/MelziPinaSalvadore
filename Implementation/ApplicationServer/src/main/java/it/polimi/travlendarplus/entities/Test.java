package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.entities.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Instant;

public class Test {
    private static void testTypeofEvent(){
        TypeOfEvent t = new TypeOfEvent();
        t.save();
    }

    private static void testDate(){
        DateOfCalendar t = new DateOfCalendar();
        t.save();
    }
    public static void main ( String args[]){
        testDate();
    }
}
