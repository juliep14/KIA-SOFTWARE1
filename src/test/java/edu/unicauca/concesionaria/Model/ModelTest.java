package edu.unicauca.concesionaria.Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    @DisplayName("Debe verificar getters, setters, equals, hashCode y toString de clsEmployee")
    void testClsEmployee() {
        clsEmployee emp1 = new clsEmployee();
        emp1.setAttEmployeeId(101);
        emp1.setAttName("Carlos");
        emp1.setAttPhone("3001234567");
        emp1.setAttSalary(2500000.0);
        Date hireDate = Date.valueOf("2023-01-01");
        emp1.setAttHireDate(hireDate);
        emp1.setAttRole("seller");
        emp1.setAttState("active");

        assertEquals(101, emp1.getAttEmployeeId());
        assertEquals("Carlos", emp1.getAttName());
        assertEquals("3001234567", emp1.getAttPhone());
        assertEquals(2500000.0, emp1.getAttSalary());
        assertEquals(hireDate, emp1.getAttHireDate());
        assertEquals("seller", emp1.getAttRole());
        assertEquals("active", emp1.getAttState());

        clsEmployee emp2 = new clsEmployee();
        emp2.setAttEmployeeId(101);
        emp2.setAttName("Carlos");
        emp2.setAttPhone("3001234567");
        emp2.setAttSalary(2500000.0);
        emp2.setAttHireDate(hireDate);
        emp2.setAttRole("seller");
        emp2.setAttState("active");

        assertEquals(emp1, emp2);
        assertEquals(emp1.hashCode(), emp2.hashCode());
        assertTrue(emp1.toString().contains("Carlos"));
    }

    @Test
    @DisplayName("Debe verificar getters, setters, equals, hashCode y toString de clsCustomer")
    void testClsCustomer() {
        clsCustomer cus1 = new clsCustomer();
        cus1.setAttCustomerId(1);
        cus1.setAttName("Ana");
        cus1.setAttPhone("3101234567");
        cus1.setAttEmail("ana@test.com");
        cus1.setAttState("active");

        assertEquals(1, cus1.getAttCustomerId());
        assertEquals("Ana", cus1.getAttName());
        assertEquals("3101234567", cus1.getAttPhone());
        assertEquals("ana@test.com", cus1.getAttEmail());
        assertEquals("active", cus1.getAttState());

        clsCustomer cus2 = new clsCustomer();
        cus2.setAttCustomerId(1);
        cus2.setAttName("Ana");
        cus2.setAttPhone("3101234567");
        cus2.setAttEmail("ana@test.com");
        cus2.setAttState("active");

        assertEquals(cus1, cus2);
        assertEquals(cus1.hashCode(), cus2.hashCode());
        assertTrue(cus1.toString().contains("Ana"));
    }

    @Test
    @DisplayName("Debe verificar getters, setters, equals, hashCode y toString de clsSalesGoal")
    void testClsSalesGoal() {
        clsEmployee emp = new clsEmployee();
        emp.setAttEmployeeId(101);

        clsSalesGoal goal1 = new clsSalesGoal();
        goal1.setAttSalesGoalId(10);
        goal1.setAttEmployee(emp);
        goal1.setAttGoalType("monthly");
        goal1.setAttTargetValue(50000000.0);
        Date start = Date.valueOf("2024-01-01");
        Date end = Date.valueOf("2024-01-31");
        goal1.setAttStartDate(start);
        goal1.setAttEndDate(end);
        goal1.setAttState("active");
        goal1.setAttAchievedValue(25000000.0);
        goal1.setAttCompliancePercentage(50.0);

        assertEquals(10, goal1.getAttSalesGoalId());
        assertEquals(emp, goal1.getAttEmployee());
        assertEquals("monthly", goal1.getAttGoalType());
        assertEquals(50000000.0, goal1.getAttTargetValue());
        assertEquals(start, goal1.getAttStartDate());
        assertEquals(end, goal1.getAttEndDate());
        assertEquals("active", goal1.getAttState());
        assertEquals(25000000.0, goal1.getAttAchievedValue());
        assertEquals(50.0, goal1.getAttCompliancePercentage());

        clsSalesGoal goal2 = new clsSalesGoal();
        goal2.setAttSalesGoalId(10);
        goal2.setAttEmployee(emp);
        goal2.setAttGoalType("monthly");
        goal2.setAttTargetValue(50000000.0);
        goal2.setAttStartDate(start);
        goal2.setAttEndDate(end);
        goal2.setAttState("active");
        goal2.setAttAchievedValue(25000000.0);
        goal2.setAttCompliancePercentage(50.0);

        assertEquals(goal1, goal2);
        assertEquals(goal1.hashCode(), goal2.hashCode());
        assertTrue(goal1.toString().contains("monthly"));
    }
}

