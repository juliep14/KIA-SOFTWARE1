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

    public void opRegisterCustomer(Integer prmId, String prmName, String prmEmail, String prmPhone, String prmState) {
        // Criterio 3 (KIA-HU-08): Datos obligatorios incompletos
        clsValidations.opValidateMandatoryCustomerFields(prmId, prmName, prmPhone);
        clsValidations.opValidateId(prmId);

        // Criterio 2 (KIA-HU-08): Documento duplicado
        if (attCustomerRepository.opCustomerExist(prmId)) {
            throw new excDuplicateDataException("El cliente con el documento " + prmId + " ya se encuentra registrado en el sistema.");
        }

        clsValidations.opValidateName(prmName, 3, 50);
        clsValidations.opValidatePhone(prmPhone);
        if (prmEmail != null && !prmEmail.isBlank()) {
            clsValidations.opValidateEmail(prmEmail);
        }

        String varState = (prmState != null && !prmState.isBlank()) ? prmState.toLowerCase().trim() : "active";
        clsValidations.opValidateState(varState);

        clsCustomer varTemp = new clsCustomer();
        varTemp.setAttCustomerId(prmId);
        varTemp.setAttName(prmName.trim());
        varTemp.setAttEmail(prmEmail == null || prmEmail.isBlank() ? null : prmEmail.toLowerCase().trim());
        varTemp.setAttPhone(prmPhone.trim());
        varTemp.setAttState(varState);

        try {
            attCustomerRepository.opRegisterCustomer(varTemp);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al registrar cliente: ", e);
        }

        opRefreshCache(prmId);
    }

    public void opUpdateCustomer(int prmCurrentId, int prmNewId, String prmName, String prmPhone, String prmEmail) {
        clsValidations.opValidateId(prmCurrentId);

        // Criterio 2 (KIA-HU-09): Cliente no encontrado
        if (!attCustomerRepository.opCustomerExist(prmCurrentId)) {
            throw new excNotFoundException("El cliente con documento " + prmCurrentId + " no fue encontrado en el sistema.");
        }

        // Criterio 3 (KIA-HU-09): Documento duplicado al actualizar
        if (prmNewId != prmCurrentId) {
            clsValidations.opValidateId(prmNewId);
            if (attCustomerRepository.opCustomerExist(prmNewId)) {
                throw new excDuplicateDataException("El número de documento " + prmNewId + " ya está en uso por otro cliente registrado.");
            }
        }

        if (prmName != null && !prmName.isBlank()) {
            clsValidations.opValidateName(prmName, 3, 50);
        }
        if (prmPhone != null && !prmPhone.isBlank()) {
            clsValidations.opValidatePhone(prmPhone);
        }
        if (prmEmail != null && !prmEmail.isBlank()) {
            clsValidations.opValidateEmail(prmEmail);
        }

        clsCustomer varCustomer = opFetchFromDB(prmCurrentId);
        varCustomer.setAttCustomerId(prmNewId);

        if (prmName != null && !prmName.isBlank()) {
            varCustomer.setAttName(prmName.trim());
        }
        if (prmPhone != null && !prmPhone.isBlank()) {
            varCustomer.setAttPhone(prmPhone.trim());
        }
        if (prmEmail != null) {
            varCustomer.setAttEmail(prmEmail.isBlank() ? null : prmEmail.toLowerCase().trim());
        }

        try {
            attCustomerRepository.opUpdateCustomer(prmCurrentId, varCustomer);
        } catch (excDatabaseException e) {
            throw new excDatabaseException("Error al actualizar cliente: ", e);
        }

        attCustomers.remove(prmCurrentId);
        attCustomers.put(prmNewId, varCustomer);
    }

    public void opUpdateCustomer(int prmId, String prmName, String prmPhone, String prmEmail) {
        opUpdateCustomer(prmId, prmId, prmName, prmPhone, prmEmail);
    }

    public clsCustomer opGetCustomerForEdit(int prmId) {
        clsValidations.opValidateId(prmId);
        if (!attCustomerRepository.opCustomerExist(prmId)) {
            throw new excNotFoundException("El cliente con documento " + prmId + " no fue encontrado en el sistema.");
        }
        return opGetCustomerById(prmId);
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
