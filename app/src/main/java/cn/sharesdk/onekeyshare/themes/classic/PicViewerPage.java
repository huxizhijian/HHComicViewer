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

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView.ScaleType;
import cn.sharesdk.onekeyshare.OnekeySharePage;
import cn.sharesdk.onekeyshare.OnekeyShareThemeImpl;

import com.mob.tools.gui.ScaledImageView;

/** 图片浏览的视图类 */
public class PicViewerPage extends OnekeySharePage implements OnGlobalLayoutListener {
	private Bitmap pic;
	/** 图片浏览的缩放控件 */
	private ScaledImageView sivViewer;

	public PicViewerPage(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	/** 设置图片用于浏览 */
	public void setImageBitmap(Bitmap pic) {
		this.pic = pic;
	}

	public void onCreate() {
		activity.getWindow().setBackgroundDrawable(new ColorDrawable(0x4c000000));

		sivViewer = new ScaledImageView(activity);
		sivViewer.setScaleType(ScaleType.MATRIX);
		activity.setContentView(sivViewer);
		if (pic != null) {
			sivViewer.getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
	}

	public void onGlobalLayout() {
		sivViewer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		sivViewer.post(new Runnable() {
			public void run() {
				sivViewer.setBitmap(pic);
			}
		});
	}

}
