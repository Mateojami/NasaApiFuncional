package org.example.view;

import org.example.api.NasaApi;
import org.example.image.Photo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;

public class NasaInterfaz extends JFrame {
    private JTextField idField;
    private JButton searchButton;
    private JLabel photoLabel;
    private NasaApi apiClient;

    public NasaInterfaz() {
        apiClient = new NasaApi();

        setTitle("NASA Api Photos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        idField = new JTextField(10);
        topPanel.add(new JLabel("Photo ID:"));
        topPanel.add(idField);

        searchButton = new JButton("Buscar");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        add(photoLabel, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idText = idField.getText();
                if (!idText.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idText);
                        displayPhotoById(id);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(NasaInterfaz.this, "Ingrese un numero valido.");
                    }
                }
            }
        });
    }

    private void displayPhotoById(int id) {
        List<Photo> photos = apiClient.getPhotos();
        Optional<Photo> photoOptional = photos.stream()
                .filter(photo -> photo.getId() == id)
                .findFirst();

        if (photoOptional.isPresent()) {
            Photo photo = photoOptional.get();
            System.out.println("Foto encontrada: " + photo.getImg_src());
            try {
                URL url = new URL(photo.getImg_src());
                BufferedImage image = loadImageFromUrl(url);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    System.out.println("No se pudo cargar la foto.");
                    photoLabel.setIcon(null);
                    JOptionPane.showMessageDialog(this, "No se pudo cargar la foto.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                photoLabel.setIcon(null);
                JOptionPane.showMessageDialog(this, "Error al cargar la imagen desde la URL: " + photo.getImg_src());
            }
        } else {
            photoLabel.setIcon(null);
            JOptionPane.showMessageDialog(this, "Foto no encontrada");
        }
    }

    private BufferedImage loadImageFromUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("Código de respuesta: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream()) {
                return ImageIO.read(inputStream);
            }
        } else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            String newUrl = connection.getHeaderField("Location");
            System.out.println("Redirigido a: " + newUrl);
            return loadImageFromUrl(new URL(newUrl));
        } else {
            System.out.println("No se pudo conectar a la URL con el código de respuesta: " + responseCode);
        }
        return null;
    }
}
