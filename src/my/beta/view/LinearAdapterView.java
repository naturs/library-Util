package my.beta.view;

import my.beta.util.L;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * 使用LinearLayout模拟GridView和ListView，数据量大时不要使用。
 *
 */
public class LinearAdapterView extends LinearLayout {
	
	static final String LOG_TAG = LinearAdapterView.class.getSimpleName();
	
	private ListAdapter mAdapter;
	
	private int mNumberPerLine = 1;
	
	private Drawable mDivider;
	
	private int mDividerHeight;
	
	public LinearAdapterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		setOrientation(LinearLayout.VERTICAL);
		
		// 获取ListView中的divider作为默认的divider设置
		ListView lv = new ListView(context);
		Drawable clone = lv.getDivider().getConstantState().newDrawable();
		setDivider(clone);
		setDividerHeight(lv.getDividerHeight());
		lv = null;
		
		L.d("default divider:%s, height:%d", mDivider, mDividerHeight);
	}
	
	/**
	 * 设置适配器，使用方法同ListView、GridView等
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		checkNumberPerLine();
		mAdapter = adapter;
		bindAdapter(mAdapter);
		if (mAdapter != null) {
			mAdapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					bindAdapter(mAdapter);
				}
			});
		}
	}
	
	/**
	 * 设置每行显示多少个Item.
	 * @param number
	 */
	public void setNumberPerLine(int number) {
		this.mNumberPerLine = number;
		checkNumberPerLine();
	}
	
	/**
	 * 设置分割线的颜色
	 * @param color #DEDEDE
	 */
	public void setDivider(String colorString) {
		mDivider = new ColorDrawable(Color.parseColor(colorString));
	}
	
	public void setDivider(Drawable d) {
		mDivider = d;
	}
	
	/**
	 * 设置分割线的高度，不显示设置为0.
	 * <p>
	 * 适用于ListView
	 * @param width
	 * @param height
	 */
	public void setDividerHeight(int height) {
		mDividerHeight = Math.max(0, height);
	}

	@SuppressWarnings("deprecation")
	private void bindAdapter(ListAdapter adapter) {
		clearList();
		
		if (adapter == null || adapter.isEmpty()) {
			return;
		}
		
		final int viewCount = adapter.getCount();
		final int totalLine = (int) Math.ceil(1.0 * viewCount / mNumberPerLine);
		
		L.d("Bind adapter : totalCount=%1$d, totalLine=%2$d.", viewCount, totalLine);
		
		int currentLine = 1;
		
		LinearLayout tempLayout = null;
		for (int i = 0; i < viewCount; i ++) {
			View itemView = adapter.getView(i, null, null);
			if (itemView == null) continue;
			if (i % mNumberPerLine == 0) {
				tempLayout = new LinearLayout(getContext());
				tempLayout.setWeightSum(mNumberPerLine);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				this.addView(tempLayout, lp);
				
				// 只在两个item之间设置分割线，首尾不要分割线
				if (currentLine < totalLine && mDividerHeight > 0) {
					View divider = new View(getContext());
					if (mDivider != null) {
						divider.setBackgroundDrawable(mDivider);
					} else {
						divider.setBackgroundColor(android.R.color.transparent);
					}
					this.addView(divider, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mDividerHeight));
				}
				
				currentLine ++;
			}
			if (tempLayout == null) {
				L.e("The temp linearlayout is null.");
				continue;
			}
			tempLayout.addView(itemView, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
		}
	}
	
	private void clearList() {
		this.removeAllViews();
	}
	
	private void checkNumberPerLine() {
		if (mNumberPerLine <= 0) {
			throw new IllegalArgumentException("the number per line must be positivie.");
		}
	}
	
}
