import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ConnectionForm extends Alert {

    private TextField nameField = new TextField();
    private TextField timeField = new TextField();


    public ConnectionForm() {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(5);

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Time:"), timeField);


        getDialogPane().setContent(grid);



    }


    public String getName(){
        return nameField.getText();
    }

    public String getTime(){
        return timeField.getText();
    }




}