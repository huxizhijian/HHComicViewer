/*
 * Copyright 2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sharesdk.onekeyshare;

import cn.sharesdk.onekeyshare.themes.classic.ClassicTheme;

/** 快捷分享的主题样式  */
public enum OnekeyShareTheme {
	/** 九格宫的主题样式 ,对应的实现类ClassicTheme */
	CLASSIC(0, new ClassicTheme());

	private final int value;
	private final OnekeyShareThemeImpl impl;

	private OnekeyShareTheme(int value, OnekeyShareThemeImpl impl) {
		this.value = value;
		this.impl = impl;
	}

	public int getValue() {
		return value;
	}

	public OnekeyShareThemeImpl getImpl() {
		return impl;
	}

	public static OnekeyShareTheme fromValue(int value) {
		for (OnekeyShareTheme theme : OnekeyShareTheme.values()) {
			if (theme.value == value) {
				return theme;
			}
		}
		return CLASSIC;
	}

}
