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
	int Connect_status = 0;// 默认未连接服务器为0

	@Override
	public void run() {
		try {
			s = new Socket("192.168.199.187", 9001);

			// 设置连接无延迟
			s.setTcpNoDelay(isAlive());

			is = s.getInputStream();
			os = s.getOutputStream();

			sendMessage("POST");
			// 连接成功后连接状态为1
			Connect_status = 1;
			new Thread(run).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getMessage();
	}

	// 断开连接，关闭流
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

	// 向服务器发送消息
	public void sendMessage(String str) {
		try {
			if (os != null) {
				dos = new BufferedWriter(new OutputStreamWriter(os));
				dos.write(str);

				dos.flush();// 写完后要记得flush

			} else {
				Log.e("<<<<<<<<<<<<>>>>>>>>>>>", "服务器没连接");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取到服务器发送过来的消息
	public String getMessage() {
		int len;
		String str = "";
		try {
			if (is != null) {

				br = new BufferedReader(new InputStreamReader(is));

				char[] ch = new char[502];
				while ((len = br.read(ch)) != -1) {
					str = String.valueOf(ch);
					// 服务器返回的数据
					String data = (String.valueOf(ch)).substring(2,
							str.lastIndexOf("))"));
					if (data.equals("0x5a") || data.contains("0x5a")
							&& data.length() == 4) {
						Log.e("心跳返回结果：", "----------" + data);
						// 处理心跳返回结果
						accessKeepAlive(data);

					} else {
						Log.e("正常返回结果：", "----------" + data);
						accessNormal(data);
					}
					if (data == null || data.equals("") || !data.contains("ok")) {
						Log.e("-----------", "与服务器连接断开");
						// Connect_status = 0;
						//						_stop();
					}
				}
				br.close();
			}else{
				Log.e("<<<<<<<<<<<>>>>>>>>>>", "服务器没连接");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// 处理心跳包返回数据
	private void accessKeepAlive(String text) {

		// 截取字符串 再处理
		if (text.equals("0x5a") || text.contains("0x5a")) {
			sendMessage("");
		}
	}

	// 处理正常返回数据
	public void accessNormal(String text) {
		if (text.equals("0x5a") || text.contains("0x5a")) {
			sendMessage("");
		}
	}

	// 向服务器发送心跳包
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
			// 连接断开
			return false;
		}
		if (s != null) {
			// 发送消息
			this.sendMessage(Comm_Data);
			return true;
		}
		return false;
	}

}
