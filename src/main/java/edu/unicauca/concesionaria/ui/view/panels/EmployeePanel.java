package edu.unicauca.concesionaria.ui.view.panels;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.Model.clsEmployee;
import edu.unicauca.concesionaria.Model.clsSalesGoal;
import edu.unicauca.concesionaria.ui.util.TableHelper;
import edu.unicauca.concesionaria.ui.util.UiKit;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class EmployeePanel {

    private final clsController controller;
    private final TableView<clsEmployee> table = TableHelper.create();
    private final TableView<clsSalesGoal> goalTable = TableHelper.create();

    public EmployeePanel(clsController controller) {
        this.controller = controller;

        // Columnas para tabla de empleados
        table.getColumns().addAll(
                TableHelper.col("ID", e -> String.valueOf(e.getAttEmployeeId())),
                TableHelper.col("Nombre", clsEmployee::getAttName),
                TableHelper.col("Teléfono", clsEmployee::getAttPhone),
                TableHelper.col("Salario", e -> String.valueOf(e.getAttSalary())),
                TableHelper.col("Contratación", e -> e.getAttHireDate() == null ? "" : String.valueOf(e.getAttHireDate())),
                TableHelper.col("Rol", clsEmployee::getAttRole),
                TableHelper.col("Estado", clsEmployee::getAttState));

        // Columnas para tabla de metas con porcentaje de cumplimiento y monto vendido
        goalTable.getColumns().addAll(
                TableHelper.col("ID Meta", g -> String.valueOf(g.getAttSalesGoalId())),
                TableHelper.col("ID Vendedor", g -> g.getAttEmployee() != null ? String.valueOf(g.getAttEmployee().getAttEmployeeId()) : ""),
                TableHelper.col("Vendedor", g -> g.getAttEmployee() != null && g.getAttEmployee().getAttName() != null ? g.getAttEmployee().getAttName() : ""),
                TableHelper.col("Tipo", clsSalesGoal::getAttGoalType),
                TableHelper.col("Meta ($)", g -> String.format("%,.2f", g.getAttTargetValue())),
                TableHelper.col("Total Vendido ($)", g -> String.format("%,.2f", g.getAttAchievedValue())),
                TableHelper.col("% Cumplimiento", g -> String.format("%.2f%%", g.getAttCompliancePercentage())),
                TableHelper.col("Fecha Inicio", g -> g.getAttStartDate() != null ? String.valueOf(g.getAttStartDate()) : ""),
                TableHelper.col("Fecha Fin", g -> g.getAttEndDate() != null ? String.valueOf(g.getAttEndDate()) : ""),
                TableHelper.col("Estado", clsSalesGoal::getAttState));

        // Cargar datos iniciales
        refreshTable();
        refreshGoalTable();
    }

    public VBox getContent() {
        // region FORMULARIO EMPLEADO
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
            refreshTable();
        }));

        var updateBtn = UiKit.primaryButton("Actualizar");
        updateBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opUpdateEmployee(
                    UiKit.parseInt(id.getText(), "ID"),
                    UiKit.emptyToNull(name.getText()),
                    UiKit.emptyToNull(phone.getText()),
                    UiKit.parseDoubleOrNull(salary.getText()),
                    state.getValue());
            clearForm.run();
            refreshTable();
        }));
        // endregion

        // region FORMULARIO META DE VENTAS
        var goalId = UiKit.field("ID meta (opcional / autogenerado)");
        var goalEmpId = UiKit.field("ID vendedor");
        ComboBox<String> goalType = new ComboBox<>();
        goalType.getItems().addAll("monthly", "quarterly", "yearly");
        goalType.setValue("monthly");
        var goalTarget = UiKit.field("Valor objetivo ($)");
        var goalStartDate = UiKit.datePicker("Fecha inicio");
        goalStartDate.setValue(LocalDate.now());
        ComboBox<String> goalState = new ComboBox<>();
        goalState.getItems().addAll("active", "inactive");
        goalState.setValue("active");

        Runnable clearGoalForm = () -> {
            goalId.clear();
            goalEmpId.clear();
            goalType.setValue("monthly");
            goalTarget.clear();
            goalStartDate.setValue(LocalDate.now());
            goalState.setValue("active");
        };

        var clearGoalBtn = UiKit.secondaryButton("Limpiar");
        clearGoalBtn.setOnAction(e -> clearGoalForm.run());

        GridPane goalFormGrid = UiKit.formGrid(2);
        UiKit.addFormRow(goalFormGrid, 0, "ID meta", goalId);
        UiKit.addFormRow(goalFormGrid, 1, "ID vendedor", goalEmpId);
        UiKit.addFormRow(goalFormGrid, 2, "Tipo de meta", goalType);
        UiKit.addFormRow(goalFormGrid, 3, "Valor objetivo ($)", goalTarget);
        UiKit.addFormRow(goalFormGrid, 4, "Fecha inicio", goalStartDate);
        UiKit.addFormRow(goalFormGrid, 5, "Estado", goalState);

        GridPane.setColumnSpan(clearGoalBtn, 2);
        goalFormGrid.add(clearGoalBtn, 0, 5);

        // Botón: Asignar Meta
        var registerGoalBtn = UiKit.primaryButton("Asignar Meta");
        registerGoalBtn.setOnAction(e -> UiKit.run(() -> {
            int empIdVal = UiKit.parseInt(goalEmpId.getText(), "ID vendedor");
            int newGoalId = controller.opRegisterSalesGoal(
                    UiKit.parseIntOrNull(goalId.getText()),
                    empIdVal,
                    goalType.getValue(),
                    UiKit.parseDouble(goalTarget.getText(), "Valor objetivo"),
                    UiKit.toSqlDate(goalStartDate),
                    goalState.getValue());

            refreshGoalTable();
            clearGoalForm.run();

            clsSalesGoal saved = controller.opGetSalesGoalById(newGoalId);
            if (saved != null) {
                UiKit.showInfo("Meta Asignada", String.format(
                        "Meta establecida con éxito para el vendedor ID %d.\n"
                                + "Objetivo: $%,.2f | Total vendido: $%,.2f\n"
                                + "Porcentaje de cumplimiento: %.2f%%",
                        empIdVal, saved.getAttTargetValue(), saved.getAttAchievedValue(), saved.getAttCompliancePercentage()));
            }
        }));

        // Botón: Actualizar Meta
        var updateGoalBtn = UiKit.primaryButton("Actualizar Meta");
        updateGoalBtn.setOnAction(e -> UiKit.run(() -> {
            int targetGoalId = UiKit.parseInt(goalId.getText(), "ID meta");
            int empIdVal = UiKit.parseInt(goalEmpId.getText(), "ID vendedor");


            controller.opUpdateSalesGoal(
                    targetGoalId,
                    UiKit.parseDoubleOrNull(goalTarget.getText()),
                    goalType.getValue());

            refreshGoalTable();
            clearGoalForm.run();

            clsSalesGoal updated = controller.opGetSalesGoalById(targetGoalId);
            if (updated != null) {
                UiKit.showInfo("Meta Actualizada", String.format(
                        "Meta ID %d modificada con éxito para el vendedor ID %d.\n"
                                + "Nuevo objetivo: $%,.2f | Total vendido: $%,.2f\n"
                                + "Porcentaje de cumplimiento: %.2f%%",
                        targetGoalId, empIdVal, updated.getAttTargetValue(), updated.getAttAchievedValue(), updated.getAttCompliancePercentage()));
            }
        }));
        // endregion

        // Listener de selección en tabla de empleados:
        // Carga los datos del empleado seleccionado en su formulario.
        table.setRowFactory(tv -> {
            TableRow<clsEmployee> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    clsEmployee emp = row.getItem();
                    if (emp != null) {
                        // Llenar formulario de empleado
                        id.setText(String.valueOf(emp.getAttEmployeeId()));
                        name.setText(emp.getAttName() != null ? emp.getAttName() : "");
                        phone.setText(emp.getAttPhone() != null ? emp.getAttPhone() : "");
                        salary.setText(String.valueOf(emp.getAttSalary()));
                        role.setValue(emp.getAttRole());
                        state.setValue(emp.getAttState());

                        if (emp.getAttHireDate() != null) {
                            hireDate.setValue(emp.getAttHireDate().toInstant()
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate());
                        } else {
                            hireDate.setValue(null);
                        }

                        // Vincular automáticamente el vendedor seleccionado al formulario de metas.
                        goalEmpId.setText(String.valueOf(emp.getAttEmployeeId()));
                    }
                }
            });
            return row;
        });

        // Listener de selección en tabla de metas:
        // Carga los datos de la meta seleccionada en el formulario.
        goalTable.setRowFactory(tv -> {
            TableRow<clsSalesGoal> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    clsSalesGoal g = row.getItem();
                    if (g != null) {
                        goalId.setText(String.valueOf(g.getAttSalesGoalId()));
                        if (g.getAttEmployee() != null) {
                            goalEmpId.setText(String.valueOf(g.getAttEmployee().getAttEmployeeId()));
                        }
                        if (g.getAttGoalType() != null) {
                            goalType.setValue(g.getAttGoalType().toLowerCase().trim());
                        }
                        goalTarget.setText(String.valueOf(g.getAttTargetValue()));
                        if (g.getAttStartDate() != null) {
                            goalStartDate.setValue(g.getAttStartDate().toInstant()
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate());
                        }
                        if (g.getAttState() != null) {
                            goalState.setValue(g.getAttState().toLowerCase().trim());
                        }
                    }
                }
            });
            return row;
        });

        TitledPane empFormSection = UiKit.section("Formulario de Empleado", form);
        empFormSection.setExpanded(true);

        TitledPane goalFormSection = UiKit.section("Configurar Meta Comercial", goalFormGrid);
        goalFormSection.setExpanded(true);

        table.setPrefHeight(230);
        goalTable.setPrefHeight(230);

        VBox box = UiKit.panelVBox(
                UiKit.title("Gestión de Empleados"),
                UiKit.muted("TBL_EMPLOYEE: Rol manager/seller (un solo manager). Teléfono único."),
                empFormSection,
                UiKit.buttonRow(registerBtn, updateBtn),
                table,
                new Separator(),
                UiKit.title("Metas Comerciales y Cumplimiento"),
                UiKit.muted("Asigne o modifique metas individuales de vendedores y consulte su porcentaje de cumplimiento alcanzado."),
                goalFormSection,
                UiKit.buttonRow(registerGoalBtn, updateGoalBtn),
                goalTable);

        return box;
    }

    private void refreshTable() {
        TableHelper.setItems(table, controller.opGetAllEmployees());
    }

    private void refreshGoalTable() {
        TableHelper.setItems(goalTable, controller.opGetAllSalesGoals());
    }
}
