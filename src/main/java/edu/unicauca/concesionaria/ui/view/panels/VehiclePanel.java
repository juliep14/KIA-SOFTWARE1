package edu.unicauca.concesionaria.ui.view.panels;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.Model.clsVehicle;
import edu.unicauca.concesionaria.ui.util.TableHelper;
import edu.unicauca.concesionaria.ui.util.UiKit;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class VehiclePanel {

    private final clsController controller;
    private final TableView<clsVehicle> table = TableHelper.create();

    @SuppressWarnings("unchecked")
    public VehiclePanel(clsController controller) {
        this.controller = controller;

        table.getColumns().addAll(
                TableHelper.col("ID", v -> String.valueOf(v.getAttVehicleId())),
                TableHelper.col("Modelo", clsVehicle::getAttModel),
                TableHelper.col("Año", v -> v.getAttYear() != null ? String.valueOf(v.getAttYear()) : ""),
                TableHelper.col("Carrocería", clsVehicle::getAttBodyType),
                TableHelper.col("Combustible", clsVehicle::getAttFuelType),
                TableHelper.col("Categoría", clsVehicle::getAttCategory),
                TableHelper.col("Placa", clsVehicle::getAttLicensePlate),
                TableHelper.col("Kilometraje", v -> formatNumber(v.getAttMileage())),
                TableHelper.col("Condición", clsVehicle::getAttCondition),
                TableHelper.col("Color", v -> v.getAttColor() != null ? v.getAttColor() : ""),
                TableHelper.col("Estado", clsVehicle::getAttStatus));

        refreshTable();
    }

    public VBox getContent() {
        var id = UiKit.field("ID Unidad");
        var model = UiKit.field("Modelo (máx. 25)");
        var year = UiKit.field("Año (opcional)");
        var bodyType = UiKit.field("Carrocería (ej. SUV, sedán)");
        var plate = UiKit.field("Placa (6 caracteres, única)");
        var mileage = UiKit.field("Kilometraje (opcional)");
        var color = UiKit.field("Color");

        ComboBox<String> fuel = new ComboBox<>();
        fuel.getItems().addAll("electric", "gasoline", "hybrid");
        fuel.setPromptText("Seleccione el combustible");

        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll("standard", "luxury");
        category.setPromptText("Seleccione la categoría");

        ComboBox<String> condition = new ComboBox<>();
        condition.getItems().addAll("new", "used");
        condition.setPromptText("Seleccione la condición");

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("available", "reserved", "sold", "maintenance");
        status.setValue("available");

        Runnable clearForm = () -> {
            id.clear();
            model.clear();
            year.clear();
            bodyType.clear();
            plate.clear();
            mileage.clear();
            color.clear();
            fuel.setValue(null);
            category.setValue(null);
            condition.setValue(null);
            status.setValue("available");
        };

        // Botón: Limpiar
        var clearBtn = UiKit.secondaryButton("Limpiar");
        clearBtn.setOnAction(e -> clearForm.run());

        GridPane form = UiKit.formGrid(2);
        UiKit.addFormRow(form, 0, "ID Unidad", id);
        UiKit.addFormRow(form, 1, "Modelo", model);
        UiKit.addFormRow(form, 2, "Año", year);
        UiKit.addFormRow(form, 3, "Carrocería", bodyType);
        UiKit.addFormRow(form, 4, "Combustible", fuel);
        UiKit.addFormRow(form, 5, "Categoría", category);
        UiKit.addFormRow(form, 6, "Placa", plate);
        UiKit.addFormRow(form, 7, "Kilometraje", mileage);
        UiKit.addFormRow(form, 8, "Condición", condition);
        UiKit.addFormRow(form, 9, "Color", color);
        UiKit.addFormRow(form, 10, "Estado", status);

        GridPane.setColumnSpan(clearBtn, 2);
        form.add(clearBtn, 0, 11);

        // Botón: Registrar Unidad (HU-02)
        var registerBtn = UiKit.primaryButton("Registrar Unidad");
        registerBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opRegisterVehicle(
                    parseIntOrNull(id.getText(), "ID Unidad"),
                    UiKit.emptyToNull(model.getText()),
                    parseIntOrNull(year.getText(), "Año"),
                    UiKit.emptyToNull(bodyType.getText()),
                    fuel.getValue(),
                    category.getValue(),
                    UiKit.emptyToNull(plate.getText()),
                    parseDoubleOrNull(mileage.getText(), "Kilometraje"),
                    condition.getValue(),
                    UiKit.emptyToNull(color.getText()),
                    status.getValue());
            clearForm.run();
            refreshTable();
        }));

        // Botón: Actualizar Unidad (HU-03). Los campos vacíos se dejan como estaban.
        var updateBtn = UiKit.primaryButton("Actualizar Unidad");
        updateBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opUpdateVehicle(
                    UiKit.parseInt(id.getText(), "ID Unidad"),
                    UiKit.emptyToNull(model.getText()),
                    parseIntOrNull(year.getText(), "Año"),
                    UiKit.emptyToNull(bodyType.getText()),
                    fuel.getValue(),
                    category.getValue(),
                    UiKit.emptyToNull(plate.getText()),
                    parseDoubleOrNull(mileage.getText(), "Kilometraje"),
                    condition.getValue(),
                    UiKit.emptyToNull(color.getText()),
                    status.getValue());
            clearForm.run();
            refreshTable();
        }));

        // Botón: Cambiar estado (HU-03, escenario de cambio de estado)
        var statusBtn = UiKit.secondaryButton("Cambiar Estado");
        statusBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opChangeVehicleStatus(
                    UiKit.parseInt(id.getText(), "ID Unidad"),
                    status.getValue());
            clearForm.run();
            refreshTable();
        }));

        // Listener en la tabla: carga la unidad en el formulario para editarla
        table.setRowFactory(tv -> {
            TableRow<clsVehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    clsVehicle vehicle = row.getItem();
                    if (vehicle != null) {
                        id.setText(String.valueOf(vehicle.getAttVehicleId()));
                        model.setText(nullToEmpty(vehicle.getAttModel()));
                        year.setText(vehicle.getAttYear() != null ? String.valueOf(vehicle.getAttYear()) : "");
                        bodyType.setText(nullToEmpty(vehicle.getAttBodyType()));
                        fuel.setValue(vehicle.getAttFuelType());
                        category.setValue(vehicle.getAttCategory());
                        plate.setText(nullToEmpty(vehicle.getAttLicensePlate()));
                        mileage.setText(formatNumber(vehicle.getAttMileage()));
                        condition.setValue(vehicle.getAttCondition());
                        color.setText(nullToEmpty(vehicle.getAttColor()));
                        status.setValue(vehicle.getAttStatus());
                    }
                }
            });
            return row;
        });

        TitledPane formSection = UiKit.section("Formulario de Unidad", form);
        formSection.setExpanded(true);

        VBox box = UiKit.panelVBox(
                UiKit.title("Gestión de Unidades"),
                UiKit.muted("Registro de unidades para el inventario y modificación de sus datos (placa, color, combustible, estado, etc.)."),
                formSection,
                UiKit.buttonRow(registerBtn, updateBtn, statusBtn),
                table);

        table.setPrefHeight(300);
        return box;
    }

    private void refreshTable() {
        TableHelper.setItems(table, controller.opGetAllVehicles());
    }

    private static Integer parseIntOrNull(String value, String fieldName) {
        return (value == null || value.isBlank()) ? null : UiKit.parseInt(value, fieldName);
    }

    private static Double parseDoubleOrNull(String value, String fieldName) {
        return (value == null || value.isBlank()) ? null : UiKit.parseDouble(value, fieldName);
    }

    private static String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private static String formatNumber(Double value) {
        if (value == null) {
            return "";
        }
        return value == Math.rint(value) ? String.valueOf(value.longValue()) : String.valueOf(value);
    }
}