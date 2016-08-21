package com.example.supermap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends Activity {

	EditText et_tel, et_password;
	LinearLayout password_lay;
	CheckBox remember_pwd;
	Button btn_login;
	TextView tv_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		initView();
		setListener();
	}

	private void setListener() {
		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��½�Ϸ���У��
				if (checkTelAndPwd()) {

					// ����û������ס����
					if (!remember_pwd.isChecked()) {
						BmobUser bmobUser = BmobUser
								.getCurrentUser(LoginActivity.this);
						if (bmobUser != null) {
							bmobUser.setUsername("");
							bmobUser.setPassword("");
							bmobUser = null;
						} else {
							// �����û�����Ϊ��ʱ�� �ɴ��û�ע����桭
						}
					} else {
						// Ĭ���Ǽ�ס����
						SharedPreferences sharedPreferences = getSharedPreferences(
								"haoxinlei", MODE_PRIVATE); // ˽������
						Editor editor = sharedPreferences.edit();
						editor.putString("password", et_password.getText()
								.toString());
						editor.commit();
					}

					// ��½��֤
					BmobUser user = new BmobUser();
					user.setUsername(et_tel.getText().toString());
					user.setPassword(et_password.getText().toString());
					user.login(LoginActivity.this, new SaveListener() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Toast.makeText(LoginActivity.this, "��½�ɹ�",
									Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							setResult(0, intent);
							finish();
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(LoginActivity.this, "�û������������",
									Toast.LENGTH_SHORT).show();
						}
					});
				}

			}

		});

		tv_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});

	}

	/**
	 * ����û����Ƿ�Ϸ�
	 * 
	 * @return
	 */
	public boolean checkTelAndPwd() {
		String strTel = et_tel.getText().toString();
		String strPwd = et_password.getText().toString();
		if (strTel.equals(" ")) {
			Toast.makeText(LoginActivity.this, "�������û���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (strPwd.equals(" ")) {
			Toast.makeText(LoginActivity.this, "����������", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (strPwd.length() > 10 || strPwd.length() < 6) {
			Toast.makeText(LoginActivity.this, "�������Ϊ6��10λ�ַ�", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (strTel.length() != 11) {
			Toast.makeText(LoginActivity.this, "�绰���������11λ", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		for (int i = 0; i < strTel.length(); i++) {
			if (strTel.charAt(i) < '0' || strTel.charAt(i) > '9') {
				Toast.makeText(LoginActivity.this, "�绰���뺬�зǷ��ַ�",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	private void initView() {
		password_lay = (LinearLayout) findViewById(R.id.password_lay);
		remember_pwd = (CheckBox) findViewById(R.id.remember_pwd);
		btn_login = (Button) findViewById(R.id.btn_login);
		et_password = (EditText) findViewById(R.id.et_password);
		et_tel = (EditText) findViewById(R.id.et_tel);
		tv_register = (TextView) findViewById(R.id.tv_register);
	}
}
