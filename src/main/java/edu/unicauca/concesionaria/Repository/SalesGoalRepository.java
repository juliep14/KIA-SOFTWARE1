package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SalesGoalRepository extends BaseRepository {

    private static final String SQL_INSERT = """
            INSERT INTO TBL_SALESGOAL
                (SGL_ID, EMP_ID, SGL_GOAL_TYPE, SGL_TARGET_VALUE, SGL_START_DATE, SGL_END_DATE, SGL_STATE)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE TBL_SALESGOAL
               SET SGL_TARGET_VALUE = ?, SGL_START_DATE = ?, SGL_GOAL_TYPE = ?, SGL_END_DATE = ?, SGL_STATE = ?
             WHERE SGL_ID = ?
            """;

    private static final String SQL_EXIST = "SELECT COUNT(*) FROM TBL_SALESGOAL WHERE SGL_ID = ?";

    private static final String SQL_ACTIVE_COUNT_BY_EMPLOYEE = """
            SELECT COUNT(*)
              FROM TBL_SALESGOAL
             WHERE EMP_ID = ? AND LOWER(TRIM(SGL_STATE)) = 'active'
            """;
   
    private static final String SQL_BY_ID = """
            SELECT SGL_ID, EMP_ID, SGL_GOAL_TYPE, SGL_TARGET_VALUE, SGL_START_DATE, SGL_END_DATE, SGL_STATE
              FROM TBL_SALESGOAL
             WHERE SGL_ID = ?
            """;

    private static final String SQL_ALL = """
            SELECT SGL_ID, EMP_ID, SGL_GOAL_TYPE, SGL_TARGET_VALUE, SGL_START_DATE, SGL_END_DATE, SGL_STATE
              FROM TBL_SALESGOAL
             ORDER BY SGL_ID
            """;

    private static final String SQL_SALES_TOTAL_DATE_RANGE = """
            SELECT NVL(SUM(SALE_PRICE), 0)
              FROM TBL_SALE
             WHERE EMP_ID = ?
               AND LOWER(TRIM(SALE_STATUS)) = 'confirmed'
               AND TRUNC(SALE_DATE_START) >= TRUNC(?)
               AND TRUNC(SALE_DATE_START) <= TRUNC(?)
            """;

    private static final String SQL_SALES_TOTAL_ALL = """
            SELECT NVL(SUM(SALE_PRICE), 0)
              FROM TBL_SALE
             WHERE EMP_ID = ?
               AND LOWER(TRIM(SALE_STATUS)) = 'confirmed'
            """;

    private final JdbcTemplate attJdbcTemplate;

    public SalesGoalRepository(JdbcTemplate prmJdbcTemplate) {
        this.attJdbcTemplate = prmJdbcTemplate;
    }

    private clsSalesGoal opRowToSalesGoal(ResultSet prmRs) throws SQLException {
        clsSalesGoal varSalesGoal = new clsSalesGoal();
        varSalesGoal.setAttSalesGoalId(prmRs.getInt("SGL_ID"));

        int varEmpId = prmRs.getInt("EMP_ID");
        if (!prmRs.wasNull()) {
            clsEmployee varEmp = new clsEmployee();
            varEmp.setAttEmployeeId(varEmpId);
            varSalesGoal.setAttEmployee(varEmp);
        }

        varSalesGoal.setAttGoalType(prmRs.getString("SGL_GOAL_TYPE"));
        varSalesGoal.setAttTargetValue(prmRs.getDouble("SGL_TARGET_VALUE"));
        varSalesGoal.setAttStartDate(prmRs.getDate("SGL_START_DATE"));
        varSalesGoal.setAttEndDate(prmRs.getDate("SGL_END_DATE"));
        varSalesGoal.setAttState(prmRs.getString("SGL_STATE"));
        return varSalesGoal;
    }

    private RowMapper<clsSalesGoal> opSalesGoalRowMapper() {
        return (rs, rowNum) -> opRowToSalesGoal(rs);
    }

    public int opRegisterSalesGoal(clsSalesGoal prmSalesGoal) {
        try {
            Integer varEmpId = prmSalesGoal.getAttEmployee() != null
                    ? prmSalesGoal.getAttEmployee().getAttEmployeeId()
                    : null;

            attJdbcTemplate.update(SQL_INSERT,
                    prmSalesGoal.getAttSalesGoalId(),
                    varEmpId,
                    prmSalesGoal.getAttGoalType(),
                    prmSalesGoal.getAttTargetValue(),
                    prmSalesGoal.getAttStartDate(),
                    prmSalesGoal.getAttEndDate(),
                    prmSalesGoal.getAttState());

            return prmSalesGoal.getAttSalesGoalId();
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public void opUpdateSalesGoal(clsSalesGoal prmSalesGoal) {
        try {
            attJdbcTemplate.update(SQL_UPDATE,
                    prmSalesGoal.getAttTargetValue(),
                    prmSalesGoal.getAttStartDate(),
                    prmSalesGoal.getAttGoalType(),
                    prmSalesGoal.getAttEndDate(),
                    prmSalesGoal.getAttState(),
                    prmSalesGoal.getAttSalesGoalId());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public boolean opSalesGoalExist(int prmId) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_EXIST, Integer.class, prmId);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public boolean opHasActiveGoal(int prmEmpId) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_ACTIVE_COUNT_BY_EMPLOYEE, Integer.class, prmEmpId);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public clsSalesGoal opGetSalesGoalById(int prmId) {
        try {
            List<clsSalesGoal> varList = attJdbcTemplate.query(SQL_BY_ID, opSalesGoalRowMapper(), prmId);
            return varList.isEmpty() ? null : varList.get(0);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public List<clsSalesGoal> opGetAllSalesGoals() {
        try {
            return attJdbcTemplate.query(SQL_ALL, opSalesGoalRowMapper());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public double opGetAchievedSales(int prmEmpId, java.util.Date prmStartDate, java.util.Date prmEndDate) {
        try {
            Double varSum;
            if (prmStartDate != null && prmEndDate != null) {
                varSum = attJdbcTemplate.queryForObject(SQL_SALES_TOTAL_DATE_RANGE, Double.class,
                        prmEmpId, prmStartDate, prmEndDate);
            } else {
                varSum = attJdbcTemplate.queryForObject(SQL_SALES_TOTAL_ALL, Double.class, prmEmpId);
            }
            return varSum != null ? varSum : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
