package fr.maxlego08.menu.zcore.utils.players;

import fr.maxlego08.menu.api.interfaces.StringConsumer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Timer;
import java.util.TimerTask;

public class BarApi {

    private final Plugin plugin;
    private String message;
    private BarColor color = BarColor.BLUE;
    private BarStyle style = BarStyle.SOLID;
    private BarFlag[] flags = new BarFlag[]{};
    private long delay = 5;
    private boolean addAll = true;
    private StringConsumer<Player> consumer;
    private boolean personnal = false;
    private Player player;

    public BarApi(Plugin plugin, String message, BarColor color, BarStyle style, BarFlag... flags) {
        this(plugin);
        this.message = message;
        this.color = color;
        this.style = style;
        this.flags = flags;
    }

    public BarApi(Plugin plugin) {
        this.plugin = plugin;
    }

    public BarApi(Plugin plugin, String message) {
        this(plugin);
        this.message = message;
    }

    public BarApi delay(long delay) {
        this.delay = delay;
        return this;
    }

    public BarApi color(BarColor color) {
        this.color = color;
        return this;
    }

    public BarApi style(BarStyle style) {
        this.style = style;
        return this;
    }

    public BarApi flags(BarFlag... flags) {
        this.flags = flags;
        return this;
    }

    public BarApi consumer(StringConsumer<Player> consumer) {
        this.consumer = consumer;
        return this;
    }

    public BarApi all() {
        this.addAll = true;
        return this;
    }

    public BarApi personnal() {
        this.personnal = true;
        return this;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the color
     */
    public BarColor getColor() {
        return this.color;
    }

    /**
     * @return the style
     */
    public BarStyle getStyle() {
        return this.style;
    }

    /**
     * @return the flags
     */
    public BarFlag[] getFlags() {
        return this.flags;
    }

    public void start() {

        if (this.player != null)
            this.startPersonnal(this.player);
        else if (this.personnal)
            this.startPersonnal();
        else {
            BossBar bar = Bukkit.createBossBar(this.message, this.color, this.style, this.flags);
            if (this.addAll)
                Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
            this.barTask(bar, null);
        }
    }

    private void startPersonnal() {
        Bukkit.getOnlinePlayers().forEach(this::startPersonnal);
    }

    private void startPersonnal(Player player) {

        BossBar bar = Bukkit.createBossBar(this.consumer != null ? this.consumer.accept(player) : this.message, this.color, this.style, this.flags);
        bar.addPlayer(player);
        this.barTask(bar, () -> bar.setTitle(this.consumer != null ? this.consumer.accept(player) : this.message));

    }

    private void barTask(BossBar bar, Runnable runnable) {
        new Timer().scheduleAtFixedRate(new TimerTask() {

            private double barC = 1.0;

            @Override
            public void run() {

                if (!BarApi.this.plugin.isEnabled()) {
                    this.cancel();
                    return;
                }

                if (this.barC <= 0.0) {
                    this.cancel();
                    bar.removeAll();
                    return;
                }

                if (runnable != null)
                    runnable.run();

                bar.setProgress(this.barC);
                this.barC -= 0.001;

            }
        }, 0, this.delay);
    }

    public BarApi user(Player player) {
        this.player = player;
        return this;
    }

}
