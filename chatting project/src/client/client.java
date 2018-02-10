package client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class client extends JFrame implements ActionListener{

	
	//login gui source variables
	private JPanel login_pane;
	private JTextField ip_tf;
	private JTextField port_tf;
	private JTextField id_tf;
	private JFrame login_gui = new JFrame();
	private JButton login_btn = new JButton("Login");
	
	
	//main gui source variables
	private JPanel contentPane;
	private JTextField textField;
	private JList User_list = new JList();
	private JButton sendNote_btn = new JButton("Send message");
	private JList Room_list = new JList();
	private JButton enjoy_btn = new JButton("enjoy chatting");
	private JButton make_btn = new JButton("make room");
	private JTextArea chat_area = new JTextArea();
	private JButton send_btn = new JButton("Send");
	
	//stuff
	private String ip;
	private int port;
	private String id;
	private Socket socket;
	
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	
	client(){
		
		login_init();
		main_init();
		start();
		
	}
	
	
	private void main_init() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 696, 578);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Total access point");
		lblNewLabel.setBounds(34, 42, 119, 15);
		contentPane.add(lblNewLabel);
		
		
		User_list.setBounds(34, 82, 119, 145);
		contentPane.add(User_list);
		
		
		sendNote_btn.setBounds(34, 256, 119, 23);
		contentPane.add(sendNote_btn);
		
		JLabel lblNewLabel_1 = new JLabel("chatting room");
		lblNewLabel_1.setBounds(34, 308, 119, 15);
		contentPane.add(lblNewLabel_1);
		
		
		Room_list.setBounds(34, 333, 119, 126);
		contentPane.add(Room_list);
		
		enjoy_btn.setBounds(34, 469, 119, 23);
		contentPane.add(enjoy_btn);
		
		
		make_btn.setBounds(34, 502, 119, 23);
		contentPane.add(make_btn);
		
		
		chat_area.setBounds(184, 38, 484, 446);
		contentPane.add(chat_area);
		
		textField = new JTextField();
		textField.setBounds(184, 503, 378, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		
		send_btn.setBounds(577, 502, 91, 23);
		contentPane.add(send_btn);
		
		this.setVisible(true);
		
	}
	
	
	private void start() {
		login_btn.addActionListener(this);
		sendNote_btn.addActionListener(this);
		enjoy_btn.addActionListener(this);
		make_btn.addActionListener(this);
		send_btn.addActionListener(this);
		
	}
	
	
	private void login_init() {
		
		login_gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_gui.setBounds(100, 100, 365, 463);
		login_pane = new JPanel();
		login_pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		login_gui.setContentPane(login_pane);
		login_pane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(29, 185, 76, 29);
		login_pane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Port");
		lblNewLabel_1.setBounds(29, 249, 76, 15);
		login_pane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("ID");
		lblNewLabel_2.setBounds(29, 305, 50, 15);
		login_pane.add(lblNewLabel_2);
		
		ip_tf = new JTextField();
		ip_tf.setBounds(133, 189, 169, 21);
		login_pane.add(ip_tf);
		ip_tf.setColumns(10);
		
		port_tf = new JTextField();
		port_tf.setBounds(133, 246, 169, 21);
		login_pane.add(port_tf);
		port_tf.setColumns(10);
		
		id_tf = new JTextField();
		id_tf.setBounds(133, 302, 169, 21);
		login_pane.add(id_tf);
		id_tf.setColumns(10);
		
		
		login_btn.setBounds(37, 371, 254, 23);
		login_pane.add(login_btn);
		
		
		this.login_gui.setVisible(true);
		
		
	}
	
	
	public static void main(String[] args) {
		
		new client();

	}

	
	private void network() {
		try {
			socket = new Socket(ip, port);
			
			if(socket != null) {
				connection();
			}
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private void connection() {
		
		try {
		is = socket.getInputStream();
		os = socket.getOutputStream();
		
		dis = new DataInputStream(is);
		dos = new DataOutputStream(os);
		}
		catch(IOException e) {
			
		}
		
		send_message(id);
		
		
		
	}
	
	private void send_message(String str) {
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == login_btn) {
			
			
			
			if(ip_tf.getText().length() == 0) {
				ip_tf.setText("please input ip");
				ip_tf.requestFocus();
			}
			else if(port_tf.getText().length() == 0) {
				port_tf.setText("please input port");
				port_tf.requestFocus();
			}
			else if(id_tf.getText().length() == 0) {
				id_tf.setText("please input id");
				id_tf.requestFocus();
			}
			else {
				
				ip = ip_tf.getText().trim();
				port = Integer.parseInt(port_tf.getText().trim());
				id = id_tf.getText().trim();
				
				network();
				
				
			}
			
			
			
			
			System.out.println("login btn clicked");
		}
		
		else if(e.getSource() == sendNote_btn) {
			System.out.println("sendNote btn clicked");
		}
		
		else if(e.getSource() == enjoy_btn) {
			System.out.println("enjoy_btn clicked");
		}
		
		else if(e.getSource() == make_btn) {
			System.out.println("make_btn clicked");
		}
		else if(e.getSource() == send_btn) {
			System.out.println("send_btn clicked");
		}
		
	}

}
