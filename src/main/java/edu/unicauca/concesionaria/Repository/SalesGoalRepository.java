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
               SET SGL_TARGET_VALUE = ?, SGL_GOAL_TYPE = ?, SGL_END_DATE = ?
             WHERE SGL_ID = ?
            """;

    private static final String SQL_DISABLE = """
            UPDATE TBL_SALESGOAL
               SET SGL_STATE = 'inactive'
             WHERE SGL_ID = ?
            """;

    private static final String SQL_COMPLETE = """
            UPDATE TBL_SALESGOAL
               SET SGL_STATE = 'complete'
             WHERE SGL_ID = ?
            """;

    private static final String SQL_EXIST = "SELECT COUNT(*) FROM TBL_SALESGOAL WHERE SGL_ID = ?";

    private static final String SQL_ACTIVE_COUNT_BY_EMPLOYEE = """
            SELECT COUNT(*)
              FROM TBL_SALESGOAL
             WHERE EMP_ID = ? AND LOWER(TRIM(SGL_STATE)) = 'active'
            """;

    private static final String SQL_NEXT_ID = "SELECT NVL(MAX(SGL_ID), 500) + 1 FROM TBL_SALESGOAL";

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

    private static final String SQL_BY_STATE = """
            SELECT SGL_ID, EMP_ID, SGL_GOAL_TYPE, SGL_TARGET_VALUE, SGL_START_DATE, SGL_END_DATE, SGL_STATE
              FROM TBL_SALESGOAL
             WHERE LOWER(TRIM(SGL_STATE)) = LOWER(TRIM(?))
             ORDER BY SGL_ID
            """;

    private static final String SQL_BY_EMPLOYEE = """
            SELECT SGL_ID, EMP_ID, SGL_GOAL_TYPE, SGL_TARGET_VALUE, SGL_START_DATE, SGL_END_DATE, SGL_STATE
              FROM TBL_SALESGOAL
             WHERE EMP_ID = ?
             ORDER BY SGL_ID
            """;

    private static final String SQL_ACTIVE_BY_EMPLOYEE = """
            SELECT SGL_ID, EMP_ID, SGL_GOAL_TYPE, SGL_TARGET_VALUE, SGL_START_DATE, SGL_END_DATE, SGL_STATE
              FROM TBL_SALESGOAL
             WHERE EMP_ID = ? AND LOWER(TRIM(SGL_STATE)) = 'active'
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
            if (prmSalesGoal.getAttSalesGoalId() <= 0) {
                Integer varNextId = attJdbcTemplate.queryForObject(SQL_NEXT_ID, Integer.class);
                prmSalesGoal.setAttSalesGoalId(varNextId != null ? varNextId : 501);
            }

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
                    prmSalesGoal.getAttGoalType(),
                    prmSalesGoal.getAttEndDate(),
                    prmSalesGoal.getAttSalesGoalId());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public void opDisableSalesGoal(int prmId) {
        try {
            attJdbcTemplate.update(SQL_DISABLE, prmId);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public void opCompleteSalesGoal(int prmId) {
        try {
            attJdbcTemplate.update(SQL_COMPLETE, prmId);
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

    public List<clsSalesGoal> opGetSalesGoalsByState(String prmState) {
        try {
            return attJdbcTemplate.query(SQL_BY_STATE, opSalesGoalRowMapper(), prmState);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public List<clsSalesGoal> opGetSalesGoalsByEmployeeId(int prmEmpId) {
        try {
            return attJdbcTemplate.query(SQL_BY_EMPLOYEE, opSalesGoalRowMapper(), prmEmpId);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public clsSalesGoal opGetActiveGoalByEmployeeId(int prmEmpId) {
        try {
            List<clsSalesGoal> varList = attJdbcTemplate.query(SQL_ACTIVE_BY_EMPLOYEE, opSalesGoalRowMapper(), prmEmpId);
            return varList.isEmpty() ? null : varList.get(0);
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
