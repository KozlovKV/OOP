package kozlov.kirill.snake.model.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.util.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for SettingsModel model fragment.
 */
public class SettingsTest {
    private static final String TEST_SETTINGS_PATH = "testSettings.json";

    @Test
    void testSettingsLoading() {
        SettingsModel settingsModel = (SettingsModel) Model.SETTINGS.get();
        settingsModel.setSettingsPath(TEST_SETTINGS_PATH);
        settingsModel.restartModel();
        Assertions.assertEquals(1, settingsModel.getRandomsCount());
        Assertions.assertEquals(2, settingsModel.getPredatorsCount());
        Assertions.assertEquals(3, settingsModel.getApplesCount());
    }

    @Test
    void testSettingsSaving() {
        SettingsModel settingsModel = (SettingsModel) Model.SETTINGS.get();
        settingsModel.setSettingsPath(TEST_SETTINGS_PATH);
        settingsModel.restartModel();
        settingsModel.setApplesCount(4);
        settingsModel.setPredatorsCount(5);
        settingsModel.setRandomsCount(6);
        settingsModel.saveToJson();

        try (
            var inputStream = new FileInputStream(TEST_SETTINGS_PATH)
        ) {
            SettingsRecord savedSettings = JsonUtils.parse(
                inputStream, SettingsRecord.class
            );
            Assertions.assertEquals(6, savedSettings.randomsCount());
            Assertions.assertEquals(5, savedSettings.predatorsCount());
            Assertions.assertEquals(4, savedSettings.applesCount());
        } catch (IOException e) {
            Assertions.fail();
        }
        if (!new File(TEST_SETTINGS_PATH).delete()) {
            Assertions.fail();
        }
    }

    @Test
    void testDefaultSettings() {
        SettingsModel settingsModel = (SettingsModel) Model.SETTINGS.get();
        settingsModel.setSettingsPath(TEST_SETTINGS_PATH);
        settingsModel.restartModel();
        settingsModel.setApplesCount(4);
        settingsModel.setPredatorsCount(5);
        settingsModel.setRandomsCount(6);

        settingsModel.setSettingsPath("invalid/path.json");
        settingsModel.restartModel();
        Assertions.assertEquals(6, settingsModel.getRandomsCount());
        Assertions.assertEquals(5, settingsModel.getPredatorsCount());
        Assertions.assertEquals(4, settingsModel.getApplesCount());
    }
}
