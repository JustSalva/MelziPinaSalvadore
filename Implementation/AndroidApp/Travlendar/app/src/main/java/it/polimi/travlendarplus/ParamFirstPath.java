package it.polimi.travlendarplus;


/**
 * Enum containing values indicating the type of path preferred by the user.
 */
public enum ParamFirstPath {
    MIN_COST ("Minimum cost"),
    MIN_LENGTH ("Minimum length"),
    MIN_TIME ("Minimum time"),
    ECO_PATH ("Eco-friendly");

    private String text;

    ParamFirstPath(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ParamFirstPath getEnumFromString(String text) {
        switch (text) {
            case "Minimum cost":
                return MIN_COST;
            case "Minimum length":
                return MIN_LENGTH;
            case "Minimum time":
                return MIN_TIME;
            default:
                return ECO_PATH;
        }
    }
}
