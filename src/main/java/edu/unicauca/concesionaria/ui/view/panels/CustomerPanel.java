package edu.unicauca.concesionaria.ui.view.panels;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.ui.util.TableHelper;
import edu.unicauca.concesionaria.ui.util.UiKit;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CustomerPanel {

    private final clsController controller;
    private final TableView<clsCustomer> table = TableHelper.create();

    @SuppressWarnings("unchecked")
    
    public CustomerPanel(clsController controller) {
        this.controller = controller;

        table.getColumns().addAll(
                TableHelper.col("ID Cliente", c -> String.valueOf(c.getAttCustomerId())),
                TableHelper.col("Nombre", clsCustomer::getAttName),
                TableHelper.col("Teléfono", clsCustomer::getAttPhone),
                TableHelper.col("Email", c -> c.getAttEmail() != null ? c.getAttEmail() : ""),
                TableHelper.col("Estado", clsCustomer::getAttState));

        refreshTable();
    }

    public VBox getContent() {
        var id = UiKit.field("ID Cliente");
        var name = UiKit.field("Nombre (máx. 50)");
        var phone = UiKit.field("Teléfono (10 dígitos, único)");
        var email = UiKit.field("Correo electrónico (opcional)");
        ComboBox<String> state = new ComboBox<>();
        state.getItems().addAll("active", "inactive");
        state.setValue("active");

        Runnable clearForm = () -> {
            id.clear();
            name.clear();
            phone.clear();
            email.clear();
            state.setValue("active");
        };

        // Botón: Limpiar
        var clearBtn = UiKit.secondaryButton("Limpiar");
        clearBtn.setOnAction(e -> clearForm.run());

        GridPane form = UiKit.formGrid(2);
        UiKit.addFormRow(form, 0, "ID Cliente", id);
        UiKit.addFormRow(form, 1, "Nombre", name);
        UiKit.addFormRow(form, 2, "Teléfono", phone);
        UiKit.addFormRow(form, 3, "Email", email);
        UiKit.addFormRow(form, 4, "Estado", state);

        GridPane.setColumnSpan(clearBtn, 2);
        form.add(clearBtn, 0, 5);

        // Botón: Registrar Cliente
        var registerBtn = UiKit.primaryButton("Registrar Cliente");
        registerBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opRegisterCustomer(
                    UiKit.parseInt(id.getText(), "ID Cliente"),
                    UiKit.emptyToNull(name.getText()),
                    UiKit.emptyToNull(phone.getText()),
                    UiKit.emptyToNull(email.getText()), 
                    "active");            
            clearForm.run();
            refreshTable();
        }));

        // Botón: Actualizar Cliente
        var updateBtn = UiKit.primaryButton("Actualizar Cliente");
        updateBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opUpdateCustomer(
                    UiKit.parseInt(id.getText(), "ID Cliente"),
                    UiKit.emptyToNull(name.getText()),
                    UiKit.emptyToNull(phone.getText()),
                    UiKit.emptyToNull(email.getText()),
                    state.getValue());                      
            clearForm.run();           
            refreshTable(); 
        }));

        // Listener en la tabla de clientes: Carga los datos en el formulario para edición inmediata
        table.setRowFactory(tv -> {
            TableRow<clsCustomer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    clsCustomer customer = row.getItem();
                    if (customer != null) {
                        id.setText(String.valueOf(customer.getAttCustomerId()));
                        name.setText(customer.getAttName() != null ? customer.getAttName() : "");
                        phone.setText(customer.getAttPhone() != null ? customer.getAttPhone() : "");
                        email.setText(customer.getAttEmail() != null ? customer.getAttEmail() : "");
                        state.setValue(customer.getAttState());
                    }
                }
            });
            return row;
        });

        TitledPane formSection = UiKit.section("Formulario de Cliente", form);
        formSection.setExpanded(true);

        VBox box = UiKit.panelVBox(
                UiKit.title("Gestión de Clientes"),
                UiKit.muted("Registro de clientes habilitados para venta y actualización de datos."),                
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
