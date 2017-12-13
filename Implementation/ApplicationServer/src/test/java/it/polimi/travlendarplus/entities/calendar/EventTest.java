package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntityTest;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class EventTest extends GenericEntityTest{


    @Before
    public void setUp() throws Exception {
       
    }

    @Override
    protected void clearTableQuery() throws Exception{
        em.createQuery( "DELETE FROM EVENT" ).executeUpdate();
    }

    @Override
    protected void loadTestData() throws Exception {
        //TODO
    }
}
