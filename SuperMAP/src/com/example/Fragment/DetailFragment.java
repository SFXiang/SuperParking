package com.example.Fragment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.example.Bmob.Bean;
import com.example.Bmob.Bean_copy;
import com.example.supermap.LoginActivity;
import com.example.supermap.R;
import com.example.supermap.SecondA;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment {

	TextView tv_yuding, tv_yuding_write, tv_address, tv_address_write, tv_time,
			tv_time_write, tv_time_end, tv_time_end_write;
	Button btnCancle;
	ImageView iv_cry;
	TextView tv_cry;
	RelativeLayout content_lay;
	
	String id,partName;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.detail_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// ��ʼ���ؼ�
		initView();
		// �ı��������ֺ�ͼ��
		switchText();
		// ���ü����� ȡ��
		setListener();
	}

	private void setListener() {
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog;
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setMessage("�Ƿ�ȡ����Ԥ���ĳ�λ��").setPositiveButton("��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						//ɾ��Bean_Copy���е�һ������
						Bean_copy bean_copy = new Bean_copy();
						bean_copy.setObjectId(id);
						bean_copy.delete(getActivity());
						//ɾ��Bean���е�����
						BmobQuery<Bean> bmobQuery = new BmobQuery<Bean>();
						bmobQuery.findObjects(getActivity(), new FindListener<Bean>() {
							
							@Override
							public void onSuccess(List<Bean> arg0) {
								for (Bean bean : arg0) {
									if(bean.getPartNum().toString().equals(partName)){
										bean.delete(getActivity());
										break;
									}
								}
								Toast.makeText(getActivity(), "ȡ��Ԥ���ɹ�", Toast.LENGTH_SHORT).show();
								switchText();
								SecondA.flag=0;
							}
							
							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(getActivity(), "ɾ��ʧ��", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}).setNegativeButton("��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alertDialog = builder.create();
				alertDialog.show();
			}
		});
	}

	private void switchText() {
		if (checkLogin()) {// ����Ѿ���½��
			BmobQuery<Bean_copy> bmobQuery = new BmobQuery<Bean_copy>();
			bmobQuery.findObjects(getActivity(), new FindListener<Bean_copy>() {

				@Override
				public void onSuccess(List<Bean_copy> arg0) {
					if (arg0.size() == 0) {// ��û��Ԥ���ĳ�λ
						content_lay.setVisibility(View.GONE);
						iv_cry.setVisibility(View.VISIBLE);
						tv_cry.setVisibility(View.VISIBLE);
					} else {// ��Ԥ���ĳ�λ�ˣ��±߽�����ʾ
						content_lay.setVisibility(View.VISIBLE);
						iv_cry.setVisibility(View.GONE);
						tv_cry.setVisibility(View.GONE);
						showPartDetail();
					}
				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "�����쳣", Toast.LENGTH_SHORT);
				}
			});
		} else {// ���û�е�½
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivityForResult(intent, 1);
		}
	}

	private void showPartDetail() {
		BmobQuery<Bean_copy> bmobQuery = new BmobQuery<Bean_copy>();
		bmobQuery.findObjects(getActivity(), new FindListener<Bean_copy>() {

			@Override
			public void onSuccess(List<Bean_copy> arg0) {
				for (Bean_copy bean_copy : arg0) {
					String partNum, address, time, endTime;
					id = bean_copy.getObjectId();	
					partNum = bean_copy.getPartNum();
					partName = partNum;
					address = getAddressByPartNum(partNum);
					time = bean_copy.getHour() + ":" + bean_copy.getMinute()
							+ ":" + bean_copy.getSecond();
					endTime = getEdnTime(bean_copy.getHour(),
							bean_copy.getMinute(), bean_copy.getSecond());
					tv_yuding_write.setText(partNum);
					tv_address_write.setText(address);
					tv_time_write.setText(time);
					tv_time_end_write.setText(endTime);
				}
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	protected String getEdnTime(int hour, int minute, int second) {
		int h, m, s;
		if (minute + 30 > 60) {
			h = hour + 1;
			if (h == 25)
				h = 1;
			m = minute + 30 - 60;
			s = second;
			return h + ":" + m + ":" + s;
		}
		if (minute + 30 == 60) {
			h = hour + 1;
			if (h == 25)
				h = 1;
			m = 0;
			s = second;
			return h + ":" + m + ":" + s;
		}
		if (minute + 30 < 60 && minute + 60 > 0) {
			h = hour;
			m = minute+30;
			s = second;
			return h + ":" + m + ":" + s;

		}
		return null;
	}

	public String getAddressByPartNum(String partNum) {
		if (partNum.charAt(0) == 'A') {
			return "���򳡺�С·";
		}
		if (partNum.charAt(0) == 'B') {
			return "ѧʶ·";
		}
		if (partNum.charAt(0) == 'D') {
			return "�̰�¥�Ҳ�";
		}
		if (partNum.charAt(0) == 'E') {
			return "�̰�¥���";
		}
		if (partNum.charAt(0) == 'F') {
			return "�̰�¥���";
		}
		return null;
	}

	private void initView() {
		tv_yuding = (TextView) getActivity().findViewById(R.id.tv_yuding);
		tv_yuding_write = (TextView) getActivity().findViewById(
				R.id.tv_yuding_write);
		tv_address = (TextView) getActivity().findViewById(R.id.tv_address);
		tv_address_write = (TextView) getActivity().findViewById(
				R.id.tv_adress_write);
		tv_time = (TextView) getActivity().findViewById(R.id.tv_time);
		tv_time_write = (TextView) getActivity().findViewById(
				R.id.tv_time_write);
		tv_time_end = (TextView) getActivity().findViewById(R.id.tv_time_end);
		tv_time_end_write = (TextView) getActivity().findViewById(
				R.id.tv_time_end_write);
		iv_cry = (ImageView) getActivity().findViewById(R.id.iv_cry);
		tv_cry = (TextView) getActivity().findViewById(R.id.tv_cry);

		content_lay = (RelativeLayout) getActivity().findViewById(
				R.id.contentLay);
		btnCancle = (Button) getActivity().findViewById(R.id.cancle_yuding);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ����Ƿ��½
	private boolean checkLogin() {
		BmobUser bmobUser = new BmobUser();
		if (BmobUser.getCurrentUser(getActivity()) == null
				|| BmobUser.getCurrentUser(getActivity()).getUsername() == null) {
			return false;
		}
		return true;
	}

}
