package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path( "test" )
public class RESTfulTest {

    @Inject
    RESTfulTestSettings set;

    @Path( "calcPath" )
    @GET
    @Produces( "text/plain" )
    public String baseTest () {
        try {
            return set.addEventBaseCaseTest( true, false, true, false );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "firstPath" )
    @GET
    @Produces( "text/plain" )
    public String firstPathTest () {
        try {
            return set.addEventBaseCaseTest( false, true, true, false );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "lastPath" )
    @GET
    @Produces( "text/plain" )
    public String lastPathTest () {
        try {
            return set.addEventBaseCaseTest( true, true, false, false );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "calcPathWithBreakOk" )
    @GET
    @Produces( "text/plain" )
    public String addEventBreakOk () {
        try {
            return set.addEventWithBreak( true, true, true, true, 1 * 60 * 60 );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "calcPathWithBreakErr" )
    @GET
    @Produces( "text/plain" )
    public String addEventBreakErr () {
        try {
            return set.addEventWithBreak( true, true, true, true, 2 * 60 * 60 );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "addBreakOvBreak" )
    @GET
    @Produces( "text/plain" )
    public String addBreakOverOtherBreak () {
        //2018/01/20 h:10:30 - 11:30
        try {
            return set.addBreakEvent( 1516444200, 1516447800, 1 );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "addBreakNoFeas" )
    @GET
    @Produces( "text/plain" )
    public String addNoFeasibleBreak () {
        //2018/01/20 h:11:00 - 13:00
        try {
            return set.addBreakEvent( 1516446000, 1516453200, 3600 + 1 );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "addBreakFeas" )
    @GET
    @Produces( "text/plain" )
    public String addFeasibleBreak () {
        //2018/01/20 h:11:00 - 13:00
        try {
            return set.addBreakEvent( 1516446000, 1516453200, 3600 );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    /*@Path( "swap" )
    @GET
    @Produces( "text/plain" )
    public String swap () {
        //2018/01/20 h:16:00 - 17:00
        try {
            return set.swapEvents( 1516464000, 1516467600, false );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "swapPrevOut" )
    @GET
    @Produces( "text/plain" )
    public String swapPrevOut () {
        //2018/01/20 h:15:00 - 17:00
        try {
            return set.swapEvents( 1516460400 + 1, 1516467600, false );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "swapFollOut" )
    @GET
    @Produces( "text/plain" )
    public String swapFollOut () {
        //2018/01/20 h:14:30 - 18:00
        try {
            return set.swapEvents( 1516458600, 1516471200 - 1, false );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "swapBreakFeas" )
    @GET
    @Produces( "text/plain" )
    public String swapBreakFeas () {
        //2018/01/20 h:10:55 - 12:00
        try {
            return set.swapEvents( 1516444200 + 25 * 60, 1516449600, true );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }

    @Path( "swapBreakOut" )
    @GET
    @Produces( "text/plain" )
    public String swapBreakOut () {
        //2018/01/20 h:10:30 - 12:00
        try {
            return set.swapEvents( 1516444200, 1516449600, true );
        } catch ( GMapsGeneralException e ) {
            return e.getMessage();
        }
    }*/
}
