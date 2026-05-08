package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.zcore.logger.Logger;

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
        return this.start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return this.end;
    }

    public long getElapsedTime() {
        return this.end - this.start;
    }

    public void endDisplay() {
        this.endDisplay(false);
    }

    public void endDisplay(boolean b) {
        this.end();
        if (Configuration.enableDebugTime || b) {
            Logger.info("[ElapsedTime] " + this.name + " -> " + super.format(this.getElapsedTime(), ' '));
        }
    }

}
