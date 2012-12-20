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
import facebook4j.examples.android.NewsFeedActivity;
import facebook4j.examples.android.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsPostTask extends AsyncTask<String, Void, String> {

    //private Facebook mFacebook;
    private NewsFeedActivity mActivity;
    private ProgressDialog mProgressDialog;
    private int post_mode;
    //private Throwable t = null;

    public NewsPostTask(NewsFeedActivity activity,int post_mode_) {
        //mFacebook = facebook;
        mActivity = activity;
        post_mode = post_mode_;
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
    				break;
    			case facebook_main.POST_FEED:
    				break;
        	}
        } catch (Throwable t) {
            //this.t = t;
        }
        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
//        if (t != null) {
//            mActivity.onError(t);
//            return;
//        }
    }

}
