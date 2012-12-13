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

package facebook4j.examples.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import facebook4j.Facebook;
import facebook4j.Post;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedActivity extends ListActivity {

    private Facebook mFacebook;
    private List<Post> mFeed;
    private NewsFeedAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getFeed();
    }

    // Starts PostDetailActivity.
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Post post = mFeed.get(position);
        PostDetail(post);
/*        
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("FROM", post.getFrom().getName());
        intent.putExtra("MESSAGE", post.getMessage());
        startActivity(intent);
*/        
    }
    
	private Dialog mDialog = null;
	protected void PostDetail(Post post){
		if(mDialog!=null){
			try{
				mDialog.dismiss();
			}catch(Exception ex){mDialog.cancel();}
		}

		mDialog = new Dialog(this);
		mDialog.setContentView(R.layout.post_detail);

		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent e) {
		        if(e.getKeyCode() != KeyEvent.KEYCODE_BACK) return false;
	        	if(e.getAction() != KeyEvent.ACTION_DOWN)return false;
            	mDialog.cancel();
            	mDialog = null;
		        return false;
			}
		});
		
		TextView mFrom = (TextView) mDialog.findViewById(R.id.post_detail_from);
        mFrom.setText(post.getFrom().getName());
        TextView mMessage = (TextView) mDialog.findViewById(R.id.post_detail_message);
        mMessage.setText(post.getMessage());

    	mDialog.show();
	}

    // Adds 'Refresh' menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, Menu.FIRST, 0, "Refresh");
        return result;
    }

    // menu -> Refresh
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getFeed();
        return true;
    }

    // This method is invoked from NewsFeedReaderTask if you have not yet authenticated.
    public void onError(Throwable t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Require Facebook Authentication")
               .setMessage("Click OK to start Facebook's Authentication.")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       startOAuth();
                   }
               })
               .show();
    }

    private void getFeed() {
        mFeed = new ArrayList<Post>();
        mAdapter = new NewsFeedAdapter(this, mFeed);
        NewsFeedReaderTask task = new NewsFeedReaderTask(mFacebook, this, mAdapter);
        task.execute();
    }

    // Starts OAuthActivity. This method is invoked from AlertDialog's callback in onError() method.
    private void startOAuth() {
        Intent intent = new Intent(this, OAuthActivity.class);
        startActivityForResult(intent, RequestCode.OAuth.code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (RequestCode.getInstance(requestCode)) {
        case OAuth:
        	if(data==null)break;
            Bundle extras = data.getExtras();
            mFacebook = (Facebook) extras.get(facebook_main.DATA_KEY_FACEBOOK);
            getFeed();
            break;
        default:
            break;
        }
    }



    private enum RequestCode {
        OAuth(1),
        Detail(2),
        ;
        
        private int code;
        private RequestCode(int code) {
            this.code = code;
        }
        public static RequestCode getInstance(int code) {
            for (RequestCode e : RequestCode.values()) {
                if (e.code == code) {
                    return e;
                }
            }
            return null;
        }
        
    }
    
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
			onDestroy();
			android.os.Process.killProcess(android.os.Process.myPid());
			return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
