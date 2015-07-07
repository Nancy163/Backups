package net.USky.activity;

import net.USky.parse.DataParse;
import net.USky.parse.JsonParser;
import net.USky.socket.ClientSocket;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lasttest.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class RecodeActivity extends Activity {
	protected static final String TAG = null;
	private SpeechSynthesizer mTts;
	private TextView center;
	int ret = 0;
	// 默认发音人
	private String voicer = "xiaoyan";
	// 语音听写UI
	private RecognizerDialog iatDialog;
	// 语音听写对象
	private SpeechRecognizer mIat;
	private ClientSocket socket;
	private ImageView imageView;
	private AnimationDrawable animListen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		socket = new ClientSocket();
		socket.start();

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_recode);
		center = (TextView) findViewById(R.id.login_speech_text);
		imageView = (ImageView) findViewById(R.id.login_center_image);

		// 语音初始化
		initSpeech();

		handler.sendEmptyMessage(0);
	}

	private void initSpeech() {
		// 用于验证应用的key
		SpeechUtility.createUtility(RecodeActivity.this, "appid=5577f954");
		// 创建语音听写对象
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		// 创建语音听写UI
		iatDialog = new RecognizerDialog(this, mInitListener);
		// 创建语音合成对象
		mTts = SpeechSynthesizer.createSynthesizer(this, mInitListener);

	}

	// 语音合成 设置参数
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
		// 设置标点符号 1为有标点 0为没标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		String savePath = Environment.getExternalStorageDirectory()
				+ "/iflytek/wavaudio.amr";
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, savePath);
		// 设置音频保存路径
	}

	// 语音识别 设置参数
	private void setParam2() {
		// 设置合成
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		// 设置语速
		mTts.setParameter(SpeechConstant.SPEED, "50");
		// 设置音调
		mTts.setParameter(SpeechConstant.PITCH, "50");
		// 设置音量
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
	}

	// 初始化监听
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.e(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {

				Toast.makeText(RecodeActivity.this, "初始化失败,错误码：" + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			Toast.makeText(RecodeActivity.this, "开始播放", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakPaused() {
			Toast.makeText(RecodeActivity.this, "暂停播放", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakResumed() {
			Toast.makeText(RecodeActivity.this, "继续播放", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {

		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				new Thread() {
					public void run() {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						animListen.stop();
						handler.sendEmptyMessage(1);
					};
				}.start();

			} else if (error != null) {
				Toast.makeText(RecodeActivity.this,
						error.getPlainDescription(true)+"--", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};
	private String text;
	// 子线程做耗时操作，（解析数据，保存数据，逻辑判断）
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 朗读 （语音合成）
			if (msg.what == 0) {

				showFrame(1);
				animListen.setOneShot(false);

				if (msg.obj == null) {
					text = "您好,验光开始,请在三秒后说话。。。。。。";
					playSpeech(text);
				} else {
					text = (String) msg.obj;
					playSpeech(text);
				}
				
			}
			// 录音（语音识别）
			if (msg.what == 1) {
				setParam();
				boolean isShowDialog = true;
				// showFrame(2);
				if (isShowDialog) {
					// 显示听写对话框
					iatDialog.setListener(recognizerDialogListener);
					iatDialog.show();
				} else {
					// 不显示听写对话框
					ret = mIat.startListening(recognizerListener);
					if (ret != ErrorCode.SUCCESS) {
						Toast.makeText(RecodeActivity.this, "听写失败,错误码：" + ret,
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(RecodeActivity.this, "begin" + ret,
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}

	};

	/**
	 * 启动语音播放
	 */
	public void playSpeech(String text) {
		// 设置参数
		setParam2();
		// 朗读
		int code = mTts.startSpeaking(text, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页面
			} else {
				Toast.makeText(RecodeActivity.this, "语音合成失败,错误码: " + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		public void onResult(RecognizerResult results, boolean isLast) {

			Log.e(">>>>>>>解析>>>>>>>>", results.getResultString());

			String text = JsonParser.parseIatResult(results.getResultString());
			// 向服务器发送消息
			if (text != null && text != "") {
				center.setText(text);
				Log.e("----------", text);
				if (text != null && !text.equals("")) {
					socket.sendMessage(DataParse.toJson("register", "admin",
							text));
				} else {
					socket.sendMessage(DataParse.toJson("register", "admin",
							"用户没有说话"));
				}
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// 唤醒语音播放
			String callbackresult = socket.getMessage();
			if (callbackresult.contains("0x5a")) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = callbackresult;
				handler.sendMessage(msg);
			}

		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			iatDialog.dismiss();
			// 录入语音错误返回
			Message msg = new Message();
			msg.what = 0;
			msg.obj = "您好像没有说话哦！请重新录入";
			handler.sendMessage(msg);
		}

	};

	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// "开始说话"

		}

		@Override
		public void onEndOfSpeech() {
			// "结束说话"
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			center.setText(text);
			if (isLast) {
				// TODO 最后的结果

			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			// "当前正在说话，音量大小：" + volume
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub

		}

	};

	/**
	 * 对动画进行设置
	 *
	 * @param i
	 */
	private void showFrame(int i) {
		// TODO Auto-generated method stub
		switch (i) {
		case 1:
			imageView.setBackgroundResource(R.anim.listenframe);
			animListen = (AnimationDrawable) imageView.getBackground();
			break;
		}
		animListen.start();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mTts.pauseSpeaking();
		mTts.destroy();
//		System.exit(0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTts.pauseSpeaking();
		mTts.destroy();
//		System.exit(0);
	}

}
