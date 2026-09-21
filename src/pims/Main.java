package pims;

import javax.swing.*;
import pims.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) { }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}