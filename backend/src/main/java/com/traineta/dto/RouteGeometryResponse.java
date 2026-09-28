package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteGeometryResponse {

    private Boolean success;
    private GeometryData data;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public GeometryData getData() { return data; }
    public void setData(GeometryData data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeometryData {
        private String trainNumber;
        private String format;
        private GeoJsonObject geojson;
        private List<RouteStop> stops;

        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }

        public GeoJsonObject getGeojson() { return geojson; }
        public void setGeojson(GeoJsonObject geojson) { this.geojson = geojson; }

        public List<RouteStop> getStops() { return stops; }
        public void setStops(List<RouteStop> stops) { this.stops = stops; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoJsonObject {
        private String type;
        private GeoProperties properties;
        private GeoGeometry geometry;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public GeoProperties getProperties() { return properties; }
        public void setProperties(GeoProperties properties) { this.properties = properties; }

        public GeoGeometry getGeometry() { return geometry; }
        public void setGeometry(GeoGeometry geometry) { this.geometry = geometry; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoProperties {
        private String trainNumber;

        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoGeometry {
        private String type;
        private List<List<Double>> coordinates; // [[lng, lat], [lng, lat], ...]

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public List<List<Double>> getCoordinates() { return coordinates; }
        public void setCoordinates(List<List<Double>> coordinates) { this.coordinates = coordinates; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouteStop {
        private Integer sequence;
        private String code;
        private String name;
        private Double lat;
        private Double lng;

        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }

        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
    }
}
