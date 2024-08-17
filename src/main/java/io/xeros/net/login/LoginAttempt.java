package io.xeros.net.login;

import java.util.concurrent.TimeUnit;

public class LoginAttempt {

    private long time = System.currentTimeMillis();

    public boolean inLastXMinutes(int minutes) {
        return System.currentTimeMillis() - time <= TimeUnit.MINUTES.toMillis(minutes);
    }

    public long getTime() {
        return time;
    }
}
