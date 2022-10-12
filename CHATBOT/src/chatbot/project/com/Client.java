package chatbot.project.com;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

//client ko property dedi apne window ki (jabhi cleint ka obj bnynge ek window ready hogi)
public class Client extends JFrame{
	 
	Socket socket;
	
	BufferedReader br;
	PrintWriter out;
	
	//Declare components
	private JLabel heading = new JLabel("Client Area");
	private JTextArea messageArea = new JTextArea();
	private JTextField messageInput = new JTextField();
	private Font font = new Font("Roboto", Font.PLAIN, 20);  //font family, font type, font size
	
	
	//constructor
	public Client() {
			
		try {
			
			System.out.println("Sending request to server...");
			socket = new Socket("192.168.1.34", 7777);  //IP address and second argument is port of server
			System.out.println("Connection done. ");

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
	  this.setTitle("Client Messager[END]");
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
	
 //start reading method
  public void startReading() {
		
		//this thread --> data to read krke deta rahega
		//using thread by lambda expression
		
		Runnable r1 = ()->{
			
			System.out.println("Reader started...");
			
			//because hmko baar baar server msg read krna ha
		try {
			while(true) {
				
				//read a single line
				String msg = br.readLine();
				
				if(msg.equals("exit")) {
					System.out.println("Server terminated the chat");
					JOptionPane.showMessageDialog(this, "Server Terminated the chat ");
		            messageInput.setEnabled(false);
					
					socket.close();
					break;
				}
				
				//System.out.println("Server:" + msg);
				messageArea.append("Server: "+ msg + "\n");			
			}
			}
		catch(Exception e)
		{
			System.out.println("Connection closed");
		}
			
		};
		
		//start(call) thread and reference of runnable
		new Thread(r1).start();
      }


   //start writing method
	public void startWriting() {
		
		//this thread ---> data user se lega than usko send karega server tak
		//writing code
		Runnable r2 = ()->{
			System.out.println("Writer started...");
			
			//because hmko baar baar  msg write krna ha in server
		try {
			while(socket.isClosed()) {
			
					//to take input from user in console wth of br1
					BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
					String content = br1.readLine();
					
					out.println(content);  //send krdia server pe
	                out.flush();		
	                
	            	if(content.equals("exit")) {
						socket.close();
						break;
					}	
				}			
		   }
		catch(Exception e) {
			System.out.println("Connection is closed");
		}
		
		};
		
		//start(call) thread
		new Thread(r2).start();
		
	}
  



	public static void main(String[] args) {
	
		System.out.println("this is client ");
		
		new Client();

	}

}
