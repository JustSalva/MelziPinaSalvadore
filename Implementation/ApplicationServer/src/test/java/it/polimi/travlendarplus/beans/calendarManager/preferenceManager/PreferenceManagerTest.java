package it.polimi.travlendarplus.beans.calendarManager.preferenceManager;

import it.polimi.travlendarplus.beans.calendarManager.PreferenceManager;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class PreferenceManagerTest {

    PreferenceManager pm = new PreferenceManager();
    PreferenceManagerSettings setter = new PreferenceManagerSettings();
    List < PathCombination > pcAl = new ArrayList < PathCombination >();

    @BeforeAll
    public void setUp () {
        setter.baseConfiguration();
        pcAl.add( setter.pc1 );
        pcAl.add( setter.pc2 );
        pcAl.add( setter.pc3 );
        pcAl.add( setter.pc4 );
        pcAl.add( setter.pc5 );
        pcAl.add( setter.pc6 );
    }

    @Test
    public void checkConstraints () {
        assertEquals( false, pm.checkConstraints( setter.t1, setter.toe1 ) );
        assertEquals( false, pm.checkConstraints( setter.t1, setter.toe2 ) );
        assertEquals( false, pm.checkConstraints( setter.t1, setter.toe3 ) );
        assertEquals( false, pm.checkConstraints( setter.t1, setter.toe4 ) );
        assertEquals( false, pm.checkConstraints( setter.t2, setter.toe1 ) );
        assertEquals( false, pm.checkConstraints( setter.t2, setter.toe2 ) );
        assertEquals( true, pm.checkConstraints( setter.t2, setter.toe3 ) );
        assertEquals( false, pm.checkConstraints( setter.t2, setter.toe4 ) );
        assertEquals( false, pm.checkConstraints( setter.t3, setter.toe1 ) );
        assertEquals( true, pm.checkConstraints( setter.t3, setter.toe2 ) );
        assertEquals( true, pm.checkConstraints( setter.t3, setter.toe3 ) );
        assertEquals( false, pm.checkConstraints( setter.t3, setter.toe4 ) );
        assertEquals( true, pm.checkConstraints( setter.t4, setter.toe1 ) );
        assertEquals( false, pm.checkConstraints( setter.t4, setter.toe2 ) );
        assertEquals( true, pm.checkConstraints( setter.t4, setter.toe3 ) );
        assertEquals( false, pm.checkConstraints( setter.t4, setter.toe4 ) );
    }

    @Test
    public void findBestPath () {
        assertEquals( 4, pm.findBestPath( pcAl, setter.toe1 ).getPrevPath().getTotalTime() );
        assertEquals( 5, pm.findBestPath( pcAl, setter.toe1 ).getFollPath().getTotalTime() );
        assertEquals( 3.5f, pm.findBestPath( pcAl, setter.toe2 ).getPrevPath().getTotalLength() );
        assertEquals( 2.5f, pm.findBestPath( pcAl, setter.toe2 ).getFollPath().getTotalLength() );
        assertEquals( 3.5f, pm.findBestPath( pcAl, setter.toe3 ).getPrevPath().getTotalLength() );
        assertEquals( 2.5f, pm.findBestPath( pcAl, setter.toe3 ).getFollPath().getTotalLength() );
        assertEquals( 4, pm.findBestPath( pcAl, setter.toe4 ).getPrevPath().getTotalTime() );
        assertEquals( 5, pm.findBestPath( pcAl, setter.toe4 ).getFollPath().getTotalTime() );
    }

    @Test
    public void getAllowedMeans () {
        TravelMeanEnum[] list = { TravelMeanEnum.CAR, TravelMeanEnum.BY_FOOT, TravelMeanEnum.BIKE, TravelMeanEnum.TRAIN,
                TravelMeanEnum.TRAM, TravelMeanEnum.BUS, TravelMeanEnum.SUBWAY };
        assertEquals( 6, pm.getAllowedMeans( setter.e1, list ).size() );
        assertEquals( 5, pm.getAllowedMeans( setter.e2, list ).size() );
        assertEquals( 7, pm.getAllowedMeans( setter.e3, list ).size() );
        assertEquals( 7, pm.getAllowedMeans( setter.e4, list ).size() );


    }
}
