import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javafx.scene.image.Image;
import java.util.ArrayList;


public class PathFinder extends Application{

    private BorderPane root;
    private Pane center;
    private Stage stage;
    private Image image = new Image("file:europa.gif");
    private ImageView background = new ImageView(image);
    private Scene scene;
    private boolean addOne;
    private boolean placeHandlerActive;
    private CircleLocation from;
    private CircleLocation to;
    private ListGraph<CircleLocation> graph = new ListGraph<>();
    private boolean fileSave = true;





    @Override
    public void start(Stage stage) {

        Button path = new Button("Find Path");
        Button showCon = new Button("Show Connection");
        Button place = new Button("New Place");
        Button newCon = new Button("New Connection");
        Button changeCon  = new Button("Change Connection");


        this.stage = stage;
        root = new BorderPane();
        VBox vbox = new VBox();
        root.setTop(vbox);
        root.setStyle("-fx-font-size:14");




        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);
        menuBar.setId("menu");

        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile");

        menuBar.getMenus().add(fileMenu);
        MenuItem newMap = new MenuItem("New Map");
        fileMenu.getItems().add(newMap);
        newMap.setOnAction(new PathFinder.NewMapHandler());
        newMap.setId("menuNewMap");

        MenuItem openItem = new MenuItem("Open");
        fileMenu.getItems().add(openItem);
        openItem.setOnAction(new PathFinder.OpenHandler());
        openItem.setId("menuOpenFile");

        MenuItem saveItem = new MenuItem("Save");
        fileMenu.getItems().add(saveItem);
        saveItem.setOnAction(new PathFinder.SaveHandler());
        saveItem.setId("menuSaveFile");

