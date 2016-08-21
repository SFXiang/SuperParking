package com.example.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.example.Bmob.Bean;
import com.example.DJstlia.MGraph;
import com.example.DJstlia.Point_Flag;
import com.example.supermap.LoginActivity;
import com.example.supermap.R;
import com.example.supermap.SecondA;
import com.example.supermap.SecondB;
import com.example.supermap.SecondD;
import com.example.supermap.SecondE;
import com.example.supermap.SecondF;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

	// ��ͼ�ؼ�
	MapView mapView = null;
	// ��ͼ�Ĺ���Ա
	BaiduMap baiduMap = null;

	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	public static final String TAG = "NaviSDkDemo";
	private static final String APP_FOLDER_NAME = "BNSDKDemo";
	private String mSDCardPath = null;
	public static final String ROUTE_PLAN_NODE = "routePlanNode";

	// ���帲����
	public Marker markerA, markerB, markerC, markerD, markerE, markerF;
	private BitmapDescriptor bitmapDescriptor = null;
	private LatLng latLngA, latLngB, latLngC, latLngD, latLngE, latLngF;
	MarkerOptions markerOptions = null;

	// ��λ����
	LocationClient client = null;
	LocationClientOption clientOption = null;
	myBdLocationListener bdLocationListener;
	static LatLng latLngStart = null;

	// �Ͻ�˹�����㷨��Ҫ������
	int MAX_V = 30;
	int UNDEFINE = 32767;
	int have[] = new int[30];
	int p[][] = new int[MAX_V][MAX_V];
	int d[] = new int[MAX_V];
	int v0; // ���ڼ������ʾ�붨λ������ĵ�
	Point_Flag point_Flag = new Point_Flag();
	MGraph g = new MGraph();

	// ����һ����־���� �����жϵ����ǲ��ǵ�һ�ε���
	int IsFirstNavi = 0; // 0:�����һ�ε��� 1�������ǵ�һ�ε���

	public Intent intent = new Intent();

	private Overlay overlayLine, overlayStart, overlayEnd;

	// ��½ͼ��
	private ImageView user_area;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SDKInitializer.initialize(getActivity().getApplicationContext());

		return inflater.inflate(R.layout.home_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// ��ʼ��bmob
		Bmob.initialize(getActivity().getApplicationContext(),
				"3dd366acdf71089a6a3d65985df857f6");
		mapView = (MapView) getActivity().findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		bdLocationListener = new myBdLocationListener();
		// ��ͼ״̬��ʼ��
		mapStatueInit();
		// ��ʼ���ؼ�user_area
		initView();
		// ��ʼ����λ
		initLocationView();
		// ��ʼ��������ؼ�
		initMarkerview();
		// Ϊ���ǵ���Ӽ�����Ϣ
		setMarkerListener();

	}

	private void initView() {
		user_area = (ImageView) getActivity().findViewById(R.id.user_area);
		user_area.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkLogin()) {

					SettingFragment settingFragment = new SettingFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.frameContent, settingFragment)
							.commit();
				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}
			}
		});
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

	// Ϊ���ǵ���Ӽ�����Ϣ
	private void setMarkerListener() {
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				if (arg0 == markerA)
					showDialogue(markerA, 1);

				if (arg0 == markerB) {
					showDialogue(markerB, 2);
				}
				if (arg0 == markerD) {
					showDialogue(markerD, 3);
				}
				if (arg0 == markerE) {
					showDialogue(markerE, 4);
				}
				if (arg0 == markerF) {
					showDialogue(markerF, 5);
				}

				return false;
			}
		});
	}

	/**
	 * 
	 * @param marker
	 * @param flag
	 *            1:SecondA 2:SecondB 3:SecondD 4:SecondE 5:SecondF
	 */
	public void showDialogue(final Marker marker, int flag) {

		if (flag == 1)
			intent.setClass(getActivity(), SecondA.class);
		if (flag == 2)
			intent.setClass(getActivity(), SecondB.class);
		if (flag == 3)
			intent.setClass(getActivity(), SecondD.class);
		if (flag == 4)
			intent.setClass(getActivity(), SecondE.class);
		if (flag == 5)
			intent.setClass(getActivity(), SecondF.class);
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("�Ƿ�鿴����ͣ�������")
				.setPositiveButton("�鿴", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						// ������ʾ��������Ĵ���ʵ��
						startActivity(intent);
					}
				})
				.setNegativeButton("ֱ�ӵ���",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								if (IsFirstNavi == 1) {
									if (overlayEnd != null
											&& overlayLine != null
											&& overlayStart != null) {
										overlayEnd.remove();
										overlayLine.remove();
										overlayStart.remove();
									}
								}
								MyLocationData locationData = baiduMap
										.getLocationData();
								// 1����������붨λ������Ľڵ�

								LatLng latLng = new LatLng(
										locationData.latitude,
										locationData.longitude);
								v0 = getDistance(latLng) + 1;
								// 2�������㷨�����̵�·��,,���������·���б�
								List<LatLng> list = shortPath(g, v0, p, d,
										Integer.parseInt(marker.getTitle()
												.toString()));
								// 3�����¶���һ���µ�list�б� ����λ�������Ҳ��ӽ�ȥ
								List<LatLng> list2 = new ArrayList<LatLng>();
								list2.add(latLng);
								for (int i = 0; i < list.size(); i++) {
									LatLng lng = list.get(i);
									list2.add(lng);
								}
								// 4������list�б��������
								initLine(list2);
								BitmapDescriptor descriptorEnd = new BitmapDescriptorFactory()
										.fromResource(R.drawable.zhong);
								markerOptions = new MarkerOptions()
										.position(marker.getPosition())
										.icon(descriptorEnd)
										.animateType(MarkerAnimateType.none);
								overlayEnd = baiduMap.addOverlay(markerOptions);
								((Marker) overlayEnd).setToTop(); // �������
								BitmapDescriptor descriptorStart = new BitmapDescriptorFactory()
										.fromResource(R.drawable.qi);
								markerOptions = new MarkerOptions()
										.position(latLng).icon(descriptorStart)
										.animateType(MarkerAnimateType.none);
								overlayStart = baiduMap
										.addOverlay(markerOptions);
								IsFirstNavi = 1; // ������ ���Ƿ��һ�ε�����Ϣ�ı�
								Toast.makeText(getActivity(), "·�ߵ����ɹ���",
										Toast.LENGTH_SHORT).show();
							}
						});

		dialog = builder.create();
		dialog.show();

	}

	// ��ʼ������
	private void initLine(List<LatLng> list) {
		// TODO Auto-generated method stub
		PolylineOptions options = new PolylineOptions();
		options.width(13);
		options.color(Color.BLUE);
		options.points(list);
		overlayLine = baiduMap.addOverlay(options);
	}

	// ��������붨λ������������
	public int getDistance(LatLng latLng) {

		double pk = 180 / 3.14169;
		double minDistance = 32767;
		double a1, a2, b1, b2, t1, t2, t3, tt;
		int flag = 0;
		for (int i = 0; i < 17; i++) {
			a1 = latLng.latitude / pk; // γ��
			a2 = latLng.longitude / pk; // ����
			b1 = point_Flag.getY()[i] / pk;
			b2 = point_Flag.getX()[i] / pk;
			t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
			t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
			t3 = Math.sin(a1) * Math.sin(b1);
			tt = Math.acos(t1 + t2 + t3);
			if (tt < minDistance) {
				minDistance = tt;
				flag = i;
			}
		}
		return flag;
	}

	// �Ͻ�˹�����㷨ѡ������֮����̵ľ���q
	public List<LatLng> shortPath(MGraph g, int v0, int p[][], int d[], int end) {
		int v, w, i, j, min;
		int finall[] = new int[MAX_V];
		int k = 1;
		for (v = 0; v < g.getVexnum(); ++v) {
			// ��ʼ��
			finall[v] = 0;
			d[v] = g.getArcs()[v0 - 1][v];
			for (w = 0; w < g.getVexnum(); ++w) {
				p[v][w] = 0;
			}
			if (d[v] < UNDEFINE) {
				p[v][v0 - 1] = 1;
				p[v][v] = 1;
			}
		}

		d[v0 - 1] = 0;
		finall[v0 - 1] = 1;
		have[0] = v0 - 1;

		for (i = 1; i < g.getVexnum(); ++i) {// �����vexnum-1������
			min = UNDEFINE;
			for (w = 0; w < g.getVexnum(); ++w)
				if (finall[w] == 0)
					if (d[w] < min) {
						v = w;
						min = d[w];
					}
			finall[v] = 1;
			have[k] = v;
			k++;
			for (w = 0; w < g.getVexnum(); ++w) {
				if (finall[w] == 0 && (min + g.getArcs()[v][w]) < d[w]) {
					d[w] = min + g.getArcs()[v][w];
					for (j = 0; j < g.getVexnum(); j++)
						p[w][j] = p[v][j];
					p[w][w] = 1;

				}
			}
		}
		List<LatLng> list = new ArrayList<LatLng>();

		for (i = 0; i < g.getVexnum(); i++) {
			if (p[end - 1][have[i]] == 1) {
				/*
				 * num[flag] = g.vexs[have[i]]; flag++;
				 */

				LatLng latLng = new LatLng(
						point_Flag.getY()[g.getVexs()[have[i]] - 1],
						point_Flag.getX()[g.getVexs()[have[i]] - 1]);
				list.add(latLng);

			}
		}

		return list;

	}

	// ��ʼ����ͼ������
	private void initMarkerview() {
		// TODO Auto-generated method stub

		BmobQuery<Bean> bmobQuery = new BmobQuery<Bean>();
		bmobQuery.findObjects(getActivity().getApplicationContext(),
				new FindListener<Bean>() {

					@Override
					public void onSuccess(List<Bean> arg0) {
						// TODO Auto-generated method stub
						int A = 0, B = 0, D = 0, E = 0, F = 0;
						for (int i = 0; i < arg0.size(); i++) {
							if (arg0.get(i).getPartNum().charAt(0) == 'A')
								A++;
							if (arg0.get(i).getPartNum().charAt(0) == 'B')
								B++;
							if (arg0.get(i).getPartNum().charAt(0) == 'C') {
							}
							if (arg0.get(i).getPartNum().charAt(0) == 'D')
								D++;
							if (arg0.get(i).getPartNum().charAt(0) == 'E')
								E++;
							if (arg0.get(i).getPartNum().charAt(0) == 'F')
								F++;
						}

						if (A == 18) {
							bitmapDescriptor = new BitmapDescriptorFactory()
									.fromResource(R.drawable.man);
							latLngA = new LatLng(39.535028, 116.746023);
							markerOptions = new MarkerOptions()
									.position(latLngA).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("3");
							markerA = (Marker) baiduMap
									.addOverlay(markerOptions);

						} else {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.kong);
							latLngA = new LatLng(39.535028, 116.746023);
							markerOptions = new MarkerOptions()
									.position(latLngA).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("3");
							markerA = (Marker) baiduMap
									.addOverlay(markerOptions);

						}

						if (B == 18) {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.man);
							latLngB = new LatLng(39.533995, 116.745902);
							markerOptions = new MarkerOptions()
									.position(latLngB).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("6");
							markerB = (Marker) baiduMap
									.addOverlay(markerOptions);

						} else {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.kong);

							latLngB = new LatLng(39.533995, 116.745902);
							markerOptions = new MarkerOptions()
									.position(latLngB).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("6");
							markerB = (Marker) baiduMap
									.addOverlay(markerOptions);

						}

						if (D == 20) {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.man);
							latLngD = new LatLng(39.531547, 116.747869);
							markerOptions = new MarkerOptions()
									.position(latLngD).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("15");
							markerD = (Marker) baiduMap
									.addOverlay(markerOptions);

						} else {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.kong);
							latLngD = new LatLng(39.531547, 116.747869);
							markerOptions = new MarkerOptions()
									.position(latLngD).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("15");
							markerD = (Marker) baiduMap
									.addOverlay(markerOptions);

						}
						if (E == 18) {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.man);
							latLngE = new LatLng(39.53122, 116.747955);
							markerOptions = new MarkerOptions()
									.position(latLngE).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("16");
							markerE = (Marker) baiduMap
									.addOverlay(markerOptions);

						} else {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.kong);
							latLngE = new LatLng(39.53122, 116.747955);
							markerOptions = new MarkerOptions()
									.position(latLngE).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("16");
							markerE = (Marker) baiduMap
									.addOverlay(markerOptions);

						}
						if (F == 27) {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.man);
							latLngF = new LatLng(39.532128, 116.7442);
							markerOptions = new MarkerOptions()
									.position(latLngF).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("17");
							markerF = (Marker) baiduMap
									.addOverlay(markerOptions);

						} else {
							bitmapDescriptor = BitmapDescriptorFactory
									.fromResource(R.drawable.kong);
							latLngF = new LatLng(39.532128, 116.7442);
							markerOptions = new MarkerOptions()
									.position(latLngF).icon(bitmapDescriptor)
									.animateType(MarkerAnimateType.grow)
									.title("17");
							markerF = (Marker) baiduMap
									.addOverlay(markerOptions);

						}

					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "����������ʧ��, ������������!", 3000)
								.show();
					}
				});
	}

	// ��ʼ����λ����
	private void initLocationView() {
		// TODO Auto-generated method stub
		client = new LocationClient(getActivity());
		// ��λ������
		client.registerLocationListener(bdLocationListener);
		// ������λͼ��
		baiduMap.setMyLocationEnabled(true);
		clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);// ��gps
		clientOption.setCoorType("bd09ll"); // ������������bd09ll
		clientOption.setScanSpan(1000);
		client.setLocOption(clientOption);
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL,
				true, null));
		client.start();
		Log.i("main", "��ʼ��ִ����");

	}

	private void mapStatueInit() {
		LatLng cenpt = new LatLng(39.533004, 116.746697);
		// �����ͼ״̬
		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18)
				.build();
		// ����MapStatusUpdate�����Ա�������ͼ״̬��Ҫ�����ı仯

		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// �ı��ͼ״̬
		baiduMap.setMapStatus(mMapStatusUpdate);
	}

	// ��λ
	class myBdLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mapView == null) {
				return;
			}

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(location.getDirection())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(39.533323, 116.74649);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mapView != null)
			mapView.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mapView != null)
			mapView.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mapView != null)
			mapView.onDestroy();
		if (bitmapDescriptor != null)
			bitmapDescriptor.recycle();

	}

}
