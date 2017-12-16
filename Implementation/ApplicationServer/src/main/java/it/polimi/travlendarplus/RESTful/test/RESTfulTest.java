package it.polimi.travlendarplus.RESTful.test;

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
    public String baseTest() {
        return set.addEventBaseCaseTest( true, false, true, false );
    }

    @Path( "firstPath" )
    @GET
    @Produces( "text/plain" )
    public String firstPathTest() {
        return set.addEventBaseCaseTest( false, true, true, false );
    }

    @Path( "lastPath" )
    @GET
    @Produces( "text/plain" )
    public String lastPathTest() {
        return set.addEventBaseCaseTest( true, true, false, false );
    }

    @Path( "calcPathWithBreakOk" )
    @GET
    @Produces( "text/plain" )
    public String addEventBreakOk() {
        return set.addEventWithBreak( true, true, true, true, 1 * 60 * 60 );
    }

    @Path( "calcPathWithBreakErr" )
    @GET
    @Produces( "text/plain" )
    public String addEventBreakErr() {
        return set.addEventWithBreak( true, true, true, true, 2 * 60 * 60 );
    }

    @Path( "addBreakOvBreak" )
    @GET
    @Produces( "text/plain" )
    public String addBreakOverOtherBreak() {
        //2018/01/20 h:10:30 - 11:30
        return set.addBreakEvent( 1516444200, 1516447800, 1 );
    }

    @Path( "addBreakNoFeas" )
    @GET
    @Produces( "text/plain" )
    public String addNoFeasibleBreak() {
        //2018/01/20 h:11:00 - 13:00
        return set.addBreakEvent( 1516446000, 1516453200, 3600 + 1 );
    }

    @Path( "addBreakFeas" )
    @GET
    @Produces( "text/plain" )
    public String addFeasibleBreak() {
        //2018/01/20 h:11:00 - 13:00
        return set.addBreakEvent( 1516446000, 1516453200, 3600 );
    }

    @Path( "swap" )
    @GET
    @Produces( "text/plain" )
    public String swap() {
        //2018/01/20 h:16:00 - 17:00
        return set.swapEvents( 1516464000, 1516467600, false );
    }

    @Path( "swapPrevOut" )
    @GET
    @Produces( "text/plain" )
    public String swapPrevOut() {
        //2018/01/20 h:15:00 - 17:00
        return set.swapEvents( 1516460400 + 1, 1516467600, false );
    }

    @Path( "swapFollOut" )
    @GET
    @Produces( "text/plain" )
    public String swapFollOut() {
        //2018/01/20 h:14:30 - 18:00
        return set.swapEvents( 1516458600, 1516471200 - 1, false );
    }

    @Path( "swapBreakFeas" )
    @GET
    @Produces( "text/plain" )
    public String swapBreakFeas() {
        //2018/01/20 h:10:55 - 12:00
        return set.swapEvents( 1516444200 + 25 * 60, 1516449600, true );
    }

    @Path( "swapBreakOut" )
    @GET
    @Produces( "text/plain" )
    public String swapBreakOut() {
        //2018/01/20 h:10:30 - 12:00
        return set.swapEvents( 1516444200, 1516449600, true );
    }
}
