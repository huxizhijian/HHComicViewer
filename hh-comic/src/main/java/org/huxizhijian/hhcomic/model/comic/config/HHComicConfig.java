package org.huxizhijian.hhcomic.model.comic.config;

import com.alibaba.fastjson.JSON;

import org.huxizhijian.core.util.HHPreference;

import java.util.List;

/**
 * app设置工具类
 *
 * @author huxizhijian
 * @date 2018/11/13
 */
public class HHComicConfig {

    private HHComicConfig() {
    }

    private static class Holder {
        private static final HHComicConfig INSTANCE = new HHComicConfig();
    }

    public static HHComicConfig getInstance() {
        return Holder.INSTANCE;
    }

    public void setNightMode(boolean nightMode) {
        HHPreference.setAppFlag(HHComicConfigKey.NIGHT_MODE, nightMode);
    }

    public boolean isNightMode() {
        return HHPreference.getAppFlag(HHComicConfigKey.NIGHT_MODE);
    }

    public void setLowResolutionMode(boolean lowResolutionMode) {
        HHPreference.setAppFlag(HHComicConfigKey.LOW_RESOLUTION_MODE, lowResolutionMode);
    }

    public boolean isLowResolutionMode() {
        return HHPreference.getAppFlag(HHComicConfigKey.LOW_RESOLUTION_MODE);
    }

    public void setScreenOrientation(@HHComicConfigKey.ScreenOrientation String screenOrientation) {
        HHPreference.setCustomAppProfile(HHComicConfigKey.SCREEN_ORIENTATION, screenOrientation);
    }

    @HHComicConfigKey.ScreenOrientation
    public String getScreenOrientation() {
        return HHPreference.getCustomAppProfile(HHComicConfigKey.SCREEN_ORIENTATION);
    }

    public void setPageTurningDirection(@HHComicConfigKey.PageTurningDirection String pageTurningDirection) {
        HHPreference.setCustomAppProfile(HHComicConfigKey.PAGE_TURNING_DIRECTION, pageTurningDirection);
    }

    @HHComicConfigKey.PageTurningDirection
    public String getPageTurningDirection() {
        return HHPreference.getCustomAppProfile(HHComicConfigKey.PAGE_TURNING_DIRECTION);
    }

    public void setPageTurningUseVOLButton(boolean pageTurningUseVOLButton) {
        HHPreference.setAppFlag(HHComicConfigKey.PAGE_TURNING_USE_VOL_BUTTON, pageTurningUseVOLButton);
    }

    public boolean isPageTurningUseVOLButton() {
        return HHPreference.getAppFlag(HHComicConfigKey.PAGE_TURNING_USE_VOL_BUTTON);
    }

    public void setReadingScreenAlwaysOn(boolean readingScreenAlwaysOn) {
        HHPreference.setAppFlag(HHComicConfigKey.READING_SCREEN_ALWAYS_ON, readingScreenAlwaysOn);
    }

    public boolean isReadingScreenAlwaysOn() {
        return HHPreference.getAppFlag(HHComicConfigKey.READING_SCREEN_ALWAYS_ON);
    }

    public void setDownloadPath(String downloadPath) {
        HHPreference.setCustomAppProfile(HHComicConfigKey.DOWNLOAD_PATH, downloadPath);
    }

    public String getDownloadPath() {
        return HHPreference.getCustomAppProfile(HHComicConfigKey.DOWNLOAD_PATH);
    }

    public void setSourceConfigs(List<SourceConfig> sourceConfigs) {
        String json = JSON.toJSONString(sourceConfigs);
        HHPreference.setCustomAppProfile(HHComicConfigKey.SOURCE_CONFIGS, json);
    }

    public List<SourceConfig> getSourceConfigs() {
        String json = HHPreference.getCustomAppProfile(HHComicConfigKey.SOURCE_CONFIGS);
        return (List<SourceConfig>) JSON.parse(json);
    }

    public void setLastSourceKey(String sourceKey) {
        HHPreference.setCustomAppProfile(HHComicConfigKey.LAST_SOURCE_KEY, sourceKey);
    }

    public String getLastSourceKey() {
        return HHPreference.getCustomAppProfile(HHComicConfigKey.LAST_SOURCE_KEY);
    }
}