        MenuItem saveImageItem = new MenuItem("Save Image");
        fileMenu.getItems().add(saveImageItem);
        saveImageItem.setOnAction(new PathFinder.SaveImageHandler());
        saveImageItem.setId("menuSaveImage");

        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitItem);
        exitItem.setOnAction(new PathFinder.ExitItemHandler());
        exitItem.setId("menuExit");

        FlowPane control = new FlowPane();
        center = new Pane();
        root.setCenter(center);
        control.getChildren().addAll(path, showCon, place, newCon, changeCon);
        vbox.getChildren().add(control);
        control.setAlignment(Pos.CENTER);
        control.setHgap(10);

        vbox.setSpacing(10);

        path.setId("btnFindPath");
        showCon.setId("btnShowConnection");
        place.setId("btnNewPlace");
        newCon.setId("btnNewConnection");
        changeCon.setId("btnChangeConnection");




        place.setOnAction(new PathFinder.PlaceHandler());
        newCon.setOnAction(new PathFinder.NewConnection());
        showCon.setOnAction(new PathFinder.ShowConnection());
        changeCon.setOnAction(new PathFinder.ChangeConnection());
        path.setOnAction(new PathFinder.ShowPath());

        //mapView = new GridPane();
        //center.getChildren().add(mapView);





        scene = new Scene(root, image.getWidth(), 80);
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.setOnCloseRequest(new PathFinder.ExitHandler());
        stage.show();
        center.setId("outputArea");

    }

    class ShowPath implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            Alert msgBox = new Alert(Alert.AlertType.ERROR);
            if(from == null || to == null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("Two places must be selected");
                msgBox.showAndWait();
                return;
            }
            else if(!graph.pathExists(from, to)){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("The two places does not have a path");
                msgBox.showAndWait();
                return;
            }


            TextArea textArea = new TextArea();
            textArea.setPrefColumnCount(15);
            textArea.setPrefHeight(120);
            textArea.setPrefWidth(300);
            textArea.setEditable(false);


            List<Edge<CircleLocation>> list = new ArrayList<>(graph.getPath(from, to));
            int totalWeight=0;
            for (Edge<CircleLocation> circleLocationEdge : list) {
                textArea.appendText("to " + circleLocationEdge.getDestination().getName() + " by " + circleLocationEdge.getName() + " takes " + circleLocationEdge.getWeight() + "\n");
                totalWeight+=circleLocationEdge.getWeight();
            }
            textArea.appendText("Total " + totalWeight);


            FindPathForm pathAlert = new FindPathForm(textArea);
            pathAlert.setTitle("Path");
            pathAlert.setHeaderText("The Path from " + from.getName() + " to " + to.getName());

            pathAlert.showAndWait();

        }
    }

    class ChangeConnection implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            fileSave = false;
            Alert msgBox = new Alert(Alert.AlertType.ERROR);
            if(from == null || to == null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("Two places must be selected");
                msgBox.showAndWait();
                return;
            }
            else if(!graph.pathExists(from, to)){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("The two places does not have a path");
                msgBox.showAndWait();
                return;
            }
            else if(graph.getEdgeBetween(from,to) == null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("The two places does not have a direct path to each other");
                msgBox.showAndWait();
                return;
            }

            ChangeConnectionForm form = new ChangeConnectionForm();

            form.setTitle("Connection");
            form.setHeaderText("Connection from " + from.getName()+" to " + to.getName());


            form.setName(graph.getEdgeBetween(from,to).getName());
            Optional<ButtonType> answer = form.showAndWait();
            Alert wrongInput = new Alert(Alert.AlertType.ERROR);
            if(answer.isPresent() && answer.get() == ButtonType.OK && !form.getTime().matches(".*\\d.*")){
                wrongInput.setTitle("Error!");
                wrongInput.setHeaderText("Time must only include numbers");
                wrongInput.showAndWait();
                return;
            }
            else if(answer.isPresent() && answer.get() == ButtonType.OK && Integer.parseInt(form.getTime()) < 0){
                wrongInput.setTitle("Error!");
                wrongInput.setHeaderText("Time can not be less than 0");
                wrongInput.showAndWait();
                return;
            }
            else if(answer.isPresent() && answer.get() != ButtonType.OK){
                return;
            }

            graph.setConnectionWeight(from,to,Integer.parseInt(form.getTime()));





        }
    }


    class ShowConnection implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            Alert msgBox = new Alert(Alert.AlertType.ERROR);
            if(from == null || to == null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("Two places must be selected");
                msgBox.showAndWait();
                return;
            }
            else if(!graph.pathExists(from, to)){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("The two places does not have a path");
                msgBox.showAndWait();
                return;
            }
            else if(graph.getEdgeBetween(from,to) == null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("The two places does not have a direct path to each other");
                msgBox.showAndWait();
                return;
            }


            ShowConnectionForm form = new ShowConnectionForm();
            form.setTitle("Connection");
            form.setHeaderText("Connection from " + from.getName()+" to " + to.getName());

            form.setName(graph.getEdgeBetween(from,to).getName());
            form.setTime(graph.getEdgeBetween(from,to).getWeight());

            Optional<ButtonType> answer = form.showAndWait();

            if(answer.isPresent() && answer.get() != ButtonType.OK){
                return;
            }
        }
    }

    class NewConnection implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            fileSave = false;
            Alert msgBox = new Alert(Alert.AlertType.ERROR);
            if(from == null || to == null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("Two places must be selected");
                msgBox.showAndWait();
                return;
            }
            else if(graph.getEdgeBetween(from, to)!=null){
                msgBox.setTitle("Error!");
                msgBox.setHeaderText("The two places already got a path");
                msgBox.showAndWait();
                return;
            }

            ConnectionForm form = new ConnectionForm();

            form.setTitle("Connection");
            form.setHeaderText("Connection from " + from.getName()+" to " + to.getName());



            Optional<ButtonType> answer = form.showAndWait();
            Alert wrongInput = new Alert(Alert.AlertType.ERROR);
            if(answer.isPresent() && answer.get() == ButtonType.OK && form.getName().isEmpty()){
                wrongInput.setTitle("Error!");
                wrongInput.setHeaderText("Name can not be empty!");
                wrongInput.showAndWait();
                return;
            }
            else if(answer.isPresent() && answer.get() == ButtonType.OK && !form.getTime().matches(".*\\d.*")){
                wrongInput.setTitle("Error!");
                wrongInput.setHeaderText("Time must only include numbers");
                wrongInput.showAndWait();
                return;
            }
            else if(answer.isPresent() && answer.get() == ButtonType.OK && Integer.parseInt(form.getTime()) < 0){
                wrongInput.setTitle("Error!");
                wrongInput.setHeaderText("Time can not be less than 0");
                wrongInput.showAndWait();
                return;
            }
            else if(answer.isPresent() && answer.get() != ButtonType.OK){
                return;
            }

            graph.connect(from,to, form.getName(), Integer.parseInt(form.getTime()));
            Line line = new Line(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
            line.setDisable(true);
            center.getChildren().add(line);




        }
    }

    class SelectHandler implements EventHandler<MouseEvent>
    {
        @Override public void handle(MouseEvent event) {

            CircleLocation c = (CircleLocation)event.getSource();
            if (from==null && c != to){
                from = c;
                c.setFill(Color.RED);
            }
            else if (to == null && c != from)
            {
                to = c;
                c.setFill(Color.RED);
            }


            else if(from!=null && c == from){
                c.setFill(Color.BLUE);
                from=null;
            }

            else if(to!=null && c == to){
                c.setFill(Color.BLUE);
                to=null;
            }


        }

    }


    class MouseHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            if(addOne) {
                background.setCursor(Cursor.DEFAULT);

                NameForm form = new NameForm();

                Optional<ButtonType> answer = form.showAndWait();

                if(answer.isPresent() && answer.get() != ButtonType.OK){
                    addOne = false;
                    placeHandlerActive = false;
                    return;
                }


                CircleLocation location = new CircleLocation(event.getX(), event.getY() , form.getName());
                SelectHandler selectHandler = new SelectHandler();

                location.setOnMouseClicked(selectHandler);

                Text name = new Text(location.getCenterX()+7,location.getCenterY()+20,location.getName());

                name.setDisable(true);
                center.getChildren().addAll(location, name);
                graph.add(location);

                location.setId(location.getName());


                placeHandlerActive = false;
                addOne = false;
            }
        }
    }


    class PlaceHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            fileSave = false;
            if(!placeHandlerActive) {
                placeHandlerActive = true;
                background.setCursor(Cursor.CROSSHAIR);

                addOne = true;
                background.setOnMouseClicked(new PathFinder.MouseHandler());
            }
        }
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            if(!fileSave)
            {
                Alert unsaved = new Alert(Alert.AlertType.CONFIRMATION);
                unsaved.setTitle("Warning!");
                unsaved.setContentText("Unsaved changes, create new map anyways?");
                Optional<ButtonType> answer = unsaved.showAndWait();

                if(answer.isPresent() && answer.get() != ButtonType.OK)
                {
                    return;
                }
            }


            from = null;
            to = null;


            graph = new ListGraph<>();

            center.getChildren().clear();

            image = new Image("file:europa.gif");
            background = new ImageView(image);
            center.getChildren().add(background);



            stage.setHeight(image.getHeight()+108);

            stage.setMaxWidth(image.getWidth()+16);
            stage.setMaxHeight(image.getHeight()+108);






        }

    }




    class OpenHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if(!fileSave)
            {
                Alert unsaved = new Alert(Alert.AlertType.CONFIRMATION);
                unsaved.setTitle("Warning!");
                unsaved.setContentText("Unsaved changes, exit anyways?");
                Optional<ButtonType> answer = unsaved.showAndWait();

                if(answer.isPresent() && answer.get() != ButtonType.OK)
                {
                    return;
                }
            }



            try{

                FileReader file = new FileReader("europa.graph");

                BufferedReader in = new BufferedReader(file);
                String line;

                line = in.readLine();

                from = null;
                to = null;

                graph = new ListGraph<>();
                image = new Image(line);
                background = new ImageView(image);

                center.getChildren().clear();
                center.getChildren().add(background);

                stage.setHeight(image.getHeight()+108);

                stage.setMaxWidth(image.getWidth()+16);
                stage.setMaxHeight(image.getHeight()+108);

                line = in.readLine();


                String[] splitNode = line.split(";");

                for(int index = 0; index != splitNode.length; index+=3){

                    CircleLocation node = new CircleLocation(Double.parseDouble(splitNode[index+1]),Double.parseDouble(splitNode[index+2]),splitNode[index]);
                    Text name = new Text(Double.parseDouble(splitNode[index+1])+7,Double.parseDouble(splitNode[index+2])+20,splitNode[index]);
                    SelectHandler selectHandler = new SelectHandler();

                    node.setOnMouseClicked(selectHandler);
                    graph.add(node);
                    name.setDisable(true);
                    center.getChildren().addAll(node,name);
                }


                while((line = in.readLine()) != null){
                    try {
                        String[] splitEdge = line.split(";");
                        for (CircleLocation nodes : graph.getNodes()) {
                            if (nodes.getName().equals(splitEdge[0])) {
                                from = nodes;
                            } else if (nodes.getName().equals(splitEdge[1])) {
                                to = nodes;
                            }
                        }




                        graph.connect(from, to, splitEdge[2], Integer.parseInt(splitEdge[3]));
                        Line edge = new Line(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
                        edge.setDisable(true);
                        center.getChildren().add(edge);

                    }
                    catch (Exception ignore){}
                }

                in.close();
                file.close();


            }
            catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file!");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO-error " + e.getMessage());
                alert.showAndWait();
            }

            from = null;
            to = null;
        }
    }

    class ExitItemHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            if(!fileSave)
            {
                Alert unsaved = new Alert(Alert.AlertType.CONFIRMATION);
                unsaved.setTitle("Warning!");
                unsaved.setContentText("Unsaved changes, exit anyways?");
                Optional<ButtonType> answer = unsaved.showAndWait();

                if(answer.isPresent() && answer.get() != ButtonType.OK)
                {
                    return;
                }
            }
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            try{
                WritableImage image = root.snapshot(null, null);
                BufferedImage bimage = SwingFXUtils.fromFXImage(image,null);
                ImageIO.write(bimage, "png",new File("capture.png"));
            }catch(IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Fel!");
                alert.showAndWait();
            }
        }
    }







    private void save(){
        try {
            FileWriter file = new FileWriter("europa.graph");


            PrintWriter out = new PrintWriter(file);

            int i = graph.getNodes().size();

            out.println(background.getImage().getUrl());
            for(CircleLocation node: graph.getNodes()){
                out.print(node.getName()+";"+node.getCenterX()+";"+node.getCenterY());
                i--;
                if(i != 0)
                    out.print(";");
            }
            out.println();
            for (CircleLocation node: graph.getNodes()){
                for (Edge<CircleLocation> edge : graph.getEdgesFrom(node)) {
                    out.println(node.getName()+";"+edge.getDestination().getName()+";"+edge.getName()+";"+edge.getWeight());
                }
            }
            out.close();
            file.close();
            fileSave =true;


        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file!");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO-error " + e.getMessage());
            alert.showAndWait();
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            save();
        }
    }


    class ExitHandler implements EventHandler<WindowEvent> {
        @Override public void handle(WindowEvent event){
            if (!fileSave){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Unsaved changes, exit anyway?");
                alert.setContentText(null);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK)
                    event.consume();
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);

    }
}