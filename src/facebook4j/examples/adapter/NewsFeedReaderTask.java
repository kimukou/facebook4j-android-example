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
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.examples.android.NewsFeedActivity;
import facebook4j.examples.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedReaderTask extends AsyncTask<Void, Post, NewsFeedAdapter> {

    //private Facebook mFacebook;
    private NewsFeedActivity mActivity;
    private NewsFeedAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    //private Throwable t = null;

    //public NewsFeedReaderTask(Facebook facebook, NewsFeedActivity activity, NewsFeedAdapter adapter) {
    public NewsFeedReaderTask(NewsFeedActivity activity, NewsFeedAdapter adapter) {
        //mFacebook = facebook;
        mActivity = activity;
        mAdapter = adapter;
        mActivity.setListAdapter(mAdapter);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.show();
    }
    
	@Override
	protected void onProgressUpdate(Post... progress) {
		for (Post bean_s : progress) {
			mAdapter.add(bean_s);
			mAdapter.notifyDataSetChanged();
		}
	}

    
    //see 	http://facebook4j.org/ja/code-examples.html
    //		field https://developers.facebook.com/docs/reference/api/post/
    @Override
    protected NewsFeedAdapter doInBackground(Void... params) {
    	if(facebook_main.m_facebook==null)return null;
    	Reading rd = new Reading().fields(
				"from", "message","message_tags","picture"
			)
			.limit(facebook_main.FEED_LIMIT);
        try {
            ResponseList<Post> feed = facebook_main.m_facebook.getHome(
            			rd
            		);
            for (Post post : feed) {
            	publishProgress(post);
                //mAdapter.add(post);
            }
            facebook_main.paging = feed.getPaging(); //ページング情報セット
        } catch (Throwable t) {
        	mAdapter = null;
        	facebook_main.paging = null;
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
//        mActivity.setListAdapter(result);
    }

}
