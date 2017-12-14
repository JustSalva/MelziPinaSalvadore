package it.polimi.travlendarplus.entities;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith( Arquillian.class )
public class UserDeviceTest extends GenericEntityTest {

    private List< UserDevice > userDevices;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment()
                .addClass( UserDevice.class )
                .addClass( User.class );
    }

    @Before
    public void setUp() throws Exception {
        userDevices = new ArrayList<>();

        User user1 = new User( "email1", "name1", "surname1", "password1" );
        user1.addUserDevice( "Device1" );
        userDevices.add( user1.getUserDevice( "Device1" ) );

        User user2 = new User( "email2", "name2", "surname2", "password2" );
        user2.addUserDevice( "Device2" );
        userDevices.add( user2.getUserDevice( "Device2" ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allDevicesAreFoundUsingJpqlQuery() {

        List< UserDevice > retrievedComponents =
                entityManager.createQuery(
                        " SELECT devices FROM USER_DEVICES devices ORDER BY devices.idDevice", UserDevice.class )
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
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM TRAVLENDAR_USER " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        for ( UserDevice userDevice : userDevices ) {
            entityManager.persist( userDevice.getUser() );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
