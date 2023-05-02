package com.api.jasperreportsstarter.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    private String studentID;
    private String name;
    private String midtermGrade;
    private String finalGrade;
    private String grade;
}

