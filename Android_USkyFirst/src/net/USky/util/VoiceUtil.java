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
	// Ĭ�Ϸ�����
	private String voicer = "xiaoyan";
	// ������дUI
	public  RecognizerDialog iatDialog;
	// ������д����
	private SpeechRecognizer mIat;
	private ClientSocket socket;
	private AnimationDrawable animListen;

	// ������ʼ��
	public VoiceUtil(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	// �����ϳ� ���ò���
	public void setParam() {
		// ��ղ���
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// ��������
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// ������������
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		// ��������ǰ�˵�
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// ����������˵�
		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
		// ���ñ����� 1Ϊ�б�� 0Ϊû���
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		String savePath = Environment.getExternalStorageDirectory()
				+ "/iflytek/wavaudio.amr";
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, savePath);
		// ������Ƶ����·��
	}

	// ����ʶ�� ���ò���
	private void setParam2() {
		// ���úϳ�
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// ���÷�����
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		// ��������
		mTts.setParameter(SpeechConstant.SPEED, "50");
		// ��������
		mTts.setParameter(SpeechConstant.PITCH, "50");
		// ��������
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		// ���ò�������Ƶ������
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
	}

	// ��ʼ������
	public InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.e(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {

				Toast.makeText(context, "��ʼ��ʧ��,�����룺" + code, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	/**
	 * �ϳɻص�������
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			Toast.makeText(context, "��ʼ����", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakPaused() {
			Toast.makeText(context, "��ͣ����", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakResumed() {
			Toast.makeText(context, "��������", Toast.LENGTH_SHORT).show();
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
	 * ������������
	 */
	public void playSpeech(String text) {
		// ���ò���
		setParam2();
		// �ʶ�
		int code = mTts.startSpeaking(text, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// δ��װ����ת����ʾ��װҳ��
			} else {
				Toast.makeText(context, "�����ϳ�ʧ��,������: " + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * ��дUI������
	 */
	public RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		public void onResult(RecognizerResult results, boolean isLast) {

			Log.e(">>>>>>>����>>>>>>>>", results.getResultString());

			String text = JsonParser.parseIatResult(results.getResultString());
			// �������������Ϣ
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

			// ������������
			String callbackresult = socket.getMessage();
			if (callbackresult.contains("0x5a")) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = callbackresult;
				handler.sendMessage(msg);
			}

		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
			Toast.makeText(context,
					error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
		}

	};

	/**
	 * ��д��������
	 */
	public RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// "��ʼ˵��"

		}

		@Override
		public void onEndOfSpeech() {
			// "����˵��"
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			center.setText(text);
			if (isLast) {
				// TODO ���Ľ��

			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			// "��ǰ����˵����������С��" + volume
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
	 * �Զ�����������
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
