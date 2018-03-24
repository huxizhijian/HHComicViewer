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

package cn.sharesdk.onekeyshare.themes.classic.port;

import java.util.ArrayList;

import android.content.Context;
import cn.sharesdk.onekeyshare.themes.classic.PlatformPage;
import cn.sharesdk.onekeyshare.themes.classic.PlatformPageAdapter;

import com.mob.tools.utils.R;

/** 竖屏的九宫格页面适配器 */
public class PlatformPageAdapterPort extends PlatformPageAdapter {
	private static final int DESIGN_SCREEN_WIDTH_P = 720;
	private static final int DESIGN_SEP_LINE_WIDTH = 1;
	private static final int DESIGN_LOGO_HEIGHT = 76;
	private static final int DESIGN_PADDING_TOP = 20;
	private static final int PAGE_SIZE_P = 12;
	private static final int LINE_SIZE_P = 4;

	public PlatformPageAdapterPort(PlatformPage page, ArrayList<Object> cells) {
		super(page, cells);
	}

	protected void calculateSize(Context context, ArrayList<Object> plats) {
		int screenWidth = R.getScreenWidth(context);
		lineSize = LINE_SIZE_P;

		float ratio = ((float) screenWidth) / DESIGN_SCREEN_WIDTH_P;
		sepLineWidth = (int) (DESIGN_SEP_LINE_WIDTH * ratio);
		sepLineWidth = sepLineWidth < 1 ? 1 : sepLineWidth;
		logoHeight = (int) (DESIGN_LOGO_HEIGHT * ratio);
		paddingTop = (int) (DESIGN_PADDING_TOP * ratio);
		bottomHeight = (int) (DESIGN_BOTTOM_HEIGHT * ratio);
		cellHeight = (screenWidth - sepLineWidth * 3) / 4;
		if (plats.size() <= lineSize) {
			panelHeight = cellHeight + sepLineWidth;
		} else if (plats.size() <= PAGE_SIZE_P - lineSize) {
			panelHeight = (cellHeight + sepLineWidth) * 2;
		} else {
			panelHeight = (cellHeight + sepLineWidth) * 3;
		}
	}

	protected void collectCells(ArrayList<Object> plats) {
		int count = plats.size();
		if (count < PAGE_SIZE_P) {
			int lineCount = (count / lineSize);
			if (count % lineSize != 0) {
				lineCount++;
			}
			cells = new Object[1][lineCount * lineSize];
		} else {
			int pageCount = (count / PAGE_SIZE_P);
			if (count % PAGE_SIZE_P != 0) {
				pageCount++;
			}
			cells = new Object[pageCount][PAGE_SIZE_P];
		}

		for (int i = 0; i < count; i++) {
			int p = i / PAGE_SIZE_P;
			cells[p][i - PAGE_SIZE_P * p] = plats.get(i);
		}
	}

}
