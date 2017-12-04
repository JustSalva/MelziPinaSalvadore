package it.polimi.travlendarplus.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This is the main entity class, it is to be extended by all entity classes of Travlendar+
 * The class implements the Serializable interface, since is to be implemented by all entities classes
 */
public abstract class GeneralEntity implements Serializable{

    //TODO
    //@PersistenceUnit(unitName="TravlendarDB")
    static private EntityManagerFactory entityManagerFactory;

    private EntityManager startTransaction() {
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        return entityManager;
    }

    private void commitTransaction(EntityManager entityManager){
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * This method allows to save in the database any entity
     * @throws EntityExistsException if an entity with the same key already exist in the database
     */
    public void save() throws EntityExistsException{
        EntityManager entityManager = startTransaction();
        if(!this.isAlreadyInDb())
            entityManager.persist(this);
        else
            entityManager.merge(this);
        commitTransaction(entityManager);
    }

    public abstract boolean isAlreadyInDb();

    public void remove() {
        EntityManager entityManager = startTransaction();
        GeneralEntity toBeRemoved = entityManager.merge(this);
        entityManager.remove(toBeRemoved);
        commitTransaction(entityManager);
    }
    /**
     * This method allow to load any entity from the database
     * @param entityClass the class to be read
     * @param key his primary key
     * @param <T> The specific entity to be loaded
     * @param <S> the type of the entity key
     * @return the requested entity
     * @throws EntityNotFoundException if the entity does not exist in the database anymore
     * @throws NoResultException if the entity does not exist in the database
     */
    public static <T extends GeneralEntity,S> T load( Class<T> entityClass, S key) throws EntityNotFoundException, NoResultException{
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        T result = entityManager.find(entityClass,key);
        entityManager.close();
        return result;
    }
}
