package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;

/**
 * This JPA abstract class represent a generic JPA class that have a long primary key,
 * it is to be implemented by all entities that must have such a key
 */
@MappedSuperclass
@TableGenerator( name = "longKeyId", initialValue = 10 )
public abstract class EntityWithLongKey extends GenericEntity {

    private static final long serialVersionUID = 973829971443943530L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue( strategy = GenerationType.TABLE, generator = "longKeyId" )
    private long id;

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }

    /**
     * Checks if an entityWithLongKey subclass is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            return GenericEntity.load( Class.forName( this.getClass().getName() ), id ) != null;
        } catch ( EntityNotFoundException | ClassNotFoundException e ) {
            //if class type is not found then surely it doesn't exist in DB
            return false;
        }
    }
}
