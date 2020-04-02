package main;

import db.connect;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.HTMLEditorSkin;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.objets.Page;
import main.objets.Section;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {
    connect connect = new connect();

    @FXML
    private MenuBar menubar;

    @FXML
    private Menu menuActions;

    @FXML
    private MenuItem menuAddSection;

    @FXML
    private MenuItem menuAddPage;

    @FXML
    private Menu menuApropos;

    @FXML
    private VBox vbSection;

    @FXML
    private VBox vbPage;

    @FXML
    private HTMLEditor content;

    @FXML
    private Button btSave;

    @FXML
    private Button btReset;

    @FXML
    private SplitPane splitPane;

    @FXML
    private TextField searchTextArea;

    @FXML
    private Button btSearch;

    /**
     * Lancement de la fenêtre, affiche les sections
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateSectionButton();

        /**
         * Gestion du retour chariot sur la touche entrée
         */
        content.addEventFilter(KeyEvent.KEY_PRESSED, event ->
        {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                final HTMLEditorSkin skin = (HTMLEditorSkin) content.getSkin();
                try {
                    final Method method = skin.getClass().getDeclaredMethod("executeCommand", String.class, String.class);
                    method.setAccessible(true);
                    method.invoke(skin, HTMLEditorSkin.Command.INSERT_NEW_LINE.getCommand(), null);
                } catch (Throwable ex) {
                    throw new RuntimeException("Impossible d'ajouter une nouvelle ligne", ex);
                }
            }
        });

        /**
         * Tâche pour replacer les diviseurs
         * https://stackoverflow.com/questions/36290539/set-divider-position-of-splitpane
         */
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                TimeUnit.MILLISECONDS.sleep(1000);
                splitPane.setDividerPositions(0.15, 0.30);
                return null;
            }
        };

        new Thread(task).start();
    }

    /**
     * Affiche les sections
     */
    private void generateSectionButton() {
        for (Section section : connect.getAllSections()) {
            Button bt = new Button();
            bt.getStyleClass().add("btMenu");
            bt.setPrefHeight(50);
            bt.setMinHeight(50);
            bt.setUserData(section);
            bt.setText(section.getNom());
            bt.setOnAction(actionEvent -> {
                clearPageButtons();
                content.setHtmlText("");
                generatePageButtons(section);
                SingletonTrade.getInstance().setCurrentSection(section);
                Stage stage = (Stage) content.getScene().getWindow();
                stage.setTitle("Jnotes - " + section.getNom());
            });

            ContextMenu cm = new ContextMenu();
            MenuItem delSection = new MenuItem("Supprimer cette section");
            MenuItem editSection = new MenuItem("Modifier le nom de cette section");
            cm.getItems().addAll(editSection, delSection);

            /**
             * évènement menu contectuel du bouton section
             */
            bt.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent event) {
                    cm.show(bt, event.getScreenX(), event.getScreenY());
                }
            });

            /**
             * évènement suppression de la section
             */
            delSection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    displayYesNo("Vous êtes sur le point de supprimer la section " + section.getNom() + ".\nToutes les pages de la section seront perdues.\nVoulez-vous continuer ?", "Supprimer une section");
                    if (SingletonTrade.getInstance().isYesno()) {
                        connect.deleteSection(section.getId());
                        connect.deletePagesFromSection(section.getId());
                        resetEverything();
                    }
                }
            });

            /**
             * évènement modification du nom de la section
             */
            editSection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    editSectionName(section);
                    resetEverything();
                }
            });

            bt.setPrefWidth(vbSection.getPrefWidth());
            vbSection.getChildren().addAll(bt);
        }
    }

    /**
     * Affiche les pages qui correspondent à la section sélectionnée
     *
     * @param section
     */
    private void generatePageButtons(Section section) {
        List<Page> pages = connect.getAllPagesFromSection(section.getId());
        for (Page page : pages) {
            Button bt = generatePageButton(page);
            bt.setOnAction(actionEvent -> {
                content.setHtmlText("");
                content.setUserData(page);
                content.setHtmlText(page.getContent());
                Stage stage = (Stage) content.getScene().getWindow();
                stage.setTitle("Jnotes - " + section.getNom() + " - " + page.getNom());
            });

            ContextMenu cm = new ContextMenu();
            MenuItem delPage = new MenuItem("Supprimer cette page");
            MenuItem editPage = new MenuItem("Modifier le nom de cette page");
            cm.getItems().addAll(editPage, delPage);

            /**
             * évènmenet menu contextuel d'un bouton page
             */
            bt.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent event) {
                    cm.show(bt, event.getScreenX(), event.getScreenY());
                }
            });

            /**
             * évènement suppression de la page
             */
            delPage.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    displayYesNo("Vous êtes sur le point de supprimer la page " + page.getNom() + ".\nTout son contenu sera perdu.\nVoulez-vous continuer ?", "Supprimer une page");
                    if (SingletonTrade.getInstance().isYesno()) {
                        connect.deletePage(page.getId());
                        resetPage(section);
                    }
                }
            });

            /**
             * Evènement modification du nom de la page
             */
            editPage.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    editPageName(page);
                    resetPage(section);
                }
            });

            bt.setPrefWidth(vbPage.getPrefWidth());
            vbPage.getChildren().addAll(bt);
        }
    }

    /**
     * Recherche des pages dont le contenu correspond à la recherche
     *
     * @param event
     */
    @FXML
    void SearchContent(ActionEvent event) {
        ((Stage) vbPage.getScene().getWindow()).setTitle("Jnotes - Recherche");
        clearPage();
        List<Page> pages = connect.getPageLike(searchTextArea.getText());
        for (Page page : pages) {
            Section section = connect.getSectionById(page.getSection());
            Button bt = generatePageButton(page);
            bt.setOnAction(actionEvent -> {
                content.setHtmlText("");
                content.setUserData(page);
                content.setHtmlText(page.getContent());
                Stage stage = (Stage) content.getScene().getWindow();
                stage.setTitle("Jnotes - Recherche - " + page.getNom());
            });

            ContextMenu cm = new ContextMenu();
            MenuItem delPage = new MenuItem("Supprimer cette page");
            MenuItem editPage = new MenuItem("Modifier le nom de cette page");
            cm.getItems().addAll(editPage, delPage);

            /**
             * évènmenet menu contextuel d'un bouton page
             */
            bt.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent event) {
                    cm.show(bt, event.getScreenX(), event.getScreenY());
                }
            });

            /**
             * évènement suppression de la page
             */
            delPage.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    displayYesNo("Vous êtes sur le point de supprimer la page " + page.getNom() + ".\nTout son contenu sera perdu.\nVoulez-vous continuer ?", "Supprimer une page");
                    if (SingletonTrade.getInstance().isYesno()) {
                        connect.deletePage(page.getId());
                        resetPage(section);
                    }
                }
            });

            /**
             * Evènement modification du nom de la page
             */
            editPage.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    editPageName(page);
                    resetPage(section);
                }
            });

            bt.setPrefWidth(vbPage.getPrefWidth());
            vbPage.getChildren().addAll(bt);
        }
    }

    /**
     * Replace le contenu actuel en base de la page dans le HTMLeditor
     *
     * @param event
     */
    @FXML
    void ResetContent(ActionEvent event) {
        if (content.getUserData() != null) {
            Page p = (Page) content.getUserData();
            content.setHtmlText(p.getContent());
        }
    }

    /**
     * Sauvegarde le contenu de la page
     *
     * @param event
     */
    @FXML
    void SaveContent(ActionEvent event) {
        if (content.getUserData() != null) {
            Page p = (Page) content.getUserData();
            p.setContent(content.getHtmlText());
            connect.updateContent(p);
        } else {
            displayMessageBox("Impossible de sauvegarder la page.", "Veuillez sélectionner une page avant de sauvegarder le contenu.", "Sauvegarder une page");
        }
    }

    /**
     * Ajout d'une page dans le menu Actions
     *
     * @param event
     */
    @FXML
    void menuAddPageAction(ActionEvent event) {
        createPage();
    }

    /**
     * Ajout d'une section dans le menu Actions
     *
     * @param event
     */
    @FXML
    void menuAddSectionAction(ActionEvent event) {
        createSection();
    }

    /**
     * Créé une nouvelle section
     */
    private void createSection() {
        displayUserInput("Veuillez entrer un nom pour la nouvelle section", "Créer une section");
        String retour = SingletonTrade.getInstance().getRetour();
        if (retour != null && !retour.isEmpty()) {
            if (connect.createSection(retour)) {
                clearSectionButtons();
                generateSectionButton();
                SingletonTrade.getInstance().setRetour("");
            }
        } else {
            System.out.println("Retour est vide ! : " + retour);
        }
    }

    /**
     * Créé une nouvelle page
     */
    private void createPage() {
        Section CurrentSection = SingletonTrade.getInstance().getCurrentSection();
        if (CurrentSection != null) {
            displayUserInput("Veuillez entrer un nom pour la nouvelle page qui sera créée dans : " + CurrentSection.getNom(), "Créer une page");
            String retour = SingletonTrade.getInstance().getRetour();
            if (retour != null && !retour.isEmpty()) {
                if (connect.createPage(retour, CurrentSection.getId())) {
                    clearPageButtons();
                    generatePageButtons(CurrentSection);
                    SingletonTrade.getInstance().setRetour("");
                }
            } else {
                System.out.println("Retour ou section est vide ! : " + retour);
            }
        } else {
            displayMessageBox("Impossible de créer la page.", "Veuillez sélectionner une section avant de créer une page.", "Créer une page");
        }
    }

    /**
     * Modifie le nom de la section
     *
     * @param section
     */
    private void editSectionName(Section section) {
        displayUserInput("Veuillez entrer le nouveau nom qui remplacera " + section.getNom(), "Modifier une section");
        String retour = SingletonTrade.getInstance().getRetour();
        if (retour != null && !retour.isEmpty()) {
            connect.editSection(section, retour);
        } else {
            System.out.println("Retour est vide ! : " + retour);
        }
    }

    /**
     * Modifie le nom de la page
     *
     * @param page
     */
    private void editPageName(Page page) {
        displayUserInput("Veuillez entrer le nouveau nom qui remplacera " + page.getNom(), "Modifier une page");
        String retour = SingletonTrade.getInstance().getRetour();
        if (retour != null && !retour.isEmpty()) {
            connect.editPage(page, retour);
        } else {
            System.out.println("Retour est vide ! : " + retour);
        }
    }

    /**
     * Efface les boutons dans le panneau section
     * TODO Comment je fais sans linq ?
     */
    private void clearSectionButtons() {
        List<Node> buttonsToRemove = new ArrayList<>();
        for (Node button : vbSection.getChildren()) {
            if (button instanceof Button) {
                buttonsToRemove.add(button);
            }
        }

        vbSection.getChildren().removeAll(buttonsToRemove);
        content.setUserData(null);
    }

    /**
     * Efface les boutons de le panneau page
     * TODO Comment je fais sans linq ?
     */
    private void clearPageButtons() {
        List<Node> buttonsToRemove = new ArrayList<>();
        for (Node button : vbPage.getChildren()) {
            if (button instanceof Button) {
                buttonsToRemove.add(button);
            }
        }

        vbPage.getChildren().removeAll(buttonsToRemove);
        content.setUserData(null);
    }

    /**
     * Affiche une boite de dialogue d'information
     *
     * @param message
     * @param content
     * @param title
     */
    private void displayMessageBox(String message, String content, String title) {
        try {
            MessageBoxController mbc = new MessageBoxController(message, content);
            FXMLLoader mb = new FXMLLoader(getClass().getResource("messageBox.fxml"));
            mb.setController(mbc);
            Parent dialogmb = mb.load();
            Stage stagemb = new Stage();
            stagemb.initModality(Modality.APPLICATION_MODAL);
            stagemb.setTitle(title);
            stagemb.setScene(new Scene(dialogmb, 600, 200));
            stagemb.showAndWait();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Affiche une boite de dialogue demandant une entrée utilisateur
     *
     * @param message
     * @param title
     */
    private void displayUserInput(String message, String title) {
        try {
            UserInputController ctrl = new UserInputController(message);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userInput.fxml"));
            loader.setController(ctrl);
            Parent dialog = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(dialog, 600, 200));
            stage.showAndWait();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Affiche une boite de dialogue Oui / Non
     *
     * @param message
     * @param title
     */
    private void displayYesNo(String message, String title) {
        try {
            yesnoController ctrl = new yesnoController(message);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("yesnoDialog.fxml"));
            loader.setController(ctrl);
            Parent dialog = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(dialog, 600, 200));
            stage.showAndWait();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Remet à zéro l'affichage
     */
    private void resetEverything() {
        SingletonTrade.getInstance().setYesno(false);
        clearPageButtons();
        clearSectionButtons();
        content.setHtmlText("");
        generateSectionButton();
        Stage stage = (Stage) content.getScene().getWindow();
        stage.setTitle("Jnotes");
    }

    /**
     * Remet à zéro le panneau page et le contenu
     */
    private void resetPage(Section section) {
        SingletonTrade.getInstance().setYesno(false);
        clearPageButtons();
        content.setHtmlText("");
        generatePageButtons(section);
        Stage stage = (Stage) content.getScene().getWindow();
        stage.setTitle("Jnotes - " + section.getNom());
    }

    /**
     * Vide le panneau page
     */
    private void clearPage() {
        clearPageButtons();
        content.setHtmlText("");
    }

    /**
     * Génère un bouton pour la page
     * @param page
     * @return
     */
    private Button generatePageButton(Page page){
        Button bt = new Button();
        bt.getStyleClass().add("btMenu");
        bt.setPrefHeight(50);
        bt.setMinHeight(50);
        bt.setUserData(page);
        bt.setText(page.getNom());
        return bt;
    }
}
