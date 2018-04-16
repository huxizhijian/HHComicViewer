/*
 * Copyright 2016-2018 huxizhijian
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

package cn.sharesdk.onekeyshare.themes.classic.port;

import com.mob.tools.utils.R;

import cn.sharesdk.onekeyshare.OnekeyShareThemeImpl;
import cn.sharesdk.onekeyshare.themes.classic.FriendListPage;

/** 竖屏的好友列表 */
public class FriendListPagePort extends FriendListPage {
	private static final int DESIGN_SCREEN_WIDTH = 720;
	private static final int DESIGN_TITLE_HEIGHT = 96;

	public FriendListPagePort(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	protected float getRatio() {
		float screenWidth = R.getScreenWidth(activity);
		return screenWidth / DESIGN_SCREEN_WIDTH;
	}

	protected int getDesignTitleHeight() {
		return DESIGN_TITLE_HEIGHT;
	}

}
