package it.polimi.travlendarplus.beans.calendarManager.pathManager;

import it.polimi.travlendarplus.EJBTestInjector;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.PreferenceManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


@RunWith( MockitoJUnitRunner.class )
public class PathManagerTest {
    PathManager pathManager;
    ScheduleManager scheduleManager;
    PreferenceManager preferenceManager;

    @Before
    public void test () throws Exception {
        this.pathManager = new PathManager();
        // create mocks
        this.scheduleManager = mock( ScheduleManager.class );
        this.preferenceManager = mock( PreferenceManager.class );
        // inject
        final EJBTestInjector injector = new EJBTestInjector();
        injector.assign( ScheduleManager.class, scheduleManager );
        injector.assign( PreferenceManager.class, preferenceManager );
        injector.inject( this.pathManager );
    }

    @Test
    public void shouldDoSth () {
        TravelComponent tc1 = new TravelComponent();
        TravelComponent tc2 = new TravelComponent();
        TravelComponent tc3 = new TravelComponent();
        PublicTravelMean ptm1 = new PublicTravelMean();
        PublicTravelMean ptm2 = new PublicTravelMean();
        ptm1.setType( TravelMeanEnum.CAR );
        ptm2.setType( TravelMeanEnum.BY_FOOT );

        tc1.setMeanUsed( ptm2 );
        tc1.setLength( 2.6f );
        List < TravelComponent > travels = new ArrayList < TravelComponent >();
        travels.add( tc1 );
        List < Travel > tr = new ArrayList < Travel >();
        Travel t1 = new Travel();
        t1.setMiniTravels( travels );
        tr.add( t1 );

        tc2.setMeanUsed( ptm1 );
        tc2.setLength( 18 );
        List < TravelComponent > travels2 = new ArrayList < TravelComponent >();
        travels2.add( tc2 );
        Travel t2 = new Travel();
        t2.setMiniTravels( travels2 );
        //tr.add( t2 );


        tr = pathManager.removeLongWalkingPath( tr );

        assertEquals( 1, tr.size() );
    }
}
