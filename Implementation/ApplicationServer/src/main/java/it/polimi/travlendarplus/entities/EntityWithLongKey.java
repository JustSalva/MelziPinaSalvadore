package it.polimi.travlendarplus.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EntityWithLongKey extends GenericEntity {

    private static final long serialVersionUID = 973829971443943530L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean isAlreadyInDb() {
        try {
            return GenericEntity.load( Class.forName(this.getClass().getName()), id ) != null;
        } catch (ClassNotFoundException e) {
            //if class type is not found then surely it doesn't exist in DB
            //NB this catch clause should never be thrown
            return false;
        }
    }
}
