package marlon.passwordmaker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/*
 * Author: marlon yao<yaolei135@gmail.com>
 */
public class Settings extends Activity implements OnClickListener {
	private EditText etMasterPassword;
	private RadioButton rbAlgSha256;
	private RadioButton rbAlgMD5;
	private EditText etPasswordLength;
	private EditText etModifier;
	private Button btnSave;
	private Button btnCancel;
	
	private SharedPreferences prefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        etMasterPassword = (EditText) findViewById(R.id.etMasterPassword);
        rbAlgSha256 = (RadioButton) findViewById(R.id.rbAlgSha256);
        rbAlgMD5 = (RadioButton) findViewById(R.id.rbAlgMD5);
        etPasswordLength = (EditText) findViewById(R.id.etPasswordLength);
        etModifier = (EditText) findViewById(R.id.etModifier);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        
        prefs = getSharedPreferences(Const.APP_SETTINGS, MODE_PRIVATE);
        updateUIFromPreferences();
        
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
	
	@Override
	public void onClick(View v) {
		if (v == btnSave) {
			if (savePreferences()) {
				Settings.this.setResult(RESULT_OK);
				Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			Settings.this.setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void updateUIFromPreferences() {
		String masterPassword = prefs.getString(Const.PREF_MASTER_PASSWORD, "");
		String hashAlog = prefs.getString(Const.PREF_HASH_ALOGRITHM, "sha256");
		int passwordLength = prefs.getInt(Const.PREF_PASSWORD_LENGTH, 8);
		String modifier = prefs.getString(Const.PREF_MODIFIER, "");
		
		etMasterPassword.setText(masterPassword);
		if (hashAlog.equals("md5")) {
			rbAlgMD5.setChecked(true);
		} else {
			rbAlgSha256.setChecked(true);
		}
		etPasswordLength.setText(String.valueOf(passwordLength));
		etModifier.setText(modifier);
	}
	
	private boolean savePreferences() {
		int passwordLen = 0;
		try {
			passwordLen = Integer.parseInt(etPasswordLength.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(this, "Password length must be number", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (passwordLen < 2) {
			Toast.makeText(this, "Password length at least be 2", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Const.PREF_MASTER_PASSWORD, etMasterPassword.getText().toString());
		if (rbAlgMD5.isChecked()) {
			editor.putString(Const.PREF_HASH_ALOGRITHM, "md5");
		} else {
			editor.putString(Const.PREF_HASH_ALOGRITHM, "sha256");
		}
		editor.putInt(Const.PREF_PASSWORD_LENGTH, passwordLen);
		editor.putString(Const.PREF_MODIFIER, etModifier.getText().toString());
		editor.commit();
		
		return true;
	}
}
