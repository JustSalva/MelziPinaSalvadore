package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity( name = "TYPE_OF_EVENT" )
@TableGenerator( name="typeOfEventId", initialValue = 10 )
public class TypeOfEvent extends GenericEntity {

    private static final long serialVersionUID = 1979790161261960888L;

    @Id
    @GeneratedValue( strategy=GenerationType.TABLE, generator="typeOfEventId")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column( nullable = false, name = "NAME" )
    private String name;

    @Column( nullable = false, name = "PARAM_FIRST_PATH" )
    @Enumerated( EnumType.STRING )
    private ParamFirstPath paramFirstPath;

    @JoinTable( name = "LIMITED_BY" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List< Constraint > limitedBy;

    @ElementCollection( fetch = FetchType.LAZY )
    @CollectionTable
    @Enumerated( EnumType.STRING )
    private List< TravelMeanEnum > deactivate;

    public TypeOfEvent() {
        this.limitedBy = new ArrayList<>();
        this.deactivate = new ArrayList<>();
    }

    public TypeOfEvent( String name, ParamFirstPath paramFirstPath ) {
        this();
        this.name = name;
        this.paramFirstPath = paramFirstPath;
    }

    public static TypeOfEvent load( long key ) throws EntityNotFoundException {
        return GenericEntity.load( TypeOfEvent.class, key );
    }

    public ParamFirstPath getParamFirstPath() {
        return paramFirstPath;
    }

    public void setParamFirstPath( ParamFirstPath paramFirstPath ) {
        this.paramFirstPath = paramFirstPath;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List< Constraint > getLimitedBy() {
        return Collections.unmodifiableList( limitedBy );
    }

    public void setLimitedBy( List< Constraint > limitedBy ) {
        this.limitedBy = limitedBy;
    }

    public ArrayList< Constraint > getLimitedBy( TravelMeanEnum travelMeanEnum ) {
        ArrayList< Constraint > cons = new ArrayList< Constraint >();
        for ( Constraint constraint : limitedBy ) {
            if ( constraint.getConcerns().equals( travelMeanEnum ) ) {
                cons.add( constraint );
            }
        }
        return cons;
    }

    public List< TravelMeanEnum > getDeactivate() {
        return deactivate;
    }

    public void setDeactivate( List< TravelMeanEnum > deactivate ) {
        this.deactivate = deactivate;
    }

    public boolean isDeactivated( TravelMeanEnum vehicle ) {
        return deactivate.contains( vehicle );
    }

    public void addDeactivated( TravelMeanEnum travelMean ) {
        deactivate.add( travelMean );
    }

    public void removeDeactivated( TravelMeanEnum vehicle ) {
        deactivate.removeIf( travelMean -> travelMean.equals( vehicle ) );
    }

    public void addConstraint( Constraint constraint ) {
        limitedBy.add( constraint );
    }

    public void removeConstraint( long id ) {
        limitedBy.removeIf( constraint -> constraint.getId() == id );
    }

    @Override
    public boolean isAlreadyInDb() {
        try {
            load( id );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
