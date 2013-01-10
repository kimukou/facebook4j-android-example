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
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
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
import facebook4j.examples.android.sns.ImageCache;
import facebook4j.examples.android.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedAdapter extends ArrayAdapter<Object> {
    private LayoutInflater mInflater;
    private int layoutId;
	private View layView = null;
	private List<Object> items =new ArrayList<Object>();

    public NewsFeedAdapter(Activity context) {
        super(context, 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutId = R.layout.feed_row;
		layView = context.getLayoutInflater().inflate(layoutId, null);
		items.clear();
    }

	//public NewsFeedAdapter(Context context, List<Post> objects) {
    public NewsFeedAdapter(Activity context, List<Object> objects) {
        super(context, 0, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutId = R.layout.feed_row;
		layView = context.getLayoutInflater().inflate(layoutId, null);
		items.clear();
		items.addAll(objects);
    }
    
	static class ViewHolder {  
		SmartImageView mIcon;
	    TextView mFrom;  
		TextView mMessage;
		SmartImageView mImage;
	}  

	@Override
	public void add(Object object) {
		super.add(object);
		items.add(object);
	}

    @Override
	public void insert(Object object, int index) {
		super.insert(object, index);
		items.add(index, object);
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		View rowView = convertView;
	    final SmartImageView mIcon ;
	    final TextView mFrom;
	    final TextView mMessage;
	    final SmartImageView mImage;

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
    	            
			        String id = post.getFrom().getId();
			        final String fid = "icon_" + id;
        			if(ImageCache.get(fid)==null){
    					try {
    						User user = facebook_main.m_facebook.getUser(id, new Reading().fields("picture"));
    						URL url_icon = user.getPicture()==null? null : user.getPicture().getURL();
    						AsyncHttpClient client = new AsyncHttpClient();
    						client.get(url_icon.toString(),
    							new BinaryHttpResponseHandler() {
    						    @Override
    						    public void onSuccess(byte[] fileData) {
    						    	Bitmap m_bmp = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
    						    	mIcon.setImageBitmap(m_bmp);
    								ImageCache.put(fid,m_bmp);
    						    }
    						});
    					} catch (FacebookException e) {
    					}
        			}
        			else{
				    	mIcon.setImageBitmap(ImageCache.get(fid));
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
