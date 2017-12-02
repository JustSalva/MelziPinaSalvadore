package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.entities.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Instant;

public class Test {
    private static void testTypeofEvent(EntityManager em){
        TypeOfEvent t = new TypeOfEvent();
        t.setId(1);
        t.setName("Adamo");
        t.setParamFirstPath(ParamFirstPath.ECO_PATH);
        em.persist(t);
    }

    private static void testDate(EntityManager em){
        DateOfCalendar t = new DateOfCalendar();
        t.setDate(Instant.now());
        em.persist(t);
    }
    public static void main ( String args[]){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager em = emfactory.createEntityManager();

        em.getTransaction().begin();
        testDate(em);
        em.getTransaction().commit();

        em.close();
        emfactory.close();
    }
}
