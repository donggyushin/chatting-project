package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class server extends JFrame implements ActionListener{
	
	private JPanel contentPane;
	private JTextField port_tf;
	private JTextArea textArea = new JTextArea();
	private JButton start_btn = new JButton("implement");
	private JButton stop_btn = new JButton("stop");
	
	//��Ʈ��ũ�� ���� ����
	private ServerSocket server_socket;
	private Socket socket;
	
	private Vector user_vc = new Vector();
	private Vector room_vc = new Vector();
	
	private int port;
	
	private StringTokenizer st;
	private boolean room_ch = true;
	
		

	server(){
		
		init();
		start();
		
	}
	
	private void server_start() {
		try {
			server_socket = new ServerSocket(port);
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "already working port", "notification", JOptionPane.ERROR_MESSAGE);
		}
		
		if(server_socket != null) {
			connection();
		}
		
		
	}
	private void connection() {
		
		//1���� �����忡���� 1���� �۾��� ó���� �� �ִ�. 
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(true) {
					try {
						textArea.append("waiting for client\n");
						socket = server_socket.accept();   //����� ���� ���� ���
						textArea.append("found cliend!!\n");
						
						userInfo user = new userInfo(socket);
						
						user.start();		//��ü���� ������ ����
						
					} catch (IOException e) {
						textArea.append("Server has been stopped\n");
						break;
					}	
					
				}
				
			}
			
			
		});
		
		th.start();
		
		
		
	}
	
	private void init() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 639, 474);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 10, 564, 322);
		contentPane.add(scrollPane);
		
		
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		
		JLabel lblNewLabel = new JLabel("port number");
		lblNewLabel.setBounds(40, 347, 72, 15);
		contentPane.add(lblNewLabel);
		
		port_tf = new JTextField();
		port_tf.setBounds(124, 344, 195, 21);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		
		start_btn.setBounds(29, 390, 206, 23);
		contentPane.add(start_btn);
		
		
		stop_btn.setBounds(307, 390, 195, 23);
		contentPane.add(stop_btn);
		stop_btn.setEnabled(false);
		
		this.setVisible(true);
		
		
	}
	
	private void start() {
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new server();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		
		
		if(e.getSource() == start_btn) {
			System.out.println("start_btn clicked");
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			server_start();
			
			start_btn.setEnabled(false);
			port_tf.setEditable(false);
			stop_btn.setEnabled(true);
		}
		
		else if(e.getSource() == stop_btn) {
			System.out.println("stop_btn clicked");
			
			start_btn.setEnabled(true);
			port_tf.setEditable(true);
			stop_btn.setEnabled(false);
			
			
			try {
			server_socket.close();
			user_vc.removeAllElements();
			room_vc.removeAllElements();
			}
			catch(IOException e2) {
				e2.printStackTrace();
			}
		}
		
		
	}

	class userInfo extends Thread{
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private String Nickname;
		private Socket user_socket;
		
		userInfo(Socket soc) {
			// TODO Auto-generated constructor stub
			
			user_socket = soc;
			user_network();
			
			
		}
		
		private void user_network() {
			
			try {
			is = user_socket.getInputStream();
			os = user_socket.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
			
			Nickname = dis.readUTF();
			textArea.append(Nickname + "login!!\n");
			
			//����ڵ鿡�� ���� �����ڵ� �˷��ֱ�. 
			
			broadcast("NewUser/"+Nickname);
			
			//�� �ڽſ��� ������ ����ڵ� �߰�
			for(int i = 0 ; i < user_vc.size(); i ++) {
				userInfo u = (userInfo)user_vc.elementAt(i);
				
				send_message("OldUser/"+u.Nickname);
			}
			
			//�� �ڽſ��� ������ �� ��ϵ� �߰�
			for(int i = 0 ; i < room_vc.size(); i ++) {
				roomInfo r = (roomInfo)room_vc.elementAt(i);
				
				send_message("OldRoom/"+r.room_name);
			}
			send_message("room_list_update/ ");
			
			//�׸��� ���� ���Ϳ� �� �ڽ��� �߰�
			user_vc.add(this);
			
			broadcast("user_list_update/ ");
			
			
			}
			catch(IOException e) {
				JOptionPane.showMessageDialog(null, "Stream Error has incurred", "notification", JOptionPane.ERROR_MESSAGE);
				
				
			}
			
			
		}
		
		private void broadcast(String str) {
			for(int i = 0 ; i < user_vc.size(); i ++) {
				userInfo u = (userInfo)user_vc.elementAt(i);
				
				u.send_message(str);
			}
		}
		
		private void send_message(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		public void run() {
			
			while(true) {
				
				
				try {
					String msg;
					msg = dis.readUTF();
					textArea.append("message from "+Nickname+" : "+ msg + "\n");
					inmessage(msg);
				} catch (IOException e) {
					
					textArea.append(Nickname+"'s connection has been lost\n");
					
					try {
						dis.close();
						dos.close();
						this.user_socket.close();
						user_vc.remove(this);
						broadcast("UserOut/"+Nickname);
						broadcast("user_list_update/ ");
						
						//��� �濡�� �ش� ������ ������ ������ ã�´�.
						//find user who has lost connection with server in all rooms
						//�� �ش� �濡�� ���������ͳ��� �ش� ������ �����Ѵ�. 
						//delete the user at the room_user_vc in the corresponding room
						//�ش� ���� �ش� �� ���� ������ ũ�Ⱑ 0�̶��, 
						//if the size of the room_user_vc is 0,
						//������ �� ���Ϳ��� �� �ش� ���� �����Ѵ�. 
						//delete the room in the server's room_vc
						//��ε�ĳ��Ʈ�� ��� Ŭ���̾�Ʈ���� ������ �� ������ �ѱ��
						//inform all clients information of deleted room by the broadcast
						//�׷� Ŭ���̾�Ʈ���� �ش� ���� �� ����Ʈ���� �����Ѵ�. 
						//delete the room in room_list at the client
						//�׷� �������� �ٽ� ��ε�ĳ��Ʈ�� �� ����Ʈ ������Ʈ ��û. 
						//then ask room_list_update by broadcast in the server.
						
						outer:for(int i = 0 ; i < room_vc.size(); i ++) {
							roomInfo r = (roomInfo)room_vc.elementAt(i);
							
							for(int j = 0 ; j < r.room_user_vc.size(); j ++) {
								userInfo u = (userInfo)r.room_user_vc.elementAt(i);
								
								if(Nickname.equals(u.Nickname)) {
									
									System.out.println(r.room_user_vc.size());
									
									r.room_user_vc.remove(u);
									
									System.out.println(r.room_user_vc.size());
									
									if(r.room_user_vc.size() == 0) {
										
										
										broadcast("delete_room/"+r.room_name);
										broadcast("room_list_update/ ");
										room_vc.remove(r);
										break outer;
									}
									
								}
							}
						}
						
						}
						catch(IOException e1) {}
					
					
						break;
				}
				
			}
			
		}
		
		private void inmessage(String str) {
			st = new StringTokenizer(str, "/");
			
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			if(protocol.equals("Note")) {
				String receiver = message;
				String note = st.nextToken();
				
				System.out.println("receiver: "+receiver+" note: "+note);
				
				for(int i = 0; i < user_vc.size(); i ++) {
					userInfo u = (userInfo)user_vc.elementAt(i);
					
					if(receiver.equals(u.Nickname)) {
						u.send_message("Note/"+Nickname+"/"+note);
					}
				}
			}
			
			else if(protocol.equals("CreateRoom")) {
				
				//1. ���� ���� �ִ��� ���� Ȯ��
				for(int i = 0 ; i < room_vc.size(); i ++) {
					roomInfo ru = (roomInfo)room_vc.elementAt(i);
					
					if(ru.room_name.equals(message)) {
						send_message("CreateRoomFail/ ");
						
						room_ch = false;
						break;
					}
				}
				
				if(room_ch) {
					roomInfo new_room = new roomInfo(message, this);
					
					room_vc.add(new_room);
					
					send_message("CreateRoom/"+message);
					broadcast("NewRoom/"+message);
				}
				
				room_ch = true;
				
				broadcast("room_list_update/ ");
				
			}
			
			else if(protocol.equals("Chatting")) {
				
				String msg = st.nextToken();
				//�ش� �� ã��
				for(int i = 0 ; i < room_vc.size(); i ++) {
					roomInfo r = (roomInfo)room_vc.elementAt(i);
					
					if(message.equals(r.room_name)) {		//ã������
						//�� ������ ��ü ����ڵ鿡�� �޽��� ������
						
						r.broadcast_room("Chatting/"+Nickname+"/"+msg);
						
					}
				}
			}
			
			else if(protocol.equals("JoinRoom")) {
				//�ش� ���� ���������Ϳ� ���� �߰�.
				//�켱 �ش� �� ã��
				for(int i = 0 ; i < room_vc.size(); i++) {
					roomInfo r = (roomInfo)room_vc.elementAt(i);
					if(r.room_name.equals(message)) {
						//�� ã��. 
						
						r.broadcast_room("Chatting/Notification/***"+Nickname+" has been joined***\n");
						r.add_user(this);
						
						send_message("JoinRoom/"+message);
					}
				}
			}
			
			else if(protocol.equals("ExitRoom")) {
				
				for(int i = 0 ; i < room_vc.size(); i ++) {
					roomInfo r = (roomInfo)room_vc.elementAt(i);
					
					if(r.room_name.equals(message)) {
						r.room_user_vc.remove(this);
						r.broadcast_room("Chatting/Notification/:***"+Nickname+" has been left***\n");
						
						if(r.room_user_vc.size() == 0) {
							broadcast("delete_room/"+r.room_name);
							broadcast("room_list_update/ ");
							room_vc.remove(r);
							break;
						}
						break;
						
					}
					
				}
				
				
				send_message("ExitRoom/ ");
				
				
			}
			
		}
		
		
	}
	
	class roomInfo{
		
		private String room_name;
		private Vector room_user_vc = new Vector();
		
		roomInfo(String name, userInfo u){
			this.room_name = name;
			this.room_user_vc.add(u);
		}
		
		private void broadcast_room(String str) {
			for(int i = 0 ; i < room_user_vc.size(); i ++) {
				userInfo u = (userInfo)room_user_vc.elementAt(i);
				
				u.send_message(str);
			}
		}
		
		private void add_user(userInfo u) {
			room_user_vc.add(u);
		}
		
	
		
		
		
	}
	
}
