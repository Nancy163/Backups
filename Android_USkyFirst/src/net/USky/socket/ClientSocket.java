package net.USky.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;
import android.widget.Toast;

public class ClientSocket extends Thread {
	private BufferedReader br;
	private BufferedWriter dos;
	private Socket s;
	private InputStream is;
	private OutputStream os;
	boolean flag;
	int Connect_status = 0;// Ĭ��δ���ӷ�����Ϊ0

	@Override
	public void run() {
		try {
			s = new Socket("192.168.199.187", 9001);

			// �����������ӳ�
			s.setTcpNoDelay(isAlive());

			is = s.getInputStream();
			os = s.getOutputStream();

			sendMessage("POST");
			// ���ӳɹ�������״̬Ϊ1
			Connect_status = 1;
			new Thread(run).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getMessage();
	}

	// �Ͽ����ӣ��ر���
	public void _stop() {
		if (!s.isConnected() || Connect_status == 0) {
			try {
				br.close();
				dos.flush();
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// �������������Ϣ
	public void sendMessage(String str) {
		try {
			if (os != null) {
				dos = new BufferedWriter(new OutputStreamWriter(os));
				dos.write(str);

				dos.flush();// д���Ҫ�ǵ�flush

			} else {
				Log.e("<<<<<<<<<<<<>>>>>>>>>>>", "������û����");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ��ȡ�����������͹�������Ϣ
	public String getMessage() {
		int len;
		String str = "";
		try {
			if (is != null) {

				br = new BufferedReader(new InputStreamReader(is));

				char[] ch = new char[502];
				while ((len = br.read(ch)) != -1) {
					str = String.valueOf(ch);
					// ���������ص�����
					String data = (String.valueOf(ch)).substring(2,
							str.lastIndexOf("))"));
					if (data.equals("0x5a") || data.contains("0x5a")
							&& data.length() == 4) {
						Log.e("�������ؽ����", "----------" + data);
						// �����������ؽ��
						accessKeepAlive(data);

					} else {
						Log.e("�������ؽ����", "----------" + data);
						accessNormal(data);
					}
					if (data == null || data.equals("") || !data.contains("ok")) {
						Log.e("-----------", "����������ӶϿ�");
						// Connect_status = 0;
						//						_stop();
					}
				}
				br.close();
			}else{
				Log.e("<<<<<<<<<<<>>>>>>>>>>", "������û����");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// ������������������
	private void accessKeepAlive(String text) {

		// ��ȡ�ַ��� �ٴ���
		if (text.equals("0x5a") || text.contains("0x5a")) {
			sendMessage("");
		}
	}

	// ����������������
	public void accessNormal(String text) {
		if (text.equals("0x5a") || text.contains("0x5a")) {
			sendMessage("");
		}
	}

	// �����������������
	Runnable run = new Runnable() {

		@Override
		public void run() {
			flag = true;
			// TODO Auto-generated method stub
			while (s.isConnected()) {
				if (flag) {
					String keepalive = "0x5a";
					SendData(keepalive);
				} else {
					_stop();
				}
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public synchronized boolean SendData(String Comm_Data) {
		if (!s.isConnected()) {
			// ���ӶϿ�
			return false;
		}
		if (s != null) {
			// ������Ϣ
			this.sendMessage(Comm_Data);
			return true;
		}
		return false;
	}

}
