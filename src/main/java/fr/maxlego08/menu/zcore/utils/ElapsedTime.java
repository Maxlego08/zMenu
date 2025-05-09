package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.configuration.Config;

public class ElapsedTime extends ZUtils {

    private final String name;
    private long start;
    private long end;

    /**
     * @param name
     */
    public ElapsedTime(String name) {
        super();
        this.name = name;
    }

    /**
     * Start
     */
    public void start() {
        this.start = System.nanoTime();
    }

    /**
     * Stop
     */
    public void end() {
        this.end = System.nanoTime();
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    public long getElapsedTime() {
        return this.end - this.start;
    }

    public void endDisplay() {
        endDisplay(false);
    }

    public void endDisplay(boolean b) {
        this.end();
        if (Config.enableDebugTime || b) {
            System.out.println("[ElapsedTime] " + name + " -> " + super.format(this.getElapsedTime(), ' '));
        }
    }

}
