package edu.unicauca.concesionaria.Model;

import lombok.Data;

import java.util.Date;

@Data
public class clsSalesGoal {

    private int attSalesGoalId;
    private clsEmployee attEmployee;

    private String attGoalType;
    private double attTargetValue;
    private Date attStartDate;
    private Date attEndDate;
    private String attState;

    private double attAchievedValue;
    private double attCompliancePercentage;
}
