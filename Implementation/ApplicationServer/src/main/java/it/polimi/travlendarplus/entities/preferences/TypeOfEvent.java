package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "TYPE_OF_EVENT")
public class TypeOfEvent extends GeneralEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, name = "NAME")
    private String name;

    @Column(nullable = false, name = "PARAM_FIRST_PATH")
    @Enumerated(EnumType.STRING)
    private ParamFirstPath paramFirstPath;

    @JoinTable(name = "LIMITED_BY")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Constraint> limitedBy;

    @ElementCollection
    @CollectionTable
    @Enumerated(EnumType.STRING)
    private List<TravelMeanEnum> deactivate;

    public TypeOfEvent() {
    }

    public TypeOfEvent(String name, ParamFirstPath paramFirstPath) {
        this.name = name;
        this.paramFirstPath = paramFirstPath;
        this.limitedBy = new ArrayList<>();
        this.deactivate = new ArrayList<>();
    }

    public ParamFirstPath getParamFirstPath() {
        return paramFirstPath;
    }

    public long getId() {
        return id;
    }

    public void setParamFirstPath(ParamFirstPath paramFirstPath) {
        this.paramFirstPath = paramFirstPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isLimitedBy(TravelMeanEnum travelMean){
        return limitedBy.contains(travelMean);
    }

    public List<Constraint> getLimitedBy() {
        return Collections.unmodifiableList(limitedBy);
    }

    public void setLimitedBy(List<Constraint> limitedBy) {
        this.limitedBy = limitedBy;
    }

    public List<TravelMeanEnum> getDeactivate() {
        return deactivate;
    }

    public void setDeactivate(List<TravelMeanEnum> deactivate) {
        this.deactivate = deactivate;
    }

    public boolean isDeactivated(Constraint constraint){
        return deactivate.contains(constraint);
    }

    public static TypeOfEvent load(long key){
        return GeneralEntity.load( TypeOfEvent.class, key );
    }
}
