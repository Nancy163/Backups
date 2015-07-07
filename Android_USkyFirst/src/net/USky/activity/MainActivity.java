package net.USky.activity;

import net.USky.util.SqliteUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lasttest.R;

/**
 * 
 * @author xusijing
 *
 */
public class MainActivity extends Activity {
	private SqliteUtil util;
	EditText ename, etel;
	Button login_btn;
	public static final String screKey = "4dc13ff8658333a0c178dcd3fc31490762581c0dbc28cca7";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		util = new SqliteUtil(MainActivity.this);

	}

	/**
	 * ��������г�ʼ��
	 */
	private void initView() {
		ename = (EditText) findViewById(R.id.login_ename);
		etel = (EditText) findViewById(R.id.login_etel);
		login_btn = (Button) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(OnClick);
	}

	OnClickListener OnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.login_btn:
				decide();// �ж�
				break;
			}
		}
	};

	/**
	 * ���û���������ݽ����жϴ���
	 */
	private void decide() {
		String name = ename.getText().toString().trim();
		String tel = etel.getText().toString().trim();
		if (tel == null || tel.equals("") || name == null || name.equals("")) {
			Dialog dialog = new AlertDialog.Builder(this).setMessage(
					"Ϊ�������������ù�ͨ������д������Ϣ").create();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.5f;
			window.setAttributes(lp);
			dialog.show();
			dialog.setCanceledOnTouchOutside(true);// ����Ի����ⲿȡ���Ի�����ʾ
		} else if (!isMobileNO(tel)) {
			AlertDialog dialog = new AlertDialog.Builder(this).setMessage(
					"��������ֻ�λ������ȷ").create();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.5f;
			window.setAttributes(lp);
			dialog.show();
			dialog.setCanceledOnTouchOutside(true);// ����Ի����ⲿȡ���Ի�����ʾ
		} else {

			// �����ݱ��浽���ݿ�
			util.insert(name, tel);
			// ��תҳ�棬��ʼ�����()

			Intent intent = new Intent(MainActivity.this, VideoActivity.class);
			startActivity(intent);

		}
	}

	/**
	 * ��֤�ֻ���ʽ
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * �ƶ���134��135��136��137��138��139��150��151��157(TD)��158��159��187��188
		 * ��ͨ��130��131��132��152��155��156��185��186 ���ţ�133��153��180��189����1349��ͨ��
		 * �ܽ��������ǵ�һλ�ض�Ϊ1���ڶ�λ�ض�Ϊ3��5��8������λ�õĿ���Ϊ0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"�����1λΪ����1��"[358]"����ڶ�λ����Ϊ3��5��8�е�һ����"\\d{9}"��������ǿ�����0��9�����֣���9λ��
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			Log.e("MainActivity", "----------" + mobiles.matches(telRegex));
			return mobiles.matches(telRegex);
		}
	}
}
