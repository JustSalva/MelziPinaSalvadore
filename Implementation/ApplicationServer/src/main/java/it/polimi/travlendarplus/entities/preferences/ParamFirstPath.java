package it.polimi.travlendarplus.entities.preferences;

/**
 * Enum that represent the priority given to path calculation in a specific type of event
 */
public enum ParamFirstPath {
    /**
     * Priority that advantage less costly paths
     */
    MIN_COST,

    /**
     * Priority that advantage less lengthy paths
     */
    MIN_LENGTH,

    /**
     * Priority that advantage shorter (in time) paths
     */
    MIN_TIME,

    /**
     * Priority that advantage the most ecologic paths
     */
    ECO_PATH;
}
