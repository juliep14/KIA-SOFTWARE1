package edu.unicauca.concesionaria.Service;

import edu.unicauca.concesionaria.Error.excDatabaseException;
import edu.unicauca.concesionaria.Error.excDuplicateDataException;
import edu.unicauca.concesionaria.Error.excNotFoundException;
import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.Repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository attCustomerRepository;
    private HashMap<Integer, clsCustomer> attCustomers = new HashMap<>();

    public CustomerService(CustomerRepository prmCustomerRepository) {
        this.attCustomerRepository = prmCustomerRepository;
    }

    private clsCustomer opFetchFromDB(int prmId) {
        clsCustomer varCustomer;
        try {
            varCustomer = attCustomerRepository.opGetCustomerById(prmId);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener el cliente desde BD: ", e);
        }
        if (varCustomer == null)
            throw new excNotFoundException("Cliente no encontrado");
        return varCustomer;
    }

    private void opRefreshCache(int prmId) {
        attCustomers.remove(prmId);
        clsCustomer varFresh = opFetchFromDB(prmId);
        attCustomers.put(prmId, varFresh);
    }

    public void opRegisterCustomer(int prmId, String prmName, String prmEmail, String prmPhone, String prmState) {
        clsValidations.opValidateId(prmId);

        if (attCustomerRepository.opCustomerExist(prmId))
            throw new excDuplicateDataException("Cliente ya registrado");

        clsValidations.opValidateName(prmName, 3, 50);
        clsValidations.opValidateEmail(prmEmail);
        clsValidations.opValidatePhone(prmPhone);
        clsValidations.opValidateState(prmState);

        clsCustomer varTemp = new clsCustomer();
        varTemp.setAttCustomerId(prmId);
        varTemp.setAttName(prmName.trim());
        varTemp.setAttEmail(prmEmail == null ? null : prmEmail.toLowerCase().trim());
        varTemp.setAttPhone(prmPhone);
        varTemp.setAttState(prmState.toLowerCase().trim());

        try {
            attCustomerRepository.opRegisterCustomer(varTemp);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al registrar cliente: ", e);
        }

        opRefreshCache(prmId);
    }

    public void opUpdateCustomer(int prmId, String prmName, String prmPhone, String prmEmail) {
        clsValidations.opValidateId(prmId);

        if (!attCustomerRepository.opCustomerExist(prmId))
            throw new excNotFoundException("Cliente no encontrado");

        if (prmName != null)
            clsValidations.opValidateName(prmName, 3, 50);
        if (prmEmail != null)
            clsValidations.opValidateEmail(prmEmail);
        if (prmPhone != null)
            clsValidations.opValidatePhone(prmPhone);

        clsCustomer varCustomer = opFetchFromDB(prmId);

        if (prmName != null)
            varCustomer.setAttName(prmName.trim());
        if (prmEmail != null)
            varCustomer.setAttEmail(prmEmail.toLowerCase().trim());
        if (prmPhone != null)
            varCustomer.setAttPhone(prmPhone);

        try {
            attCustomerRepository.opUpdateCustomer(varCustomer);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al actualizar cliente: ", e);
        }

        opRefreshCache(prmId);
    }

    public void opDisableCustomer(int prmId) {
        clsValidations.opValidateId(prmId);

        if (!attCustomerRepository.opCustomerExist(prmId))
            throw new excNotFoundException("Cliente no encontrado");

        try {
            attCustomerRepository.opDisableCustomer(prmId);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al inactivar al cliente: ", e);
        }
        opRefreshCache(prmId);
    }

    public clsCustomer opGetCustomerById(int prmId) {
        clsValidations.opValidateId(prmId);

        clsCustomer varCustomer = attCustomers.get(prmId);
        if (varCustomer != null)
            return varCustomer;

        varCustomer = opFetchFromDB(prmId);
        attCustomers.put(prmId, varCustomer);
        return varCustomer;
    }

    public List<clsCustomer> opGetAllCustomers() {
        try {
            return attCustomerRepository.opGetAllCustomers();
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al obtener clientes: ", e);
        }
    }
}
