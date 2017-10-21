open util/integer
open util/boolean
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
	/*min and max Hour are modeled as an integer, as all other time variables int this model to allow easier comparisons,
 	the concept modeled is just the same, but we avoid useless complexity*/
	maxTime: one Int, 
	minTime: one Int,
}{
	maxTime > minTime
}

sig Ticket{
	cost: one Float, 
	ticketUsed: set TravelComponent,
	relatedTo: some PublicTravelMean,
}

/*sig GeneralTicket extends Ticket{
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
}*/

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
	isScheduled: one Bool,
}

sig BreakEvent extends GenericEvent{
	minimum: one Int, //NB minimum time required to make a break
}

sig Event extends GenericEvent{
	type: one TypeOfEvent,
	feasiblePaths: set Travel,
	departureLocation: one Location,
	eventLocation: one Location,
	/* descriptive variables are omitted, the variable prevLocChoice is
	omitted cause it's only an operative variable and it would not enrich the model */
} {
	startingTime <	endingTime
	 #feasiblePaths>=0
	(eventLocation= departureLocation or isScheduled=False)implies #feasiblePaths=0 else #feasiblePaths>0

	//TODO condizione sui luoghi di partenza e arrivo
}

sig Travel{
	composed: seq TravelComponent
}{
	#composed>=0
}

sig TravelComponent{
	departureLocation: one Location,
	arrivalLocation: one Location,
	startingTime: one Int,
	endingTime: one Int,
	meanUsed: one TravelMean,
	travelDistance: one Int, //Distances are modeled as integers (should be float)
}{
	departureLocation != arrivalLocation
	endingTime > startingTime
}

sig Location{
	latitude: one Float,
	longitude: one Float,
	address: one StringModel, //Maybe Useless
}

//Float abstraction
sig Float {}

sig Email{}

/*******************FACTS*******************/
fact email_Is_Unique{
	no disjoint u, u' : User | u.email = u'.email
}

fact travelsAlwaysLeadToDestination{
	all e: Event, travel: Travel  | (	travel in e.feasiblePaths) implies 
		(travel.composed[0].departureLocation=e.departureLocation 
			and (one i: travel.composed.inds | 	(all i1: travel.composed.inds | ( i1<=i and travel.composed[i].arrivalLocation=e.eventLocation)))
			/*and (all  i2: travel.composed.inds | travel.composed[i2].departureLocation!=e.eventLocation )*/
			
		)
	//condizione cui segmenti intermedi che devono essere tutti connessi, controllare, non esatta

	//	( #feasiblePath = 0 )<=>( departureLocation = eventLocation ) da usare per il caso degenere in cui 
	//  partenza e arrivo sono lo stesso posto
}

fact noLoopsInTravels{
	/*all travel: Travel | ( !(travel.composed).hasDups  and
		all i1: travel.composed.inds | one i2: travel.composed.inds | 
			(travel.composed[i1].arrivalLocation=travel.composed[i2].departureLocation) iff i2=i1+1)*/
}

fact noTypeOfEventWithoutUser{
	no type: TypeOfEvent | (all u: User | !(type in u.preferences))
}

fact noEventWithoutUser{
	no event: Event | (all u: User | !(event in u.plan))
}

fact noTravelsWithoutEvent{
	no t:Travel | ( all e:Event | !( t in (e.feasiblePaths)) )
}

fact noTravelsComponentWithoutTravel{
	no c:TravelComponent | ( all t:Travel | !( c in univ.( t.composed)) )
}

//No two coinciding but distinct locations
fact noLocationOverlapping {
	no disj l, l1: Location | ( l.longitude = l1.longitude and l.latitude = l1.latitude )
}

fact noScheduledEventsOverlapping{
	all u:User | (all event1: GenericEvent |( ((event1 in u.plan) and event1.isScheduled=True)
		implies(no disjoint event2: GenericEvent | (event2 in u.plan) and event2.isScheduled=True and 
			(event1.startingTime>event2.endingTime or event2.startingTime>event1.endingTime)) 
		)
	)
}

//Forall scheduled breaks is always granted the minumum break time
fact breakEventAlwaysGranted{
	all u:User | (all break: BreakEvent | ( ((break in u.breaks) and break.isScheduled=True)
		implies( no event1: u.plan | (event1.isScheduled=True and 
		no event2: u.plan | ( event2.isScheduled=True and 
			let precedent= event1.endingTime | let successor= event2.startingTime |
			(precedent>break.startingTime or successor>break.endingTime) implies
				 (successor-precedent<break.minimum)
				 )  )   )     )      )
}

fact allTravelsRespectDeactivateConstraints{
	all event: Event | let travelComponent=event.feasiblePaths.composed |
			 ( no i: travelComponent.inds | travelComponent[i].meanUsed in event.type.deactivate )
}


fact allTravelsRespectDistanceConstraints{
	all event: Event | let travelComponent=event.feasiblePaths.composed |
			 ( no i: travelComponent.inds | 
				all constraint: event.type.isLimitedBy & DistanceConstraint | 
					(constraint.concerns=travelComponent[i].meanUsed) implies
			 			(travelComponent[i].travelDistance>constraint.minLenght and travelComponent[i].travelDistance<constraint.maxLenght)
			)
}

fact allTravelsRespectPeriodConstraints{
	all event: Event | let travelComponent=event.feasiblePaths.composed |
			 ( no i: travelComponent.inds | 
				all constraint: event.type.isLimitedBy & PeriodOfDayConstraint | 
					(constraint.concerns=travelComponent[i].meanUsed) implies(
						(travelComponent[i].startingTime>constraint.maxTime) or
						(travelComponent[i].endingTime<constraint.minTime) )
			)
}



/*******************PREDICATES*******************/

pred complexTravels{ 
all t:Travel | #t.composed>2
all event:Event |  event.isScheduled= True
}

pred show{ }

run complexTravels for 3 but 1 Event, 1 User, 1 BreakEvent
