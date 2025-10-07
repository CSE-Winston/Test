package com.escape.puzzles;

import java.util.ArrayList;
import java.util.List;

public abstract class Puzzle {
    protected String puzzleId;
    protected String type;
    protected String title;
    protected String description;
    protected String difficulty;
    protected boolean isSolved;
    protected List<String> hints;
    protected int attemptsRemaining;
    
    public Puzzle(String puzzleId, String type, String title, String description, String difficulty) {
        this.puzzleId = puzzleId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.isSolved = false;
        this.hints = new ArrayList<>();
        this.attemptsRemaining = 3;
    }
    
    // Abstract methods that each puzzle type must implement
    public abstract boolean checkSolution(Object playerInput);
    public abstract String getSolutionHint(int hintLevel);
    public abstract void reset();
    
    // Common methods
    public boolean solve(Object playerInput) {
        if (isSolved) {
            return true;
        }
        
        boolean correct = checkSolution(playerInput);
        if (correct) {
            isSolved = true;
            onPuzzleSolved();
        } else {
            attemptsRemaining--;
            onPuzzleFailed();
        }
        return correct;
    }
    
    protected void onPuzzleSolved() {
        System.out.println("Puzzle solved: " + title);
    }
    
    protected void onPuzzleFailed() {
        System.out.println("Incorrect. Attempts remaining: " + attemptsRemaining);
    }
    
    public void addHint(String hint) {
        hints.add(hint);
    }
    
    public String getHint(int index) {
        if (index >= 0 && index < hints.size()) {
            return hints.get(index);
        }
        return "No more hints available.";
    }
    
    // Getters and setters
    public String getPuzzleId() { return puzzleId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isSolved() { return isSolved; }
    public int getAttemptsRemaining() { return attemptsRemaining; }
    public List<String> getAllHints() { return new ArrayList<>(hints); }
}

// Ledger Assembly Puzzle - Foyer
class LedgerAssemblyPuzzle extends Puzzle {
    private String correctCode;
    private List<String> collectedPages;
    private int requiredPages;
    
    public LedgerAssemblyPuzzle(String puzzleId, String solution, int requiredPages) {
        super(puzzleId, "LEDGER_ASSEMBLY", "Torn Ledger Pages", 
              "Collect and reassemble torn pages to reveal a code.", "EASY");
        this.correctCode = solution;
        this.collectedPages = new ArrayList<>();
        this.requiredPages = requiredPages;
    }
    
    public void collectPage(String pageContent) {
        if (!collectedPages.contains(pageContent)) {
            collectedPages.add(pageContent);
        }
    }
    
    public boolean hasAllPages() {
        return collectedPages.size() >= requiredPages;
    }
    
    @Override
    public boolean checkSolution(Object playerInput) {
        if (!(playerInput instanceof String)) {
            return false;
        }
        String inputCode = (String) playerInput;
        return inputCode.equals(correctCode);
    }
    
    @Override
    public String getSolutionHint(int hintLevel) {
        switch(hintLevel) {
            case 0: return "Look at the dates on the pages.";
            case 1: return "Use the last two digits of each date.";
            case 2: return "The code is 1847.";
            default: return "No more hints.";
        }
    }
    
    @Override
    public void reset() {
        isSolved = false;
        collectedPages.clear();
        attemptsRemaining = 3;
    }
    
    public int getCollectedPageCount() {
        return collectedPages.size();
    }
}

// Portrait Eyes Puzzle - Parlor
class PortraitEyesPuzzle extends Puzzle {
    private List<String> correctSequence;
    private List<String> currentSequence;
    
    public PortraitEyesPuzzle(String puzzleId, List<String> solution) {
        super(puzzleId, "PORTRAIT_EYES", "Portrait Eyes", 
              "Adjust portrait eyes to match victim descriptions.", "EASY_MEDIUM");
        this.correctSequence = new ArrayList<>(solution);
        this.currentSequence = new ArrayList<>();
        // Initialize with random states
        for (int i = 0; i < solution.size(); i++) {
            currentSequence.add("BROWN");
        }
    }
    
