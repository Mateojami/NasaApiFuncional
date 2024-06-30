package org.example;

import org.example.view.NasaInterfaz;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NasaInterfaz viewer = new NasaInterfaz();
            viewer.setVisible(true);
        });
    }
}
