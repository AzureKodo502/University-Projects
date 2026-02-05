package com.example.progetto_ecommerce_v3.Model;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CollegamentiModel {

    public static void collegamentiSocial(String url){
        try {
            URI uri = new URI(url);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
