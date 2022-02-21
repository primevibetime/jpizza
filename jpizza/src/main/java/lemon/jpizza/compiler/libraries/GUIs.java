package lemon.jpizza.compiler.libraries;

import lemon.jpizza.compiler.vm.JPExtension;
import lemon.jpizza.compiler.vm.VM;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

public class GUIs extends JPExtension {

    static JFrame frame;

    public GUIs(VM vm) {
        super(vm);
    }

    @Override
    public String name() {
        return "guis";
    }

    @Override
    public void setup() {

        func("createGUI", (args) -> {

            frame = new JFrame(args[0].asString());
            frame.setTitle(args[0].asString());
            frame.setFocusTraversalKeysEnabled(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                URL url = new URL("https://raw.githubusercontent.com/Lemon-Chad/jpizza/main/pizzico512.png");
                Image image = ImageIO.read(url);
                frame.setIconImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Ok;
        }, Collections.singletonList("String"));

    }

}
