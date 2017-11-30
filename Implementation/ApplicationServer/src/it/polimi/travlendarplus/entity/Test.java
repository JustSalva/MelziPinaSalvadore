package it.polimi.travlendarplus.entity;

import it.polimi.travlendarplus.entity.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entity.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entity.preferences.TypeOfEvent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;

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
        t.setDate(LocalDate.now());
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
