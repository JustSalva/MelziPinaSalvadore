package com.shakk.travlendar;


public class Location {

    private String name;
    private Position location;

    public Location(String name, Position location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Position getLocation() {
        return location;
    }

    public static class Position {
        private float latitude;
        private float longitude;
        private String address;

        public Position(String address) {
            this.address = address;
        }

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
