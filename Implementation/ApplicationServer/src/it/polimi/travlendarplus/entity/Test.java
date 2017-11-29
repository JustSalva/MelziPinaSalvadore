package it.polimi.travlendarplus.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Test {
    public static void main ( String args[]){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager em = emfactory.createEntityManager();
        TypeOfEvent t = new TypeOfEvent();
        em.getTransaction().begin();
        t.setId(1);
        t.setName("Adamo");
        t.setParamFirstPath(ParamFirstPath.ECO_PATH);
        em.persist(t);
        em.getTransaction().commit();

        em.close();
        emfactory.close();
    }
}
