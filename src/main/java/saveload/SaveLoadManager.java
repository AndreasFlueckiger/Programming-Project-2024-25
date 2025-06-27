package main.saveload;

import java.awt.FileDialog;
import java.io.*;

import main.logic.attack.Attack;
import main.logic.attack.AttackUtilities;
import main.ui.initialScreen.InitialFrame;
import main.rules.CtrlRules;
import main.rules.designPatterns.RulesFacade;

/**
 * Singleton class responsible for managing save and load operations
 * for the Battleship game's core state using Java serialization.
 */
public class SaveLoadManager {

    // Static instance for singleton pattern
    static SaveLoadManager s = null;

    /**
     * Private constructor to prevent external instantiation.
     */
    private SaveLoadManager() {}

    /**
     * Returns the singleton instance of the SaveLoadManager.
     * Instantiates the object lazily if not already created.
     */
    public static SaveLoadManager get() {
        if (s == null)
            s = new SaveLoadManager();
        return s;
    }

    /**
     * Serializes and saves the current game controller (CtrlRules)
     * to a user-specified file using a FileDialog.
     */
    public void Save() {
        // Get current controller from the RulesFacade
        CtrlRules ctrl = RulesFacade.getRules().getCtrl();

        try {
            // Open file dialog from the Attack window
            Attack attackFrame = Attack.getAttackFrame();
            FileDialog fd = new FileDialog(attackFrame, "Choose a file", FileDialog.SAVE);
            fd.setDirectory("./");
            fd.setFile("save.txt");
            fd.setVisible(true);

            String fileName = fd.getFile();
            if (fileName == null) {
                System.out.println("You cancelled the choice");
                return;
            } else {
                System.out.println("You chose " + fileName);
            }

            // Create output stream and serialize CtrlRules object
            FileOutputStream f = new FileOutputStream(new File(fileName));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(ctrl);
            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error initializing stream");
        } catch (Exception e) {
            System.out.println("Unknown error while saving, save file might be corrupted");
        }
    }

    /**
     * Deserializes and loads a previously saved CtrlRules object
     * from file, restoring the game state and updating the GUI.
     */
    public void Load() {
        try {
            // Open file dialog from the initial frame
            InitialFrame initialFrame = InitialFrame.getInitialFrame();
            FileDialog fd = new FileDialog(initialFrame, "Choose a file", FileDialog.LOAD);
            fd.setDirectory("./");
            fd.setVisible(true);

            String fileName = fd.getFile();
            if (fileName == null) {
                System.out.println("You cancelled the choice");
                return;
            } else {
                System.out.println("You chose " + fileName);
            }

            // Deserialize the CtrlRules object
            FileInputStream fi = new FileInputStream(new File(fileName));
            ObjectInputStream oi = new ObjectInputStream(fi);
            CtrlRules ctrl = (CtrlRules) oi.readObject();

            // Override the controller in the RulesFacade
            RulesFacade.getRules().overrideCtrl(ctrl);

            // Show attack frame and hide initial screen
            Attack.getAttackFrame().setVisible(true);
            initialFrame.setVisible(false);

            // Refresh the board UI and re-enable buttons
            ctrl.refreshBoard();
            AttackUtilities.getAttackUtilites().buttonEnable();

            oi.close();
            fi.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Should log a user-friendly message here
        } catch (Exception e) {
            System.out.println("Unknown error while loading, save file might be corrupted");
        }
    }
}
