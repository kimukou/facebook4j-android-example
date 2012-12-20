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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.examples.android.NewsFeedActivity;
import facebook4j.examples.android.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsSearchTask extends AsyncTask<String, Void, NewsFeedAdapter> {

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
        search_mode = search_mode_;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.show();
    }

    @Override
    protected NewsFeedAdapter doInBackground(String... params) {
    	if(facebook_main.m_facebook==null)return null;
    	String word = params[0];
        try {
        	switch(search_mode){
	    		case facebook_main.SEACH_POSTS:
	                ResponseList<Post> feed = facebook_main.m_facebook.searchPosts(word
//							,new Reading().fields("from", "message")
//							.limit(facebook_main.FEED_LIMIT)
							);
	                for (Post post : feed) {
	                	mAdapter.add(post);
	                }
	    			break;
	    		case facebook_main.SEACH_USERS:
	    			break;
	    		case facebook_main.SEACH_PLACES:
	    			break;
	    		case facebook_main.SEACH_LOCATIONS:
	    			break;
	    		case facebook_main.SEACH_EVENTS:
	    			break;
	    		case facebook_main.SEACH_CHECKINS:
	    			break;
	    		case facebook_main.SEACH_GROUPS:
	    			break;
        	}

        } catch (Throwable t) {
        	mAdapter = null;
            //this.t = t;
        }
        return mAdapter;
    }

    @Override
    protected void onPostExecute(NewsFeedAdapter result) {
        mProgressDialog.dismiss();
//        if (t != null) {
//            mActivity.onError(t);
//            return;
//        }
        mActivity.setListAdapter(result);
    }

}
