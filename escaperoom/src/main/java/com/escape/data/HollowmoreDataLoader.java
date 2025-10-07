package com.escape.data;

import com.escape.game.*;
import com.escape.objects.*;
import com.escape.puzzles.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.*;

public class HollowmoreDataLoader {
    
    private static final String HOLLOWMORE_JSON = "json/hollowmore.json";
    
    public static void loadGameData(HollowmoreGameManager gameManager) {
        try {
            FileReader reader = new FileReader(HOLLOWMORE_JSON);
            JSONObject root = (JSONObject) new JSONParser().parse(reader);
            
            // Load game config
            loadGameConfig(root, gameManager);
            
            // Load all rooms
            JSONArray roomsArray = (JSONArray) root.get("rooms");
            for (Object obj : roomsArray) {
                JSONObject roomJson = (JSONObject) obj;
                Room room = loadRoom(roomJson, gameManager);
                gameManager.addRoom(room);
            }
            
            // Load all items
            JSONArray itemsArray = (JSONArray) root.get("items");
            if (itemsArray != null) {
                for (Object obj : itemsArray) {
                    JSONObject itemJson = (JSONObject) obj;
                    loadItem(itemJson);
                }
            }
            
            reader.close();
            System.out.println("✓ Game data loaded successfully from " + HOLLOWMORE_JSON);
            
        } catch (Exception e) {
            System.err.println("Failed to load game data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void loadGameConfig(JSONObject root, HollowmoreGameManager gameManager) {
        JSONObject config = (JSONObject) root.get("game_config");
        if (config != null) {
            String title = (String) config.get("title");
            Long timeLimit = (Long) config.get("time_limit_minutes");
            String startRoom = (String) config.get("player_start_room");
            
            System.out.println("Loading: " + title);
            System.out.println("Time limit: " + timeLimit + " minutes");
            System.out.println("Starting room: " + startRoom);
        }
    }
    
    private static Room loadRoom(JSONObject roomJson, HollowmoreGameManager gameManager) {
        String roomId = (String) roomJson.get("roomId");
        String name = (String) roomJson.get("name");
        String description = (String) roomJson.get("description");
        
        Room room = new Room(roomId, name, description);
        
        // Load puzzle
        JSONObject puzzleJson = (JSONObject) roomJson.get("puzzle");
        if (puzzleJson != null) {
            loadPuzzle(puzzleJson, room, gameManager);
        }
        
        // Load dialogue
        JSONObject dialogueJson = (JSONObject) roomJson.get("dialogue");
        if (dialogueJson != null) {
            loadDialogue(dialogueJson, room);
        }
        
        // Load interactive objects
        JSONArray objectsArray = (JSONArray) roomJson.get("interactiveObjects");
        if (objectsArray != null) {
            for (Object obj : objectsArray) {
                JSONObject objJson = (JSONObject) obj;
                InteractiveObject interactiveObj = loadInteractiveObject(objJson);
                if (interactiveObj != null) {
                    room.addObject(interactiveObj);
                }
            }
        }
        
        // Load exit info
        String exitTo = (String) roomJson.get("exitTo");
        if (exitTo != null) {
            room.setExitTo(exitTo);
        }
        
        String requiredItem = (String) roomJson.get("requiredItem");
        if (requiredItem != null) {
            room.setRequiredItem(requiredItem);
        }
        
        return room;
    }
    
    private static void loadPuzzle(JSONObject puzzleJson, Room room, HollowmoreGameManager gameManager) {
        String puzzleId = (String) puzzleJson.get("puzzleId");
        String type = (String) puzzleJson.get("type");
        String title = (String) puzzleJson.get("title");
        String description = (String) puzzleJson.get("description");
        
        RoomPuzzle roomPuzzle = new RoomPuzzle(puzzleId, type, title);
        room.setPuzzle(roomPuzzle);
        
        Puzzle puzzle = null;
        
        switch (type) {
            case "LEDGER_ASSEMBLY":
                String solution = (String) puzzleJson.get("solution");
                puzzle = new LedgerAssemblyPuzzle(puzzleId, solution, 3);
                break;
                
            case "PORTRAIT_EYES":
                JSONArray solutionArray = (JSONArray) puzzleJson.get("solution");
                List<String> portraitSolution = new ArrayList<>();
                for (Object s : solutionArray) {
                    portraitSolution.add((String) s);
                }
                puzzle = new PortraitEyesPuzzle(puzzleId, portraitSolution);
                break;
                
            case "CIPHER_DECODE":
                String encrypted = (String) puzzleJson.get("encryptedText");
                String decrypted = (String) puzzleJson.get("solution");
                Long shift = (Long) puzzleJson.get("shift");
                puzzle = new CipherPuzzle(puzzleId, encrypted, decrypted, shift.intValue());
                break;
                
            case "ITEM_ARRANGEMENT":
                JSONArray arrangementArray = (JSONArray) puzzleJson.get("solution");
                List<String> arrangement = new ArrayList<>();
                for (Object s : arrangementArray) {
                    arrangement.add((String) s);
                }
                puzzle = new ItemArrangementPuzzle(puzzleId, arrangement);
                break;
                
            case "LOGIC_GRID":
                JSONArray victims = (JSONArray) puzzleJson.get("victims");
                JSONArray weapons = (JSONArray) puzzleJson.get("weapons");
                JSONArray times = (JSONArray) puzzleJson.get("times");
                
                List<String> victimList = jsonArrayToStringList(victims);
                List<String> weaponList = jsonArrayToStringList(weapons);
                List<String> timeList = jsonArrayToStringList(times);
                
                LogicGridPuzzle logicPuzzle = new LogicGridPuzzle(puzzleId, victimList, weaponList, timeList);
                
                // Load solution
                JSONObject solutionObj = (JSONObject) puzzleJson.get("solution");
                String[][] solutionGrid = new String[victimList.size()][2];
                for (int i = 0; i < victimList.size(); i++) {
                    String victim = victimList.get(i);
                    JSONObject victimData = (JSONObject) solutionObj.get(victim);
                    solutionGrid[i][0] = (String) victimData.get("weapon");
                    solutionGrid[i][1] = (String) victimData.get("time");
                }
                logicPuzzle.setSolution(solutionGrid);
                puzzle = logicPuzzle;
                break;
                
            case "TOKEN_SEQUENCE":
                JSONArray tokenArray = (JSONArray) puzzleJson.get("solution");
                List<String> tokens = jsonArrayToStringList(tokenArray);
                puzzle = new TokenSequencePuzzle(puzzleId, tokens, tokens.size());
                break;
        }
        
        if (puzzle != null) {
            // Load hints
            JSONArray hintsArray = (JSONArray) puzzleJson.get("hints");
            if (hintsArray != null) {
                for (Object h : hintsArray) {
                    puzzle.addHint((String) h);
                }
            }
            
            gameManager.addPuzzle(puzzle);
        }
    }
    
    private static void loadDialogue(JSONObject dialogueJson, Room room) {
        RoomDialogue dialogue = new RoomDialogue();
        
        JSONArray onEnter = (JSONArray) dialogueJson.get("onEnter");
        if (onEnter != null) {
            for (Object line : onEnter) {
                dialogue.addOnEnter((String) line);
            }
        }
        
        JSONArray onSolved = (JSONArray) dialogueJson.get("onPuzzleSolved");
        if (onSolved != null) {
            for (Object line : onSolved) {
                dialogue.addOnPuzzleSolved((String) line);
            }
        }
        
        room.setDialogue(dialogue);
    }
    
    private static InteractiveObject loadInteractiveObject(JSONObject objJson) {
        String objectId = (String) objJson.get("objectId");
        String name = (String) objJson.get("name");
        String type = (String) objJson.get("type");
        
        Long xLong = (Long) objJson.get("x");
        Long yLong = (Long) objJson.get("y");
        double x = xLong != null ? xLong.doubleValue() : 0;
        double y = yLong != null ? yLong.doubleValue() : 0;
        
        String sprite = (String) objJson.get("sprite");
        String description = (String) objJson.get("description");
        
        InteractiveObject obj = null;
        
        switch (type) {
            case "COLLECTIBLE":
                CollectibleObject collectible = new CollectibleObject(objectId, name, x, y, sprite);
                collectible.setDescription(description);
                String evidenceValue = (String) objJson.get("evidenceValue");
                if (evidenceValue != null) {
                    collectible.setEvidenceValue(evidenceValue);
                }
                obj = collectible;
                break;
                
            case "CONTAINER":
                ContainerObject container = new ContainerObject(objectId, name, x, y, sprite);
                container.setDescription(description);
                
                String unlockCode = (String) objJson.get("unlockCode");
                if (unlockCode != null) {
                    container.setUnlockCode(unlockCode);
                }
                
                String unlockCondition = (String) objJson.get("unlockCondition");
                if (unlockCondition != null) {
                    container.setUnlockCondition(unlockCondition);
                }
                
                JSONArray containsItems = (JSONArray) objJson.get("containsItems");
                if (containsItems != null) {
                    for (Object itemId : containsItems) {
                        container.addItem((String) itemId);
                    }
                }
                
                Boolean locked = (Boolean) objJson.get("locked");
                if (locked != null && !locked) {
                    container.unlock();
                }
                
                obj = container;
                break;
                
            case "CYCLIC":
                CyclicObject cyclic = new CyclicObject(objectId, name, x, y, sprite);
                cyclic.setDescription(description);
                
                JSONArray states = (JSONArray) objJson.get("states");
                if (states != null) {
                    List<String> stateList = jsonArrayToStringList(states);
                    cyclic.setStates(stateList);
                }
                
                String currentState = (String) objJson.get("currentState");
                if (currentState != null) {
                    cyclic.setCurrentState(currentState);
                }
                
                obj = cyclic;
                break;
                
            case "DRAGGABLE":
                DraggableObject draggable = new DraggableObject(objectId, name, x, y, sprite);
                draggable.setDescription(description);
                obj = draggable;
                break;
                
            case "EXAMINE":
                ExamineObject examine = new ExamineObject(objectId, name, x, y, sprite);
                examine.setDescription(description);
                
                String examineText = (String) objJson.get("description");
                if (examineText != null) {
                    examine.setExamineText(examineText);
                }
                
                String containsText = (String) objJson.get("containsText");
                if (containsText != null) {
                    examine.setExamineText(containsText);
                }
                
                obj = examine;
                break;
                
            case "EVIDENCE_HOLDER":
                EvidenceHolderObject holder = new EvidenceHolderObject(objectId, name, x, y, sprite);
                holder.setDescription(description);
                
                String correctItem = (String) objJson.get("correctItem");
                if (correctItem != null) {
                    holder.setCorrectItem(correctItem);
                }
                
                obj = holder;
                break;
                
            case "TOKEN_SLOT":
                Long slotNum = (Long) objJson.get("slotNumber");
                int slotNumber = slotNum != null ? slotNum.intValue() : 0;
                
                TokenSlotObject slot = new TokenSlotObject(objectId, name, x, y, slotNumber);
                slot.setDescription(description);
                
                String correctToken = (String) objJson.get("correctToken");
                if (correctToken != null) {
                    slot.setCorrectToken(correctToken);
                }
                
                obj = slot;
                break;
                
            case "PUZZLE_SURFACE":
            case "AUDIO_PLAYER":
            default:
                // For now, create as examine object
                ExamineObject defaultObj = new ExamineObject(objectId, name, x, y, sprite);
                defaultObj.setDescription(description);
                obj = defaultObj;
                break;
        }
        
        return obj;
    }
    
    private static void loadItem(JSONObject itemJson) {
        String itemId = (String) itemJson.get("itemId");
        String name = (String) itemJson.get("name");
        String type = (String) itemJson.get("type");
        String description = (String) itemJson.get("description");
        String sprite = (String) itemJson.get("sprite");
        
        Item item = new Item(itemId, name, type);
        item.setDescription(description);
        item.setSprite(sprite);
        
        // In full implementation, add to game manager's item registry
    }
    
    // Helper method
    private static List<String> jsonArrayToStringList(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) {
            for (Object item : array) {
                list.add((String) item);
            }
        }
        return list;
    }
}

/**
 * Alternative loader that creates all rooms programmatically
 * Use this if you don't have the JSON file yet
 */
class HollowmoreRoomBuilder {
    
