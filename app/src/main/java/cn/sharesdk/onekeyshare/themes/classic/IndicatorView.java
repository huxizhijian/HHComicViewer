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

package cn.sharesdk.onekeyshare.themes.classic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/** 九宫格滑动时，下面显示的圆圈 */
public class IndicatorView extends View {
	private static final int DESIGN_INDICATOR_RADIUS = 6;
	private static final int DESIGN_INDICATOR_DISTANCE = 14;
	private static final int DESIGN_BOTTOM_HEIGHT = 52;
	/** 九格宫有多少页数 */
	private int count;
	/** 当前显示的是九格宫中的第几页 */
	private int current;

	public IndicatorView(Context context) {
		super(context);
	}

	public void setScreenCount(int count) {
		this.count = count;
	}

	public void onScreenChange(int currentScreen, int lastScreen) {
		if (currentScreen != current) {
			current = currentScreen;
			postInvalidate();
		}
	}

	protected void onDraw(Canvas canvas) {
		if (count <= 1) {
			this.setVisibility(View.GONE);
			return;
		}
		float height = getHeight();
		float radius = height * DESIGN_INDICATOR_RADIUS / DESIGN_BOTTOM_HEIGHT;
		float distance = height * DESIGN_INDICATOR_DISTANCE / DESIGN_BOTTOM_HEIGHT;
		float windowWidth = radius * 2 * count + distance * (count - 1);
		float left = (getWidth() - windowWidth) / 2;
		float cy = height / 2;

		canvas.drawColor(0xffffffff);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		for (int i = 0; i < count; i++) {
			if (i == current) {
				paint.setColor(0xff5d71a0);
			} else {
				paint.setColor(0xffafb1b7);
			}
			float cx = left + (radius * 2 + distance) * i;
			canvas.drawCircle(cx, cy, radius, paint);
		}
	}

}
