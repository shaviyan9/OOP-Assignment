import ui.LoginFrame;
import ui.UIHelper;

import javax.swing.*;

/**
 * Application entry point.
 *
 * How to compile (from the PlacementSystem directory):
 *   javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out $(find src -name "*.java")
 *
 * How to run:
 *   java -cp "out:lib/mysql-connector-j-8.x.x.jar" Main
 *   (Windows: use ; instead of : for classpath separator)
 *
 * Prerequisites:
 *   1. MySQL server running with database created using database.sql
 *   2. mysql-connector-j JAR in the lib/ folder
 *   3. Update db/DBConnection.java with your MySQL USER and PASSWORD
 */
public class Main {
    public static void main(String[] args) {
        // Apply look-and-feel on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            UIHelper.applyLookAndFeel();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
