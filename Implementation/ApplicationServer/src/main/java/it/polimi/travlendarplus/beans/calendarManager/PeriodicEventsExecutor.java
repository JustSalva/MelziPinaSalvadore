package it.polimi.travlendarplus.beans.calendarManager;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import java.util.concurrent.Executor;

@Stateless
public class PeriodicEventsExecutor implements Executor {

    @Override
    @Asynchronous
    public void execute ( Runnable command ) {
        command.run();
    }

}
