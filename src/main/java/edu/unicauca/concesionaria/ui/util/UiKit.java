package edu.unicauca.concesionaria.ui.util;

import edu.unicauca.concesionaria.ui.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;

public final class UiKit {

    private UiKit() {
    }

    public static void styleRoot(Node node) {
        node.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
    }

    public static Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + Theme.ACCENT_SOFT + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-font-size: 11px;");
        label.setWrapText(true);
        return label;
    }

    public static TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(inputStyle());
        HBox.setHgrow(tf, Priority.ALWAYS);
        return tf;
    }

    public static DatePicker datePicker(String prompt) {
        DatePicker dp = new DatePicker();
        dp.setPromptText(prompt);
        dp.setStyle(inputStyle());
        return dp;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + Theme.ACCENT + ";"
                        + "-fx-text-fill: white; -fx-font-weight: bold;"
                        + "-fx-background-radius: 6; -fx-padding: 8 16;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + Theme.ACCENT_HOVER + ";"
                        + "-fx-text-fill: white; -fx-font-weight: bold;"
                        + "-fx-background-radius: 6; -fx-padding: 8 16;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + Theme.ACCENT + ";"
                        + "-fx-text-fill: white; -fx-font-weight: bold;"
                        + "-fx-background-radius: 6; -fx-padding: 8 16;"));
        return btn;
    }

    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + Theme.BG_INPUT + ";"
                        + "-fx-text-fill: " + Theme.TEXT + "; -fx-border-color: " + Theme.BORDER + ";"
                        + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        return btn;
    }

    public static TitledPane section(String title, Node content) {
        TitledPane pane = new TitledPane(title, content);
        pane.setExpanded(false);
        pane.setStyle(
                "-fx-text-fill: " + Theme.TEXT + ";"
                        + "-fx-background-color: " + Theme.BG_PANEL + ";");
        return pane;
    }

    public static VBox panelVBox(Node... children) {
        VBox box = new VBox(12, children);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: " + Theme.BG_PANEL + "; -fx-background-radius: 8;");
        return box;
    }

    public static ScrollPane scrollable(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + Theme.BG_DARK + "; -fx-background-color: " + Theme.BG_DARK + ";");
        return scroll;
    }

    public static GridPane formGrid(int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));
        return grid;
    }

    public static void addFormRow(GridPane grid, int row, String label, Node input) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + Theme.TEXT + ";");
        lbl.setMinWidth(120);
        grid.add(lbl, 0, row);
        grid.add(input, 1, row);
        GridPane.setHgrow(input, Priority.ALWAYS);
    }

    public static HBox buttonRow(Button... buttons) {
        HBox row = new HBox(10, buttons);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 0, 0));
        return row;
    }

    public static void run(Runnable action) {
        try {
            action.run();
            showInfo("Operación completada", "La operación se ejecutó correctamente.");
        } catch (Exception ex) {
            showError("Error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    public static void runSilent(Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            showError("Error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    public static void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("KIA Concesionaria");
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    public static void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("KIA Concesionaria");
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private static void styleAlert(Alert alert) {
        alert.getDialogPane().setStyle("-fx-background-color: " + Theme.BG_PANEL + ";");
    }

    public static String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + fieldName + "' debe ser un número entero válido.");
        }
    }

    public static double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + fieldName + "' debe ser un número válido.");
        }
    }

    public static Double parseDoubleOrNull(String value) {
        String v = emptyToNull(value);
        if (v == null) {
            return null;
        }
        return Double.parseDouble(v);
    }

    public static Date toSqlDate(DatePicker picker) {
        LocalDate ld = picker.getValue();
        if (ld == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }
        return Date.valueOf(ld);
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obligatorio: " + fieldName);
        }
        return value.trim();
    }

    private static String inputStyle() {
        return "-fx-background-color: " + Theme.BG_INPUT + ";"
                + "-fx-text-fill: " + Theme.TEXT + ";"
                + "-fx-prompt-text-fill: " + Theme.TEXT_MUTED + ";"
                + "-fx-background-radius: 6;";
    }
}
