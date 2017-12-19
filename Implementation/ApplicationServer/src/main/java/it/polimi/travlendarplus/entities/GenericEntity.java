package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.Serializable;

/**
 * This is the main entity class, it is to be extended by all entity classes of Travlendar+
 * The class implements the Serializable interface, since is to be implemented by all entities classes
 */
public abstract class GenericEntity implements Serializable {

    private static final long serialVersionUID = -5052903462329369713L;

    static private EntityManagerFactory entityManagerFactory;

    /**
     * This method allows to load any entity from the database
     *
     * @param entityClass the class to be read
     * @param key         his primary key
     * @param <T>         The specific entity to be loaded
     * @param <S>         the type of the entity key
     * @return the requested entity
     * @throws EntityNotFoundException if the entity does not exist in the database
     */
    public static < T, S > T load ( Class < T > entityClass, S key ) throws EntityNotFoundException {
        entityManagerFactory = Persistence.createEntityManagerFactory( "TravlendarDB" );
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        T result = entityManager.find( entityClass, key );
        entityManager.close();
        if ( result == null ) {
            throw new EntityNotFoundException();
        }
        return result;
    }

    /**
     * Starts a transaction with the persistence unit
     * @return the entity manager which will handle the transaction
     */
    private EntityManager startTransaction () {
        entityManagerFactory = Persistence.createEntityManagerFactory( "TravlendarDB" );
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        return entityManager;
    }

    /**
     * Commits and close a transaction
     * @param entityManager the entity manager which is handling the transaction
     */
    private void commitTransaction ( EntityManager entityManager ) {
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * This method allows to save in the database any entity
     *
     * @throws EntityExistsException if an entity with the same key already exist in the database
     */
    public void save () throws EntityExistsException {
        EntityManager entityManager = startTransaction();
        if ( !this.isAlreadyInDb() )
            entityManager.persist( this );
        else
            entityManager.merge( this );
        commitTransaction( entityManager );
    }

    /**
     * Checks if a specific entity is present in database
     * @return true if it is present, false otherwise
     */
    public abstract boolean isAlreadyInDb ();

    /**
     * Removes an entity form the database
     */
    public void remove () {
        EntityManager entityManager = startTransaction();
        GenericEntity toBeRemoved = entityManager.merge( this );
        entityManager.remove( toBeRemoved );
        commitTransaction( entityManager );
    }
}
