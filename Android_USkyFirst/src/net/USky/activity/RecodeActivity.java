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
	// Ĭ�Ϸ�����
	private String voicer = "xiaoyan";
	// ������дUI
	private RecognizerDialog iatDialog;
	// ������д����
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

		// ������ʼ��
		initSpeech();

		handler.sendEmptyMessage(0);
	}

	private void initSpeech() {
		// ������֤Ӧ�õ�key
		SpeechUtility.createUtility(RecodeActivity.this, "appid=5577f954");
		// ����������д����
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
		// ��ʼ����дDialog,���ֻʹ����UI��д����,���贴��SpeechRecognizer
		// ����������дUI
		iatDialog = new RecognizerDialog(this, mInitListener);
		// ���������ϳɶ���
		mTts = SpeechSynthesizer.createSynthesizer(this, mInitListener);

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
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.e(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {

				Toast.makeText(RecodeActivity.this, "��ʼ��ʧ��,�����룺" + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	/**
	 * �ϳɻص�������
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			Toast.makeText(RecodeActivity.this, "��ʼ����", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakPaused() {
			Toast.makeText(RecodeActivity.this, "��ͣ����", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onSpeakResumed() {
			Toast.makeText(RecodeActivity.this, "��������", Toast.LENGTH_SHORT)
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
	// ���߳�����ʱ���������������ݣ��������ݣ��߼��жϣ�
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// �ʶ� �������ϳɣ�
			if (msg.what == 0) {

				showFrame(1);
				animListen.setOneShot(false);

				if (msg.obj == null) {
					text = "����,��⿪ʼ,���������˵��������������";
					playSpeech(text);
				} else {
					text = (String) msg.obj;
					playSpeech(text);
				}
				
			}
			// ¼��������ʶ��
			if (msg.what == 1) {
				setParam();
				boolean isShowDialog = true;
				// showFrame(2);
				if (isShowDialog) {
					// ��ʾ��д�Ի���
					iatDialog.setListener(recognizerDialogListener);
					iatDialog.show();
				} else {
					// ����ʾ��д�Ի���
					ret = mIat.startListening(recognizerListener);
					if (ret != ErrorCode.SUCCESS) {
						Toast.makeText(RecodeActivity.this, "��дʧ��,�����룺" + ret,
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
				Toast.makeText(RecodeActivity.this, "�����ϳ�ʧ��,������: " + code,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * ��дUI������
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		public void onResult(RecognizerResult results, boolean isLast) {

			Log.e(">>>>>>>����>>>>>>>>", results.getResultString());

			String text = JsonParser.parseIatResult(results.getResultString());
			// �������������Ϣ
			if (text != null && text != "") {
				center.setText(text);
				Log.e("----------", text);
				if (text != null && !text.equals("")) {
					socket.sendMessage(DataParse.toJson("register", "admin",
							text));
				} else {
					socket.sendMessage(DataParse.toJson("register", "admin",
							"�û�û��˵��"));
				}
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
			iatDialog.dismiss();
			// ¼���������󷵻�
			Message msg = new Message();
			msg.what = 0;
			msg.obj = "������û��˵��Ŷ��������¼��";
			handler.sendMessage(msg);
		}

	};

	/**
	 * ��д��������
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

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
