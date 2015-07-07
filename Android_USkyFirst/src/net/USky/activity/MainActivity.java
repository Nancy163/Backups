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
	 * 对组件进行初始化
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
				decide();// 判断
				break;
			}
		}
	};

	/**
	 * 对用户输入的内容进行判断处理
	 */
	private void decide() {
		String name = ename.getText().toString().trim();
		String tel = etel.getText().toString().trim();
		if (tel == null || tel.equals("") || name == null || name.equals("")) {
			Dialog dialog = new AlertDialog.Builder(this).setMessage(
					"为了我们与您更好沟通，请填写完整信息").create();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.5f;
			window.setAttributes(lp);
			dialog.show();
			dialog.setCanceledOnTouchOutside(true);// 点击对话框外部取消对话框显示
		} else if (!isMobileNO(tel)) {
			AlertDialog dialog = new AlertDialog.Builder(this).setMessage(
					"您输入的手机位数不正确").create();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.5f;
			window.setAttributes(lp);
			dialog.show();
			dialog.setCanceledOnTouchOutside(true);// 点击对话框外部取消对话框显示
		} else {

			// 把数据保存到数据库
			util.insert(name, tel);
			// 跳转页面，开始云验光()

			Intent intent = new Intent(MainActivity.this, VideoActivity.class);
			startActivity(intent);

		}
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			Log.e("MainActivity", "----------" + mobiles.matches(telRegex));
			return mobiles.matches(telRegex);
		}
	}
}