    public void cyclePortrait(int portraitIndex) {
        String[] eyeColors = {"BLUE", "BROWN", "GREEN", "HAZEL"};
        String current = currentSequence.get(portraitIndex);
        
        int currentIndex = 0;
        for (int i = 0; i < eyeColors.length; i++) {
            if (eyeColors[i].equals(current)) {
                currentIndex = i;
                break;
            }
        }
        
        int nextIndex = (currentIndex + 1) % eyeColors.length;
        currentSequence.set(portraitIndex, eyeColors[nextIndex]);
    }
    
    @Override
    public boolean checkSolution(Object playerInput) {
        // Check if current sequence matches correct sequence
        return currentSequence.equals(correctSequence);
    }
    
    @Override
    public String getSolutionHint(int hintLevel) {
        switch(hintLevel) {
            case 0: return "Check the ledger for eye color descriptions.";
            case 1: return "The portraits are in order: Blue, Brown, Green, Hazel.";
            case 2: return "Portrait 1: BLUE, Portrait 2: BROWN, Portrait 3: GREEN, Portrait 4: HAZEL";
            default: return "No more hints.";
        }
    }
    
    @Override
    public void reset() {
        isSolved = false;
        currentSequence.clear();
        for (int i = 0; i < correctSequence.size(); i++) {
            currentSequence.add("BROWN");
        }
        attemptsRemaining = 3;
    }
    
    public List<String> getCurrentSequence() {
        return new ArrayList<>(currentSequence);
    }
}

// Cipher Puzzle - Library
class CipherPuzzle extends Puzzle {
    private String encryptedText;
    private String decryptedSolution;
    private int caesarShift;
    
    public CipherPuzzle(String puzzleId, String encrypted, String solution, int shift) {
        super(puzzleId, "CIPHER_DECODE", "Ciphered Diary", 
              "Decode the scrambled text.", "MEDIUM");
        this.encryptedText = encrypted;
        this.decryptedSolution = solution;
        this.caesarShift = shift;
    }
    
    public String decrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) ((c - base - shift + 26) % 26 + base);
            }
            result.append(c);
        }
        return result.toString();
    }
    
    @Override
    public boolean checkSolution(Object playerInput) {
        if (!(playerInput instanceof String)) {
            return false;
        }
        String input = ((String) playerInput).toUpperCase().trim();
        return input.equals(decryptedSolution.toUpperCase().trim());
    }
    
    @Override
    public String getSolutionHint(int hintLevel) {
        switch(hintLevel) {
            case 0: return "The phonograph shows the number 3.";
            case 1: return "Try shifting each letter back by 3 positions.";
            case 2: return "The message says: " + decryptedSolution;
            default: return "No more hints.";
        }
    }
    
    @Override
    public void reset() {
        isSolved = false;
        attemptsRemaining = 3;
    }
    
    public String getEncryptedText() {
        return encryptedText;
    }
}

// Item Arrangement Puzzle - Kitchen
class ItemArrangementPuzzle extends Puzzle {
    private List<String> correctArrangement;
    private List<String> currentArrangement;
    
    public ItemArrangementPuzzle(String puzzleId, List<String> solution) {
        super(puzzleId, "ITEM_ARRANGEMENT", "Table Settings", 
              "Arrange items in the correct order.", "MEDIUM");
        this.correctArrangement = new ArrayList<>(solution);
        this.currentArrangement = new ArrayList<>();
    }
    
    public void placeItem(String item, int position) {
        while (currentArrangement.size() <= position) {
            currentArrangement.add(null);
        }
        currentArrangement.set(position, item);
    }
    
    @Override
    public boolean checkSolution(Object playerInput) {
        return currentArrangement.equals(correctArrangement);
    }
    
