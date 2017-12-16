package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;

@MappedSuperclass
@TableGenerator( name = "longKeyId", initialValue = 10 )
public abstract class EntityWithLongKey extends GenericEntity {

    private static final long serialVersionUID = 973829971443943530L;

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE, generator = "longKeyId" )
    private long id;

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }

    @Override
    public boolean isAlreadyInDb () {
        try {
            return GenericEntity.load( Class.forName( this.getClass().getName() ), id ) != null;
        } catch ( EntityNotFoundException | ClassNotFoundException e ) {
            //if class type is not found then surely it doesn't exist in DB
            //NB this catch clause should never be thrown
            return false;
        }
    }
}
