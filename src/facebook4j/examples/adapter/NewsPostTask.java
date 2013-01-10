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

import java.io.File;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import facebook4j.Media;
import facebook4j.Photo;
import facebook4j.PostUpdate;
import facebook4j.Reading;
import facebook4j.examples.android.NewsFeedActivity;
import facebook4j.examples.android.R;
import facebook4j.examples.sns.facebook_main;
import facebook4j.examples.util.ShortURL;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsPostTask extends AsyncTask<String, Void, String> {

    //private Facebook mFacebook;
    private NewsFeedActivity mActivity;
    private ProgressDialog mProgressDialog;
    private Resources m_r;
    private int post_mode;
    //private Throwable t = null;
    public NewsFeedAdapter mAdapter;
    
    public NewsPostTask(NewsFeedActivity activity,int post_mode_) {
        //mFacebook = facebook;
        mActivity = activity;
        mAdapter = mActivity.mAdapter;
        post_mode = post_mode_;
        m_r = mActivity.getResources();
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
    	if(facebook_main.m_facebook==null)return null;
    	String word = params[0];
    	String ret = null;
        try {
        	switch(post_mode){
    			case facebook_main.POST_STATUS:
    	            ret = facebook_main.m_facebook.postStatusMessage(word);
    				break;
    			case facebook_main.POST_PHOTE:
        			File mfile = new File(mActivity.getCacheDir(), "dst.txt");
        			if(!mfile.exists()){
        	            ret = facebook_main.m_facebook.postStatusMessage(word);
        				break;
        			}
    				Media source = new Media(mfile);
//    				String photoId = facebook_main.m_facebook.postPhoto(source);
//    				String photoId = facebook_main.m_facebook.postPhoto(source,
//    						word,
//    		                "",
//    		                false);//trueだとHomeタイムライン等で非表示
    				String photoId = facebook_main.m_facebook.postPhoto(source,
    				word,
	                "",
	                true);//trueだとHomeタイムライン等で非表示
    				mfile.delete();
    				Photo photo = facebook_main.m_facebook.getPhoto(photoId);
    				URL exURL =  ShortURL.expand(new URL(m_r.getString(R.string.fb_login_url)));
    				PostUpdate post =  new PostUpdate(exURL)
    								   .picture(photo.getPicture())
    								   .message(word);
    				ret = facebook_main.m_facebook.postFeed(post);
    				break;
    			case facebook_main.POST_FEED:
    				break;
        	}
        } catch (Throwable t) {
        	ret = null;
            //this.t = t;
        }
        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        if(result==null)return;
        
    	Reading rd = new Reading().fields(
				"from", "message","message_tags","picture"
			)
			.limit(facebook_main.FEED_LIMIT);

    	try{
        	switch(post_mode){
    			case facebook_main.POST_STATUS:
    			case facebook_main.POST_PHOTE:
    			case facebook_main.POST_FEED:
    		        mAdapter.insert(facebook_main.m_facebook.getPost(result,rd),0);
    				break;
        	}
    	}
    	catch(Exception ex){
    		
    	}

//        if (t != null) {
//            mActivity.onError(t);
//            return;
//        }
    }

}
