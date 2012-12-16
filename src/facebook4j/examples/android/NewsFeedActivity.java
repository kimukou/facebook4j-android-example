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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import facebook4j.Post;
import facebook4j.examples.android.adapter.NewsFeedAdapter;
import facebook4j.examples.android.adapter.NewsFeedReaderTask;
import facebook4j.examples.android.android_super.BaseActivity;
import facebook4j.examples.android.android_super.OnActivityResultCallback;
import facebook4j.examples.android.sns.AuthFbActivity;
import facebook4j.examples.android.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedActivity extends BaseActivity {

    private List<Post> mFeed;
    private NewsFeedAdapter mAdapter;
    private Resources m_r;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        m_r = getResources();
        facebook_main.init(this);
        facebook_main.loginOAuth();
        if(facebook_main.isFacebookLogin()){
        	Button btn = _findViewById(R.id.button_login);
        	btn.setText(m_r.getString(R.string.sts_logout));
            getFeed();
        }
        else{
        	startOAuth();
        }
    }
    
    public void onClickStsChange(View v){
    	Button btn = (Button)v;
    	if(m_r.getString(R.string.sts_logout).equals(btn.getText())){
    		btn.setText(m_r.getString(R.string.sts_login));
    		facebook_main.eraseAccessToken();
    		setListAdapter(null);
    	}
    	else{
    		startOAuth();
    	}
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
				
		mDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		
		mDialog.setContentView(R.layout.post_detail);

		mDialog.setTitle(m_r.getString(R.string.app_name));
		mDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.icon);

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
		Button mBtn = (Button) mDialog.findViewById(R.id.post_detail_ok);
		mBtn.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
            	mDialog.cancel();
            	mDialog = null;
			}
		});
		
		TextView mFrom = (TextView) mDialog.findViewById(R.id.post_detail_from);
        TextView mMessage = (TextView) mDialog.findViewById(R.id.post_detail_message);
		if(post!=null){
	        mFrom.setText(post.getFrom().getName());
	        mMessage.setText(post.getMessage());
		}
		else{
	        mFrom.setText("Failture");
	        mMessage.setText("Can't Get PostData");
		}
		

		DisplayMetrics metrics = m_r.getDisplayMetrics();  
		int dialogWidth = (int) (metrics.widthPixels * 0.9); 
/*		
		int dialogHeight = (int) (metrics.heightPixels * 0.45);  
		int dlg_ask_height_p = m_r.getInteger(R.integer.dlg_ask_height_p);
		if("KDDI".equals(Build.BRAND) && "IS03".equals(Build.MODEL))dlg_ask_height_p += 5;
		int dialogHeight = (int) (metrics.heightPixels * dlg_ask_height_p * 1/100); 
*/		 
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();  
	    lp.width = dialogWidth;  
	    //lp.height =dialogHeight;
	    mDialog.getWindow().setAttributes(lp);  

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
/*    
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
*/
    
    private void getFeed() {
        mFeed = new ArrayList<Post>();
        mAdapter = new NewsFeedAdapter(this, mFeed);
        NewsFeedReaderTask task = new NewsFeedReaderTask(this, mAdapter);
        task.execute();
    }

    // Starts OAuthActivity. This method is invoked from AlertDialog's callback in onError() method.
    private void startOAuth() {
        Intent intent = new Intent(this, AuthFbActivity.class);
        //startActivityForResult(intent, RequestCode.OAuth.code);
    	startActivityForCallback(intent, new OnActivityResultCallback() {
            // ここで値を受け取れる
            public void onResult(int resultCode, Intent data) {
            	final int state = data==null ? 0:data.getIntExtra("State",0);
            	if(state==1){
                	Button btn = _findViewById(R.id.button_login);
                	btn.setText(m_r.getString(R.string.sts_logout));
                    //Bundle extras = data.getExtras();
                    //mFacebook = (Facebook) extras.get(facebook_main.DATA_KEY_FACEBOOK);
                    getFeed();
            	}
            }
    	});

    }
/*
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
*/    
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
			onDestroy();
			android.os.Process.killProcess(android.os.Process.myPid());
			return false;
        }
        return super.onKeyDown(keyCode, event);
    }

	@SuppressWarnings("unchecked")
	protected <T extends View> T _findViewById(final int id){
	    return (T)findViewById(id);
	}

}
