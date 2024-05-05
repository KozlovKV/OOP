package kozlov.kirill.snake.model.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import kozlov.kirill.snake.model.ModelFragment;
import kozlov.kirill.util.JsonUtils;
import kozlov.kirill.util.ParsingException;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model fragment for settings.
 */
@Data
public class SettingsModel implements ModelFragment {
    private final Logger logger = LogManager.getLogger("model");

    private int fieldWidth;
    private int fieldHeight;
    private int applesCount;

    /**
     * Constructor.
     * <br>
     * Calls `loadFromJson()` method. Programmers must ensure that first JSON reading
     * will be successful
     */
    public SettingsModel() {
        try {
            loadFromJson();
        } catch (IOException e) {
            logger.error("Failed to load initial settings", e);
        }
    }

    @Override
    public SettingsModel restartModel() {
        try {
            loadFromJson();
        } catch (IOException e) {
            logger.error("Failed to reload settings", e);
        }
        return this;
    }

    private String settingsPath = "settings.json";

    private void loadFromJson() throws IOException {
        logger.info("Loading settings...");
        InputStream inputStream;
        SettingsRecord settingsRecord = null;
        try {
            inputStream = new FileInputStream(settingsPath);
        } catch (IOException notFoundException) {
            inputStream = getClass().getClassLoader().getResourceAsStream(
                settingsPath
            );
        }
        if (inputStream == null) {
            var e = new FileNotFoundException(
                "Couldn't find settings JSON file either in specified path "
                    + "either in this path in resources using path " + settingsPath
            );
            logger.error("Failed to load settings file", e);
            throw e;
        }
        try {
            settingsRecord = JsonUtils.parse(inputStream, SettingsRecord.class);
        } catch (ParsingException e) {
            logger.error("Failed to parse settings", e);
            throw e;
        }
        inputStream.close();
        fieldWidth = settingsRecord.fieldWidth();
        fieldHeight = settingsRecord.fieldHeight();
        applesCount = settingsRecord.applesCount();
        logger.info("Settings loaded");
    }

    /**
     * JSON saving.
     * <br>
     * Saves settings using SettingsRecord to path settingsPath
     */
    public void saveToJson() {
        logger.info("Saving settings...");
        try (
            var outputStream = new FileOutputStream(
                settingsPath
            )
        ) {
            SettingsRecord settingsRecord = new SettingsRecord(
                fieldWidth, fieldHeight, applesCount
            );
            JsonUtils.serialize(settingsRecord, outputStream);
        } catch (IOException e) {
            logger.error("Failed to save settings", e);
        }
        logger.info("Settings saved");
    }
}
