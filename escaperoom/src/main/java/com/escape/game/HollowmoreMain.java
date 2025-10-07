package com.escape;

import com.escape.game.*;
import com.escape.data.HollowmoreDataLoader;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main entry point for Hollowmore Manor
 * Provides main menu and game initialization
 */
public class HollowmoreMain extends Application {
    
    private Stage primaryStage;
    private boolean useJsonData = false; // Toggle this to use JSON or hardcoded data
    
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        primaryStage.setTitle("Hollowmore Manor");
        primaryStage.setResizable(false);
        
        showMainMenu();
        primaryStage.show();
    }
    
    private void showMainMenu() {
        VBox menu = new VBox(30);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: #1a1a1a;");
        menu.setPrefSize(768, 576);
        
        // Title
        Label title = new Label("HOLLOWMORE MANOR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        title.setTextFill(Color.DARKRED);
        
        Label subtitle = new Label("A Murder Mystery Escape Game");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitle.setTextFill(Color.LIGHTGRAY);
        
        // Buttons
        Button startButton = createMenuButton("Start Game");
        Button demoButton = createMenuButton("Demo (Foyer Only)");
        Button testButton = createMenuButton("Console Test");
        Button exitButton = createMenuButton("Exit");
        
        // Button actions
        startButton.setOnAction(e -> startFullGame());
        demoButton.setOnAction(e -> startDemo());
        testButton.setOnAction(e -> runConsoleTest());
        exitButton.setOnAction(e -> primaryStage.close());
        
        // Instructions
        Label instructions = new Label(
            "Click objects to interact • ENTER to continue dialogue • ESC to close menus"
        );
        instructions.setFont(Font.font("Arial", 12));
        instructions.setTextFill(Color.GRAY);
        
        menu.getChildren().addAll(
            title, subtitle, 
            startButton, demoButton, testButton, exitButton,
            instructions
        );
        
        Scene scene = new Scene(menu, 768, 576);
        primaryStage.setScene(scene);
    }
    
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setPrefWidth(250);
        button.setPrefHeight(50);
        button.setStyle(
            "-fx-background-color: #8B0000; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #B22222; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: #8B0000; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10;"
            )
        );
        
        return button;
    }
    
    private void startFullGame() {
        try {
            HollowmoreGameManager gameManager = HollowmoreGameManager.getInstance();
            
            if (useJsonData) {
                // Load from JSON file
                System.out.println("Loading game data from JSON...");
                HollowmoreDataLoader.loadGameData(gameManager);
                gameManager.startGame("foyer");
            } else {
                // Use hardcoded demo data
                System.out.println("Using demo data...");
                setupDemoRooms(gameManager);
                gameManager.startGame("foyer");
            }
            
            // Create and show game UI
            HollowmoreUI gameUI = new HollowmoreUI();
            Scene gameScene = new Scene(gameUI, 768, 576);
            
            primaryStage.setScene(gameScene);
            gameUI.requestFocus();
            gameUI.startGameLoop();
            
        } catch (Exception e) {
            System.err.println("Error starting game: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Failed to start game. Check console for details.");
        }
    }
    
    private void startDemo() {
        try {
            // Run the demo with just Foyer and Parlor
            HollowmoreDemo demo = new HollowmoreDemo();
            demo.start(primaryStage);
            
        } catch (Exception e) {
            System.err.println("Error starting demo: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Failed to start demo. Check console for details.");
        }
    }
    
    private void runConsoleTest() {
        // Run console test in separate thread
        new Thread(() -> {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RUNNING CONSOLE TEST");
            System.out.println("=".repeat(50) + "\n");
            
            HollowmoreGameManager gameManager = HollowmoreGameManager.getInstance();
            setupDemoRooms(gameManager);
            gameManager.startGame("foyer");
            
            // Simulate gameplay
            System.out.println("\n--- Test 1: Collecting Items ---");
            gameManager.addToInventory("ledger_page_1");
            gameManager.addToInventory("ledger_page_2");
            gameManager.addToInventory("ledger_page_3");
            gameManager.printStatus();
            
            System.out.println("\n--- Test 2: Solving Puzzle ---");
            boolean solved = gameManager.solvePuzzle("1847");
            System.out.println("Puzzle solved: " + solved);
            
            if (solved) {
                gameManager.addToInventory("diary_scrap_1");
                gameManager.addToInventory("flashlight");
                gameManager.addToInventory("parlor_key");
            }
            
            gameManager.printStatus();
            
            System.out.println("\n--- Test 3: Room Transition ---");
            boolean moved = gameManager.transitionToRoom("parlor");
            System.out.println("Moved to parlor: " + moved);
            
            gameManager.printStatus();
            
            System.out.println("\n--- Test 4: Timer ---");
            System.out.println("Time remaining: " + gameManager.getTimeRemainingFormatted());
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("CONSOLE TEST COMPLETE");
            System.out.println("=".repeat(50) + "\n");
            
        }).start();
    }
    
    private void setupDemoRooms(HollowmoreGameManager gameManager) {
        // This is the simplified setup - use HollowmoreDemo's methods
        HollowmoreDemo.setupFoyerForDemo(gameManager);
        HollowmoreDemo.setupParlorForDemo(gameManager);
    }
    
    private void showErrorDialog(String message) {
        VBox errorBox = new VBox(20);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-background-color: #1a1a1a;");
        errorBox.setPrefSize(768, 576);
        
        Label errorLabel = new Label("ERROR");
        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        errorLabel.setTextFill(Color.RED);
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(600);
        
        Button backButton = createMenuButton("Back to Menu");
        backButton.setOnAction(e -> showMainMenu());
        
        errorBox.getChildren().addAll(errorLabel, messageLabel, backButton);
        
        Scene errorScene = new Scene(errorBox, 768, 576);
        primaryStage.setScene(errorScene);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * Extended HollowmoreDemo with public static setup methods
 */
class HollowmoreDemoExtended extends HollowmoreDemo {
    
    public static void setupFoyerForDemo(HollowmoreGameManager gameManager) {
        Room foyer = new Room("foyer", "The Foyer", 
            "The entry hall is lined with broken furniture, a chalk outline on the floor, " +
            "and a wall murder-board with pinned names.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("The town calls it Hollowmore.");
        dialogue.addOnEnter("They say it remembers those who enter... and keeps them.");
        dialogue.addOnPuzzleSolved("The credenza clicks open.");
        dialogue.addOnPuzzleSolved("Inside, you find a torn diary page and an old flashlight.");
        foyer.setDialogue(dialogue);
        
        LedgerAssemblyPuzzle puzzle = new LedgerAssemblyPuzzle("foyer_ledger", "1847", 3);
        puzzle.addHint("The pages contain dates and initials.");
        puzzle.addHint("Look for a pattern in the death dates.");
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("foyer_ledger", "LEDGER_ASSEMBLY", "Torn Ledger Pages");
        foyer.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        // Add objects
        CollectibleObject page1 = new CollectibleObject("ledger_page_1", "Torn Page", 150, 200, "page1.png");
        page1.setEvidenceValue("Victim 1: J.M. - 03/18/47");
        foyer.addObject(page1);
        
        CollectibleObject page2 = new CollectibleObject("ledger_page_2", "Torn Page", 500, 150, "page2.png");
        page2.setEvidenceValue("Victim 2: A.R. - 07/22/47");
        foyer.addObject(page2);
        
        CollectibleObject page3 = new CollectibleObject("ledger_page_3", "Torn Page", 350, 300, "page3.png");
        page3.setEvidenceValue("Victim 3: L.K. - 11/09/47");
        foyer.addObject(page3);
        
        ExamineObject board = new ExamineObject("murder_board", "Murder Board", 400, 100, "board.png");
        board.setExamineText("A cork board with photos connected by red string. Names are crossed out.");
        foyer.addObject(board);
        
        ContainerObject credenza = new ContainerObject("credenza", "Locked Credenza", 300, 400, "credenza.png");
        credenza.setUnlockCode("1847");
        credenza.addItem("diary_scrap_1");
        credenza.addItem("flashlight");
        credenza.addItem("parlor_key");
        foyer.addObject(credenza);
        
        foyer.setExitTo("parlor");
        gameManager.addRoom(foyer);
    }
    
    public static void setupParlorForDemo(HollowmoreGameManager gameManager) {
        Room parlor = new Room("parlor", "The Parlor",
            "Dusty furniture fills the room. Portraits hang on the walls.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("The portraits seem to watch your every move.");
        dialogue.addOnPuzzleSolved("The safe swings open!");
        parlor.setDialogue(dialogue);
        
        java.util.List<String> solution = java.util.Arrays.asList("BLUE", "BROWN", "GREEN", "HAZEL");
        PortraitEyesPuzzle puzzle = new PortraitEyesPuzzle("parlor_portraits", solution);
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("parlor_portraits", "PORTRAIT_EYES", "Portrait Eyes");
        parlor.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        parlor.setExitTo("library");
        parlor.setRequiredItem("parlor_key");
        gameManager.addRoom(parlor);
    }
}