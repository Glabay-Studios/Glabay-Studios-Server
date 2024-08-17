package io.xeros;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class ServerProperties {

    public enum PropType {
        SERVER,
        CACHE
    }

    private static final Properties serverProperties = new Properties();
    private static final Properties cacheProperties = new Properties();

    public static Properties getServerProperties() {
        return serverProperties;
    }

    public static Properties getCacheProperties() {
        return cacheProperties;
    }

    public static void loadProperties(PropType type) {
        File propFile = new File("./" + type.name().toLowerCase(Locale.getDefault()) + ".properties");
        try {
            if (!propFile.exists()) {
                propFile.createNewFile();
            }
            switch (type) {
                case CACHE:
                    cacheProperties.load(new FileInputStream(propFile));
                    File assetsLink = new File("./assetslink.txt");
                    if (assetsLink.exists()) {
                        String cachePath = new String(java.nio.file.Files.readAllBytes(assetsLink.toPath())).replace("cache\\", "");
                        cacheProperties.setProperty("cache", cachePath);
                        saveProperties(type, cacheProperties);
                        assetsLink.delete();
                    }
                    break;
                case SERVER:
                    serverProperties.load(new FileInputStream(propFile));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace(System.err); // Consider a more robust error handling strategy
        }
    }

    public static void saveProperties(PropType type, Properties p) {
        String path = "./" + type.name().toLowerCase(Locale.getDefault()) + ".properties";
        try (FileOutputStream fr = new FileOutputStream(path)) {
            p.store(fr, "Properties");
        } catch (IOException e) {
            e.printStackTrace(System.err); // Consider a more robust error handling strategy
        }
    }
}
