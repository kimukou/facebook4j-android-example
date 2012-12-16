/*
 * Copyright 2012 Ryuji Yamashita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package facebook4j.examples.android.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import facebook4j.Post;
import facebook4j.examples.android.R;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedAdapter extends ArrayAdapter<Post> {
    private LayoutInflater mInflater;
    private int layoutId;
	private View layView = null;

	
    //public NewsFeedAdapter(Context context, List<Post> objects) {
    public NewsFeedAdapter(Activity context, List<Post> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutId = R.layout.feed_row;
		layView = context.getLayoutInflater().inflate(layoutId, null);
    }
    
	static class ViewHolder {  
	    TextView mFrom;  
		TextView mMessage;
	}  


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		View rowView = convertView;
	    TextView mFrom = null;
	    TextView mMessage = null;

        if (rowView == null) {
        	try {
				rowView = mInflater.inflate(layoutId, parent, false);
			} catch (Exception e) {
				rowView = layView; //一部の機種でinflaterできずNullPointerで落ちる事が有るためその代替
			}
            holder = new ViewHolder();
            LinearLayout ln = (LinearLayout)rowView;
            mFrom= (TextView)ln.getChildAt(0);
            mMessage= (TextView)ln.getChildAt(1);
            holder.mFrom = mFrom;
            holder.mMessage = mMessage;
            rowView.setTag(holder);
        }
        else{
            holder = (ViewHolder) rowView.getTag();
            mFrom = holder.mFrom;
            mMessage = holder.mMessage;
        }

        Post post = this.getItem(position);
        if (post != null) {
            mFrom.setText(post.getFrom().getName());
            mMessage.setText(post.getMessage() == null ? "" : post.getMessage());
        }
        return rowView;
    }
}
