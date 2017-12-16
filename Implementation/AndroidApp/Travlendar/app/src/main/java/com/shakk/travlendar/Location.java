package com.shakk.travlendar;


public class Location {

    private String name;
    private Position location;

    public String getName() {
        return name;
    }

    public Position getLocation() {
        return location;
    }

    public class Position {
        private float latitude;
        private float longitude;
        private String address;

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public String getAddress() {
            return address;
        }
    }
}
