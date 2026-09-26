package com.example.traveljava.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.util.List;

@Data
public class travelRecommendVO {
    private  Boolean success;
    private  String city;
    private Integer days;
    private Double totalBudget;
    private List<DailyItinerary> dailyItinerary;
    @JsonProperty("budgetBreakdown")
    private budgetBreakDown budgetBreakDown;
    private List<String> tips;
    private List<String> warnings;
    private String error;
    private String rawResponse;

    @Data
    public static class DailyItinerary {
        private Integer day;
        private String date;
        private TimeSlot  morning;
        private TimeSlot afternoon;
        private TimeSlot evening;
    }

     //定义返morning 和 afternoon的属性类
    @Data
    public static class TimeSlot {
        private String spot;
        private String duration;
        private String transportation;
        private String description;
        private String ticket;
    }


    //budgetBreakDown 属性类
    @Data
    public static class budgetBreakDown {
        private Double accommodation;
        private Double food;
        private Double transportation;
        private Double tickets;
        private Double other;
    }
}
