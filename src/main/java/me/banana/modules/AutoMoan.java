package me.banana.modules;


import me.banana.BananaHack;
import me.banana.util.OLEPOSSUtils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoMoan extends Module {

    public AutoMoan() {
        super(BananaHack.BANANATROLL, "AutoMoan","Moans sexual things to the closest person");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<MoanMode> moanmode = sgGeneral.add(new EnumSetting.Builder<MoanMode>()
        .name("Message Mode")
        .description("What kind of messages to send.")
        .defaultValue(MoanMode.Submissive)
        .build()
    );

    private final Setting<Boolean> iFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("Ignore Friends")
        .description("Doesn't send messages when there is only friends nearby.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("Delay")
        .description("Tick delay between moans.")
        .defaultValue(30)
        .range(0, 60)
        .sliderRange(0, 60)
        .build()
    );
    private final Setting<Boolean> msg = sgGeneral.add(new BoolSetting.Builder()
        .name("Msg")
        .description("msgs instead of putting it in main chat")
        .defaultValue(false)
        .build()
    );


    public enum MoanMode {
        Dominant,
        Submissive,
    }

    int lastNum;
    List<Message> messageQueue = new ArrayList<>();

    double timer = 0;
    static final String[] Submissive = new String[]{
        "fuck me harder daddy",
        "deeper! daddy deeper!",
        "Fuck yes your so big!",
        "I love your cock %s!",
        "Do not stop fucking my ass before i cum!",
        "Oh your so hard for me",
        "Want to widen my ass up %s?",
        "I love you daddy",
        "Make my bussy pop",
        "%s loves my bussy so much",
        "i made %s cum so hard with my tight bussy",
        "Your cock is so big and juicy daddy!",
        "Please fuck me as hard as you can",
        "im %s's personal femboy cumdupster!",
        "Please shoot your hot load deep inside me daddy!",
        "I love how %s's dick feels inside of me!",
        "%s gets so hard when he sees my ass!",
        "%s really loves fucking my ass really hard!",
        "why wont u say the last message",
    };

    static final String[] Dominant = new String[]{
        "Be a good boy for daddy",
        "I love pounding your ass %s!",
        "Give your bussy to daddy!",
        "I love how you drip pre-cum while i fuck your ass %s",
        "Slurp up and down my cock like a good boy",
        "Come and jump on daddy's cock %s",
        "I love how you look at me while you suck me off %s",
        "%s looks so cute when i fuck him",
        "%s's bussy is so incredibly tight!",
        "%s takes dick like the good boy he is",
        "I love how you shake your ass on my dick",
        "%s moans so cutely when i fuck his ass",
        "%s is the best cum dumpster there is!",
        "%s is always horny and ready for his daddy's dick",
        "My dick gets rock hard every time i see %s",
        "why wont u say the last message",
    };
    Random r = new Random();

    @EventHandler
    private void onRender(Render3DEvent event){
        timer = Math.min(delay.get(), timer + event.frameTime);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        timer++;
        if (mc.player != null && mc.world != null && !(getClosest() == null)) {
            MOAN();
            if (timer >= delay.get()) {
                Message msg = messageQueue.get(0);
                ChatUtils.sendPlayerMsg(msg.message);
                timer = 0;
                if (!messageQueue.isEmpty()) {
                    messageQueue.clear();
                }
            }
        }
    }

    void MOAN() {
        if (getClosest().getName().getString() != null) {
            switch (moanmode.get()) {
                case Submissive -> {
                    int num = r.nextInt(0, Submissive.length - 1);
                    if (num == lastNum) {
                        num = num < Submissive.length - 1 ? num + 1 : 0;
                    }
                    lastNum = num;
                    messageQueue.add(0, new Message((msg.get() ? "/w " + getClosest().getName().getString() + " " : "") + Submissive[num].replace("%s", getClosest().getName().getString()), false));
                }
                case Dominant -> {
                    int num = r.nextInt(0, Dominant.length - 1);
                    if (num == lastNum) {
                        num = num < Dominant.length - 1 ? num + 1 : 0;
                    }
                    lastNum = num;
                    messageQueue.add(0, new Message((msg.get() ? "/w " + getClosest().getName().getString() + " " : "") + Dominant[num].replace("%s", getClosest().getName().getString()), false));
                }
            }
        }
    }

    PlayerEntity getClosest() {
        PlayerEntity closest = null;
        float distance = -1;
        if (!mc.world.getPlayers().isEmpty()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player != mc.player && (!iFriends.get() || !Friends.get().isFriend(player))) {
                    if (closest == null || OLEPOSSUtils.distance(mc.player.getPos(), player.getPos()) < distance) {
                        closest = player;
                        distance = (float) OLEPOSSUtils.distance(mc.player.getPos(), player.getPos());
                    }
                }
            }
        }
        return closest;
    }
    record Message(String message, boolean kill) {}
}
