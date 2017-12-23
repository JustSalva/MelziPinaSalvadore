package it.polimi.travlendarplus.entities.travelMeans;

/**
 * Enum that represent all supported travel means in Travlendar+ application
 */
public enum TravelMeanEnum {
    CAR( "driving" ),
    BIKE( "bicycling" ),
    SUBWAY( "subway" ),
    BUS( "bus" ),
    TRAIN( "train" ),
    TRAM( "tram" ),
    BY_FOOT( "walking" ),
    SHARING_CAR( "driving" ),
    SHARING_BIKE( "bicycling" ),
    OTHER( "other" );// = not classified travel means ( ex. plane is not yet supported )

    private final String gMapsParam;

    TravelMeanEnum ( String param ) {
        gMapsParam = param;
    }

    /**
     * Check the consistency of a travel mean
     *
     * @param travelMean mean to be checked
     * @return true if consistent, false otherwise
     */
    public static boolean isValid ( TravelMeanEnum travelMean ) {
        return ( isPublicTravelMean( travelMean ) ||
                travelMean == TravelMeanEnum.CAR ||
                travelMean == TravelMeanEnum.BIKE ||
                travelMean == TravelMeanEnum.BY_FOOT ||
                travelMean == TravelMeanEnum.SHARING_CAR ||
                travelMean == TravelMeanEnum.SHARING_BIKE ||
                travelMean == TravelMeanEnum.OTHER );
    }

    /**
     * Check if a travel mean is public
     *
     * @param travelMean mean to be checked
     * @return true if a public travel mean, false otherwise
     */
    public static boolean isPublicTravelMean ( TravelMeanEnum travelMean ) {
        return ( travelMean == TravelMeanEnum.SUBWAY ||
                travelMean == TravelMeanEnum.BUS ||
                travelMean == TravelMeanEnum.TRAIN ||
                travelMean == TravelMeanEnum.TRAM );
    }

    public String getParam () {
        return gMapsParam;
    }
}
