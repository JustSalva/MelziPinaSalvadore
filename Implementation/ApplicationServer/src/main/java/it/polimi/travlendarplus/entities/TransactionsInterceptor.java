package it.polimi.travlendarplus.entities;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

@TransactionsInterceptorBinding
@Interceptor
public class TransactionsInterceptor {
    //TODO it doesn't works!
    @Resource
    EntityManagerFactory entityManagerFactory;

    @AroundInvoke
    public Object manageTransaction(InvocationContext context)throws Exception{
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Object result = context.proceed();
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
        //TODO rollback, handle transactions fails
    }
}
