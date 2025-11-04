package pe.edu.upeu.conceptos_poo.saborsistemas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pe.edu.upeu.conceptos_poo.saborsistemas.components.StageManager;

@SpringBootApplication
public class SaborSistemasAplication extends Application {
    private ConfigurableApplicationContext configurableApplicationContext;
    private Parent parent;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SaborSistemasAplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
        configurableApplicationContext = builder.run(getParameters().getRaw().toArray(new String[0]));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        fxmlLoader.setControllerFactory(configurableApplicationContext::getBean);
        parent= fxmlLoader.load();
    }
    @Override
    public void start(Stage stage) throws Exception {
        StageManager.setPrimaryStage(stage);
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("SaborSistemas - Login");
        stage.show();

    }

    //@Bean
    //public CommandLineRunner createEncryptedPassword(PasswordEncoder passwordEncoder) {
        //return args -> {
            //String contrasenaPlana = "admin123"; // La contraseña que quieres usar
            //String contrasenaEncriptada = passwordEncoder.encode(contrasenaPlana);

            //System.out.println("******************************************************************");
            //System.out.println("COPIA ESTA CONTRASEÑA ENCRIPTADA PARA LA BASE DE DATOS:");
            //System.out.println(contrasenaEncriptada);
            //System.out.println("Contraseña plana original: " + contrasenaPlana);
            //System.out.println("******************************************************************");
        //};
    //}
}
