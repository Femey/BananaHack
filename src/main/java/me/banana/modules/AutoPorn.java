package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.systems.modules.Module;
import java.awt.Desktop;
import java.net.URI;

public class AutoPorn extends Module {
    public AutoPorn() {
        super(BananaHack.BANANATROLL, "AutoPorn", "Opens your browser to Porn");
    }

    @Override
    public void onActivate() {
        String url = "https://www.pornhub.com/video/search?search=trans";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
