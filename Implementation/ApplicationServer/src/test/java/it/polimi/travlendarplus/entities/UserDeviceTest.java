package it.polimi.travlendarplus.entities;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class UserDeviceTest extends GenericEntityTest{

    private List< UserDevice > userDevices;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment();
    }

    @Before
    public void setUp() throws Exception {
        userDevices = new ArrayList<>(  );
        userDevices.add( new UserDevice( "Device1",
                new User( "email1", "name1", "surname1", "password1" ) ) );
        userDevices.add( new UserDevice( "Device2",
                new User( "email2", "name2", "surname2", "password2" ) ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allDevicesAreFoundUsingJpqlQuery() {
        //all locations loaded
        List < UserDevice > retrievedComponents =
                entityManager.createQuery(
                        " SELECT devices FROM USER_DEVICES devices ORDER BY devices.idDevice", UserDevice.class)
                        .getResultList();
        assertContainsAllUserDevices( retrievedComponents );
    }


    private void assertContainsAllUserDevices( List< UserDevice > retrievedDevices ) {

        Assert.assertEquals( userDevices.size(), retrievedDevices.size() );
        assertDevicesAreEquals( userDevices.get( 0 ), retrievedDevices.get( 0 ) );
        assertDevicesAreEquals( userDevices.get( 1 ), retrievedDevices.get( 1 ) );
    }

    private void assertDevicesAreEquals( UserDevice expected, UserDevice retrieved ) {
        Assert.assertEquals( expected.getIdDevice(), retrieved.getIdDevice() );
    }


    @Override
    protected void clearTableQuery(){
        //entityManager.createQuery( "DELETE FROM USER_DEVICES " ).executeUpdate();
    }

    @Override
    protected void loadTestData(){
        for ( UserDevice userDevice : userDevices ){
            entityManager.persist( userDevice.getUser() );
            entityManager.persist( userDevice );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
