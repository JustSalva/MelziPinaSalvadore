package it.polimi.travlendarplus.beans.calendarManager.preferenceManager;

import it.polimi.travlendarplus.TestUtilities;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.PrivateTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManagerSettings {

    Event e1 = new Event();
    Event e2 = new Event();
    Event e3 = new Event();
    Event e4 = new Event();
    TypeOfEvent toe1, toe2, toe3, toe4;
    Travel t1, t2, t3, t4;
    List < Instant > stTime, enTime;
    float[] dist;
    List < TravelMean > tme;
    PathCombination pc1, pc2, pc3, pc4, pc5, pc6;

    public void baseConfiguration () {
        t1 = setTravel1();
        t2 = setTravel2();
        t3 = setTravel3();
        t4 = setTravel4();
        pc1 = new PathCombination( t1, t2 );
        pc2 = new PathCombination( t1, t3 );
        pc3 = new PathCombination( t2, t3 );
        pc4 = new PathCombination( t1, t4 );
        pc5 = new PathCombination( t2, t4 );
        pc6 = new PathCombination( t3, t4 );
        toe1 = TestUtilities.setTypeOfEvent( "1", ParamFirstPath.MIN_TIME );
        toe2 = TestUtilities.setTypeOfEvent( "2", ParamFirstPath.MIN_LENGTH );
        toe3 = TestUtilities.setTypeOfEvent( "3", ParamFirstPath.MIN_LENGTH );
        toe4 = TestUtilities.setTypeOfEvent( "4", ParamFirstPath.MIN_TIME );
        TestUtilities.setDeactivatedMean( toe1, TravelMeanEnum.BUS );
        TestUtilities.setDeactivatedMean( toe2, TravelMeanEnum.BY_FOOT );
        TestUtilities.setDeactivatedMean( toe2, TravelMeanEnum.SUBWAY );
        TestUtilities.setDistanceConstraint( toe1, TravelMeanEnum.BY_FOOT, 0, 0 );
        TestUtilities.setDistanceConstraint( toe2, TravelMeanEnum.CAR, 0, 3 );
        TestUtilities.setPeriodConstraint( toe3, TravelMeanEnum.TRAIN, 0, 15 );
        TestUtilities.setDistanceConstraint( toe4, TravelMeanEnum.BY_FOOT, 0, 0 );
        TestUtilities.setPeriodConstraint( toe4, TravelMeanEnum.CAR, 9, 20 );
        e1.setType( toe1 );
        e2.setType( toe2 );
        e3.setType( toe3 );
        e4.setType( toe4 );

    }

    private Travel setTravel1 () {
        refreshArrayList();
        addParam( stTime, Instant.ofEpochSecond( 8 ) );
        addParam( stTime, Instant.ofEpochSecond( 10 ) );
        addParam( stTime, Instant.ofEpochSecond( 12 ) );
        addParam( stTime, Instant.ofEpochSecond( 13 ) );
        addParam( enTime, Instant.ofEpochSecond( 10 ) );
        addParam( enTime, Instant.ofEpochSecond( 12 ) );
        addParam( enTime, Instant.ofEpochSecond( 13 ) );
        addParam( enTime, Instant.ofEpochSecond( 16 ) );
        dist[ 0 ] = 0.5f;
        dist[ 1 ] = 0.5f;
        dist[ 2 ] = 0.5f;
        dist[ 3 ] = 2f;
        addParam( tme, new PrivateTravelMean( "", TravelMeanEnum.BY_FOOT, 0 ) );
        addParam( tme, new PublicTravelMean( "", TravelMeanEnum.SUBWAY, 0 ) );
        addParam( tme, new PrivateTravelMean( "", TravelMeanEnum.BY_FOOT, 0 ) );
        addParam( tme, new PublicTravelMean( "", TravelMeanEnum.TRAIN, 0 ) );
        return TestUtilities.setTravelMultiPaths( stTime, enTime, dist, tme );
    }

    private Travel setTravel2 () {
        refreshArrayList();
        addParam( stTime, Instant.ofEpochSecond( 8 ) );
        addParam( stTime, Instant.ofEpochSecond( 12 ) );
        addParam( enTime, Instant.ofEpochSecond( 12 ) );
        addParam( enTime, Instant.ofEpochSecond( 14 ) );
        dist[ 0 ] = 2f;
        dist[ 1 ] = 3f;
        addParam( tme, new PrivateTravelMean( "", TravelMeanEnum.BY_FOOT, 0 ) );
        addParam( tme, new PublicTravelMean( "", TravelMeanEnum.BUS, 0 ) );
        return TestUtilities.setTravelMultiPaths( stTime, enTime, dist, tme );
    }

    private Travel setTravel3 () {
        refreshArrayList();
        addParam( stTime, Instant.ofEpochSecond( 8 ) );
        addParam( stTime, Instant.ofEpochSecond( 9 ) );
        addParam( stTime, Instant.ofEpochSecond( 11 ) );
        addParam( enTime, Instant.ofEpochSecond( 9 ) );
        addParam( enTime, Instant.ofEpochSecond( 11 ) );
        addParam( enTime, Instant.ofEpochSecond( 12 ) );
        dist[ 0 ] = 1f;
        dist[ 1 ] = 0.5f;
        dist[ 2 ] = 1f;
        addParam( tme, new PublicTravelMean( "", TravelMeanEnum.TRAM, 0 ) );
        addParam( tme, new PrivateTravelMean( "", TravelMeanEnum.BY_FOOT, 0 ) );
        addParam( tme, new PublicTravelMean( "", TravelMeanEnum.TRAM, 0 ) );
        return TestUtilities.setTravelMultiPaths( stTime, enTime, dist, tme );
    }

    private Travel setTravel4 () {
        refreshArrayList();
        addParam( stTime, Instant.ofEpochSecond( 8 ) );
        addParam( enTime, Instant.ofEpochSecond( 13 ) );
        dist[ 0 ] = 4f;
        addParam( tme, new PrivateTravelMean( "", TravelMeanEnum.CAR, 0 ) );
        return TestUtilities.setTravelMultiPaths( stTime, enTime, dist, tme );
    }

    private void refreshArrayList () {
        stTime = new ArrayList < Instant >();
        enTime = new ArrayList < Instant >();
        dist = new float[ 5 ];
        tme = new ArrayList < TravelMean >();
    }

    private void addParam ( List al, Object param ) {
        al.add( param );
    }

}
