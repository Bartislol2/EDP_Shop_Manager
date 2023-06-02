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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pl.gawryszewski.edp_projekt.application.MyListener;
import pl.gawryszewski.edp_projekt.model.DataBaseHandler;
import pl.gawryszewski.edp_projekt.model.Item;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MyShopController implements Initializable {
    private List<Item> items;
    private List<ItemController> itemControllers;
    private MyListener myListener;
    private ItemController currentItem;
    private ItemDescriptionController itemDescriptionController;
    private EventBus eventBus;
    @FXML
    private VBox menu;
    @FXML
    private FlowPane itemDisplay;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TextField userInput;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label loadingText;
    @FXML
    public void switchBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pl/gawryszewski/edp_projekt/hello-view.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadingText.setText("Fetching data...");
        loadingText.setVisible(true);
        Runnable task = () -> {
            FXMLLoader initLoader = new FXMLLoader();
            initLoader.setLocation(getClass().getResource("/pl/gawryszewski/edp_projekt/item-description-view.fxml"));
            Platform.runLater(() -> {
                try {
                    AnchorPane itemDescription = initLoader.load();
                    itemDescriptionController = initLoader.getController();
                    menu.getChildren().add(itemDescription);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            myListener = itemController -> {
                if (currentItem != null) {
                    currentItem.setUnclicked();
                }
                currentItem = itemController;
                currentItem.setClicked();
                itemDescriptionController.setValues(currentItem.getItem());
            };
            items = DataBaseHandler.getInstance().getAll();
            itemControllers = new ArrayList<>();
            Platform.runLater(() ->{
                for (int i = 0; i < items.size(); i++) {
                    addItemToDisplay(items.get(i));
                    progressBar.setProgress((double) (i + 1) / items.size());
                }
                loadingText.setText("Data loaded!");
            });
            eventBus = new EventBus();
            eventBus.register(this);
            System.out.println("Initialized");
        };
        new Thread(task).start();
    }
    @FXML
    public void showAddingItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/pl/gawryszewski/edp_projekt/item-dialog-view.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.setTitle("Add item");
            ItemDialogController controller = fxmlLoader.getController();
            dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            setDialogProperty(dialog, controller);
            Optional<ButtonType> result = dialog.showAndWait();
            if(result.isPresent() && result.get()==ButtonType.OK){
                Runnable task = () -> {
                    Item item = controller.createItem();
                    int id = DataBaseHandler.getInstance().addOne(item);
                    if(id!=-1){
                        items.add(item);
                        Platform.runLater(() -> addItemToDisplay(item));
                        item.setId(id);
                        System.out.println("Added");
                    }
                    else{
                        System.out.println("Could not add item");
                    }
                };
                new Thread(task).start();
            }
            else {
                System.out.println("CANCEL");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @FXML
    public void showUpdatingItemDialog() {
        if (currentItem != null)
        {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/pl/gawryszewski/edp_projekt/item-dialog-view.fxml"));
            try {
                DialogPane dialogPane = fxmlLoader.load();
                ItemDialogController controller = fxmlLoader.getController();
                controller.setValues(currentItem.getItem());
                dialog.getDialogPane().setContent(dialogPane);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                setDialogProperty(dialog, controller);
                dialog.setTitle("Update item");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK)
                {
                    Runnable task = () -> {
                        Item item = controller.createItem();
                        Platform.runLater(()->{
                            currentItem.updateItem(item);
                            DataBaseHandler.getInstance().updateItem(currentItem.getItem());
                            itemDescriptionController.setValues(currentItem.getItem());
                        });
                        System.out.println("Updated");
                    };
                    new Thread(task).start();
                }
                else
                {
                    System.out.println("CANCEL");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void deleteCurrentItem() {
        if(currentItem!=null)
        {
            Runnable task = () -> {
                Platform.runLater(()->{
                    itemDisplay.getChildren().remove(currentItem.getItemView());
                    items.remove(currentItem.getItem());
                    itemDescriptionController.clear();
                    currentItem = null;
                });
                DataBaseHandler.getInstance().deleteItem(currentItem.getItem());
                itemControllers.remove(currentItem);
                System.out.println("Deleted");
            };
            new Thread(task).start();
        }
    }

    private void addItemToDisplay(Item item){
        FXMLLoader itemLoader = new FXMLLoader();
        itemLoader.setLocation(getClass().getResource("/pl/gawryszewski/edp_projekt/item-view.fxml"));
        try {
            AnchorPane itemView = itemLoader.load();
            ItemController itemController = itemLoader.getController();
            itemController.setItem(item, myListener);
            itemController.setItemView(itemView);
            itemControllers.add(itemController);
            itemDisplay.getChildren().add(itemView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void getByFilter(){
        String filter = userInput.getText();
        progressBar.setProgress(0);
        loadingText.setText("Fetching data...");
        Runnable task = () -> {
            items = DataBaseHandler.getInstance().getByFilter(filter);
            itemControllers.clear();
            if(!items.isEmpty()){
                Platform.runLater(() -> eventBus.post(new ItemsReceivedEvent()));
            }
            else{
                Platform.runLater(() -> eventBus.post(new NoItemsFoundEvent()));
            }

        };
        new Thread(task).start();
    }

    private void setDialogProperty(Dialog<ButtonType> dialog, ItemDialogController controller){
        controller.getItemPrice().textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isFloat = false;
            try{
                Float.parseFloat(newValue);
                isFloat=true;
            } catch (NumberFormatException ignored){
            }
            dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(!isFloat);
        });
    }

    @Subscribe
    public void onItemsReceived(ItemsReceivedEvent event){
        itemDisplay.getChildren().clear();
        currentItem = null;
        itemDescriptionController.clear();
        loadingText.setVisible(true);
        for (int i = 0; i < items.size(); i++) {
            addItemToDisplay(items.get(i));
            progressBar.setProgress((double) (i + 1) / items.size());
        }
        loadingText.setText("Data loaded!");
    }

    @Subscribe
    public void noItemsFound(NoItemsFoundEvent event){
        itemDisplay.getChildren().clear();
        progressBar.setProgress(0);
        loadingText.setText("No items found!");
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
