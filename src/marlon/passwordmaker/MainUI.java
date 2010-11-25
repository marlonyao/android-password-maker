package marlon.passwordmaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * Author: marlon yao<yaolei135@gmail.com>
 */
public class MainUI extends Activity implements OnFocusChangeListener, OnKeyListener, OnClickListener, OnItemClickListener {
	private static final String TAG = "PasswordMaker";
	private static final int MENUID_SETTINGS = 1;
	private static final int MENUID_COPY_PASSWORD = 2;
	private static final int MENUID_TOGGLE_PASSWORD_DISPLAY = 3;

	private AutoCompleteTextView etUrl;
	private EditText etPassword;
	private RadioButton rbAllPrintable;
	private RadioButton rbOnlyAlphaNums;
	private Button btnMakePassword;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        etUrl = (AutoCompleteTextView) findViewById(R.id.etUrl);
        updateRememberedUrls();
        etUrl.setThreshold(1);
        
        etPassword = (EditText) findViewById(R.id.etPassword);
        registerForContextMenu(etPassword);
        
        rbAllPrintable = (RadioButton) findViewById(R.id.rbAllPrintable);
        rbOnlyAlphaNums = (RadioButton) findViewById(R.id.rbOnlyAlphaNums);
        btnMakePassword = (Button) findViewById(R.id.btnMakePassword);
        
        etUrl.setOnFocusChangeListener(this);
        etUrl.setOnKeyListener(this);
        etUrl.setOnItemClickListener(this);
        rbAllPrintable.setOnClickListener(this);
        rbOnlyAlphaNums.setOnClickListener(this);
        btnMakePassword.setOnClickListener(this);
    }
    
    @Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			updatePassword();
		}
	}
    
    @Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (event.getAction() == KeyEvent.ACTION_UP)
				updatePassword();
			return true;
		}
		return false;
	}
    
    @Override
	public void onClick(View v) {
		updatePassword();
		
		if (v == btnMakePassword)
			Toast.makeText(this, "Password Maked!", Toast.LENGTH_SHORT).show();
	}
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	updatePassword();
    }
    
    private List<String> getRememberedUrls() {
    	SharedPreferences prefs = getSharedPreferences(Const.APP_SETTINGS, MODE_PRIVATE);
        String strUrls = prefs.getString(Const.PREF_REMEMBERED_URLS, "");
        String[] urls = strUrls.split(Const.URL_SEPARATOR);
        
        return Arrays.asList(urls);
    }
    
    private void updateRememberedUrls() {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item);
    	for (String url : getRememberedUrls()) {
    		adapter.add(url);
    	}
        etUrl.setAdapter(adapter);
    }
    

	@SuppressWarnings("unchecked")
	private void rememberedUrl(String url) {
		if (url.trim().length() == 0)
			return;
		List<String> savedUrls = getRememberedUrls();
		if (savedUrls.size() > 0 && savedUrls.size() <= Const.MAX_REMEMBER_URLS && savedUrls.get(0).equals(url))
			return;
		
		List<String> urls = new ArrayList<String>();
		urls.add(url);
		
		for (String strUrl : savedUrls) {
			if (urls.size() > Const.MAX_REMEMBER_URLS)
				break;
			if (strUrl.equals(url))
				continue;
			urls.add(strUrl);
		}
		
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) etUrl.getAdapter();
		adapter.clear();
		for (String strUrl : urls) {
			adapter.add(strUrl);
		}
		
		String strUrls = Utils.join(urls, Const.URL_SEPARATOR);
		
		SharedPreferences prefs = getSharedPreferences(Const.APP_SETTINGS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Const.PREF_REMEMBERED_URLS, strUrls);
		editor.commit();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, MENUID_SETTINGS, Menu.NONE, "Settings");
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case MENUID_SETTINGS:
        	Log.d(TAG, "options menu selected");
        	Intent intent = new Intent(this, Settings.class);
        	startActivityForResult(intent, 0);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//    	super.onCreateContextMenu(menu, v, menuInfo);
		menu.clear();
		menu.setHeaderTitle("Context Menu");
		menu.add(0, MENUID_COPY_PASSWORD, 0, "Copy Password to Clipboard");
		if ((etPassword.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0) {
   			menu.add(0, MENUID_TOGGLE_PASSWORD_DISPLAY, 0, "Display Password");
   		} else {
   			menu.add(0, MENUID_TOGGLE_PASSWORD_DISPLAY, 0, "Hide Password");
   		}
		
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
       	switch (item.getItemId()) {
       	case MENUID_COPY_PASSWORD:
       		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(etPassword.getText().toString());
       		Toast.makeText(this, "Password Copied!", Toast.LENGTH_SHORT).show();
       		return true;
       	case MENUID_TOGGLE_PASSWORD_DISPLAY:
       		int inputType = etPassword.getInputType();
       		if ((inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0) {
       			etPassword.setInputType(inputType & ~InputType.TYPE_TEXT_VARIATION_PASSWORD);
       		} else {
       			etPassword.setInputType(inputType | InputType.TYPE_TEXT_VARIATION_PASSWORD);
       		}
       	default:
       		return super.onOptionsItemSelected(item);
       	}
	}

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK)
    		updatePassword();
	}

	private void updatePassword() {
    	String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`~!@#$%^&*()_-+={}|[]\\:\";\'<>?,./";
    	if (rbOnlyAlphaNums.isChecked()) {
    		charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    	}
    	
    	SharedPreferences prefs = getSharedPreferences(Const.APP_SETTINGS, MODE_PRIVATE);
    	String masterPassword = prefs.getString(Const.PREF_MASTER_PASSWORD, "");
    	String hashAlg = prefs.getString(Const.PREF_HASH_ALOGRITHM, "sha256");
    	int passwordLen = prefs.getInt(Const.PREF_PASSWORD_LENGTH, 8);
    	String modifier = prefs.getString(Const.PREF_MODIFIER, "");
    	
    	String password = PasswordMaker.GeneratorPassword2(masterPassword, etUrl.getText().toString(),  
				charset, hashAlg, passwordLen, modifier);
		etPassword.setText(password);
		
		// remeber url
		rememberedUrl(etUrl.getText().toString());
    }

}