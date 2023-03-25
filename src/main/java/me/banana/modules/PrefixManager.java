package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class PrefixManager extends Module {
    public enum PrefixMode {
        BananaHack,
        Custom,
        Default
    }

    public enum Format {
        Normal,
        Heavy,
        Italic,
        Underline,
        Crossed,
        Cursed
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    // General
    private final Setting<String> bananahackPrefix = sgGeneral.add(new StringSetting.Builder()
            .name("bananahack-prefix")
            .description("What prefix to use for BananaHack modules.")
            .defaultValue("BananaHack")
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<SettingColor> bananahackColor = sgGeneral.add(new ColorSetting.Builder()
            .name("prefix-color")
            .description("Color display for the prefix.")
            .defaultValue(new SettingColor(255,193,0,255))
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<Format> bananahackFormatting = sgGeneral.add(new EnumSetting.Builder<Format>()
            .name("prefix-format")
            .description("What type of minecraft formatting should be applied to the prefix.")
            .defaultValue(Format.Normal)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<Boolean> bananahackFormatBrackets = sgGeneral.add(new BoolSetting.Builder()
            .name("format-brackets")
            .description("Whether the formatting should apply to the brackets as well.")
            .visible(() -> bananahackFormatting.get() != Format.Normal)
            .onChanged(cope -> setPrefixes())
            .defaultValue(true)
            .build()
    );

    private final Setting<String> bananahackLeftBracket = sgGeneral.add(new StringSetting.Builder()
            .name("left-bracket")
            .description("What to be displayed as left bracket for the prefix.")
            .defaultValue("[")
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<String> bananahackRightBracket = sgGeneral.add(new StringSetting.Builder()
            .name("right-bracket")
            .description("What to be displayed as right bracket for the prefix.")
            .defaultValue("]")
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<SettingColor> bananahackLeftColor = sgGeneral.add(new ColorSetting.Builder()
            .name("left-color")
            .description("Color display for the left bracket.")
            .defaultValue(new SettingColor(150,150,150,255))
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<SettingColor> bananahackRightColor = sgGeneral.add(new ColorSetting.Builder()
            .name("right-color")
            .description("Color display for the right bracket.")
            .defaultValue(new SettingColor(150,150,150,255))
            .onChanged(cope -> setPrefixes())
            .build()
    );


    // Meteor
    private final Setting<PrefixMode> prefixMode = sgGeneral.add(new EnumSetting.Builder<PrefixMode>()
            .name("prefix-mode")
            .description("What prefix to use for Meteor modules.")
            .defaultValue(PrefixMode.Default)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<String> meteorPrefix = sgGeneral.add(new StringSetting.Builder()
            .name("meteor-prefix")
            .description("What to use as meteor prefix text")
            .defaultValue("Motor")
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<SettingColor> meteorColor = sgGeneral.add(new ColorSetting.Builder()
            .name("prefix-color")
            .description("Color display for the meteor prefix")
            .defaultValue(new SettingColor(142, 60, 222, 255))
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<Format> meteorFormatting = sgGeneral.add(new EnumSetting.Builder<Format>()
            .name("prefix-format")
            .description("What type of minecraft formatting should be applied to the prefix.")
            .defaultValue(Format.Normal)
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<Boolean> meteorFormatBrackets = sgGeneral.add(new BoolSetting.Builder()
            .name("format-brackets")
            .description("Whether the formatting should apply to the brackets as well.")
            .visible(() -> prefixMode.get() == PrefixMode.Custom && meteorFormatting.get() != Format.Normal)
            .onChanged(cope -> setPrefixes())
            .defaultValue(true)
            .build()
    );

    private final Setting<String> meteorLeftBracket = sgGeneral.add(new StringSetting.Builder()
            .name("left-bracket")
            .description("What to be displayed as left bracket for the meteor prefix")
            .defaultValue("[")
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<String> meteorRightBracket = sgGeneral.add(new StringSetting.Builder()
            .name("right-bracket")
            .description("What to be displayed as right bracket for the meteor prefix")
            .defaultValue("]")
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<SettingColor> meteorLeftColor = sgGeneral.add(new ColorSetting.Builder()
            .name("left-color")
            .description("Color display for the left bracket")
            .defaultValue(new SettingColor(150,150,150,255))
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );

    private final Setting<SettingColor> meteorRightColor = sgGeneral.add(new ColorSetting.Builder()
            .name("right-color")
            .description("Color display for the right bracket")
            .defaultValue(new SettingColor(150,150,150,255))
            .visible(()-> prefixMode.get() == PrefixMode.Custom)
            .onChanged(cope -> setPrefixes())
            .build()
    );


    public PrefixManager() {
        super(BananaHack.BANANAHACK, "PrefixManager", "Allows you to customize prefixes used by Meteor.");
    }


    @Override
    public void onActivate(){
        setPrefixes();
    }

    @Override
    public void onDeactivate() {
        ChatUtils.unregisterCustomPrefix("me.banana.modules");
        ChatUtils.unregisterCustomPrefix("meteordevelopment");
    }

    public void setPrefixes() {
        if (isActive()) {
            ChatUtils.registerCustomPrefix("me.banana.modules", this::getBananaHackPrefix);

            switch (prefixMode.get()) {
                case BananaHack -> ChatUtils.registerCustomPrefix("meteordevelopment", this::getBananaHackPrefix);
                case Custom -> ChatUtils.registerCustomPrefix("meteordevelopment", this::getMeteorPrefix);
                case Default -> ChatUtils.unregisterCustomPrefix("meteordevelopment");
            }
        }
    }

    private Formatting getFormat(Format format) {
        return switch (format) {
            case Normal -> null;
            case Heavy -> Formatting.BOLD;
            case Italic -> Formatting.ITALIC;
            case Underline -> Formatting.UNDERLINE;
            case Cursed -> Formatting.OBFUSCATED;
            case Crossed -> Formatting.STRIKETHROUGH;
        };
    }

    public Text getBananaHackPrefix() {
        MutableText logo = Text.literal(bananahackPrefix.get());
        MutableText left = Text.literal(bananahackLeftBracket.get());
        MutableText right = Text.literal(bananahackRightBracket.get());
        MutableText prefix = Text.literal("");

        if (bananahackFormatting.get() != Format.Normal) logo.setStyle(Style.EMPTY.withFormatting(getFormat(bananahackFormatting.get())));
        logo.setStyle(logo.getStyle().withColor(TextColor.fromRgb(bananahackColor.get().getPacked())));

        if (bananahackFormatting.get() != Format.Normal && bananahackFormatBrackets.get()) left.setStyle(Style.EMPTY.withFormatting(getFormat(bananahackFormatting.get())));
        if (bananahackFormatting.get() != Format.Normal && bananahackFormatBrackets.get()) right.setStyle(Style.EMPTY.withFormatting(getFormat(bananahackFormatting.get())));
        left.setStyle(left.getStyle().withColor(TextColor.fromRgb(bananahackLeftColor.get().getPacked())));
        right.setStyle(right.getStyle().withColor(TextColor.fromRgb(bananahackRightColor.get().getPacked())));

        prefix.append(left);
        prefix.append(logo);
        prefix.append(right);
        prefix.append(" ");

        return prefix;
    }

    public Text getMeteorPrefix() {
        MutableText logo = Text.literal(meteorPrefix.get());
        MutableText left = Text.literal(meteorLeftBracket.get());
        MutableText right = Text.literal(meteorRightBracket.get());
        MutableText prefix = Text.literal("");

        if (meteorFormatting.get() != Format.Normal) logo.setStyle(Style.EMPTY.withFormatting(getFormat(meteorFormatting.get())));
        logo.setStyle(logo.getStyle().withColor(TextColor.fromRgb(meteorColor.get().getPacked())));

        if (meteorFormatting.get() != Format.Normal && meteorFormatBrackets.get()) left.setStyle(Style.EMPTY.withFormatting(getFormat(bananahackFormatting.get())));
        if (meteorFormatting.get() != Format.Normal && meteorFormatBrackets.get()) right.setStyle(Style.EMPTY.withFormatting(getFormat(bananahackFormatting.get())));
        left.setStyle(left.getStyle().withColor(TextColor.fromRgb(meteorLeftColor.get().getPacked())));
        right.setStyle(right.getStyle().withColor(TextColor.fromRgb(meteorRightColor.get().getPacked())));

        prefix.append(left);
        prefix.append(logo);
        prefix.append(right);
        prefix.append(" ");

        return prefix;
    }
}
