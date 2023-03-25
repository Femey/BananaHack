package me.banana;

import me.banana.commands.CommandTest;
import me.banana.modules.PrefixManager;
import me.banana.modules.StrafePlus;
import me.banana.modules.WobblyAnchor;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class BananaHack extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category BANANAHACK = new Category("BananaHack");

    @Override
    public void onInitialize() {
        LOG.info("Initializing BananaHack");

        // Modules
        Modules.get().add(new WobblyAnchor());
        Modules.get().add(new StrafePlus());
        Modules.get().add(new PrefixManager());

        // Commands
        Commands.get().add(new CommandTest());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(BANANAHACK);
    }

    @Override
    public String getPackage() {
        return "me.banana";
    }
}