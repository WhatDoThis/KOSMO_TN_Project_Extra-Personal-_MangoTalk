import java.io.*;
import java.net.*;

class MangoTalk_User extends Thread 
{
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String chatId;

	Socket s;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;

	String ip = "192.168.0.5";
	int port = 4000;

	MangoTalk_User(){
		connect();
	}

	void connect(){
		try{
			s = new Socket(ip, port);

			is = s.getInputStream();
			os = s.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);

			start();
			speak();
		}catch(IOException ie){
			connect();
		}
	}

	public void run(){
		try{
			while(true){
				String msg = dis.readUTF();
				pln(msg);
			}
		}catch(IOException ie){
			pln("서버 다운.. 2초 후에 종료합니다");
			try{
				Thread.sleep(2000);
				System.exit(0);
			}catch(InterruptedException iee){}
		}finally{
			closeAll();
		}
	}

	void speak(){
		p("채팅ID(기본 GUEST): ");
		try{
			chatId = br.readLine();
			chatId = chatId.trim();
			if(chatId.length() == 0) chatId = "GUEST";

			dos.writeUTF(chatId);
			dos.flush();

			sendMsg();
		}catch(IOException ie){
		}
	}

	void sendMsg(){
		try{
			while(true){
				String msg = br.readLine();
				dos.writeUTF(chatId + ": " + msg);
				dos.flush();
			}
		}catch(IOException ie){
		}finally{
			closeAll();
		}
	}
	
	void closeAll(){
		try{
			dis.close();
			dos.close();
			is.close();
			os.close();
			s.close();
		}catch(IOException ie){}
	}

	void pln(String str){
		System.out.println(str);
	}

	void p(String str){
		System.out.print(str);
	}

	public static void main(String[] args) 
	{
		new MangoTalk_User();
	}
}