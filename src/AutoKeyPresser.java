import gui.AutoKeyPresserGUI;

import javax.swing.*;

import static javax.swing.UIManager.*;

public class AutoKeyPresser {

    public static void main(String[] args) throws Exception {

        try {
            setLookAndFeel(getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            AutoKeyPresserGUI dialog = new AutoKeyPresserGUI();
            dialog.setTitle("Auto Key Presser");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
