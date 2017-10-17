open util/integer
/* NOTE modellazione
1 - Period class has not been modelled cause its aim is to avoid the user to insert 
a periodic event again and again, but for modelling issues it's the same as have many
 different events at different times
2- per ora non faccio enviroment condition => semmai aggiungere alla fine

*/

// to be tested strings sets have to be specified, so I've created an alias just for simplicity
sig StringModel{ }

sig User{
	name : one StringModel,
	surname: one StringModel,
	email: one Int, // in this model email is an integer to allow comparisons.
	//TODO vedo se è il caso mettere sig email
	preferences: one Preferences,
	breaks: set BreakEvent,
	hold: set Ticket, //tickets owned
	plan: set Event, //Planned Events
	//TODO home and office relations?
	
}

sig Preferences{
//TODO param firstPath+ enable notification
contain: some TypeOfEvent,
}

sig TypeOfEvent{
	deactivate: set TravelMean,
	isLimitedBy: set Constraint,
}

abstract sig Constraint{
	isAbout: one TravelMean,
}

sig DistanceConstraint extends Constraint{
	maxLenght: one Int,
	minLenght: one Int
} {
	maxLenght > 0
	minLenght > 0
	maxLenght > minLenght
}

sig DayPeriodConstraint extends Constraint{
	/*min and max Hour are modeled as integer to allow easier comparisons,
 	the concept modeled is just the same, but we avoid useless complexity*/
	maxHour: one Int, 
	minHour: one Int,
}{
	/*maxHour >= 0 and maxHour =< 24
	minHour >= 0 and minHour =< 24*/
	maxHour > minHour
}
//Tickets models may be useless => check later
abstract sig Ticket{
	cost: one Float, 
	distance: one Float, //Controllo
}

/*sig GeneralTicket extends Ticket{
	lineName: one StringModel,
}
sig DistanceTicket extends Ticket{
	distance: one StringModel,
}
sig GeneralTicket extends Ticket{
	lineName: one StringModel,
}*/

sig TravelMean{
	name: one StringModel,
	//TODO forse meglio lasciare così
}

//TODO rivedere travel mean
sig PrivateTravelMean extends TravelMean{ }
sig SharingTravelMean extends TravelMean{ }
sig PublicTravelMean extends TravelMean{ }

sig BreakEvent{
	flexibleStart: one Int,
	flexibleEnd: one Int,
	minimum: one Int, //NB minimum time required to make a break
}

sig Event{
	startingTime: one Int,
	endingTime: one Int,
	type: one TypeOfEvent,
	feasiblePath: some Travel,
	departureLocation: one Location,
	eventLocation: one Location,
	/* descriptive variables are omitted, the variable prevLocChoice is
	omitted cause it's only an operative variable and it would not enrich the model */
} {
	startingTime < endingTime
	
	//TODO condizione sui luoghi di partenza e arrivo
}

sig Travel{
	composed: some TravelComponent
}

sig TravelComponent{
	departureLocation: one Location,
	arrivalLocation: one Location,
	startingTime: one Int,
	endingTime: one Int,
	ticketUsed: lone Ticket, //TODO chiedo 
	travelMeanUsed: one TravelMean,
}{
	departureLocation != arrivalLocation
}

sig Location{
	latitude: one Float,
	longitude: one Float,
	address: one StringModel, //Maybe Useless
}

sig Date{//Maybe not necessary 
}

//Float abstraction
sig Float {}

/*fact NoBreak{
	all u: User  |  #u.breaks=1
}*/

/*******************FACTS*******************/
fact email_Is_Unique{
	no disjoint u, u' : User | u.email = u'.email
}

fact travelsAlwaysLeadToDestination{
	all e: Event, t: Travel  | (	t in e.feasiblePath) implies 
			(one begin : t.composed | begin.departureLocation = e.departureLocation ) and
			(one end : t.composed | end.arrivalLocation = e.eventLocation)
	//condizione cui segmenti intermedi che devono essere tutti connessi

	//	( #feasiblePath = 0 )<=>( departureLocation = eventLocation ) da usare per il caso degenere in cui 
	//  partenza e arrivo sono lo stesso posto
}




/*******************PREDICATES*******************/



pred show{ }

run show for 2 but 1 Event