    @Override
    public String getSolutionHint(int hintLevel) {
        switch(hintLevel) {
            case 0: return "Check the diary for table etiquette.";
            case 1: return "Fork left, plate center, knife right, spoon far right.";
            case 2: return "Correct order: " + correctArrangement.toString();
            default: return "No more hints.";
        }
    }
    
    @Override
    public void reset() {
        isSolved = false;
        currentArrangement.clear();
        attemptsRemaining = 3;
    }
}

// Logic Grid Puzzle - Greenhouse
class LogicGridPuzzle extends Puzzle {
    private List<String> victims;
    private List<String> weapons;
    private List<String> times;
    private String[][] solution; // [victim_index][0=weapon, 1=time]
    private String[][] playerGrid;
    
    public LogicGridPuzzle(String puzzleId, List<String> victims, 
                          List<String> weapons, List<String> times) {
        super(puzzleId, "LOGIC_GRID", "Victim Pattern", 
              "Deduce the murder pattern.", "MEDIUM_HARD");
        this.victims = new ArrayList<>(victims);
        this.weapons = new ArrayList<>(weapons);
        this.times = new ArrayList<>(times);
        this.playerGrid = new String[victims.size()][2];
    }
    
    public void setSolution(String[][] solution) {
        this.solution = solution;
    }
    
    public void assignToVictim(int victimIndex, String weapon, String time) {
        playerGrid[victimIndex][0] = weapon;
        playerGrid[victimIndex][1] = time;
    }
    
    @Override
    public boolean checkSolution(Object playerInput) {
        for (int i = 0; i < victims.size(); i++) {
            if (!playerGrid[i][0].equals(solution[i][0]) || 
                !playerGrid[i][1].equals(solution[i][1])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String getSolutionHint(int hintLevel) {
        switch(hintLevel) {
            case 0: return "Read all the clues carefully.";
            case 1: return "J.M. at midnight with knife, A.R. at 3AM with rope.";
            case 2: return "L.K. at dawn with poison, S.T. at noon with blunt object.";
            default: return "No more hints.";
        }
    }
    
    @Override
    public void reset() {
        isSolved = false;
        playerGrid = new String[victims.size()][2];
        attemptsRemaining = 3;
    }
}

// Token Sequence Puzzle - Cellar
class TokenSequencePuzzle extends Puzzle {
    private List<String> correctSequence;
    private List<String> currentSequence;
    private int slotCount;
    
    public TokenSequencePuzzle(String puzzleId, List<String> solution, int slots) {
        super(puzzleId, "TOKEN_SEQUENCE", "Final Performance", 
              "Place tokens in the correct order.", "HARD");
        this.correctSequence = new ArrayList<>(solution);
        this.currentSequence = new ArrayList<>();
        this.slotCount = slots;
    }
    
    public void placeToken(String token, int slotIndex) {
        while (currentSequence.size() <= slotIndex) {
            currentSequence.add(null);
        }
        currentSequence.set(slotIndex, token);
    }
    
    public void removeToken(int slotIndex) {
        if (slotIndex < currentSequence.size()) {
            currentSequence.set(slotIndex, null);
        }
    }
    
    @Override
    public boolean checkSolution(Object playerInput) {
        if (currentSequence.size() != correctSequence.size()) {
            return false;
        }
        return currentSequence.equals(correctSequence);
    }
    
    @Override
    public String getSolutionHint(int hintLevel) {
        switch(hintLevel) {
            case 0: return "Think chronologically - first to last.";
            case 1: return "The phonograph melody matches the sequence.";
            case 2: return "Correct order: " + correctSequence.toString();
            default: return "No more hints.";
        }
    }
    
    @Override
    public void reset() {
        isSolved = false;
        currentSequence.clear();
        attemptsRemaining = 3;
    }
    
    public List<String> getCurrentSequence() {
        return new ArrayList<>(currentSequence);
    }
}