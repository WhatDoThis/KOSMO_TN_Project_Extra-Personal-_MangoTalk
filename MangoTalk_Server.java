import java.io.*;
import java.net.*;
import java.util.*;

class MangoTalk_Server extends Thread{
	ServerSocket ss;
	Socket s;
	int port = 4000;
	MangoTalk_Module mtm;
	Vector<MangoTalk_Module> v = new Vector<MangoTalk_Module>();
	boolean isFirst = true;

	MangoTalk_Server(){
		try{
			ss = new ServerSocket(port);
			pln(port + " 번 포트에서 대기중..");

			while(true){
				s = ss.accept();
				mtm = new MangoTalk_Module(this);
				v.add(mtm);
				mtm.start();
				if(isFirst){
					start();
					isFirst = false;
				}
			}
		}catch(IOException ie){
			pln(port+" 번 포트는 유효하지 않음");
		}finally{
			try{
				if(ss != null) ss.close();
			}catch(IOException ie){}
		}
	}

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	public void run(){
		try{
			while(true){
				String msg = br.readLine();
				mtm.sendMem("관리자: " + msg);
			}
		}catch(IOException ie){
		}
	}

	void pln(String str){
		System.out.println(str);
	}

	public static void main(String[] args)
	{
		new MangoTalk_Server();
	}
}

class MangoTalk_Module extends Thread{
	MangoTalk_Server ms;
	Socket s;

	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;

	String chatId;

	MangoTalk_Module(MangoTalk_Server ms){
		this.ms = ms;
		this.s = ms.s;
		try{
			is = s.getInputStream();
			os = s.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
		}catch(IOException ie){}
	}
	public void run(){
		listen();
	}
	void listen(){
		String msg = "";
		try{
			chatId = dis.readUTF();
			sendMem(chatId+ "님 입장!(인원: "+ms.v.size()+"명)"); //다른 클들에게
			ms.pln(chatId+ "님 입장!(인원: "+ms.v.size()+"명)"); //관리자창에게

			while(true){
				msg = dis.readUTF();
				sendMem(msg);
				ms.pln(msg);
			}
		}catch(IOException ie){
			ms.v.remove(this);
			sendMem(chatId+ "님 퇴장!(인원: "+ms.v.size()+"명)"); //다른 클들에게
			ms.pln(chatId+ "님 퇴장!(인원: "+ms.v.size()+"명)"); //관리자창에게 
		}finally{
			closeAll();
		}
	}
	
	void sendMem(String msg){
		try{
			for(MangoTalk_Module mtm : ms.v){
				mtm.dos.writeUTF(msg);
				mtm.dos.flush();
			}
		}catch(IOException ie){}
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
}