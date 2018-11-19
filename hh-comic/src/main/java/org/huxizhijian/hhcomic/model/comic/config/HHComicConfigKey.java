package org.huxizhijian.hhcomic.model.comic.config;

import androidx.annotation.StringDef;

/**
 * app设置的各项key值
 *
 * @author huxizhijian
 * @date 2018/11/2
 */
public class HHComicConfigKey {

    private HHComicConfigKey() {
    }

    /**
     * 夜间模式
     */
    public static final String NIGHT_MODE = "night_mode";

    /**
     * 在线阅读低清晰度模式，省流量
     */
    public static final String LOW_RESOLUTION_MODE = "low_resolution_mode";

    /**
     * 屏幕方向
     */
    public static final String SCREEN_ORIENTATION = "screen_orientation";

    /**
     * 代替枚举使用StringDef在编译期检查值
     */
    @StringDef({SCREEN_HORIZONTAL, SCREEN_VERTICAL, SCREEN_AUTO})
    public @interface ScreenOrientation {
    }

    /**
     * 横屏
     */
    public static final String SCREEN_HORIZONTAL = "landscape";

    /**
     * 竖屏
     */
    public static final String SCREEN_VERTICAL = "portrait";

    /**
     * 跟随系统
     */
    public static final String SCREEN_AUTO = "auto";

    /**
     * 翻页方向
     */
    public static final String PAGE_TURNING_DIRECTION = "page_turning_direction";

    /**
     * 代替枚举使用StringDef在编译期检查值
     */
    @StringDef({LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP})
    public @interface PageTurningDirection {
    }

    /**
     * 从左到右
     */
    public static final String LEFT_TO_RIGHT = "left_to_right";

    /**
     * 从右到左
     */
    public static final String RIGHT_TO_LEFT = "right_to_left";

    /**
     * 从上到下
     */
    public static final String TOP_TO_BOTTOM = "top_to_bottom";

    /**
     * 从下到上
     */
    public static final String BOTTOM_TO_TOP = "bottom_to_top";

    /**
     * 使用音量键翻页
     */
    public static final String PAGE_TURNING_USE_VOL_BUTTON = "page_turning_use_vol_button";

    /**
     * 阅读时保持屏幕常亮
     */
    public static final String READING_SCREEN_ALWAYS_ON = "reading_screen_always_on";

    /**
     * 下载位置
     */
    public static final String DOWNLOAD_PATH = "download_path";

    /**
     * 源排序/开启信息
     */
    public static final String SOURCE_CONFIGS = "source_configs";
}
