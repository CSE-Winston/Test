package com.escape.game;

import com.escape.objects.*;
import com.escape.puzzles.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

/**
 * Demo setup for Hollowmore Manor - Foyer Room
 * This demonstrates a complete working puzzle room
 */
public class HollowmoreDemo extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize game manager
        HollowmoreGameManager gameManager = HollowmoreGameManager.getInstance();
        
        // Setup the Foyer room
        setupFoyerRoom(gameManager);
        
        // Setup additional rooms (simplified for demo)
        setupParlorRoom(gameManager);
        
        // Start the game
        gameManager.startGame("foyer");
        
        // Create UI
        HollowmoreUI gameUI = new HollowmoreUI();
        Scene scene = new Scene(gameUI, 768, 576);
        
        primaryStage.setTitle("Hollowmore Manor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        gameUI.requestFocus();
        gameUI.startGameLoop();
    }
    
    private void setupFoyerRoom(HollowmoreGameManager gameManager) {
        // Create the Foyer room
        Room foyer = new Room("foyer", "The Foyer", 
            "The entry hall is lined with broken furniture, a chalk outline on the floor, " +
            "and a wall murder-board with pinned names. A locked credenza sits in the corner.");
        
        // Setup dialogue
        RoomDialogue foyerDialogue = new RoomDialogue();
        foyerDialogue.addOnEnter("The town calls it Hollowmore.");
        foyerDialogue.addOnEnter("They say it remembers those who enter... and keeps them.");
        foyerDialogue.addOnPuzzleSolved("The credenza clicks open.");
        foyerDialogue.addOnPuzzleSolved("Inside, you find a torn diary page and an old flashlight.");
        foyer.setDialogue(foyerDialogue);
        
        // Create the puzzle
        LedgerAssemblyPuzzle ledgerPuzzle = new LedgerAssemblyPuzzle("foyer_ledger", "1847", 3);
        ledgerPuzzle.addHint("The pages contain dates and initials.");
        ledgerPuzzle.addHint("Look for a pattern in the death dates.");
        ledgerPuzzle.addHint("The last two digits of each date: 18__, 47__");
        
        RoomPuzzle foyerPuzzleInfo = new RoomPuzzle("foyer_ledger", "LEDGER_ASSEMBLY", 
                                                     "Torn Ledger Pages");
        foyer.setPuzzle(foyerPuzzleInfo);
        gameManager.addPuzzle(ledgerPuzzle);
        
        // Create interactive objects
        
        // 1. Torn Page 1
        CollectibleObject page1 = new CollectibleObject("ledger_page_1", "Torn Page", 150, 200, "page1.png");
        page1.setDescription("A yellowed page with partial text");
        page1.setEvidenceValue("Victim 1: J.M. - 03/18/47");
        foyer.addObject(page1);
        
        // 2. Torn Page 2
        CollectibleObject page2 = new CollectibleObject("ledger_page_2", "Torn Page", 500, 150, "page2.png");
        page2.setDescription("Another torn page from the ledger");
        page2.setEvidenceValue("Victim 2: A.R. - 07/22/47");
        foyer.addObject(page2);
        
        // 3. Torn Page 3
        CollectibleObject page3 = new CollectibleObject("ledger_page_3", "Torn Page", 350, 300, "page3.png");
        page3.setDescription("The final piece of the ledger");
        page3.setEvidenceValue("Victim 3: L.K. - 11/09/47");
        foyer.addObject(page3);
        
        // 4. Murder Board (examine only)
        ExamineObject murderBoard = new ExamineObject("murder_board", "Murder Board", 400, 100, "board.png");
        murderBoard.setExamineText("A cork board covered with photos and red string. " +
                                  "Names are crossed out in black marker. You see initials: J.M., A.R., L.K.");
        foyer.addObject(murderBoard);
        
        // 5. Locked Credenza
        ContainerObject credenza = new ContainerObject("credenza", "Locked Credenza", 300, 400, "credenza.png");
        credenza.setDescription("A dusty wooden credenza with a 4-digit combination lock.");
        credenza.setUnlockCode("1847");
        credenza.addItem("diary_scrap_1");
        credenza.addItem("flashlight");
        foyer.addObject(credenza);
        
        // Set room exit
        foyer.setExitTo("parlor");
        
        // Add room to game
        gameManager.addRoom(foyer);
        
        // Add items that can be found
        Item diaryScraps = new Item("diary_scrap_1", "Diary Scrap - Part 1", "EVIDENCE");
        diaryScraps.setDescription("A torn page mentioning the first victim.");
        // gameManager.addItem(diary1); -- would add to game's item registry
        
        Item flashlight = new Item("flashlight", "Flashlight", "TOOL");
        flashlight.setDescription("An old but functional flashlight.");
        // gameManager.addItem(flashlight);
        
        System.out.println("=== FOYER SETUP COMPLETE ===");
        System.out.println("Puzzle: Collect 3 torn pages and find the code (1847)");
        System.out.println("Unlock the credenza to progress");
        System.out.println("================================\n");
    }
    
    private void setupParlorRoom(HollowmoreGameManager gameManager) {
        // Create Parlor room
        Room parlor = new Room("parlor", "The Parlor",
            "Dusty furniture fills the room. Portraits hang on the walls, " +
            "one with gouged-out eyes. A safe hums behind it.");
        
        // Setup dialogue
        RoomDialogue parlorDialogue = new RoomDialogue();
        parlorDialogue.addOnEnter("The portraits seem to watch your every move.");
        parlorDialogue.addOnEnter("One has had its eyes violently gouged out.");
        parlorDialogue.addOnPuzzleSolved("A clicking sound comes from behind the portrait.");
        parlorDialogue.addOnPuzzleSolved("The safe swings open, revealing its contents.");
        parlor.setDialogue(parlorDialogue);
        
        // Create portrait puzzle
        List<String> solution = Arrays.asList("BLUE", "BROWN", "GREEN", "HAZEL");
        PortraitEyesPuzzle portraitPuzzle = new PortraitEyesPuzzle("parlor_portraits", solution);
        portraitPuzzle.addHint("Check the ledger for eye color descriptions.");
        portraitPuzzle.addHint("The portraits are in chronological order.");
        portraitPuzzle.addHint("Blue eyes, brown eyes, green eyes, hazel eyes.");
        
        RoomPuzzle parlorPuzzleInfo = new RoomPuzzle("parlor_portraits", "PORTRAIT_EYES", 
                                                      "Portrait Eyes");
        parlor.setPuzzle(parlorPuzzleInfo);
        gameManager.addPuzzle(portraitPuzzle);
        
        // Create portrait objects
        for (int i = 0; i < 4; i++) {
            CyclicObject portrait = new CyclicObject("portrait_" + (i+1), 
                                                     "Portrait " + (i+1), 
                                                     100 + i * 150, 150, 
                                                     "portrait" + (i+1) + ".png");
            portrait.setStates(Arrays.asList("BLUE", "BROWN", "GREEN", "HAZEL"));
            portrait.setCurrentState("BROWN"); // Default state
            parlor.addObject(portrait);
        }
        
        // Safe
        ContainerObject safe = new ContainerObject("hidden_safe", "Wall Safe", 450, 200, "safe.png");
        safe.setDescription("A heavy wall safe behind one of the portraits.");
        safe.addItem("parlor_key");
        safe.addItem("diary_scrap_2");
        parlor.addObject(safe);
        
        parlor.setExitTo("library");
        parlor.setRequiredItem("parlor_key");
        
        gameManager.addRoom(parlor);
        
        System.out.println("=== PARLOR SETUP COMPLETE ===");
        System.out.println("Puzzle: Adjust portrait eyes to BLUE, BROWN, GREEN, HAZEL");
        System.out.println("============================\n");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * Alternative console-based demo for testing without JavaFX
 */
class ConsoleDemo {
    public static void main(String[] args) {
        HollowmoreGameManager gameManager = HollowmoreGameManager.getInstance();
        
        // Setup foyer
        setupFoyerForConsole(gameManager);
        
        // Start game
        gameManager.startGame("foyer");
        
        // Simulate gameplay
        System.out.println("\n--- SIMULATING FOYER GAMEPLAY ---\n");
        
        // Collect pages
        System.out.println("1. Collecting torn pages...");
        gameManager.addToInventory("ledger_page_1");
        gameManager.addToInventory("ledger_page_2");
        gameManager.addToInventory("ledger_page_3");
        
        gameManager.printStatus();
        
        // Try to solve puzzle
        System.out.println("2. Attempting to solve ledger puzzle...");
        boolean solved = gameManager.solvePuzzle("1847");
        
        if (solved) {
            System.out.println("✓ Puzzle solved!");
            gameManager.addToInventory("diary_scrap_1");
            gameManager.addToInventory("flashlight");
        }
        
        gameManager.printStatus();
        
        // Move to next room
        System.out.println("\n3. Moving to Parlor...");
        gameManager.transitionToRoom("parlor");
        
        gameManager.printStatus();
    }
    
    private static void setupFoyerForConsole(HollowmoreGameManager gameManager) {
        Room foyer = new Room("foyer", "The Foyer", 
            "The