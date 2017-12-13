package it.polimi.travlendarplus.entities;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public abstract class GenericEntityTest {


    @PersistenceContext
    public EntityManager entityManager;

    @Inject
    public UserTransaction userTransaction;



    public static JavaArchive createDeployment() {
        return ShrinkWrap.create( JavaArchive.class )
                .addPackage( GenericEntity.class.getPackage() )
                .addClass( Location.class )
                .addAsResource(  "test-persistence.xml", "META-INF/persistence.xml" )
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
    }

    public void preparePersistenceTest() throws Exception {
        clearData();
        insertData();
        startTransaction();
    }

    @After
    public void commitTransaction() throws Exception {
        userTransaction.commit();
    }

    private void clearData() throws Exception {
        startTransaction();
        clearTableQuery();
        commitTransaction();
    }

    protected abstract void clearTableQuery();

    private void insertData() throws Exception {
        startTransaction();

        loadTestData();
        commitTransaction();
        // clear the persistence context (first-level cache)
        entityManager.clear();
    }

    protected abstract void loadTestData();

    private void startTransaction() throws Exception {
        userTransaction.begin();
        entityManager.joinTransaction();
    }

}
