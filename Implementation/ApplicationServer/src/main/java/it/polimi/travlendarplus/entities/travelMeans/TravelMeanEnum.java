package it.polimi.travlendarplus.entities.travelMeans;

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
    OTHER( "other" );//TODO what does it mean?
    //TODO others?

    private final String gMapsParam;

    private TravelMeanEnum ( String param ) {
        gMapsParam = param;
    }

    public static boolean isValid ( TravelMeanEnum travelMean ) {
        return ( isPublicTravelMean( travelMean ) ||
                travelMean == TravelMeanEnum.CAR ||
                travelMean == TravelMeanEnum.BIKE ||
                travelMean == TravelMeanEnum.BY_FOOT ||
                travelMean == TravelMeanEnum.SHARING_CAR ||
                travelMean == TravelMeanEnum.SHARING_BIKE ||
                travelMean == TravelMeanEnum.OTHER );
    }

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
