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
	email: one Email, // in this model email is an integer to allow comparisons.
	//TODO vedo se è il caso mettere sig email
	preferences: some TypeOfEvent,
	breaks: set BreakEvent,
	hold: set Ticket, //tickets owned
	plan: set Event, //Planned Events
	preferredLocations: set Location,
	
}

sig TypeOfEvent{
	/*Param first path not modeled since it's used only 
	to decide which path suggest first to the user*/
	deactivate: set TravelMean,
	isLimitedBy: set Constraint,
}

abstract sig Constraint{
	concerns: one TravelMean,
}

sig DistanceConstraint extends Constraint{
	maxLenght: one Int,
	minLenght: one Int
} {
	maxLenght > 0
	minLenght > 0
	maxLenght > minLenght
}

sig PeriodOfDayConstraint extends Constraint{
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
	ticketUsed: set TravelComponent,
	relatedTo: some PublicTravelMean,
}

sig GeneralTicket extends Ticket{
	lineName: one StringModel,
}

sig PathTicket extends GeneralTicket{
	departureLocation: one Location,
	arrivalLocation: one Location,
}

sig DistanceTicket extends Ticket{
	distance: one StringModel,
}

sig PeriodTicket extends Ticket{
	name: one StringModel,
	startDate: one Int,
	endDate: one Int,
	decorator: one Ticket,
}

abstract sig TravelMean{
	name: one StringModel,
	//speed and eco consumption are useless to modelling purpose
}
sig PrivateTravelMean extends TravelMean{ }
sig SharingTravelMean extends TravelMean{ }
sig PublicTravelMean extends TravelMean{ }

abstract sig GenericEvent{
	startingTime: one Int,
	endingTime: one Int,
}

sig BreakEvent{
	minimum: one Int, //NB minimum time required to make a break
}

sig Event extends GenericEvent{
	type: one TypeOfEvent,
	feasiblePath: some Travel,
	departureLocation: one Location,
	eventLocation: one Location,
	/* descriptive variables are omitted, the variable prevLocChoice is
	omitted cause it's only an operative variable and it would not enrich the model */
} {
	startingTime <	endingTime
	eventLocation != departureLocation
	
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
	meanUsed: one TravelMean,
}{
	departureLocation != arrivalLocation
}

sig Location{
	latitude: one Float,
	longitude: one Float,
	address: one StringModel, //Maybe Useless
}

//Float abstraction
sig Float {}

sig Email{}

/*fact NoBreak{
	all u: User  |  #u.breaks=1
}*/

/*******************FACTS*******************/
fact email_Is_Unique{
	no disjoint u, u' : User | u.email = u'.email
}

fact travelsAlwaysLeadToDestination{
	/*all e: Event, t: Travel  | (	t in e.feasiblePath) implies (
			(one begin, end : t.composed | begin.departureLocation = e.departureLocation and end.arrivalLocation = e.eventLocation and ( #t.composed>1 implies
			(all intermediate: t.composed | intermediate!= end and (one  generic: t.composed | generic.departureLocation= intermediate.arrivalLocation)) and
			all segment: t.composed | segment!= end and segment!= begin and segment.departureLocation!= e.departureLocation and segment.arrivalLocation != e.eventLocation
			)))*/
	//condizione cui segmenti intermedi che devono essere tutti connessi, controllare, non esatta

	//	( #feasiblePath = 0 )<=>( departureLocation = eventLocation ) da usare per il caso degenere in cui 
	//  partenza e arrivo sono lo stesso posto
}

fact noTypeOfEventWithoutUser{
	no type: TypeOfEvent | (all u: User | !(type in u.preferences))
}

fact noEventWithoutUser{
	no event: Event | (all u: User | !(event in u.plan))
}

fact noTravelsWithoutEvent{
	no t:Travel | ( all e:Event | !( t = e.feasiblePath) )
}

fact noTravelsComponentWithoutTravel{
	no c:TravelComponent | ( all t:Travel | !( c in t.composed) )
}

//No two coinciding but distinct locations
fact NoLocationOverlapping {
	no disj l, l1: Location | ( l.longitude = l1.longitude and l.latitude = l1.latitude )
}




/*******************PREDICATES*******************/

pred complexTravels{ all t:Travel | #t.composed>1}

pred show{ }

run complexTravels for 2 but 1 Event, 1 User
