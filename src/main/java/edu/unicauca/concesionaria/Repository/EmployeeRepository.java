package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Model.clsEmployee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeRepository extends BaseRepository {

    private static final String SQL_INSERT = """
            INSERT INTO TBL_EMPLOYEE
                (EMP_ID, EMP_NAME, EMP_PHONE, EMP_SALARY, EMP_HIRE_DATE, EMP_ROLE, EMP_STATE)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE TBL_EMPLOYEE
               SET EMP_NAME = ?, EMP_PHONE = ?, EMP_SALARY = ?, EMP_STATE = ?
             WHERE EMP_ID = ?
            """;

    private static final String SQL_DISABLE = """
            UPDATE TBL_EMPLOYEE
               SET EMP_STATE = 'inactive'
             WHERE EMP_ID = ?
            """;

    private static final String SQL_EXIST = "SELECT COUNT(*) FROM TBL_EMPLOYEE WHERE EMP_ID = ?";

    private static final String SQL_MANAGER_COUNT = """
            SELECT COUNT(*)
              FROM TBL_EMPLOYEE
             WHERE UPPER(EMP_ROLE) = 'MANAGER'
            """;

    private static final String SQL_BY_ID = """
            SELECT EMP_ID, EMP_NAME, EMP_PHONE, EMP_SALARY, EMP_HIRE_DATE, EMP_ROLE, EMP_STATE
              FROM TBL_EMPLOYEE
             WHERE EMP_ID = ?
            """;

    private static final String SQL_ALL = """
            SELECT EMP_ID, EMP_NAME, EMP_PHONE, EMP_SALARY, EMP_HIRE_DATE, EMP_ROLE, EMP_STATE
              FROM TBL_EMPLOYEE
             ORDER BY EMP_ID
            """;

    private final JdbcTemplate attJdbcTemplate;

    public EmployeeRepository(JdbcTemplate prmJdbcTemplate) {
        this.attJdbcTemplate = prmJdbcTemplate;
    }

    private clsEmployee opRowToEmployee(ResultSet prmRs) throws SQLException {
        clsEmployee varEmployee = new clsEmployee();
        varEmployee.setAttEmployeeId(prmRs.getInt("EMP_ID"));
        varEmployee.setAttName(prmRs.getString("EMP_NAME"));
        varEmployee.setAttPhone(prmRs.getString("EMP_PHONE"));
        varEmployee.setAttSalary(prmRs.getDouble("EMP_SALARY"));
        varEmployee.setAttHireDate(prmRs.getDate("EMP_HIRE_DATE"));
        varEmployee.setAttRole(prmRs.getString("EMP_ROLE"));
        varEmployee.setAttState(prmRs.getString("EMP_STATE"));
        return varEmployee;
    }

    private RowMapper<clsEmployee> opEmployeeRowMapper() {
        return (rs, rowNum) -> opRowToEmployee(rs);
    }

    public void opRegisterEmployee(clsEmployee prmEmployee) {
        try {
            attJdbcTemplate.update(SQL_INSERT,
                    prmEmployee.getAttEmployeeId(),
                    prmEmployee.getAttName(),
                    prmEmployee.getAttPhone(),
                    prmEmployee.getAttSalary(),
                    prmEmployee.getAttHireDate(),
                    prmEmployee.getAttRole(),
                    prmEmployee.getAttState());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public void opUpdateEmployee(clsEmployee prmEmployee) {
        try {
            attJdbcTemplate.update(SQL_UPDATE,
                    prmEmployee.getAttName(),
                    prmEmployee.getAttPhone(),
                    prmEmployee.getAttSalary(),
                    prmEmployee.getAttState(),
                    prmEmployee.getAttEmployeeId());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public boolean opEmployeeExist(int prmId) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_EXIST, Integer.class, prmId);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public boolean opManagerExists() {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_MANAGER_COUNT, Integer.class);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public clsEmployee opGetEmployeeById(int prmId) {
        try {
            List<clsEmployee> varList = attJdbcTemplate.query(SQL_BY_ID, opEmployeeRowMapper(), prmId);
            return varList.isEmpty() ? null : varList.get(0);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public List<clsEmployee> opGetAllEmployees() {
        try {
            return attJdbcTemplate.query(SQL_ALL, opEmployeeRowMapper());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
}
