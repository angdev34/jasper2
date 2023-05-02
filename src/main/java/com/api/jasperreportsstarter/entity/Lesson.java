package com.api.jasperreportsstarter.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    private String courseName;
    private String instructorName;
    private String day;
    private String startTime;
    private String endTime;


}

