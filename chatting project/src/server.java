import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class server extends JFrame implements ActionListener{
	
	
	private JPanel contentPane;
	private JTextField port_tf;
	private JTextArea textArea = new JTextArea();
	private JButton start_btn = new JButton("implement server");
	private JButton stop_btn = new JButton("stop server");
	
	//source for network
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	private String Nickname;
	
	
	server(){
		
		unit();
		start();
	}
	
	private void start() {
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
	}
	
	
	private void unit() {
		
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
		
		
		this.setVisible(true);
		
	}

	public static void main(String[] args) {
		
		
		new server();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == start_btn) {
			System.out.println("start btn clicked");
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			server_start();
			
		}
		else if(e.getSource() == stop_btn) {
			System.out.println("stop btn clicked");
		}
		
	}
	
	private void server_start() {
		
		
		try {
			server_socket = new ServerSocket(port);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		if(server_socket != null) {
			connection();
		}
		
	}
	
	private void connection() {
		
		
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					
					try {
						textArea.append("waiting for client");
						socket = server_socket.accept();//클라이언트 접속 무한대기
						
						is = socket.getInputStream();
						os = socket.getOutputStream();
						dis = new DataInputStream(is);
						dos = new DataOutputStream(os);
						
						textArea.append("found client");
						
						Nickname = dis.readUTF();
						
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					
					
					
					
				}
				
			}
		});
		
		th.start();
		
		
		
	}
	
	

}
