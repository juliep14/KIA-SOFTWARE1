package edu.unicauca.concesionaria.Model;

import lombok.Data;

import java.util.Date;

@Data
public class clsEmployee {

    private int attEmployeeId;
    private String attName;
    private String attPhone;
    private double attSalary;
    private Date attHireDate;
    private String attRole;
    private String attState;

}
