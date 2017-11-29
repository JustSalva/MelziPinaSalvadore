package it.polimi.travlendarplus.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TypeOfEvent implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private ParamFirstPath paramFirstPath;

   /* @ElementCollection
    private List<Constraint> limitedBy;


    private ArrayList<TravelMean> deactivate; //TODO qui non è meglio un enum?*/

    public TypeOfEvent() {
    }

    public TypeOfEvent(String name, ParamFirstPath paramFirstPath) {
        this.name = name;
        this.paramFirstPath = paramFirstPath;
       /* this.limitedBy = new ArrayList<>();
        this.deactivate = new ArrayList<>();*/
    }

    public ParamFirstPath getParamFirstPath() {
        return paramFirstPath;
    }

    public Integer getId() {
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

    public void setId(Integer id) {
        this.id = id;
    }
/*
    public boolean isLimitedBy(TravelMean travelMean){
        return limitedBy.contains(travelMean);
    }

    public boolean isDeactivated(Constraint constraint){
        return deactivate.contains(constraint);
    }
*/
}
