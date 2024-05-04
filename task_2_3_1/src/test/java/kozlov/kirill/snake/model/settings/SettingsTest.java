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
    private final String TEST_SETTINGS_PATH = "testSettings.json";

    @Test
    void testSettingsLoading() {
        SettingsModel settingsModel = (SettingsModel) Model.SETTINGS.get();
        settingsModel.setSettingsPath(TEST_SETTINGS_PATH);
        settingsModel.restartModel();
        Assertions.assertEquals(1, settingsModel.getFieldWidth());
        Assertions.assertEquals(2, settingsModel.getFieldHeight());
        Assertions.assertEquals(3, settingsModel.getApplesCount());
    }

    @Test
    void testSettingsSaving() {
        SettingsModel settingsModel = (SettingsModel) Model.SETTINGS.get();
        settingsModel.setSettingsPath(TEST_SETTINGS_PATH);
        settingsModel.restartModel();
        settingsModel.setApplesCount(4);
        settingsModel.setFieldHeight(5);
        settingsModel.setFieldWidth(6);
        settingsModel.saveToJson();

        try (
            var inputStream = new FileInputStream(TEST_SETTINGS_PATH)
        ) {
            SettingsRecord savedSettings = JsonUtils.parse(
                inputStream, SettingsRecord.class
            );
            Assertions.assertEquals(6, savedSettings.fieldWidth());
            Assertions.assertEquals(5, savedSettings.fieldHeight());
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
        settingsModel.setFieldHeight(5);
        settingsModel.setFieldWidth(6);

        settingsModel.setSettingsPath("invalid/path.json");
        settingsModel.restartModel();
        Assertions.assertEquals(6, settingsModel.getFieldWidth());
        Assertions.assertEquals(5, settingsModel.getFieldHeight());
        Assertions.assertEquals(4, settingsModel.getApplesCount());
    }
}
