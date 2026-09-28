package com.traineta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AutocompleteSearchResponse {

    private Boolean success;
    private Object data;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationSearchResult {
        private String code;
        private String name;
        private String city;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainSearchResult {
        private String number;
        private String name;
        private String source;
        private String destination;

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
    }
}
