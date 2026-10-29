package edu.unicauca.concesionaria.ui;

import edu.unicauca.concesionaria.ConcesionariaApplication;
import edu.unicauca.concesionaria.Controller.clsController;
import edu.unicauca.concesionaria.ui.util.UiKit;
import edu.unicauca.concesionaria.ui.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

public class ConcesionariaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void launch(String[] args) {
        Application.launch(ConcesionariaFxApplication.class, args);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(ConcesionariaApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage stage) {
        clsController controller = springContext.getBean(clsController.class);
        MainView mainView = new MainView(controller);
        Scene scene = new Scene(mainView.getRoot(), 1100, 720);
        UiKit.styleRoot(scene.getRoot());
        applyTheme(scene);
        stage.setTitle("KIA Concesionaria — Clientes y empleados");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void applyTheme(Scene scene) {
        var css = Objects.requireNonNull(
                getClass().getResource("/styles/dark-purple.css"),
                "No se encontró dark-purple.css");
        scene.getStylesheets().add(css.toExternalForm());
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
}
