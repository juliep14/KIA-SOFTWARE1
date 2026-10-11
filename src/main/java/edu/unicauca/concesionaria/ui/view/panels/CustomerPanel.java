package edu.unicauca.concesionaria.ui.view.panels;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.ui.util.TableHelper;
import edu.unicauca.concesionaria.ui.util.UiKit;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CustomerPanel {

    private final clsController controller;
    private final TableView<clsCustomer> table = TableHelper.create();
    private final UiKit.FeedbackBanner banner = new UiKit.FeedbackBanner();

    // Almacena el documento original del cliente que se está editando actualmente
    private Integer editingOriginalDocument = null;

    public CustomerPanel(clsController controller) {
        this.controller = controller;

        table.getColumns().addAll(
                TableHelper.col("Documento", c -> String.valueOf(c.getAttCustomerId())),
                TableHelper.col("Nombre", clsCustomer::getAttName),
                TableHelper.col("Teléfono", clsCustomer::getAttPhone),
                TableHelper.col("Email", c -> c.getAttEmail() != null ? c.getAttEmail() : ""),
                TableHelper.col("Estado", clsCustomer::getAttState));

        refreshTable();
    }

    public VBox getContent() {
        var docField = UiKit.field("Número de documento (obligatorio)");
        var nameField = UiKit.field("Nombre completo (máx. 50 caracteres)");
        var phoneField = UiKit.field("Teléfono de contacto (10 dígitos)");
        var emailField = UiKit.field("Correo electrónico (opcional)");

        Runnable clearForm = () -> {
            docField.clear();
            nameField.clear();
            phoneField.clear();
            emailField.clear();
            editingOriginalDocument = null;
        };

        var clearBtn = UiKit.secondaryButton("Limpiar");
        clearBtn.setOnAction(e -> {
            clearForm.run();
            banner.clear();
        });

        GridPane form = UiKit.formGrid(2);
        UiKit.addFormRow(form, 0, "Documento", docField);
        UiKit.addFormRow(form, 1, "Nombre", nameField);
        UiKit.addFormRow(form, 2, "Teléfono", phoneField);
        UiKit.addFormRow(form, 3, "Email", emailField);

        GridPane.setColumnSpan(clearBtn, 2);
        form.add(clearBtn, 0, 4);

        // KIA-HU-08: Registro de cliente
        var registerBtn = UiKit.primaryButton("Registrar Cliente");
        registerBtn.setOnAction(e -> UiKit.run(() -> {
            Integer docVal = UiKit.parseIntOrNull(docField.getText());
            String nameVal = UiKit.emptyToNull(nameField.getText());
            String phoneVal = UiKit.emptyToNull(phoneField.getText());
            String emailVal = UiKit.emptyToNull(emailField.getText());

            // Validación de obligatorios y formato en CustomerService
            controller.opRegisterCustomer(docVal, nameVal, phoneVal, emailVal, "active");

            refreshTable();
            banner.showSuccess("Cliente registrado exitosamente y habilitado para procesos de venta.");
            clearForm.run();
        }));

        // KIA-HU-09: Modificación de información de clientes
        var updateBtn = UiKit.primaryButton("Actualizar Cliente");
        updateBtn.setOnAction(e -> UiKit.run(() -> {
            Integer newDocVal = UiKit.parseIntOrNull(docField.getText());
            if (newDocVal == null) {
                throw new IllegalArgumentException("El número de documento es obligatorio para actualizar el cliente.");
            }

            // Si el cliente fue cargado previamente para edición se usa editingOriginalDocument;
            // de lo contrario se asume el documento indicado en el campo.
            int currentDoc = (editingOriginalDocument != null) ? editingOriginalDocument : newDocVal;

            controller.opUpdateCustomer(
                    currentDoc,
                    newDocVal,
                    UiKit.emptyToNull(nameField.getText()),
                    UiKit.emptyToNull(phoneField.getText()),
                    UiKit.emptyToNull(emailField.getText()));

            refreshTable();
            banner.showSuccess(String.format("Información del cliente actualizada exitosamente (Documento: %d).", newDocVal));
            clearForm.run();
        }));

        // Listener en la tabla de clientes: Carga los datos en el formulario para edición inmediata
        table.setRowFactory(tv -> {
            TableRow<clsCustomer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    clsCustomer customer = row.getItem();
                    if (customer != null) {
                        docField.setText(String.valueOf(customer.getAttCustomerId()));
                        nameField.setText(customer.getAttName() != null ? customer.getAttName() : "");
                        phoneField.setText(customer.getAttPhone() != null ? customer.getAttPhone() : "");
                        emailField.setText(customer.getAttEmail() != null ? customer.getAttEmail() : "");

                        editingOriginalDocument = customer.getAttCustomerId();
                    }
                }
            });
            return row;
        });

        TitledPane formSection = UiKit.section("Formulario de Cliente", form);
        formSection.setExpanded(true);

        VBox box = UiKit.panelVBox(
                UiKit.title("Gestión de Clientes"),
                UiKit.muted("Registro de clientes habilitados para venta (KIA-HU-08) y actualización de datos (KIA-HU-09)."),
                banner,
                formSection,
                UiKit.buttonRow(registerBtn, updateBtn),
                table);

        table.setPrefHeight(300);
        return box;
    }

    private void refreshTable() {
        TableHelper.setItems(table, controller.opGetAllCustomers());
    }
}
