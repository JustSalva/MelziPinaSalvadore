package it.polimi.travlendarplus;


/**
 * Enum containing travel means that can be used by the user.
 */
public enum TravelMeanEnum {
    BIKE( "Bike" ),
    BUS( "Bus" ),
    BY_FOOT( "By Foot" ),
    CAR( "Car" ),
    SUBWAY( "Subway" ),
    TRAIN( "Train" ),
    TRAM( "Tram" ),
    SHARING_BIKE( "Sharing Bike" ),
    SHARING_CAR( "Sharing Car" );

    private final String travelMean;

    TravelMeanEnum ( String travelMean ) {
        this.travelMean = travelMean;
    }

    public static TravelMeanEnum getEnumFromString ( String text ) {
        switch ( text ) {
            case "Bike":
                return BIKE;
            case "Bus":
                return BUS;
            case "By Foot":
                return BY_FOOT;
            case "Car":
                return CAR;
            case "Subway":
                return SUBWAY;
            case "Train":
                return TRAIN;
            case "Tram":
                return TRAM;
            case "Sharing Bike":
                return SHARING_BIKE;
            default:
                return SHARING_CAR;
        }
    }

    public String getTravelMean () {
        return travelMean;
    }
}
