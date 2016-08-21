package com.example.Fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.example.supermap.LoginActivity;
import com.example.supermap.R;

public class SettingFragment extends Fragment {

	RelativeLayout login_lay;
	TextView tv_login, tv_tel;
	LinearLayout about_us, current_version, update;
	Button btn_back;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.setting_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
		// ���ݵ�½״̬ �ı��½�ı�
		switchText();
		// ���õ�¼��ť����
		setListener();
		
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(checkLogin()){
			switchText();
		}
	}
	private void setListener() {
		login_lay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkLogin()) {
					// ����Ѿ���¼�� do nothing
				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivityForResult(intent, 0);
				}
			}
		});
		// �˳���¼��ť���ü����¼�
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog alertDialog;
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setMessage("�Ƿ��˳���ǰ�û���")
						.setPositiveButton("��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										BmobUser.logOut(getActivity()); // ��������û�����
										getActivity().recreate();
									}
								})
						.setNegativeButton("��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

				alertDialog = builder.create();
				alertDialog.show();
			}
		});
	}

	private void switchText() {
		BmobUser bmobUser = new BmobUser();
		if (checkLogin()) {// �Ѿ���½
			tv_login.setText("�û��ѵ�½");
			tv_login.setVisibility(View.VISIBLE);
			tv_tel.setText(bmobUser.getCurrentUser(getActivity()).getUsername()
					.toString());
			btn_back.setVisibility(View.VISIBLE);
		} else {
			tv_login.setText("δ��¼");
			tv_tel.setText("");
			tv_login.setVisibility(View.VISIBLE);
			btn_back.setVisibility(View.GONE);
		}
	}

	private boolean checkLogin() {
		BmobUser bmobUser = new BmobUser();
		if (BmobUser.getCurrentUser(getActivity()) == null
				|| BmobUser.getCurrentUser(getActivity()).getUsername() == null) {
			return false;
		}
		return true;
	}

	private void initView() {
		login_lay = (RelativeLayout) getActivity().findViewById(R.id.login_lay);
		tv_login = (TextView) getActivity().findViewById(R.id.tv_login);
		tv_tel = (TextView) getActivity().findViewById(R.id.tv_login_tel);
		about_us = (LinearLayout) getActivity().findViewById(R.id.about_us_lay);
		current_version = (LinearLayout) getActivity().findViewById(
				R.id.current_version);
		update = (LinearLayout) getActivity().findViewById(R.id.update);
		btn_back = (Button) getActivity().findViewById(R.id.btn_back);
	}

}
