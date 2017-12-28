package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This JPA class represent a set of user preferences( =type of event)
 */
@Entity( name = "TYPE_OF_EVENT" )
@TableGenerator( name = "typeOfEventId", initialValue = 10 )
public class TypeOfEvent extends GenericEntity {

    private static final long serialVersionUID = 1979790161261960888L;

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE, generator = "typeOfEventId" )
    private long id;

    /**
     * Name, given by the user, of the type of event
     */
    @Column( nullable = false, name = "NAME" )
    private String name;

    /**
     * Priority given to path calculation in a specific type of event
     *
     * @see ParamFirstPath
     */
    @Column( nullable = false, name = "PARAM_FIRST_PATH" )
    @Enumerated( EnumType.STRING )
    private ParamFirstPath paramFirstPath;

    /**
     * Set of constraints to be applied for each travel relative to an event that use a type of event
     */
    @JoinTable( name = "LIMITED_BY" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < Constraint > limitedBy;

    /**
     * Set of deactivated travel means, that cannot be used in a travel relative to an event that use a type of event
     */
    @ElementCollection( fetch = FetchType.LAZY )
    @CollectionTable
    @Enumerated( EnumType.STRING )
    private List < TravelMeanEnum > deactivate;

    public TypeOfEvent () {
        this.limitedBy = new ArrayList <>();
        this.deactivate = new ArrayList <>();
    }

    public TypeOfEvent ( String name, ParamFirstPath paramFirstPath ) {
        this();
        this.name = name;
        this.paramFirstPath = paramFirstPath;
    }

    /**
     * Allows to load a TypeOfEvent class from the database
     *
     * @param key primary key of the typeOfEvent tuple
     * @return the requested tuple as a TypeOfEvent class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static TypeOfEvent load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( TypeOfEvent.class, key );
    }

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }

    public ParamFirstPath getParamFirstPath () {
        return paramFirstPath;
    }

    public void setParamFirstPath ( ParamFirstPath paramFirstPath ) {
        this.paramFirstPath = paramFirstPath;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public List < Constraint > getLimitedBy () {
        return Collections.unmodifiableList( limitedBy );
    }

    public void setLimitedBy ( List < Constraint > limitedBy ) {
        this.limitedBy = limitedBy;
    }

    /**
     * Retrieve all constraints that are relative to a specific travel mean
     *
     * @param travelMeanEnum travel mean the requested constraints are relative to
     * @return a list of constraints that related to the specified travel mean if any, an empty list otherwise
     */
    public ArrayList < Constraint > getLimitedBy ( TravelMeanEnum travelMeanEnum ) {
        ArrayList < Constraint > cons = new ArrayList < Constraint >();
        for ( Constraint constraint : limitedBy ) {
            if ( constraint.getConcerns().equals( travelMeanEnum ) ) {
                cons.add( constraint );
            }
        }
        return cons;
    }

    public List < TravelMeanEnum > getDeactivate () {
        return deactivate;
    }

    public void setDeactivate ( List < TravelMeanEnum > deactivate ) {
        this.deactivate = deactivate;
    }

    /**
     * Checks if a travel mean is deactivated
     *
     * @param vehicle travel mean to be checked
     * @return true if the specified travel mean is deactivates, false otherwise
     */
    public boolean isDeactivated ( TravelMeanEnum vehicle ) {
        return deactivate.contains( vehicle );
    }

    /**
     * Adds a travel mean to the deactivated list
     *
     * @param travelMean travel mean to be added in such list
     */
    public void addDeactivated ( TravelMeanEnum travelMean ) {
        deactivate.add( travelMean );
    }

    /**
     * Removes a deactivate constraint, if present
     *
     * @param vehicle travel mean that is to be removed from deactivate constraints
     */
    public void removeDeactivated ( TravelMeanEnum vehicle ) {
        deactivate.removeIf( travelMean -> travelMean.equals( vehicle ) );
    }

    /**
     * Adds a generic constraint to a type of event
     *
     * @param constraint generic constraint to be added
     */
    public void addConstraint ( Constraint constraint ) {
        limitedBy.add( constraint );
    }

    /**
     * Removes a generic constraint from a type of event, if present
     *
     * @param id identifier of the constraint to be removed
     */
    public void removeConstraint ( long id ) {
        limitedBy.removeIf( constraint -> constraint.getId() == id );
    }

    /**
     * Checks if a typeOfEvent is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            load( id );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
