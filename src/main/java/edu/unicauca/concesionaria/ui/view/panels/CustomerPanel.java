package edu.unicauca.concesionaria.ui.view.panels;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.Model.clsCustomer;
import edu.unicauca.concesionaria.ui.util.TableHelper;
import edu.unicauca.concesionaria.ui.util.UiKit;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CustomerPanel {

    private final clsController controller;
    private final TableView<clsCustomer> table = TableHelper.create();

    public CustomerPanel(clsController controller) {
        this.controller = controller;
        table.getColumns().addAll(
                TableHelper.col("ID", c -> String.valueOf(c.getAttCustomerId())),
                TableHelper.col("Nombre", clsCustomer::getAttName),
                TableHelper.col("Teléfono", clsCustomer::getAttPhone),
                TableHelper.col("Email", clsCustomer::getAttEmail),
                TableHelper.col("Estado", clsCustomer::getAttState));
    }

    public VBox getContent() {
        var id = UiKit.field("ID");
        var name = UiKit.field("Nombre (máx. 50)");
        var phone = UiKit.field("Teléfono (10 dígitos)");
        var email = UiKit.field("Email (opcional, único)");
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

        var clearBtn = UiKit.secondaryButton("Limpiar");
        clearBtn.setOnAction(e -> clearForm.run());

        GridPane form = UiKit.formGrid(2);
        UiKit.addFormRow(form, 0, "ID", id);
        UiKit.addFormRow(form, 1, "Nombre", name);
        UiKit.addFormRow(form, 2, "Teléfono", phone);
        UiKit.addFormRow(form, 3, "Email", email);
        UiKit.addFormRow(form, 4, "Estado", state);

        GridPane.setColumnSpan(clearBtn, 2);
        form.add(clearBtn, 0, 5);

        var registerBtn = UiKit.primaryButton("Registrar");
        registerBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opRegisterCustomer(
                    UiKit.parseInt(id.getText(), "ID"),
                    UiKit.requireNonBlank(name.getText(), "Nombre"),
                    UiKit.requireNonBlank(phone.getText(), "Teléfono"),
                    UiKit.emptyToNull(email.getText()),
                    state.getValue());
            clearForm.run();
        }));

        var updateBtn = UiKit.primaryButton("Actualizar");
        updateBtn.setOnAction(e -> UiKit.run(() -> {
            controller.opUpdateCustomer(
                    UiKit.parseInt(id.getText(), "ID"),
                    UiKit.emptyToNull(name.getText()),
                    UiKit.emptyToNull(phone.getText()),
                    UiKit.emptyToNull(email.getText()));
            clearForm.run();
        }));

        var disableBtn = UiKit.primaryButton("Inactivar");
        disableBtn.setOnAction(e -> UiKit.run(() ->
                controller.opDisableCustomer(UiKit.parseInt(id.getText(), "ID"))));

        var getByIdBtn = UiKit.secondaryButton("Buscar por ID");
        getByIdBtn.setOnAction(e -> UiKit.run(() -> {
            clsCustomer c = controller.opGetCustomerById(UiKit.parseInt(id.getText(), "ID"));
            TableHelper.setItems(table, java.util.List.of(c));
        }));

        var listBtn = UiKit.secondaryButton("Listar todos");
        listBtn.setOnAction(e -> UiKit.runSilent(() ->
                TableHelper.setItems(table, controller.opGetAllCustomers())));

        VBox box = UiKit.panelVBox(
                UiKit.title("Clientes"),
                UiKit.muted("TBL_CUSTOMER: CUS_ID, CUS_NAME, CUS_PHONE (único), CUS_EMAIL (único, opcional), CUS_STATE."),
                UiKit.section("Formulario", form),
                UiKit.buttonRow(registerBtn, updateBtn, disableBtn, getByIdBtn, listBtn),
                table);
        table.setPrefHeight(280);
        return box;
    }
}
