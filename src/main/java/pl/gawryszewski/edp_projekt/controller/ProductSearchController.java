package pl.gawryszewski.edp_projekt.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.gawryszewski.edp_projekt.application.MyListener;
import pl.gawryszewski.edp_projekt.model.ApiCommunicator;
import pl.gawryszewski.edp_projekt.model.Item;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProductSearchController implements Initializable {

    @FXML
    private Label loadingText;
    @FXML
    private TextField tfInput;
    @FXML
    private FlowPane itemDisplay;
    @FXML
    private VBox menu;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Spinner<Integer> offersAmount;
    @FXML
    private BorderPane mainBorderPane;
    private ItemController currentItem;
    private ApiCommunicator communicator;
    private ItemDescriptionController itemDescriptionController;
    private MyListener myListener;
    private EventBus eventBus;

    @FXML
    public void switchBack(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pl/gawryszewski/edp_projekt/hello-view.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Runnable task = () -> {
            communicator = new ApiCommunicator();
            FXMLLoader initLoader = new FXMLLoader();
            initLoader.setLocation(getClass().getResource("/pl/gawryszewski/edp_projekt/item-description-view.fxml"));
            Platform.runLater(()->{
                try {
                    AnchorPane itemDescription = initLoader.load();
                    itemDescription.setPrefHeight(475);
                    itemDescriptionController = initLoader.getController();
                    menu.getChildren().add(itemDescription);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            myListener = item -> {
                if (currentItem != null) {
                    currentItem.setUnclicked();
                }
                currentItem = item;
                currentItem.setClicked();
                itemDescriptionController.setValues(currentItem.getItem());
            };
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
            valueFactory.setValue(1);
            offersAmount.setValueFactory(valueFactory);
            eventBus = new EventBus();
            eventBus.register(this);
        };
        new Thread(task).start();
    }
    @FXML
    public void searchByUserInput(){
        String input = tfInput.getText();
        progressBar.setProgress(0);
        loadingText.setText("Fetching data...");
        loadingText.setVisible(true);
        Runnable task = () -> {
            List<Item> items = communicator.getOffersByProductName(input, offersAmount.getValue());
            if(!items.isEmpty()) {
                Platform.runLater(() -> {
                    itemDisplay.getChildren().clear();
                    loadingText.setVisible(true);
                    for (int i = 0; i < items.size(); i++) {
                        addItemToDisplay(items.get(i));
                        progressBar.setProgress((double) (i + 1) / items.size());
                    }
                    loadingText.setText("Data loaded!");
                });
            }
            else{
                Platform.runLater(()-> eventBus.post(new NoItemsFoundEvent()));
            }
        };
        new Thread(task).start();
    }
    private void addItemToDisplay(Item item){
        FXMLLoader itemLoader = new FXMLLoader();
        itemLoader.setLocation(getClass().getResource("/pl/gawryszewski/edp_projekt/item-view.fxml"));
        try {
            AnchorPane itemView = itemLoader.load();
            ItemController itemController = itemLoader.getController();
            itemController.setItem(item, myListener);
            itemController.setItemView(itemView);
            itemDisplay.getChildren().add(itemView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void noItemsFound(NoItemsFoundEvent event){
        itemDisplay.getChildren().clear();
        loadingText.setText("No offers found!");
        currentItem = null;
        itemDescriptionController.clear();
        Platform.runLater(()->{
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            dialog.getDialogPane().setContent(new Label("No items found"));
            dialog.setTitle("Error");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
        });
    }

}
