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
public class RSAEncryptionTest extends GenericEntityTest {


    private List< RSAEncryption > RSAkeys;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( RSAEncryption.class );
    }

    @Before
    public void setUp() throws Exception {
        RSAkeys = new ArrayList<>();
        RSAkeys.add( new RSAEncryption( "idDevice1" ) );
        RSAkeys.add( new RSAEncryption( "idDevice2" ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allRSAKeysAreFoundUsingJpqlQuery() {

        List< RSAEncryption > retrievedkeys =
                entityManager.createQuery( " SELECT ek FROM Encryption_keys ek ORDER BY ek.idDevice", RSAEncryption.class )
                        .getResultList();
        assertContainsAllKeys( retrievedkeys );
    }

    private void assertContainsAllKeys( List< RSAEncryption > retrievedKeys ) {

        Assert.assertEquals( RSAkeys.size(), retrievedKeys.size() );
        assertKeysAreEquals( RSAkeys.get( 0 ), retrievedKeys.get( 0 ) );
        assertKeysAreEquals( RSAkeys.get( 1 ), retrievedKeys.get( 1 ) );
    }

    private void assertKeysAreEquals( RSAEncryption expected, RSAEncryption retrieved ) {
        Assert.assertEquals( expected.getIdDevice(), retrieved.getIdDevice() );
        Assert.assertEquals( expected.getPublicKey(), retrieved.getPublicKey() );
        Assert.assertEquals( expected.getPrivateKey(), retrieved.getPrivateKey() );
    }


    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM Encryption_keys " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        for ( RSAEncryption key : RSAkeys ) {
            entityManager.persist( key );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }
}
