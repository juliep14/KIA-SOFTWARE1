package edu.unicauca.concesionaria.Repository;
 
import edu.unicauca.concesionaria.Model.clsVehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
 
@Repository
public class VehicleRepository extends BaseRepository {
 
    private static final String SQL_INSERT = """
            INSERT INTO TBL_VEHICLE (VEH_ID, VEH_MODEL, VEH_YEAR, VEH_BODY_TYPE, VEH_FUEL_TYPE,
                                     VEH_CATEGORY, VEH_LICENSE_PLATE, VEH_MILEAGE, VEH_CONDITION,
                                     VEH_STATUS, VEH_COLOR)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
 
    private static final String SQL_UPDATE = """
            UPDATE TBL_VEHICLE
               SET VEH_MODEL = ?, VEH_YEAR = ?, VEH_BODY_TYPE = ?, VEH_FUEL_TYPE = ?,
                   VEH_CATEGORY = ?, VEH_LICENSE_PLATE = ?, VEH_MILEAGE = ?, VEH_CONDITION = ?,
                   VEH_STATUS = ?, VEH_COLOR = ?
             WHERE VEH_ID = ?
            """;
 
    private static final String SQL_UPDATE_STATUS = """
            UPDATE TBL_VEHICLE
               SET VEH_STATUS = ?
             WHERE VEH_ID = ?
            """;
 
    private static final String SQL_EXIST = "SELECT COUNT(*) FROM TBL_VEHICLE WHERE VEH_ID = ?";
 
    private static final String SQL_PLATE_EXIST =
            "SELECT COUNT(*) FROM TBL_VEHICLE WHERE VEH_LICENSE_PLATE = ?";
 
    private static final String SQL_PLATE_EXIST_OTHER =
            "SELECT COUNT(*) FROM TBL_VEHICLE WHERE VEH_LICENSE_PLATE = ? AND VEH_ID <> ?";
 
    private static final String SQL_BY_ID = """
            SELECT VEH_ID, VEH_MODEL, VEH_YEAR, VEH_BODY_TYPE, VEH_FUEL_TYPE, VEH_CATEGORY,
                   VEH_LICENSE_PLATE, VEH_MILEAGE, VEH_CONDITION, VEH_STATUS, VEH_COLOR
              FROM TBL_VEHICLE
             WHERE VEH_ID = ?
            """;
 
    private static final String SQL_ALL = """
            SELECT VEH_ID, VEH_MODEL, VEH_YEAR, VEH_BODY_TYPE, VEH_FUEL_TYPE, VEH_CATEGORY,
                   VEH_LICENSE_PLATE, VEH_MILEAGE, VEH_CONDITION, VEH_STATUS, VEH_COLOR
              FROM TBL_VEHICLE
             ORDER BY VEH_ID
            """;
 
    private final JdbcTemplate attJdbcTemplate;
 
    public VehicleRepository(JdbcTemplate prmJdbcTemplate) {
        this.attJdbcTemplate = prmJdbcTemplate;
    }
 
    private clsVehicle opRowToVehicle(ResultSet prmRs) throws SQLException {
        clsVehicle varVehicle = new clsVehicle();
        varVehicle.setAttVehicleId(prmRs.getInt("VEH_ID"));
        varVehicle.setAttModel(prmRs.getString("VEH_MODEL"));
 
        int varYear = prmRs.getInt("VEH_YEAR");
        varVehicle.setAttYear(prmRs.wasNull() ? null : varYear);
 
        varVehicle.setAttBodyType(prmRs.getString("VEH_BODY_TYPE"));
        varVehicle.setAttFuelType(prmRs.getString("VEH_FUEL_TYPE"));
        varVehicle.setAttCategory(prmRs.getString("VEH_CATEGORY"));
        varVehicle.setAttLicensePlate(prmRs.getString("VEH_LICENSE_PLATE"));
 
        double varMileage = prmRs.getDouble("VEH_MILEAGE");
        varVehicle.setAttMileage(prmRs.wasNull() ? null : varMileage);
 
        varVehicle.setAttCondition(prmRs.getString("VEH_CONDITION"));
        varVehicle.setAttStatus(prmRs.getString("VEH_STATUS"));
        varVehicle.setAttColor(prmRs.getString("VEH_COLOR"));
        return varVehicle;
    }
 
    private RowMapper<clsVehicle> opVehicleRowMapper() {
        return (rs, rowNum) -> opRowToVehicle(rs);
    }
 
    public void opRegisterVehicle(clsVehicle prmVehicle) {
        try {
            attJdbcTemplate.update(SQL_INSERT,
                    prmVehicle.getAttVehicleId(),
                    prmVehicle.getAttModel(),
                    prmVehicle.getAttYear(),
                    prmVehicle.getAttBodyType(),
                    prmVehicle.getAttFuelType(),
                    prmVehicle.getAttCategory(),
                    prmVehicle.getAttLicensePlate(),
                    prmVehicle.getAttMileage(),
                    prmVehicle.getAttCondition(),
                    prmVehicle.getAttStatus(),
                    prmVehicle.getAttColor());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public void opUpdateVehicle(clsVehicle prmVehicle) {
        try {
            attJdbcTemplate.update(SQL_UPDATE,
                    prmVehicle.getAttModel(),
                    prmVehicle.getAttYear(),
                    prmVehicle.getAttBodyType(),
                    prmVehicle.getAttFuelType(),
                    prmVehicle.getAttCategory(),
                    prmVehicle.getAttLicensePlate(),
                    prmVehicle.getAttMileage(),
                    prmVehicle.getAttCondition(),
                    prmVehicle.getAttStatus(),
                    prmVehicle.getAttColor(),
                    prmVehicle.getAttVehicleId());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public void opUpdateVehicleStatus(int prmId, String prmStatus) {
        try {
            attJdbcTemplate.update(SQL_UPDATE_STATUS, prmStatus, prmId);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public boolean opVehicleExist(int prmId) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_EXIST, Integer.class, prmId);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public boolean opPlateExist(String prmPlate) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(SQL_PLATE_EXIST, Integer.class, prmPlate);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public boolean opPlateExistInOtherVehicle(String prmPlate, int prmVehicleId) {
        try {
            Integer varCount = attJdbcTemplate.queryForObject(
                    SQL_PLATE_EXIST_OTHER, Integer.class, prmPlate, prmVehicleId);
            return varCount != null && varCount > 0;
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public clsVehicle opGetVehicleById(int prmId) {
        try {
            List<clsVehicle> varList = attJdbcTemplate.query(SQL_BY_ID, opVehicleRowMapper(), prmId);
            return varList.isEmpty() ? null : varList.get(0);
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
 
    public List<clsVehicle> opGetAllVehicles() {
        try {
            return attJdbcTemplate.query(SQL_ALL, opVehicleRowMapper());
        } catch (Exception e) {
            throw opWrap(e);
        }
    }
}
