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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.examples.android.adapter.NewsFeedAdapter;
import facebook4j.examples.android.adapter.NewsFeedReaderTask;
import facebook4j.examples.android.adapter.NewsPostTask;
import facebook4j.examples.android.adapter.NewsSearchTask;
import facebook4j.examples.android.android_super.BaseActivity;
import facebook4j.examples.android.android_super.OnActivityResultCallback;
import facebook4j.examples.android.sns.AuthFbActivity;
import facebook4j.examples.android.sns.facebook_main;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
public class NewsFeedActivity extends BaseActivity {

	private final String TAG = getClass().getSimpleName();
    private List<Object> mFeed;
    private NewsFeedAdapter mAdapter;
    private Resources m_r;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

        // アダプターをセットする前にフッターをセット
        ListView listView = getListView();
        // フッターをレイアウトファイルから生成
        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        // フッターを追加
        listView.addFooterView(footer, null, true);
        
        m_r = getResources();
        facebook_main.init(this);
        facebook_main.loginOAuth();
        if(facebook_main.isFacebookLogin()){
        	getProfile();
            getFeed();
        }
        else{
        	startOAuth();
        }
    }

	
    public void onClickSearch(View v){
        choiceDlgShow(R.layout.spinner_search,R.id.spinner_search,v);
    }
	
    public void onClickPostSelect(View v){
        choiceDlgShow(R.layout.spinner_post,R.id.spinner_post,v);
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
    
    private int selected_pos = 0;
    private String selected_str = "";
	private Dialog choiceDlg = null;
	private void choiceDlgShow(final int layout_id,final int spinner_id,final View pv) {
		  if(choiceDlg!=null){
			  choiceDlg.dismiss();
			  choiceDlg = null;
		  }

	       choiceDlg = new Dialog(this);
	       //choiceDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
	       choiceDlg.setContentView(layout_id);
	       
    	   switch(spinner_id){
	   			case R.id.spinner_post:
	   				choiceDlg.setTitle("postmode");
	   				break;
	   			case R.id.spinner_search:
	   				choiceDlg.setTitle("searchmode");
	   				break;
    	   }

	       choiceDlg.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent e) {
					Log.v(TAG, "choiceDlg onKey");
			        if(e.getKeyCode() != KeyEvent.KEYCODE_BACK) return false;
		        	if(e.getAction() != KeyEvent.ACTION_DOWN)return false;
	            	Log.v(TAG, "onKey ACTION_DOWN KEYCODE_BACK");
	            	choiceDlg.cancel();
	            	choiceDlg = null;
			        return false;
				}
			});

	       choiceDlg.findViewById(R.id.ChoiceCancel).setOnClickListener(
	    			new OnClickListener() {
	    				public void onClick(View v) {
	    					choiceDlg.dismiss();
	    					choiceDlg = null;
	    				}
	    			});

	       choiceDlg.findViewById(R.id.ChoiceOK).setOnClickListener(
	    			new OnClickListener() {
	    				public void onClick(View v) {
	    					   String word = "";
	    		        	   switch(spinner_id){
	    	       	   			case R.id.spinner_post:
	    	       	   				{
	    	       	   					EditText mMessage = (EditText) choiceDlg.findViewById(R.id.EditMsg);
	    	       	   					word = mMessage.getText().toString();
	    	       	   				}
	    	       	   				break;
	    			   			case R.id.spinner_search:
	    			   				{
	    			   					EditText mMessage = (EditText) choiceDlg.findViewById(R.id.EditMsg);
	    			   					word = mMessage.getText().toString();
	    			   				}
	    			   				break;
	    		        	   }
	    		        	   
	    		        	   choiceDlg.dismiss();
	    		        	   choiceDlg=null;
	    		        	   switch(spinner_id){
	    			   			case R.id.spinner_post:
	    			   				postAction(selected_pos,word);
	    			   				break;
	    			   			case R.id.spinner_search:
	    			            	getSearch(selected_pos,word);
	    			   				break;
	    		        	   }

	    				}
	    			});

	       Spinner spinner = (Spinner) choiceDlg.findViewById(spinner_id);
    	   switch(spinner_id){
	   			case R.id.spinner_post:
	   				spinner.setSelection(selected_pos);
   	   				break;
    	   }


	       // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
	       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view,
	                    int position, long id) {
	                Spinner spinner = (Spinner) parent;
	                String item = (String) spinner.getSelectedItem();
	                selected_str = item;
	                selected_pos = position;
	            }
	            @Override
	            public void onNothingSelected(AdapterView<?> arg0) {
	            }
	       });

	        DisplayMetrics metrics = m_r.getDisplayMetrics();  
			int dialogWidth = (int) (metrics.widthPixels * 0.9);  
			//int dialogHeight = (int) (metrics.heightPixels * 0.3);  
			WindowManager.LayoutParams lp = choiceDlg.getWindow().getAttributes();  
		    lp.width = dialogWidth;  
		    //lp.height =dialogHeight;
		    choiceDlg.getWindow().setAttributes(lp);  
	       choiceDlg.show();
	}
    

    // Starts PostDetailActivity.
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Object post = mFeed.get(position);
        // クリックされたViewがフッターか判定
        if (v.getId() == R.id.footer) {
        	if(facebook_main.m_facebook==null)return;
        	if(facebook_main.paging==null)return;
        	try {
				ResponseList<?> page = facebook_main.m_facebook.fetchNext(facebook_main.paging);
				for (Object obj : page) {
	                mAdapter.add(obj);
	            }
            	facebook_main.paging = page.getPaging(); //ページング情報セット
			} catch (FacebookException e) {
				Log.e(TAG, "",e);
			}
        	return;
        }
        
    	if(mAdapter==null)return;
    	Object post = mAdapter.getItem(position);
        dispDetail(post);
