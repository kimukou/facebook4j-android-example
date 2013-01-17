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

package facebook4j.examples.adapter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import facebook4j.Checkin;
import facebook4j.Event;
import facebook4j.Group;
import facebook4j.Location;
import facebook4j.Place;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.examples.android.NewsFeedActivity;
import facebook4j.examples.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsSearchTask extends AsyncTask<String, Object, NewsFeedAdapter> {

    //private Facebook mFacebook;
    private NewsFeedActivity mActivity;
    private NewsFeedAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private int search_mode;
    //private Throwable t = null;

    //public NewsFeedReaderTask(Facebook facebook, NewsFeedActivity activity, NewsFeedAdapter adapter) {
    public NewsSearchTask(NewsFeedActivity activity, NewsFeedAdapter adapter,int search_mode_) {
        //mFacebook = facebook;
        mActivity = activity;
        mAdapter = adapter;
        mActivity.setListAdapter(mAdapter);
        search_mode = search_mode_;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.show();
        mAdapter.clear();
    }
    
	@Override
	protected void onProgressUpdate(Object... progress) {
		for (Object bean_s : progress) {
			mAdapter.add(bean_s);
			mAdapter.notifyDataSetChanged();
		}
	}


    //see 	http://facebook4j.org/ja/code-examples.html
    //		field https://developers.facebook.com/docs/reference/api/post/
    @Override
    protected NewsFeedAdapter doInBackground(String... params) {
    	if(facebook_main.m_facebook==null)return null;
    	String word = params[0];
    	Reading rd = new Reading().fields(
    				"from", "message","message_tags","picture"
    			)
    			.limit(facebook_main.FEED_LIMIT);

    	
    	facebook_main.FEED_MODE = search_mode;
        try {
        	switch(search_mode){
	    		case facebook_main.SEACH_POSTS:
	                ResponseList<Post> posts = facebook_main.m_facebook.searchPosts(
	                		word
	                		,rd
					);
	                for (Post post : posts) {
	                	publishProgress(post);
	                	//mAdapter.add(post);
	                }
	                facebook_main.paging = posts.getPaging(); //ページング情報セット
	    			break;
	    		case facebook_main.SEACH_USERS:
	    			ResponseList<User> users = facebook_main.m_facebook.searchUsers(
	                		word
	                		,rd
					);
	                for (User user : users) {
	                	publishProgress(user);
	                	//mAdapter.add(user);
	                }
	                facebook_main.paging = users.getPaging(); //ページング情報セット
	    			break;
	    		//see http://facebook4j.org/ja/code-examples.html#paging
	    		case facebook_main.SEACH_PLACES:
	    			//場所を検索=>名前版
	    			ResponseList<Place> places = facebook_main.m_facebook.searchPlaces(
	                		word
	                		,rd
					);
	                for (Place place : places) {
	                	publishProgress(place);
	                	//mAdapter.add(place);
	                }
	                facebook_main.paging = places.getPaging(); //ページング情報セット
	    			break;
	    		case facebook_main.SEACH_LOCATIONS:
	    			//位置情報を検索 placeId?
	    			ResponseList<Location> locations = facebook_main.m_facebook.searchLocations(
	                		word
	                		,rd
					);
	                for (Location location : locations) {
	                	publishProgress(location);
	                	//mAdapter.add(location);
	                }
	                facebook_main.paging = locations.getPaging(); //ページング情報セット
	    			break;
	    		case facebook_main.SEACH_EVENTS:
	    			ResponseList<Event> events = facebook_main.m_facebook.searchEvents(
	                		word
	                		,rd
					);
	                for (Event event : events) {
	                	publishProgress(event);
	                	//mAdapter.add(event);
	                }
	                facebook_main.paging = events.getPaging(); //ページング情報セット
	    			break;
	    		case facebook_main.SEACH_CHECKINS:
	    			ResponseList<Checkin> checkins = facebook_main.m_facebook.searchCheckins(
//	                		word,
	                		rd
					);
	                for (Checkin checkin : checkins) {
	                	publishProgress(checkin);
	                	//mAdapter.add(checkin);
	                }
	                facebook_main.paging = checkins.getPaging(); //ページング情報セット
	    			break;
	    		case facebook_main.SEACH_GROUPS:
	    			ResponseList<Group> groups = facebook_main.m_facebook.searchGroups(
	                		word,
	                		rd
					);
	                for (Group group : groups) {
	                	publishProgress(group);
	                	//mAdapter.add(group);
	                }
	                facebook_main.paging = groups.getPaging(); //ページング情報セット
	    			break;
        	}

        } catch (Throwable t) {
        	mAdapter = null;
        	facebook_main.paging = null;
            //this.t = t;
        }
        return mAdapter;
    }

    @Override
	protected void onCancelled(NewsFeedAdapter result) {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(NewsFeedAdapter result) {
        mProgressDialog.dismiss();
//        if (t != null) {
//            mActivity.onError(t);
//            return;
//        }
//        mActivity.setListAdapter(result);
    }

}
