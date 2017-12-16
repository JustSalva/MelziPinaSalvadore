package it.polimi.travlendarplus;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EJBTestInjector {

    private static final List < Class < ? extends Annotation > > EJB_ANNOTATIONS;

    static {
        EJB_ANNOTATIONS = new ArrayList < Class < ? extends Annotation > >();
        EJB_ANNOTATIONS.add( EJB.class );
        EJB_ANNOTATIONS.add( PersistenceContext.class );
        EJB_ANNOTATIONS.add( Resource.class );
    }

    final Map < Class < ? >, Object > mappings = new HashMap < Class < ? >, Object >();

    private static boolean hasEJBAnnotation ( final Field field ) {
        for ( final Class < ? extends Annotation > annotation : EJB_ANNOTATIONS ) {
            if ( field.isAnnotationPresent( annotation ) ) {
                return true;
            }
        }
        return false;
    }

    public void inject ( final Object bean ) throws Exception {
        for ( final Field field : getEJBAnnotatedFields( bean ) ) {
            injectField( field, bean );
        }
    }

    public void assign ( final Class < ? > type, final Object instance ) {
        mappings.put( type, instance );
    }

    private void injectField ( final Field field, final Object bean ) throws Exception {
        final Object instanceToInject = mappings.get( field.getType() );
        if ( !field.isAccessible() ) {
            field.setAccessible( true );
        }
        field.set( bean, instanceToInject );
    }

    private List < Field > getEJBAnnotatedFields ( final Object bean ) {
        final Class < ? extends Object > beanClass = bean.getClass();
        final List < Field > annotatedFields = new ArrayList < Field >();
        for ( final Field field : beanClass.getDeclaredFields() ) {
            if ( hasEJBAnnotation( field ) ) {
                annotatedFields.add( field );
            }
        }
        return annotatedFields;
    }
}