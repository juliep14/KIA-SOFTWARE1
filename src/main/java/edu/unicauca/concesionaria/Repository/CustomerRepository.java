package edu.unicauca.concesionaria.Repository;

import edu.unicauca.concesionaria.Model.clsCustomer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerRepository extends BaseRepository {

    private static final String SQL_INSERT = """
            INSERT INTO TBL_CUSTOMER (CUS_ID, CUS_NAME, CUS_PHONE, CUS_EMAIL, CUS_STATE)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE TBL_CUSTOMER
               SET CUS_NAME = ?, CUS_PHONE = ?, CUS_EMAIL = ?
             WHERE CUS_ID = ?
            """;

    private static final String SQL_DISABLE = """
            UPDATE TBL_CUSTOMER
               SET CUS_STATE = 'inactive'
             WHERE CUS_ID = ?
            """;

    private static final String SQL_EXIST = "SELECT COUNT(*) FROM TBL_CUSTOMER WHERE CUS_ID = ?";

    private static final String SQL_BY_ID = """
            SELECT CUS_ID, CUS_NAME, CUS_PHONE, CUS_EMAIL, CUS_STATE
              FROM TBL_CUSTOMER
             WHERE CUS_ID = ?
            """;

    private static final String SQL_ALL = """
            SELECT CUS_ID, CUS_NAME, CUS_PHONE, CUS_EMAIL, CUS_STATE
              FROM TBL_CUSTOMER
             ORDER BY CUS_ID
            """;

    private final JdbcTemplate attJdbcTemplate;

    public CustomerRepository(JdbcTemplate prmJdbcTemplate) {
        this.attJdbcTemplate = prmJdbcTemplate;
    }

    private clsCustomer opRowToCustomer(ResultSet prmRs) throws SQLException {
        clsCustomer varCustomer = new clsCustomer();
        varCustomer.setAttCustomerId(prmRs.getInt("CUS_ID"));
        varCustomer.setAttName(prmRs.getString("CUS_NAME"));
        varCustomer.setAttPhone(prmRs.getString("CUS_PHONE"));
        varCustomer.setAttEmail(prmRs.getString("CUS_EMAIL"));
        varCustomer.setAttState(prmRs.getString("CUS_STATE"));
        return varCustomer;
    }

    private RowMapper<clsCustomer> opCustomerRowMapper() {
        return (rs, rowNum) -> opRowToCustomer(rs);
    }

    public void opRegisterCustomer(clsCustomer prmCustomer) {
        try {
            attJdbcTemplate.update(SQL_INSERT,
                    prmCustomer.getAttCustomerId(),
                    prmCustomer.getAttName(),
                    prmCustomer.getAttPhone(),
                    prmCustomer.getAttEmail(),
                    prmCustomer.getAttState());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public void opUpdateCustomer(clsCustomer prmCustomer) {
        try {
            attJdbcTemplate.update(SQL_UPDATE,
                    prmCustomer.getAttName(),
                    prmCustomer.getAttPhone(),
                    prmCustomer.getAttEmail(),
                    prmCustomer.getAttCustomerId());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public void opDisableCustomer(int prmId) {
        try {
            attJdbcTemplate.update(SQL_DISABLE, prmId);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public boolean opCustomerExist(int prmId) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_EXIST, Integer.class, prmId);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public clsCustomer opGetCustomerById(int prmId) {
        try {
            List<clsCustomer> varList = attJdbcTemplate.query(SQL_BY_ID, opCustomerRowMapper(), prmId);
            return varList.isEmpty() ? null : varList.get(0);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }

    public List<clsCustomer> opGetAllCustomers() {
        try {
            return attJdbcTemplate.query(SQL_ALL, opCustomerRowMapper());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
}
