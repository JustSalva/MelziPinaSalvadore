open util/integer
open util/boolean
/* Modeling Notes
1 - Period class has not been modelled cause its aim is to avoid the user to insert 
a periodic event again and again, but for modelling issues it's the same as have many
 different events at different times
2- Enviroment conditions are not modeled 
3- The time is modeled as incremental integers that rapresent consecutive points in the timeline, even across multiple days
*/

// Strings sets have to be specified, so I've created an alias just for simplicity
sig StringModel{ }

sig User{
	name : one StringModel,
	surname: one StringModel,
	email: one Email,
	preferences: some TypeOfEvent,
	breaks: set BreakEvent,
	hold: set Ticket, //tickets owned
	plan: set Event, //Planned Events
	preferredLocations: set Location,
	
}

sig TypeOfEvent{
	/*Param first path not modeled since it's used only 
	to decide which path the system suggests first to the user*/
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
	/*Min and max Hour are modeled as an integer, as all other time variables int this model to allow easier comparisons,
 	the concept modeled is just the same, but we avoid useless complexity.
	Doing so min and max hour becomes min and max time*/
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
	minimum: one Int, //NB = minimum time required to make a break
}{
	minus[endingTime, startingTime]>=minimum
	minimum>0
}

sig Event extends GenericEvent{
	type: one TypeOfEvent,
	feasiblePaths: set Travel,
	departureLocation: one Location,
	eventLocation: one Location,
	travelTime: one Int, //Rapresent the minimum travel time reqired
	/* descriptive variables are omitted, the variable prevLocChoice is
	omitted cause it's only an operative variable and it would not enrich the model */
} {
	 #feasiblePaths>=0
	(eventLocation= departureLocation or isScheduled=False)implies (#feasiblePaths=0 and travelTime=0 )else (#feasiblePaths>0 and travelTime>0) 
}

sig Travel{
	composed: seq TravelComponent,
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
	address: one StringModel,
}{
	not latitude=longitude
}

//Float abstraction
sig Float {}

sig Email{}

/******************FACTS******************/

// any user's email must be univocal
fact email_Is_Unique{
	no disjoint u, u' : User | u.email = u'.email
}

// There must not exists only a location corresponding to a latitude and a longitude
fact location_Is_Unique{
	no disjoint l, l' : Location | l.latitude=l'.latitude and l.longitude=l'.longitude and l.address=l'.address
}

//All travels must have a connected path and lead to the destination
fact travelsAlwaysLeadToDestination{
	all e: Event, travel: Travel  | (	travel in e.feasiblePaths) implies (let sequence=travel.composed |
		(sequence.first.departureLocation=e.departureLocation 
			and sequence.last.arrivalLocation= e.eventLocation
			and ( #sequence=2 implies sequence[1].departureLocation=sequence[0].arrivalLocation)
			and (no element: sequence.rest.butlast.elems | element.departureLocation=e.departureLocation)
			and (no element: sequence.rest.butlast.elems | element.departureLocation=e.eventLocation)
			and (no element: sequence.rest.butlast.elems | element.arrivalLocation=e.departureLocation)
			and (no element: sequence.rest.butlast.elems | element.arrivalLocation=e.eventLocation)
			and (all element: sequence.rest.butlast.elems | let i=sequence.indsOf[element]|
				 (sequence[minus[i,1]].arrivalLocation=element.departureLocation)and (sequence[plus[i,1]].departureLocation=element.arrivalLocation))
		 )  )
}

// Into an event two proposed paths must not be identical
fact noTravelsWithSameTravelComponents{
	all e: Event, travel: e.feasiblePaths  | (no duplicate:(e.feasiblePaths-travel) | (travel.composed = duplicate.composed) )  
}

fact noTypeOfEventWithoutUser{
	no type: TypeOfEvent | (all u: User | !(type in u.preferences))
}

fact noEventWithoutUser{
	no event: Event | (all u: User | !(event in u.plan))
}

fact noBreakEventWithoutUser{
	no break: BreakEvent | (all u: User | !(break in u.breaks))
}

fact noTravelsWithoutEvent{
	no t:Travel | ( all e:Event | !( t in (e.feasiblePaths)) )
}

fact noTravelsComponentWithoutTravel{
	no c:TravelComponent | ( all t:Travel | !( c in univ.( t.composed)) )
}
fact noConstraintWithoutTypeOfEvent{
	no c:Constraint | ( all t:TypeOfEvent | !( c in  t.isLimitedBy) )
}

//No two coinciding but distinct locations
fact noLocationOverlapping {
	no disj l, l1: Location | ( l.longitude = l1.longitude and l.latitude = l1.latitude )
}

//returns the maximum time at which the user must start travelling
fun relativeStart[e:Event]: one Int{
	minus[e.startingTime, e.travelTime]
}
//events and their travels times doesn't overlap (if they are scheduled) with other events.
fact noScheduledEventsOverlapping{
	all u:User |(all e1:u.plan | (no e2:(u.plan-e1)|(e1.isScheduled=True and e2.isScheduled=True and
		((relativeStart[e1]>=relativeStart[e2] and relativeStart[e1]=<e2.endingTime)or(e1.endingTime>=relativeStart[e2] and e1.endingTime<=e2.endingTime)))))
}

//Forall scheduled breaks is always granted the minumum break time
fact breakEventAlwaysGranted{
	all u:User | (all break: BreakEvent | ( ((break in u.breaks) and break.isScheduled=True)
		implies( no event1: u.plan | (event1.isScheduled=True and 
		no event2: u.plan | ( event2.isScheduled=True and 
			let precedent= event1.endingTime |  all travel2: Travel | (travel2 in event2.feasiblePaths) and let successor=event2.startingTime | 
			(precedent>break.startingTime or successor>break.endingTime) implies
				 (minus[successor,precedent] < break.minimum)
				 )  )   )     )      )
}

fact allTravelsRespectDeactivateConstraints{
	all event: Event | ( no travelComponent:event.feasiblePaths.composed.elems|( travelComponent.meanUsed in event.type.deactivate ) )
}


fact allTravelsRespectDistanceConstraints{
	all event: Event | all travelComponent:event.feasiblePaths.composed.elems |
			 ( all constraint: (event.type.isLimitedBy & DistanceConstraint) | (
					(constraint.concerns=travelComponent.meanUsed) implies
			 			(travelComponent.travelDistance>=constraint.minLenght and travelComponent.travelDistance<=constraint.maxLenght)
			))
}

fact allTravelsRespectPeriodConstraints{
	all event: Event | all travelComponent:event.feasiblePaths.composed.elems |
			 (all constraint: (event.type.isLimitedBy & PeriodOfDayConstraint) | (
					(constraint.concerns=travelComponent.meanUsed) implies(
						(travelComponent.endingTime<=constraint.maxTime) and
						(travelComponent.startingTime>=constraint.minTime) ) )
			)
}



/******************PREDICATES******************/

pred addEvent[u:User, e:Event, u':User]{
	//preCondition
	not(e in u.plan)
	//postCondition
	u.name=u'.name
	u.surname=u'.surname
	//u.email=u'.email
	u.preferences=u'.preferences
	u.breaks=u'.breaks
	u.hold=u'.hold
	(u.plan + e)=u'.plan
	u.preferredLocations=u'.preferredLocations
	
}

pred changeTravel[e:Event,travel:Travel, e':Event]{
	//precondition
	not ( travel in e.feasiblePaths)
	//postconditions
	e.type = e'.type
	(e.feasiblePaths +travel) = e'.feasiblePaths
	e.departureLocation = e'.departureLocation
	e.eventLocation = e'.eventLocation
}

pred changeEventPreferences[event:Event, type2:TypeOfEvent, event':Event ]{
	//precondition
	event.type != type2
	//postconditions
	event'.type =type2
	(event'.feasiblePaths & event.feasiblePaths) = none // if preferences are totally change I expect others paths
	event'.departureLocation = event.departureLocation
	event'.eventLocation = event.eventLocation
}
run addEvent
run changeTravel
run changeEventPreferences

pred complexTravels{ 
# Event=2
#DistanceConstraint>0
#TypeOfEvent=1
all event:Event| #event.type.deactivate>0
all a:GenericEvent| a.isScheduled=True
all a:Event| a.travelTime>0
}
pred show{ }

run complexTravels for 5 but 1 User

