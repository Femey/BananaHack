package me.banana;

import me.banana.commands.CommandTest;
import me.banana.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class BananaHack extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category BANANAHACK = new Category("BananaHack");
    public static final Category BANANATROLL = new Category("BananaTroll");

    @Override
    public void onInitialize() {
        LOG.info("Initializing BananaHack");

        // Modules
        Modules.get().add(new AutoMoan());
        Modules.get().add(new WobblyAnchor());
        Modules.get().add(new StrafePlus());
        Modules.get().add(new BananaRPC());
        Modules.get().add(new BedAuraPlus());
        Modules.get().add(new PrefixManager());
        Modules.get().add(new NoSlowPlus());
        Modules.get().add(new SprintPlus());
        Modules.get().add(new CrystalAuraPlus());
        Modules.get().add(new SpyglassAimbot());
        Modules.get().add(new HornyAura());
        Modules.get().add(new WideScaffold());
        Modules.get().add(new TargetStrafe());
        Modules.get().add(new ResetVL());
        Modules.get().add(new IgnoreDeaths());
        Modules.get().add(new AutoPorn());
        Modules.get().add(new LecternCrash());
        Modules.get().add(new SexDupe());


        // Commands
        Commands.get().add(new CommandTest());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(BANANAHACK);
        Modules.registerCategory(BANANATROLL);
    }

    @Override
    public String getPackage() {
        return "me.banana";
    }
}