/*        
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("FROM", post.getFrom().getName());
        intent.putExtra("MESSAGE", post.getMessage());
        startActivity(intent);
*/        
    }
    
	private Dialog mDialog = null;
	protected void dispDetail(Object obj){
		if(mDialog!=null){
			try{
				mDialog.dismiss();
			}catch(Exception ex){mDialog.cancel();}
		}

		mDialog = new Dialog(this);
				
		mDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		
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
		Button mBtn = (Button) mDialog.findViewById(R.id.post_detail_ok);
		mBtn.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
            	mDialog.cancel();
            	mDialog = null;
			}
		});
		
//		TextView mFrom = (TextView) mDialog.findViewById(R.id.post_detail_from);
        TextView mMessage = (TextView) mDialog.findViewById(R.id.post_detail_message);
        String title="";
    	Reading rd = new Reading().fields("picture");

		if(obj!=null){
//	        mFrom.setText(post.getFrom().getName());
			switch(facebook_main.FEED_MODE){
				case facebook_main.GET_HOME:
				case facebook_main.SEACH_POSTS:
					Post post = (Post)obj;
					title = post.getFrom().getName();
			        mMessage.setText(post.getMessage());
					try {
						User user = facebook_main.m_facebook.getUser(post.getFrom().getId(), rd);
						URL url = user.getPicture()==null? null : user.getPicture().getURL();
				        if(url==null)break;
						AsyncHttpClient client = new AsyncHttpClient();
						client.get(url.toString(),
							new BinaryHttpResponseHandler() {
						    @Override
						    public void onSuccess(byte[] fileData) {
						    	Bitmap m_bmp = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
								mDialog.setFeatureDrawable(Window.FEATURE_LEFT_ICON,new BitmapDrawable(m_bmp));
						    }
						});
					} catch (FacebookException e1) {
						Log.e(TAG, "",e1);
					}
					break;
				default:
					title="Failture";
			        mMessage.setText("Can't Get PostData");
					break;
			}
		}
		else{
//	        mFrom.setText("Failture");
			title="Failture";
	        mMessage.setText("Can't Get PostData");
		}
		mDialog.setTitle(title);

		DisplayMetrics metrics = m_r.getDisplayMetrics();  
		int dialogWidth = (int) (metrics.widthPixels * 0.9); 
		int dialogHeight = (int) (metrics.heightPixels * 0.7);  
		
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();  
	    lp.width = dialogWidth;  
	    lp.height =dialogHeight;
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
    // Starts OAuthActivity. This method is invoked from AlertDialog's callback in onError() method.
    private void startOAuth() {
        Intent intent = new Intent(this, AuthFbActivity.class);
        //startActivityForResult(intent, RequestCode.OAuth.code);
    	startActivityForCallback(intent, new OnActivityResultCallback() {
            // ここで値を受け取れる
            public void onResult(int resultCode, Intent data) {
            	final int state = data==null ? 0:data.getIntExtra("State",0);
            	if(state==1){
                	getProfile();
                    getFeed();
            	}
            }
    	});

    }
    
    private void getFeed() {
        facebook_main.FEED_MODE =facebook_main.GET_HOME;
        mFeed = new ArrayList<Object>();
        mAdapter = new NewsFeedAdapter(this, mFeed);
        NewsFeedReaderTask task = new NewsFeedReaderTask(this, mAdapter);
        task.execute();
    }
    
//=====================================================================================

    private void getSearch(int search_mode,String word) {
        mFeed = new ArrayList<Object>();
        mAdapter = new NewsFeedAdapter(this, mFeed);
        NewsSearchTask task = new NewsSearchTask(this, mAdapter,search_mode);
        task.execute(word);
    }

    //see	 https://developers.facebook.com/docs/reference/api/post/
    //		http://facebook4j.org/en/javadoc/facebook4j/api/PostMethods.html#postFeed(facebook4j.PostUpdate) ですね。
    //		PostUpdateのpictureに画像URLを入れる感じです。
    //		アップロードなら http://facebook4j.org/en/javadoc/facebook4j/api/PhotoMethods.html#postPhoto(facebook4j.Media) です。
    private void postAction(int post_mode,String word) {
    	switch(post_mode){
    		case facebook_main.POST_STATUS:
    			break;
    		case facebook_main.POST_PHOTE:
    			break;
    		case facebook_main.POST_FEED:
    			break;
    	}
        NewsPostTask task = new NewsPostTask(this, post_mode);
        task.execute(word);
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

	@SuppressWarnings("unchecked")
	protected <T extends View> T _findViewById(final int id){
	    return (T)findViewById(id);
	}

	
    //see http://facebook4j.org/en/javadoc/facebook4j/api/UserMethods.html#getUser(java.lang.String)
	private void getProfile() {
		try {
			User user = facebook_main.m_facebook.getMe(new Reading().fields("picture","name"));
			//User user = facebook_main.m_facebook.getUser(facebook_main.m_facebook.getId()); //△(pictureが取れない)
			URL url = user.getPicture()==null? null : user.getPicture().getURL();
			//URL url = facebook_main.m_facebook.getPictureURL();
			if(url!=null){
				SmartImageView iv = _findViewById(R.id.user_image);
				iv.setImageUrl(url.toString());
			}
			TextView tx = _findViewById(R.id.user_name);
			tx.setText(user.getName());
		} catch (FacebookException e) {
			Log.e(TAG, "getProfile",e);
			e.printStackTrace();
		}
		Button btn = _findViewById(R.id.button_login);
		btn.setText(m_r.getString(R.string.sts_logout));
	}

}
