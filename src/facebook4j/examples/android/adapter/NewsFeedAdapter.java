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

import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import facebook4j.Checkin;
import facebook4j.Event;
import facebook4j.FacebookException;
import facebook4j.Group;
import facebook4j.Location;
import facebook4j.Place;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.User;
import facebook4j.examples.android.R;
import facebook4j.examples.android.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedAdapter extends ArrayAdapter<Object> {
    private LayoutInflater mInflater;
    private int layoutId;
	private View layView = null;

	
    //public NewsFeedAdapter(Context context, List<Post> objects) {
    public NewsFeedAdapter(Activity context, List<Object> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutId = R.layout.feed_row;
		layView = context.getLayoutInflater().inflate(layoutId, null);
    }
    
	static class ViewHolder {  
		SmartImageView mIcon;
	    TextView mFrom;  
		TextView mMessage;
		SmartImageView mImage;
	}  


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		View rowView = convertView;
	    SmartImageView mIcon = null;
	    TextView mFrom = null;
	    TextView mMessage = null;
	    SmartImageView mImage = null;

        if (rowView == null) {
        	try {
				rowView = mInflater.inflate(layoutId, parent, false);
			} catch (Exception e) {
				rowView = layView; //一部の機種でinflaterできずNullPointerで落ちる事が有るためその代替
			}
            holder = new ViewHolder();
            LinearLayout ln = (LinearLayout)rowView;
            
            LinearLayout ln2 = (LinearLayout)ln.getChildAt(0);
            mIcon= (SmartImageView)ln2.getChildAt(0);
            mFrom= (TextView)ln2.getChildAt(1);
            
            mMessage= (TextView)ln.getChildAt(1);
            mImage= (SmartImageView)ln.getChildAt(2);
            
            holder.mIcon = mIcon;
            holder.mFrom = mFrom;
            holder.mMessage = mMessage;
            holder.mImage = mImage;
            rowView.setTag(holder);
        }
        else{
            holder = (ViewHolder) rowView.getTag();
            mIcon = holder.mIcon;
            mFrom = holder.mFrom;
            mMessage = holder.mMessage;
            mImage = holder.mImage;
        }

    	switch(facebook_main.FEED_MODE){
    		case facebook_main.GET_HOME:
    		case facebook_main.SEACH_POSTS:
    	        Post post = (Post)getItem(position);
    	        if (post != null) {
    	            mFrom.setText(post.getFrom().getName());
    	            mMessage.setText(post.getMessage() == null ? "" : post.getMessage());
    	            URL url= post.getPicture();
    	            if(url!=null){
    	            	mImage.setVisibility(View.VISIBLE);
    	            	mImage.setImageUrl(url.toString());
    	            }
    	            else{
    	            	mImage.setVisibility(View.GONE);
    	            }
    	            Reading rd = new Reading().fields("picture");
					try {
						User user = facebook_main.m_facebook.getUser(post.getFrom().getId(), rd);
						URL url_icon = user.getPicture()==null? null : user.getPicture().getURL();
	    	            mIcon.setImageUrl(url_icon.toString());
					} catch (FacebookException e) {
						
					}
    	        }
    			break;
    		case facebook_main.SEACH_USERS:
    			User user = (User)getItem(position);
    	        if (user != null) {
    	        	
    	        }
    			break;
    		case facebook_main.SEACH_PLACES:
    			Place place = (Place)getItem(position);
    	        if (place != null) {
    	        	
    	        }
    			break;
    		case facebook_main.SEACH_LOCATIONS:
    			Location location = (Location)getItem(position);
    	        if (location != null) {
    	        	
    	        }
    			break;
    		case facebook_main.SEACH_EVENTS:
    			Event event = (Event)getItem(position);
    	        if (event != null) {
    	        	
    	        }
    			break;
    		case facebook_main.SEACH_CHECKINS:
    			Checkin checkin = (Checkin)getItem(position);
    	        if (checkin != null) {
    	        	
    	        }
    			break;
    		case facebook_main.SEACH_GROUPS:
    			Group group = (Group)getItem(position);
    	        if (group != null) {
    	        	
    	        }
    			break;
    	}
        
        return rowView;
    }
}
