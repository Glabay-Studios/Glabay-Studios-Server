package io.xeros.net.login.captcha;

import com.github.cage.Cage;
import io.xeros.Configuration;
import io.xeros.util.Captcha;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CaptchaRequirement {

    private final String captcha;
    private final byte[] image;
    private final long time;
    private int attempts;

    public CaptchaRequirement() throws IOException {
        String captcha = Captcha.generateCaptchaString();
        if (Configuration.LOWERCASE_CAPTCHA)
            captcha = captcha.toLowerCase();
        this.captcha = captcha;
        this.image = Captcha.toByteArray(new Cage().drawImage(this.captcha));
        time = System.currentTimeMillis();
    }

    /**
     * @return true if passes captcha or none required
     */
    public boolean isIncorrect(String captchaInput) {
        addAttempt();
        return !match(captchaInput);
    }

    public boolean match(String captchaInput) {
        return captchaInput.equals(captcha);
    }

    public void addAttempt() {
        attempts++;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public boolean expired() {
        return System.currentTimeMillis() - time > TimeUnit.MINUTES.toMillis(5);
    }
    public int getAttempts() {
        return attempts;
    }

    public byte[] getImage() {
        return image;
    }

    public String getCaptcha() {
        return captcha;
    }
}
