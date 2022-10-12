package chatbot.project.com;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Server extends JFrame{
	
	ServerSocket server;
	
	Socket socket;    
	
	BufferedReader br;  //for input stream   -- streams are unidirectional in java (send data in one direction only)
	PrintWriter out;    //for output stream
	
	//Declare components
	private JLabel heading = new JLabel("Server Area");
	private JTextArea messageArea = new JTextArea();
	private JTextField messageInput = new JTextField();
	private Font font = new Font("Roboto", Font.PLAIN, 20);  //font family, font type, font size
	 
	//create constructor for server
	public Server() {
		
		//server port 
		try {
		server = new ServerSocket(7777);  // server ban gya
		
		//accept krnge request ko
		System.out.println("server is ready to accept connection");
		System.out.println("waiting....");  //wait krega jbtk req ni aygu
		
		socket = server.accept();   //cleint ka connection accept karega
		
		//br se data read hojye
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//to write data
		out = new PrintWriter(socket.getOutputStream());
		
		creatGUI();
		handleEvents();
		startReading();
		//startWriting();
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	 //method to create GUI of application
	  public void creatGUI() {
			
			//this -- current window ha hmre
		  this.setTitle("Server Messager[END]");
		  this.setSize(600,700);
		  this.setLocationRelativeTo(null);    //window center pe kardega
		  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //jab cross button pe click krnge program close hojyg
		  this.setVisible(true);  //window visible hojygi
		  
		  //coding for component
		
		  heading.setFont(font);
		  messageArea.setFont(font);
	      messageInput.setFont(font);
	      //heading.setIcon(new ImageIcon("chat.png"));
	    
	      
	      heading.setHorizontalAlignment(SwingConstants.CENTER);
	      heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	      
	      messageArea.setEditable(false);
	      //set layout for frame
	      this.setLayout(new BorderLayout());
	      
	      //adding components to frame
	      this.add(heading, BorderLayout.NORTH);
	      JScrollPane jScrollPane = new JScrollPane(messageArea);
	      this.add(jScrollPane, BorderLayout.CENTER);
	      this.add(messageInput, BorderLayout.SOUTH);
			
		}
	  
	  
	  //method to handle events
	   public void handleEvents() {
		  
		   //interface  KeyListener
		   messageInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
						
			}

			@Override
			public void keyPressed(KeyEvent e) {
						
			}

			@Override
			public void keyReleased(KeyEvent e) {
			
				//System.out.println("key released "+e.getKeyCode());
				if(e.getKeyCode() == 10) {
					//System.out.println("You have pressed enter button");
					String contentToSend = messageInput.getText();
					messageArea.append("Me: " + contentToSend + "\n");
					out.println(contentToSend);
					out.flush();
					messageInput.setText("");
					messageInput.requestFocus();
				}
				
			}
			   
		   });
		   
		   
	   }
	   
	
	//we need to read and write data simultaneously (sath me)
	// we need to do two task together --> we use Multithreading concept here
	public void startReading() {
		
		//this thread --> data to read krke deta rahega
		//using thread by lambda expression
		
		Runnable r1 = ()->{
			
			System.out.println("Reader started...");
			
			//because hmko baar baar client msg read krna ha
			//jabhi while ke andr exception aygi loop break hojyga and thread band hojyga
	    try {
			while(true) {
				
				//read a single line
				String msg = br.readLine();
				
				if(msg.equals("exit")) {
					System.out.println("Client terminated the chat");
					JOptionPane.showMessageDialog(this, "Client Terminated the chat ");
		            messageInput.setEnabled(false);
					
					//connection close
					socket.close();
					break;
				}
				
				messageArea.append("Client: "+ msg + "\n");
				
			}
			}
		catch(Exception e)
		{
			System.out.println("Connection is closed");
		}
			
		};
		
		//start(call) thread and reference of runnable
		new Thread(r1).start();
		
	}
	
	public void startWriting() {
		
		//this thread ---> data user se lega than usko send karega cleint tak
		//writing code
		Runnable r2 = ()->{
			System.out.println("Writer started...");
			
			//because hmko baar baar  msg write krna ha in server
		try {
			while(!socket.isClosed()) {
					
					//to take input from user in console wth of br1
					BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
					String content = br1.readLine();
					
				
					out.println(content);  //send krdia client pe
                    out.flush();
                    
                	if(content.equals("exit")) {
						socket.close();
						break;
					}

			}
			
			}
		catch(Exception e)
		{
			System.out.println("Connection is closed");
		}
			
		};
		
		//start(call) thread
		new Thread(r2).start();
		
		
	}
	

	

	public static void main(String[] args) {
		System.out.println("Server is running....");
		//server object banjyga to exceute constructor
		new Server();
	}

}
