package io.xeros.model;

public class Animation {

    public static final Animation RESET_ANIMATION = new Animation(65_535);

    private final int id;
    private final int delay;
    private final AnimationPriority animationPriority;

    public Animation(int id) {
        this(id, 0, AnimationPriority.LOW);
    }

    public Animation(int id, int delay) {
        this(id, delay, AnimationPriority.LOW);
    }

    public Animation(int id, int delay, AnimationPriority animationPriority) {
        this.id = id;
        this.delay = delay;
        this.animationPriority = animationPriority;
    }

    public int getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }

    public AnimationPriority getAnimationPriority() {
        return animationPriority;
    }
}
