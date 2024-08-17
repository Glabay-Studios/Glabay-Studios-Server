package io.xeros.model.tickable;

public class TickableContainer<T> {

    private boolean stopped;
    private final Tickable<T> tickable;
    private int ticks;

    public TickableContainer(Tickable<T> tickable) {
        this.tickable = tickable;
    }

    /**
     * Tick the container {@link Tickable}
     * @param t
     * @return <code>true</code> if still running
     */
    public boolean tick(T t) {
        if (isStopped()) {
            return false;
        } else {
            tickable.tick(this, t);
            ticks++;
            return !isStopped();
        }
    }

    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getTicks() {
        return ticks;
    }
}
