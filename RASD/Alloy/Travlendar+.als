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
/* In this model we keep only a generic ticket*/
sig Ticket{
	cost: one Float, 
	ticketUsed: set TravelComponent,
	relatedTo: some PublicTravelMean,
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
	isScheduled: one Bool,
}{
	startingTime <	endingTime
}

sig BreakEvent extends GenericEvent{
	minimum: one Int, //NB minimum time required to make a break
}{
	minus[endingTime, startingTime]>=minimum
	minimum>0
}

sig Event extends GenericEvent{
	type: one TypeOfEvent,
	feasiblePaths: set Travel,
	departureLocation: one Location,
	eventLocation: one Location,
	/* descriptive variables are omitted, the variable prevLocChoice is
	omitted cause it's only an operative variable and it would not enrich the model */
} {
	 #feasiblePaths>=0
	(eventLocation= departureLocation or isScheduled=False)implies #feasiblePaths=0 else #feasiblePaths>0

	//TODO condizione sui luoghi di partenza e arrivo
}

sig Travel{
	composed: seq TravelComponent
}{
	#composed>=0
	not composed.hasDups
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

fact location_Is_Unique{
	no disjoint l, l' : Location | l.latitude=l'.latitude and l.longitude=l'.longitude and l.address=l'.address
}

//All travels must have a connected path and lead to the destination
fact travelsAlwaysLeadToDestination{
	all e: Event, travel: Travel  | (	travel in e.feasiblePaths) implies (let sequence=travel.composed |
		(sequence.first.departureLocation=e.departureLocation 
			and sequence.last.arrivalLocation= e.eventLocation
			and (no element: sequence.rest.butlast.elems | element.departureLocation=e.departureLocation)
			and (no element: sequence.rest.butlast.elems | element.departureLocation=e.eventLocation)
			and (no element: sequence.rest.butlast.elems | element.arrivalLocation=e.departureLocation)
			and (no element: sequence.rest.butlast.elems | element.arrivalLocation=e.eventLocation)
			and (all element: sequence.rest.butlast.elems | let i=sequence.indsOf[element]|
				 (sequence[minus[i,1]].arrivalLocation=element.departureLocation)and (sequence[add[i,1]].departureLocation=element.arrivalLocation))
		 )  )
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

// returns the difference between end and start travel times
fun travelTime [travel: Travel]: one Int{
 	minus[sum[(univ.(travel.composed)).endingTime], sum[(univ.(travel.composed)).startingTime]]
}

//Compute the time the user has to start travelling to reach the event in time => the time when begins the travel's time slot
fun relativeStart [event: GenericEvent, travel: Travel ]: one Int{
	minus[event.startingTime, travelTime[travel] ]
}

//events and their travels times doesn't overlap (if they are scheduled) with other events.
fact noScheduledEventsOverlapping{
	all u:User | (all event1: GenericEvent |( ((event1 in u.plan) and event1.isScheduled=True)
		implies(no disjoint event2: GenericEvent | (event2 in u.plan) and event2.isScheduled=True and ( 
			all travel1: Travel | (travel1 in event1.feasiblePaths) and ( all travel2: Travel | (travel2 in event2.feasiblePaths) and
			(relativeStart[event1, travel1]>event2.endingTime or relativeStart[event2, travel2]>event1.endingTime)) )  )
		)
	)
}

//Forall scheduled breaks is always granted the minumum break time
fact breakEventAlwaysGranted{
	all u:User | (all break: BreakEvent | ( ((break in u.breaks) and break.isScheduled=True)
		implies( no event1: u.plan | (event1.isScheduled=True and 
		no event2: u.plan | ( event2.isScheduled=True and 
			let precedent= event1.endingTime |  all travel2: Travel | (travel2 in event2.feasiblePaths) and let successor= relativeStart[event2, travel2] | 
			(precedent>break.startingTime or successor>break.endingTime) implies
				 (minus[successor,precedent] < break.minimum)
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
# Event=1
# BreakEvent=0
#Travel=1
all t:Travel | #t.composed=3
all event:Event |  event.isScheduled= True
all event:BreakEvent |  event.isScheduled= True
}

pred show{ }

run complexTravels for 3 but 1 User, 1 BreakEvent, 5 Location
