package it.polimi.travlendarplus.entities.travelMeans;

public enum TravelMeanEnum {
    CAR ("driving"),
    BIKE ("bicycling"),
    SUBWAY ("subway"),
    BUS ("bus"),
    TRAIN ("train"),
    TRAM ("tram"),
    BY_FOOT ("walking"),
    SHARING_CAR ("driving"),
    SHARING_BIKE ("bicycling");
    //TODO others?

    private final String gMapsParam;

    private TravelMeanEnum(String param) {
        gMapsParam = param;
    }

    public String getParam() {
        return gMapsParam;
    }
}
