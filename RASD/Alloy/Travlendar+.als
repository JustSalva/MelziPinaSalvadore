open util/integer
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

sig DIstanceConstraint extends Constraint{
	maxLenght: one Int,
	minLenght: one Int
}{
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
	maxHour >= 0 and maxHour =< 24
	minHour >= 0 and minHour =< 24
	maxHour > minHour
}

sig Ticket{
	//TODO
}

sig TravelMean{
	//TODO
}

sig BreakEvent{
	//TODO
}

sig Event{
	//TODO
}
sig Date{ }

/*fact NoBreak{
	all u: User  |  #u.breaks=1
}*/

pred show{ }

run show for 3 
