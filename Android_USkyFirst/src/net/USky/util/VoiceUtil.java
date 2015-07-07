package net.USky.util;

import net.USky.activity.RecodeActivity;
import net.USky.parse.DataParse;
import net.USky.parse.JsonParser;
import net.USky.socket.ClientSocket;

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

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceUtil {
	Context context;
	Handler handler;
	protected static final String TAG = null;
	private SpeechSynthesizer mTts;
	private TextView center;
	int ret = 0;
	// 默认发音人
	private String voicer = "xiaoyan";
	// 语音听写UI
	public  RecognizerDialog iatDialog;
	// 语音听写对象
	private SpeechRecognizer mIat;
	private ClientSocket socket;
	private AnimationDrawable animListen;

	// 语音初始化
	public VoiceUtil(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
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
	public InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.e(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {

				Toast.makeText(context, "初始化失败,错误码：" + code, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			Toast.makeText(context, "开始播放", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakPaused() {
			Toast.makeText(context, "暂停播放", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakResumed() {
			Toast.makeText(context, "继续播放", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(context,
						error.getPlainDescription(true), Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

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
				Toast.makeText(context, "语音合成失败,错误码: " + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 听写UI监听器
	 */
	public RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		public void onResult(RecognizerResult results, boolean isLast) {

			Log.e(">>>>>>>解析>>>>>>>>", results.getResultString());

			String text = JsonParser.parseIatResult(results.getResultString());
			// 向服务器发送消息
			if (text != null && text != "") {
				center.setText(text);
				Log.e("----------", text);
				socket.sendMessage(DataParse.toJson("register", "admin", text));
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
			Toast.makeText(context,
					error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
		}

	};

	/**
	 * 听写监听器。
	 */
	public RecognizerListener recognizerListener = new RecognizerListener() {

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
	public void showFrame(int i,ImageView imageView) {
		// TODO Auto-generated method stub
		switch (i) {
		case 1:
			imageView.setBackgroundResource(R.anim.listenframe);
			animListen = (AnimationDrawable) imageView.getBackground();
			animListen.setOneShot(false);
			break;
		}
		animListen.start();
	}

}
