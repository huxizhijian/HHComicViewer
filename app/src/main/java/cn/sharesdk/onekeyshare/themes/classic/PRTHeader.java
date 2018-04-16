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

import static com.mob.tools.utils.R.getStringRes;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mob.tools.utils.R;

/** 下拉刷新的头部控件  */
public class PRTHeader extends LinearLayout {
	private static final int DESIGN_SCREEN_WIDTH = 720;
	private static final int DESIGN_AVATAR_WIDTH = 64;
	private static final int DESIGN_AVATAR_PADDING = 24;

	private TextView tvHeader;
	private RotateImageView ivArrow;
	private ProgressBar pbRefreshing;

	public PRTHeader(Context context) {
		super(context);
		int[] size = R.getScreenSize(context);
		float screenWidth = size[0] < size[1] ? size[0] : size[1];
		float ratio = screenWidth / DESIGN_SCREEN_WIDTH;

		setOrientation(VERTICAL);

		LinearLayout llInner = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		addView(llInner, lp);

		ivArrow = new RotateImageView(context);
		int resId = R.getBitmapRes(context, "ssdk_oks_ptr_ptr");
		if (resId > 0) {
			ivArrow.setImageResource(resId);
		}
		int avatarWidth = (int) (ratio * DESIGN_AVATAR_WIDTH);
		lp = new LayoutParams(avatarWidth, avatarWidth);
		lp.gravity = Gravity.CENTER_VERTICAL;
		int avataPadding = (int) (ratio * DESIGN_AVATAR_PADDING);
		lp.topMargin = lp.bottomMargin = avataPadding;
		llInner.addView(ivArrow, lp);

		pbRefreshing = new ProgressBar(context);
		resId = R.getBitmapRes(context, "ssdk_oks_classic_progressbar");
		Drawable pbdrawable = context.getResources().getDrawable(resId);
		pbRefreshing.setIndeterminateDrawable(pbdrawable);
		llInner.addView(pbRefreshing, lp);
		pbRefreshing.setVisibility(View.GONE);

		tvHeader = new TextView(getContext());
		tvHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvHeader.setPadding(avataPadding, 0, avataPadding, 0);
		tvHeader.setTextColor(0xff09bb07);
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		llInner.addView(tvHeader, lp);
	}

	public void onPullDown(int percent) {
		if (percent > 100) {
			int degree = (percent - 100) * 180 / 20;
			if (degree > 180) {
				degree = 180;
			}
			if (degree < 0) {
				degree = 0;
			}
			ivArrow.setRotation(degree);
		} else {
			ivArrow.setRotation(0);
		}

		if (percent < 100) {
			int resId = getStringRes(getContext(), "ssdk_oks_pull_to_refresh");
			if (resId > 0) {
				tvHeader.setText(resId);
			}
		} else {
			int resId = getStringRes(getContext(), "ssdk_oks_release_to_refresh");
			if (resId > 0) {
				tvHeader.setText(resId);
			}
		}
	}

	public void onRequest() {
		ivArrow.setVisibility(View.GONE);
		pbRefreshing.setVisibility(View.VISIBLE);
		int resId = getStringRes(getContext(), "ssdk_oks_refreshing");
		if (resId > 0) {
			tvHeader.setText(resId);
		}
	}

	public void reverse() {
		pbRefreshing.setVisibility(View.GONE);
		ivArrow.setRotation(180);
		ivArrow.setVisibility(View.VISIBLE);
	}

}
