package it.polimi.travlendarplus.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.ticket.DeleteTicketHandler;
import it.polimi.travlendarplus.activity.handler.ticket.GetTicketsHandler;
import it.polimi.travlendarplus.activity.listener.DragToDeleteTicketListener;
import it.polimi.travlendarplus.activity.listener.MyTouchTicketListener;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;
import it.polimi.travlendarplus.database.view_model.TicketsViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.ticket.DeleteTicketController;
import it.polimi.travlendarplus.retrofit.controller.ticket.GetTicketsController;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Activity that shows a ticket info.
 */
public class TicketsViewerActivity extends MenuActivity {
    // UI references.
    private LinearLayout ticketsContainer_linearLayout;
    // View models.
    private UserViewModel userViewModel;
    private TicketsViewModel ticketsViewModel;
    // Variables references.
    private List < Ticket > ticketsList;
    private boolean ticketsDownloaded = false;
    private String token;
    // Server responses handlers.
    private Handler getTicketsHandler;
    private Handler deleteTicketHandler;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tickets_viewer );
        super.setupMenuToolbar();

        ticketsContainer_linearLayout = findViewById( R.id.ticketsContainer_linearLayout );

        // Setup view models.
        userViewModel = ViewModelProviders.of( this ).get( UserViewModel.class );
        ticketsViewModel = ViewModelProviders.of( this ).get( TicketsViewModel.class );
        // Attach listeners to view models.
        userViewModel.getUser().observe( this, user -> token = user != null ? user.getToken() : "" );
        ticketsViewModel.getTickets().observe( this, tickets -> {
            ticketsList = tickets;
            fillTicketsLayout();
        } );
        // Setup handlers.
        getTicketsHandler = new GetTicketsHandler( Looper.getMainLooper(), this );
        deleteTicketHandler = new DeleteTicketHandler( Looper.getMainLooper(), this );

        if ( !ticketsDownloaded ) {
            loadTicketsFromServer();
            ticketsDownloaded = true;
        }

        findViewById( R.id.addTicket_button ).setOnClickListener(
                click -> startActivity( new Intent( getApplicationContext(), TicketEditorActivity.class ) )
        );
    }

    /**
     * Sends request to get tickets from the server.
     */
    private void loadTicketsFromServer () {
        // Send request to server.
        waitForServerResponse();
        GetTicketsController getTicketsController = new GetTicketsController( getTicketsHandler );
        getTicketsController.start( token );
    }

    /**
     * Sends request to delete a tickets to the server.
     */
    public void deleteTicket ( int ticketId ) {
        waitForServerResponse();
        DeleteTicketController deleteTicketController =
                new DeleteTicketController( deleteTicketHandler );
        deleteTicketController.start( token, ticketId );
    }

    /**
     * Clears the layout, then adds views to it representing tickets.
     */
    public void fillTicketsLayout () {
        ticketsContainer_linearLayout.removeAllViews();
        for ( Ticket ticket : ticketsList ) {
            GridLayout ticketGridLayout = insertTicketGridLayout( ticket );
            ticketGridLayout.setOnTouchListener( new MyTouchTicketListener() );
            ticketGridLayout.setId( ( int ) ticket.getId() );
            ticketsContainer_linearLayout.addView( ticketGridLayout );
        }
        // Add text view to remove ticket.
        TextView textView = new TextView( getApplicationContext() );
        textView.setText( "Drag here to remove" );
        textView.setTextColor( Color.parseColor( "#000000" ) );
        textView.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
        textView.setOnDragListener( new DragToDeleteTicketListener( this ) );
        ticketsContainer_linearLayout.addView( textView );
    }

    /**
     * Returns a GridLayout containing all the ticket's information.
     *
     * @param ticket Ticket with the information.
     * @return GridLayout containing ticket information.
     */
    private GridLayout insertTicketGridLayout ( Ticket ticket ) {
        GridLayout gridLayout = new GridLayout( getApplicationContext() );
        gridLayout.setColumnCount( 2 );
        gridLayout.setBackground( getResources().getDrawable( R.drawable.rectangle, getTheme() ) );
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = MATCH_PARENT;
        params.height = WRAP_CONTENT;
        params.setMargins( 10, 10, 10, 10 );
        params.columnSpec = GridLayout.spec( GridLayout.UNDEFINED, GridLayout.CENTER, 0.5f );
        gridLayout.setLayoutParams( params );
        gridLayout.addView( createTextView( ticket.getType().getType().concat( " ticket" ) ) );
        gridLayout.addView( createTextView( "COST: ".concat( Float.toString( ticket.getCost() ) ) ) );

        switch ( ticket.getType() ) {
            case GENERIC:
                insertGenericTicketFields( gridLayout, ticket );
                break;
            case DISTANCE:
                insertDistanceTicketFields( gridLayout, ticket );
                break;
            case PATH:
                insertPathTicketFields( gridLayout, ticket );
                break;
            case PERIOD:
                insertPeriodTicketFields( gridLayout, ticket );
                switch ( ticket.getPeriodTicket().getDecoratorType() ) {
                    case GENERIC:
                        insertGenericTicketFields( gridLayout, ticket );
                        break;
                    case DISTANCE:
                        insertDistanceTicketFields( gridLayout, ticket );
                        break;
                    case PATH:
                        insertPathTicketFields( gridLayout, ticket );
                        break;
                    default:
                        break;
                }
                break;
        }
        return gridLayout;
    }

    /**
     * Creates a TextView to be inserted in the GridLayout.
     *
     * @param content Content to be written in the TextView.
     * @return The TextView created in right way.
     */
    private TextView createTextView ( String content ) {
        TextView textView = new TextView( getApplicationContext() );
        textView.setText( content );
        textView.setTextColor( Color.parseColor( "#000000" ) );
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec( GridLayout.UNDEFINED, 1f );
        textView.setLayoutParams( params );
        textView.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
        return textView;
    }

    /**
     * Inserts the generic tickets details in the GridLayout.
     *
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket     Ticket containing details to be inserted.
     */
    private void insertGenericTicketFields ( GridLayout gridLayout, Ticket ticket ) {
        String lineName = ticket.getGenericTicket().getLineName();
        gridLayout.addView( createTextView( lineName ) );
    }

    /**
     * Inserts the distance tickets details in the GridLayout.
     *
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket     Ticket containing details to be inserted.
     */
    private void insertDistanceTicketFields ( GridLayout gridLayout, Ticket ticket ) {
        String distance = Float.toString( ticket.getDistanceTicket().getDistance() );
        gridLayout.addView( createTextView( distance.concat( " km" ) ) );
    }

    /**
     * Inserts the path tickets details in the GridLayout.
     *
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket     Ticket containing details to be inserted.
     */
    private void insertPathTicketFields ( GridLayout gridLayout, Ticket ticket ) {
        String startLocation = stringSplitter( ticket.getGenericTicket().getPathTicket().getArrivalLocation() );
        String endLocation = stringSplitter( ticket.getGenericTicket().getPathTicket().getDepartureLocation() );
        gridLayout.addView( createTextView( "FROM: \n".concat( startLocation ) ) );
        gridLayout.addView( createTextView( "TO: \n".concat( endLocation ) ) );
        gridLayout.addView( createTextView( "LINE: \n".concat( ticket.getGenericTicket().getLineName() ) ) );
    }

    /**
     * Inserts the period tickets details in the GridLayout.
     *
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket     Ticket containing details to be inserted.
     */
    private void insertPeriodTicketFields ( GridLayout gridLayout, Ticket ticket ) {
        String name = ticket.getPeriodTicket().getName();
        gridLayout.addView( createTextView(
                "START: \n".concat(
                        DateUtility.getInstantFromSeconds( ticket.getPeriodTicket().getStartDate() ).substring( 0, 10 )
                )
        ) );
        gridLayout.addView( createTextView(
                "END: \n".concat(
                        DateUtility.getInstantFromSeconds( ticket.getPeriodTicket().getEndDate() ).substring( 0, 10 )
                )
        ) );
        gridLayout.addView( createTextView( name ) );
    }

    public String getToken () {
        return token;
    }

    public Handler getDeleteTicketHandler () {
        return deleteTicketHandler;
    }

    /**
     * Add returns \n to a string after a white space, if at least 12 characters have been read.
     *
     * @param string String to be modified.
     * @return String modified.
     */
    public String stringSplitter ( String string ) {
        // Split string between spaces.
        String[] split = string.split( " " );
        String tobeReturned = "";
        String partialString = "";
        // For each part, add to partialString. When partialString is larger than 12 character,
        // add it to toBeReturned and clear it.
        for ( String s : split ) {
            partialString = partialString.concat( s + " " );
            if ( partialString.length() > 12 ) {
                tobeReturned = tobeReturned.concat( partialString.concat( "\n" ) );
                partialString = "";
            }
        }
        return tobeReturned.concat( partialString );
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
            startActivity( new Intent( this, CalendarActivity.class ) );
        }
    }
}
