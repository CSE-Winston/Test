module com.escape {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.escape to javafx.fxml;
    opens com.escape.game to javafx.fxml;
    
    exports com.escape;
    exports com.escape.game;
    exports com.escape.puzzles;
    exports com.escape.objects;
    exports com.escape.data;
}