import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class FindPathForm extends Alert {

    public FindPathForm(TextArea textArea) {
        super(AlertType.INFORMATION);
        GridPane pane = new GridPane();

        pane.setPadding(new Insets(10));
        pane.getChildren().add(textArea);
        pane.setAlignment(Pos.CENTER);

        getDialogPane().setContent(pane);


    }
}