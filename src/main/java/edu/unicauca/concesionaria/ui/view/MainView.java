package edu.unicauca.concesionaria.ui.view;

import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.ui.Theme;
import edu.unicauca.concesionaria.ui.util.UiKit;
import edu.unicauca.concesionaria.ui.view.panels.CustomerPanel;
import edu.unicauca.concesionaria.ui.view.panels.EmployeePanel;
import edu.unicauca.concesionaria.ui.view.panels.VehiclePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainView {

    private final BorderPane root = new BorderPane();

    public MainView(clsController controller) {
        Label header = new Label("KIA Concesionaria");
        header.setStyle("-fx-text-fill: " + Theme.TEXT + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label hint = UiKit.muted("Gestión de clientes, empleados y unidades según TBL_CUSTOMER, TBL_EMPLOYEE y TBL_VEHICLE.");

        HBox topBar = new HBox(16, header, hint);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 16, 12, 16));
        topBar.setStyle("-fx-background-color: " + Theme.BG_PANEL + ";");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");

        tabs.getTabs().add(createTab("Empleados", new EmployeePanel(controller).getContent()));
        tabs.getTabs().add(createTab("Clientes", new CustomerPanel(controller).getContent()));
        tabs.getTabs().add(createTab("Unidades", new VehiclePanel(controller).getContent()));

        root.setTop(topBar);
        root.setCenter(tabs);
        UiKit.styleRoot(root);
    }

    private Tab createTab(String title, javafx.scene.Node content) {
        Tab tab = new Tab(title, UiKit.scrollable(content));
        tab.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
        return tab;
    }

    public BorderPane getRoot() {
        return root;
    }
}