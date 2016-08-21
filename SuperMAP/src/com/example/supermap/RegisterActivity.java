package com.example.supermap;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.arg1 == 60) {
				btncode.setClickable(false);
			}
			if (msg.arg1 == 1) {
				btncode.setClickable(true);
				btncode.setText("��ȡ��֤��");
			} else {
				btncode.setText("ʣ��ʱ�䣺" + msg.arg1 + "s");
			}
		};
	};

	EditText et_tel, et_password, et_password_again;
	Button btnRegister;

	EditText code;
	Button btncode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		setListener();
		btncode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (et_tel.getText().toString().equals("")
						|| et_tel.getText().toString() == null) {
					Toast.makeText(getApplicationContext(), "��������绰����",
							Toast.LENGTH_SHORT).show();
				} else {
					BmobSMS.requestSMSCode(getApplicationContext(), et_tel
							.getText().toString(), "��֤��",
							new RequestSMSCodeListener() {

								@Override
								public void done(Integer arg0,
										BmobException arg1) {
									// TODO Auto-generated method stub
									Toast.makeText(
											RegisterActivity.this,
											"��֤���ѷ��͵�"
													+ et_tel.getText()
															.toString(), 3000)
											.show();

									new Thread(new Runnable() {

										@Override
										public void run() {
											for (int i = 0; i < 60; i++) {
												try {
													Thread.sleep(1000);
													Message message = new Message();
													message.arg1 = 60 - i;
													handler.sendMessage(message);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}

											}
										}
									}).start();
								}
							});
				}
			}
		});
	}

	private void setListener() {
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tel, pwd, pwdAgain;
				tel = et_tel.getText().toString();
				pwd = et_password.getText().toString();
				pwdAgain = et_password_again.getText().toString();
				if (checkTelAndPwd()) {// ����������Ϸ�
					if (pwd.equals(pwdAgain)) {
						BmobUser bmobUser = new BmobUser();
						bmobUser.setUsername(tel);
						bmobUser.setPassword(pwd);
						bmobUser.setMobilePhoneNumber(tel);
						bmobUser.signOrLogin(getApplicationContext(), code
								.getText().toString(), new SaveListener() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								Toast.makeText(RegisterActivity.this,
										"��ϲ��ע��ɹ���", 3000).show();
								finish();
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(RegisterActivity.this, "ע��ʧ��",
										3000).show();
							}
						});
					} else {
						Toast.makeText(RegisterActivity.this, "ȷ�����벻һ��",
								Toast.LENGTH_SHORT).show();
						et_tel.setText("");
						et_password.setText("");
						et_password_again.setText("");
					}

				} else {
					// ���������벻�Ϸ�����Ҫ���������������
					et_tel.setText("");
					et_password.setText("");
					et_password_again.setText("");
				}
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
		String strPwdAgain = et_password_again.getText().toString();
		String strCode = code.getText().toString();
		if (strTel.equals("")) {
			Toast.makeText(RegisterActivity.this, "�������û���", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (strPwdAgain.equals("")) {
			Toast.makeText(RegisterActivity.this, "������ȷ������", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (strPwd.equals("")) {
			Toast.makeText(RegisterActivity.this, "����������", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (strTel.length() != 11) {
			Toast.makeText(RegisterActivity.this, "�绰���������11λ",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (strPwd.length() > 10 || strPwd.length() < 6) {
			Toast.makeText(RegisterActivity.this, "���������6-10λ�ַ�",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (strPwdAgain.length() > 10 || strPwd.length() < 6) {
			Toast.makeText(RegisterActivity.this, "ȷ�����������6-10λ�ַ�",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		for (int i = 0; i < strTel.length(); i++) {
			if (strTel.charAt(i) < '0' || strTel.charAt(i) > '9') {
				Toast.makeText(RegisterActivity.this, "�绰���뺬�зǷ��ַ�",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		if (strCode.equals("") || strCode == null) {
			Toast.makeText(RegisterActivity.this, "��������֤��", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	private void initView() {
		et_password = (EditText) findViewById(R.id.et_password);
		et_password_again = (EditText) findViewById(R.id.et_password_again);
		et_tel = (EditText) findViewById(R.id.et_tel);
		btnRegister = (Button) findViewById(R.id.btn_register);
		code = (EditText) findViewById(R.id.code);
		btncode = (Button) findViewById(R.id.btncode);
	}
}