    public static void buildAllRooms(HollowmoreGameManager gameManager) {
        buildFoyer(gameManager);
        buildParlor(gameManager);
        buildLibrary(gameManager);
        buildKitchen(gameManager);
        buildGreenhouse(gameManager);
        buildCellar(gameManager);
        
        System.out.println("✓ All 6 rooms built successfully");
    }
    
    private static void buildFoyer(HollowmoreGameManager gameManager) {
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
        puzzle.addHint("Use the last two digits: 18__, 47__");
        
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
        
        ContainerObject credenza = new ContainerObject("credenza", "Locked Credenza", 300, 400, "credenza.png");
        credenza.setUnlockCode("1847");
        credenza.addItem("diary_scrap_1");
        credenza.addItem("flashlight");
        foyer.addObject(credenza);
        
        ExamineObject board = new ExamineObject("murder_board", "Murder Board", 400, 100, "board.png");
        board.setExamineText("Photos connected by red string. Initials: J.M., A.R., L.K.");
        foyer.addObject(board);
        
        foyer.setExitTo("parlor");
        gameManager.addRoom(foyer);
    }
    
    private static void buildParlor(HollowmoreGameManager gameManager) {
        Room parlor = new Room("parlor", "The Parlor",
            "Dusty furniture fills the room. Portraits hang on the walls, one with gouged-out eyes.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("The portraits seem to watch your every move.");
        dialogue.addOnPuzzleSolved("The safe swings open!");
        parlor.setDialogue(dialogue);
        
        List<String> solution = Arrays.asList("BLUE", "BROWN", "GREEN", "HAZEL");
        PortraitEyesPuzzle puzzle = new PortraitEyesPuzzle("parlor_portraits", solution);
        puzzle.addHint("Check the diary for eye colors.");
        puzzle.addHint("Blue, Brown, Green, Hazel - in order.");
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("parlor_portraits", "PORTRAIT_EYES", "Portrait Eyes");
        parlor.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        // Add portraits
        for (int i = 0; i < 4; i++) {
            CyclicObject portrait = new CyclicObject("portrait_" + (i+1), "Portrait " + (i+1),
                100 + i * 150, 150, "portrait.png");
            portrait.setStates(Arrays.asList("BLUE", "BROWN", "GREEN", "HAZEL"));
            portrait.setCurrentState("BROWN");
            parlor.addObject(portrait);
        }
        
        ContainerObject safe = new ContainerObject("hidden_safe", "Wall Safe", 450, 200, "safe.png");
        safe.addItem("parlor_key");
        safe.addItem("diary_scrap_2");
        parlor.addObject(safe);
        
        parlor.setExitTo("library");
        parlor.setRequiredItem("parlor_key");
        gameManager.addRoom(parlor);
    }
    
    private static void buildLibrary(HollowmoreGameManager gameManager) {
        Room library = new Room("library", "The Library",
            "Shelves lined with rotting books. A phonograph plays a warped lullaby.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("The air smells of burnt paper and decay.");
        dialogue.addOnPuzzleSolved("The text becomes clear: THE GREENHOUSE HOLDS THE FINAL SECRET");
        library.setDialogue(dialogue);
        
        CipherPuzzle puzzle = new CipherPuzzle("library_cipher",
            "WKH JUHHQKRXVH KROGV WKH ILQDO VHFUHW",
            "THE GREENHOUSE HOLDS THE FINAL SECRET", 3);
        puzzle.addHint("The phonograph shows the number 3.");
        puzzle.addHint("Try shifting each letter back by 3.");
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("library_cipher", "CIPHER_DECODE", "Ciphered Diary");
        library.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        ExamineObject diary = new ExamineObject("burned_diary", "Half-Burned Diary", 400, 300, "diary.png");
        diary.setExamineText("WKH JUHHQKRXVH KROGV WKH ILQDO VHFUHW");
        library.addObject(diary);
        
        ContainerObject bookshelf = new ContainerObject("bookshelf", "Bookshelf", 100, 200, "shelf.png");
        bookshelf.addItem("greenhouse_key");
        library.addObject(bookshelf);
        
        library.setExitTo("kitchen");
        gameManager.addRoom(library);
    }
    
    private static void buildKitchen(HollowmoreGameManager gameManager) {
        Room kitchen = new Room("kitchen", "The Kitchen",
            "A dining table set for six. Chairs overturned, cutlery scattered.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("The table was set for dinner that never happened.");
        dialogue.addOnPuzzleSolved("A hidden compartment opens, revealing diary scraps.");
        kitchen.setDialogue(dialogue);
        
        List<String> solution = Arrays.asList("FORK_LEFT", "PLATE_CENTER", "KNIFE_RIGHT", "SPOON_FAR_RIGHT");
        ItemArrangementPuzzle puzzle = new ItemArrangementPuzzle("kitchen_knives", solution);
        puzzle.addHint("Fork left, knife right, spoon outer right.");
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("kitchen_knives", "ITEM_ARRANGEMENT", "Table Settings");
        kitchen.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        kitchen.setExitTo("greenhouse");
        kitchen.setRequiredItem("greenhouse_key");
        gameManager.addRoom(kitchen);
    }
    
    private static void buildGreenhouse(HollowmoreGameManager gameManager) {
        Room greenhouse = new Room("greenhouse", "The Greenhouse",
            "Broken glass, creeping vines, mannequins staged like murder victims.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("The mannequins are arranged like a tableau.");
        dialogue.addOnPuzzleSolved("The cellar door clicks open.");
        greenhouse.setDialogue(dialogue);
        
        List<String> victims = Arrays.asList("J.M.", "A.R.", "L.K.", "S.T.");
        List<String> weapons = Arrays.asList("Knife", "Rope", "Poison", "Blunt");
        List<String> times = Arrays.asList("Midnight", "3AM", "Dawn", "Noon");
        
        LogicGridPuzzle puzzle = new LogicGridPuzzle("greenhouse_logic", victims, weapons, times);
        String[][] solution = {
            {"Knife", "Midnight"},
            {"Rope", "3AM"},
            {"Poison", "Dawn"},
            {"Blunt", "Noon"}
        };
        puzzle.setSolution(solution);
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("greenhouse_logic", "LOGIC_GRID", "Victim Pattern");
        greenhouse.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        greenhouse.setExitTo("cellar");
        gameManager.addRoom(greenhouse);
    }
    
    private static void buildCellar(HollowmoreGameManager gameManager) {
        Room cellar = new Room("cellar", "The Cellar - Marionette Room",
            "A stage-like space with ropes hanging. A grand marionette sits at the center.");
        
        RoomDialogue dialogue = new RoomDialogue();
        dialogue.addOnEnter("This is where the Hollowmaker stages their 'performances'.");
        dialogue.addOnPuzzleSolved("The killer's confession plays...");
        cellar.setDialogue(dialogue);
        
        List<String> tokens = Arrays.asList("TOKEN_1847", "TOKEN_DIARY", "TOKEN_KEY", "TOKEN_WEAPON");
        TokenSequencePuzzle puzzle = new TokenSequencePuzzle("cellar_final", tokens, 4);
        puzzle.addHint("Think chronologically - first victim to last.");
        
        RoomPuzzle puzzleInfo = new RoomPuzzle("cellar_final", "TOKEN_SEQUENCE", "Final Performance");
        cellar.setPuzzle(puzzleInfo);
        gameManager.addPuzzle(puzzle);
        
        gameManager.addRoom(cellar);
    }
}