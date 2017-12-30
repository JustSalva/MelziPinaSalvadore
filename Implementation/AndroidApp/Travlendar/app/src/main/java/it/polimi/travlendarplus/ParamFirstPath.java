package it.polimi.travlendarplus;


/**
 * Enum containing values indicating the type of path preferred by the user.
 */
public enum ParamFirstPath {
    MIN_LENGTH( "Minimum length" ),
    MIN_TIME( "Minimum time" ),;

    private String text;

    ParamFirstPath ( String text ) {
        this.text = text;
    }

    public static ParamFirstPath getEnumFromString ( String text ) {
        switch ( text ) {
            case "Minimum length":
                return MIN_LENGTH;
            default:
                return MIN_TIME;
        }
    }

    public String getText () {
        return text;
    }
}
