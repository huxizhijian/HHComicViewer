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

package cn.sharesdk.onekeyshare.themes.classic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/** 编辑页面中删除图片“X”按钮 */
public class XView extends View {
	private float ratio;

	public XView(Context context) {
		super(context);
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	protected void onDraw(Canvas canvas) {
		int width = getWidth() / 2;
		int height = getHeight() / 2;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xffa0a0a0);
		canvas.drawRect(width, 0, getWidth(), height, paint);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3f * ratio);
		paint.setColor(0xffffffff);
		float left = 8f * ratio;
		canvas.drawLine(width + left, left, getWidth() - left, width - left, paint);
		canvas.drawLine(width + left, width - left, getWidth() - left, left, paint);
	}

}
