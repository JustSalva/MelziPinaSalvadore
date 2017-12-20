package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

/**
 * This helper class provide static constants to be used in the fields-checking
 * process to communicate to the client which fields have been mistaken
 */
public class WrongFields {

    public static final String ADDRESS = "address";
    public static final String COST = " cost";
    public static final String DELTA_DAYS_MUST_BE_GREATER_THEN_ZERO =
            "deltaDays must be greater than zero";
    public static final String DELTA_DAYS_IS_LESS_THAN_THE_SLACK =
            "deltaDay value is less than the slack between start and end time";
    public static final String DISTANCE = " distance";
    public static final String DISTANCE_CONSTRAINT = " distanceConstraint ";
    public static final String EMAIL = "email";
    public static final String ID_DEVICE = "idDevice";
    public static final String INVALID_TICKET_DECORATOR = "invalid ticket decorator";
    public static final String LINE_NAME = " line name ";
    public static final String LOCATION_NOT_FOUND =
            "Location not found! Please specify a correct location.";
    public static final String MAX_HOUR_MUST_BE_LESS_THAN_24_HOURS =
            " max hour must be less than 24 h";
    public static final String MIN_HOUR_MUST_BE_LESS_THAN_MAX_HOUR =
            " min hour must be less than max hour";
    public static final String MIN_HOUR_MUST_BE_GREATER_THAN_ZERO =
            " min hour must be greater than zero";
    public static final String MIN_LENGTH_MUST_BE_GREATER_THAN_ZERO =
            " min length must be greater than zero";
    public static final String MIN_LENGTH_MUST_BE_LESS_THAN_MAX_LENGHT =
            " min length must be less than max length";
    public static final String MIN_TIME_MUST_BE_LESS_THAN_ENDING_TIME =
            " minimum time must be less than ending time";
    public static final String MIN_TIME_MUST_BE_LESS_THAN_SLACK =
            "minimum time must be less than the slack between start and ending time";
    public static final String NAME = "name";
    public static final String NO_PREVIOUS_LOCATION = "Not exists a previous location";
    public static final String NOT_ALLOWED_TRAVEL_MEAN_VALUE =
            " travel mean value not allowed";
    public static final String ONLY_FUTURE_EVENTS_ALLOWED =
            "only future events are allowed";
    public static final String PARAM_FIRST_PATH = "paramFirstPath";
    public static final String PASSWORD = "password";
    public static final String PATH_NOT_FEASIBLE = "path not feasible";
    public static final String PERIOD_CONSTRAINT = " periodConstraint ";
    public static final String START_TIME_GREATER_THAN_END_TIME =
            "starting time must be less than ending time";
    public static final String START_TIME_MUST_BE_LESS_THEN_END_TIME =
            "in a periodic event starting day must be less than ending day";
    public static final String SURNAME = "surname";
    public static final String TRAVEL_MEAN = "travel mean ";
    public static final String TYPE = "type";
    public static final String TYPE_OF_EVENT_NOT_FOUND = "TypeOfEvent not found";

}
