package it.polimi.travlendarplus.entities;

import javax.persistence.*;
import java.io.Serializable;

public abstract class GeneralEntity implements Serializable{

    //TODO
    //@PersistenceUnit(unitName="TravlendarDB")
    static protected EntityManagerFactory entityManagerFactory;

    public void save() throws EntityExistsException{
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(this);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public abstract GeneralEntity load( long key ) throws EntityNotFoundException, NoResultException;
    /*{
        return entityManager.find(GeneralEntity.class,key);
    }*/

    public static <T,S> T loadHelper( Class<T> entityClass, S key){
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        T result = entityManager.find(entityClass,key);
        entityManager.close();
        return result;
    }


}
