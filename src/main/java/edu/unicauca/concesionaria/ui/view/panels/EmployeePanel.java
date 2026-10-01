package edu.unicauca.concesionaria.ui.view.panels;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.ui.util.TableHelper;
import edu.unicauca.concesionaria.ui.util.UiKit;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class EmployeePanel {

    private final clsController controller;
    private final TableView<clsEmployee> table = TableHelper.create();

    public EmployeePanel(clsController controller) {
        this.controller = controller;
        table.getColumns().addAll(
                TableHelper.col("ID", e -> String.valueOf(e.getAttEmployeeId())),
                TableHelper.col("Nombre", clsEmployee::getAttName),
                TableHelper.col("Teléfono", clsEmployee::getAttPhone),
                TableHelper.col("Salario", e -> String.valueOf(e.getAttSalary())),
                TableHelper.col("Contratación", e -> e.getAttHireDate() == null ? "" : String.valueOf(e.getAttHireDate())),
                TableHelper.col("Rol", clsEmployee::getAttRole),
                TableHelper.col("Estado", clsEmployee::getAttState));
    }

    public VBox getContent() {
        var id = UiKit.field("ID empleado");
        var name = UiKit.field("Nombre (máx. 50)");
        var phone = UiKit.field("Teléfono (10 dígitos, único)");
        var salary = UiKit.field("Salario (NUMBER(8))");
        ComboBox<String> role = new ComboBox<>();
        role.getItems().setAll("seller", "manager");
        role.setValue("seller");
        ComboBox<String> state = new ComboBox<>();
        state.getItems().addAll("active", "inactive");
        state.setValue("active");
        var hireDate = UiKit.datePicker("Fecha contratación");

        Runnable clearForm = () -> {
            id.clear();
            name.clear();
            phone.clear();
            salary.clear();
            role.setValue("seller");
            state.setValue("active");
            hireDate.setValue(null);
        };

        var clearBtn = UiKit.secondaryButton("Limpiar");
        clearBtn.setOnAction(e -> clearForm.run());

        GridPane form = UiKit.formGrid(2);
        UiKit.addFormRow(form, 0, "ID empleado", id);
        UiKit.addFormRow(form, 1, "Nombre", name);
        UiKit.addFormRow(form, 2, "Teléfono", phone);
        UiKit.addFormRow(form, 3, "Salario", salary);
        UiKit.addFormRow(form, 4, "Rol", role);
        UiKit.addFormRow(form, 5, "Estado", state);
        UiKit.addFormRow(form, 6, "Contratación", hireDate);

        GridPane.setColumnSpan(clearBtn, 2);
        form.add(clearBtn, 0, 7);

        var registerBtn = UiKit.primaryButton("Registrar");
        registerBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opRegisterEmployee(
                    UiKit.parseInt(id.getText(), "ID"),
                    UiKit.requireNonBlank(name.getText(), "Nombre"),
                    UiKit.requireNonBlank(phone.getText(), "Teléfono"),
                    UiKit.parseDouble(salary.getText(), "Salario"),
                    UiKit.toSqlDate(hireDate),
                    role.getValue(),
                    state.getValue());
            clearForm.run();
        }));

        var updateBtn = UiKit.primaryButton("Actualizar");
        updateBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opUpdateEmployee(
                    UiKit.parseInt(id.getText(), "ID"),
                    UiKit.emptyToNull(name.getText()),
                    UiKit.emptyToNull(phone.getText()),
                    UiKit.parseDoubleOrNull(salary.getText()));
            clearForm.run();
        }));

        var disableBtn = UiKit.primaryButton("Inactivar");
        disableBtn.setOnAction(e -> UiKit.run(() ->
                controller.opDisableEmployee(UiKit.parseInt(id.getText(), "ID"))));

        var getByIdBtn = UiKit.secondaryButton("Buscar por ID");
        getByIdBtn.setOnAction(e -> UiKit.run(() -> {
            clsEmployee emp = controller.opGetEmployeeById(UiKit.parseInt(id.getText(), "ID"));
            TableHelper.setItems(table, java.util.List.of(emp));
        }));

        var listAllBtn = UiKit.secondaryButton("Listar todos");
        listAllBtn.setOnAction(e -> UiKit.runSilent(() ->
                TableHelper.setItems(table, controller.opGetAllEmployees())));

        VBox box = UiKit.panelVBox(
                UiKit.title("Empleados"),
                UiKit.muted("TBL_EMPLOYEE: sin concesionaria. Rol manager/seller (un solo manager). Teléfono único."),
                UiKit.section("Formulario", form),
                UiKit.buttonRow(registerBtn, updateBtn, disableBtn, getByIdBtn, listAllBtn),
                table);
        table.setPrefHeight(280);
        return box;
    }
}
