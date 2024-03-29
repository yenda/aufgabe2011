package master;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.JScrollPane;

public class MasterGUI {

	private JFrame frame;
	private JTextField textField_N;
	private JTextField txtLocalhost;
	private JTextField textField_WorkerPort;
	private JTextField textFieldWorkerToDelete;
	private JTextPane textPaneWorkerID;
	private JTextPane textPaneWorkerAddress;
	private JTextPane textPaneWorkerPort;
	private JTextArea textAreaDisplayResults;
	private JLabel lblNbOfWorkers;
	private JLabel lblDisplayExecutionTime;
	private JLabel lblDisplayNumberOfCycles;
	private JLabel lblDisplayCPUExecutionTime;
	private JButton btnFactoriseN;
	private JButton btnAddWorker;
	private JButton btnDeleteWorker;

	/**
	 * Create the application.
	 */
	public MasterGUI() {
		frame = new JFrame();
		frame.setTitle("Distributed Prime Number Factorisation Master");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 615, 400);
		frame.getContentPane().setLayout(null);
		
		JLabel lblN = new JLabel("N:");
		lblN.setBounds(10, 109, 46, 14);
		frame.getContentPane().add(lblN);
		
		textField_N = new JTextField();
		textField_N.setBounds(55, 106, 241, 20);
		frame.getContentPane().add(textField_N);
		textField_N.setColumns(10);
		
		JLabel lblWorkerAddress = new JLabel("Worker address :");
		lblWorkerAddress.setBounds(10, 50, 110, 14);
		frame.getContentPane().add(lblWorkerAddress);
		
		JLabel lblWorkerPort = new JLabel("Worker port :");
		lblWorkerPort.setBounds(10, 25, 110, 14);
		frame.getContentPane().add(lblWorkerPort);
		
		btnAddWorker = new JButton("Add Worker");
		btnAddWorker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String address = txtLocalhost.getText();
				try{
					int port = Integer.parseInt(textField_WorkerPort.getText());				
					Master.addWorker(address, port);
				}
				catch(NumberFormatException ex){
					JOptionPane.showMessageDialog(null, "Port is invalid", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnAddWorker.setBounds(130, 72, 166, 23);
		frame.getContentPane().add(btnAddWorker);
		
		txtLocalhost = new JTextField();
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(130, 47, 166, 20);
		frame.getContentPane().add(txtLocalhost);
		txtLocalhost.setColumns(10);
		
		textField_WorkerPort = new JTextField();
		textField_WorkerPort.setText("2552");
		textField_WorkerPort.setBounds(130, 22, 166, 20);
		frame.getContentPane().add(textField_WorkerPort);
		textField_WorkerPort.setColumns(10);
		
		btnFactoriseN = new JButton("Factorise N");
		btnFactoriseN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BigInteger N = new BigInteger(textField_N.getText());
				Master.calculateMessage(N);
			}
		});
		btnFactoriseN.setBounds(10, 134, 121, 23);
		frame.getContentPane().add(btnFactoriseN);
		
		JButton btnStopFactorisation = new JButton("Stop factorisation");
		btnStopFactorisation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Master.terminate();
			}
		});
		btnStopFactorisation.setBounds(141, 134, 155, 23);
		frame.getContentPane().add(btnStopFactorisation);
		
		JLabel lblResults = new JLabel("Results :");
		lblResults.setFont(lblResults.getFont().deriveFont(lblResults.getFont().getStyle() | Font.BOLD));
		lblResults.setBounds(10, 243, 110, 14);
		frame.getContentPane().add(lblResults);
		
		JLabel lblCPUExecutionTime = new JLabel("CPU time :");
		lblCPUExecutionTime.setBounds(10, 168, 110, 14);
		frame.getContentPane().add(lblCPUExecutionTime);
		
		JLabel lblExecutionTime = new JLabel("Execution time :");
		lblExecutionTime.setBounds(10, 193, 110, 14);
		frame.getContentPane().add(lblExecutionTime);
		
		JLabel lblNbOfCycles = new JLabel("Number of cycles :");
		lblNbOfCycles.setBounds(10, 218, 110, 14);
		frame.getContentPane().add(lblNbOfCycles);
		
		lblDisplayCPUExecutionTime = new JLabel("");
		lblDisplayCPUExecutionTime.setBounds(125, 168, 145, 14);
		frame.getContentPane().add(lblDisplayCPUExecutionTime);
		
		lblDisplayExecutionTime = new JLabel("");
		lblDisplayExecutionTime.setBounds(125, 193, 145, 14);
		frame.getContentPane().add(lblDisplayExecutionTime);
		
		lblDisplayNumberOfCycles = new JLabel("");
		lblDisplayNumberOfCycles.setBounds(125, 218, 145, 14);
		frame.getContentPane().add(lblDisplayNumberOfCycles);
		
		textPaneWorkerID = new JTextPane();
		textPaneWorkerID.setEditable(false);
		textPaneWorkerID.setBounds(310, 72, 43, 135);
		frame.getContentPane().add(textPaneWorkerID);
		
		JLabel lblWorkerList = new JLabel("Workers  :");
		lblWorkerList.setBounds(306, 22, 72, 14);
		frame.getContentPane().add(lblWorkerList);
		
		textFieldWorkerToDelete = new JTextField();
		textFieldWorkerToDelete.setText("1");
		textFieldWorkerToDelete.setBounds(390, 213, 46, 20);
		frame.getContentPane().add(textFieldWorkerToDelete);
		textFieldWorkerToDelete.setColumns(10);
		
		btnDeleteWorker = new JButton("Delete Worker");
		btnDeleteWorker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ID = Integer.parseInt(textFieldWorkerToDelete.getText());
				Master.removeWorker(ID-1);
			}
		});
		btnDeleteWorker.setBounds(446, 212, 128, 23);
		frame.getContentPane().add(btnDeleteWorker);
		
		JLabel lblWorkerID = new JLabel("Worker's ID  : ");
		lblWorkerID.setBounds(306, 216, 111, 14);
		frame.getContentPane().add(lblWorkerID);
		
		lblNbOfWorkers = new JLabel("0");
		lblNbOfWorkers.setBounds(388, 22, 46, 14);
		frame.getContentPane().add(lblNbOfWorkers);
		
		textPaneWorkerAddress = new JTextPane();
		textPaneWorkerAddress.setEditable(false);
		textPaneWorkerAddress.setBounds(363, 72, 155, 135);
		frame.getContentPane().add(textPaneWorkerAddress);
		
		textPaneWorkerPort = new JTextPane();
		textPaneWorkerPort.setEditable(false);
		textPaneWorkerPort.setBounds(528, 72, 61, 137);
		frame.getContentPane().add(textPaneWorkerPort);
		
		JLabel lblId = new JLabel("ID");
		lblId.setBounds(306, 50, 46, 14);
		frame.getContentPane().add(lblId);
		
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(363, 50, 46, 14);
		frame.getContentPane().add(lblAddress);
		
		JLabel lblHost = new JLabel("Host");
		lblHost.setBounds(528, 50, 46, 14);
		frame.getContentPane().add(lblHost);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 258, 579, 93);
		frame.getContentPane().add(scrollPane);
		
		textAreaDisplayResults = new JTextArea();
		textAreaDisplayResults.setLineWrap(true);
		textAreaDisplayResults.setEditable(false);
		scrollPane.setViewportView(textAreaDisplayResults);
		
		frame.setVisible(true);
	}
	
    /**
     * Refresh the Text Pane containing the list of workers
     * @param List<WorkerData> : the list of Workers with the data associated
     */
    public void refreshTextPaneWorkerList(ArrayList<WorkerData> workerList){
        int i = 1;
        String contentID = "";
        String contentAddress = "";
        String contentPort = "";
        lblNbOfWorkers.setText(String.valueOf(workerList.size()));
        
        for(WorkerData worker : workerList){
                contentID += i + "\n";
                i++;
                contentAddress += worker.getWorkerAddress() + "\n";
                contentPort += worker.getWorkerPort() + "\n";                           
        }
        textPaneWorkerID.setText(contentID);
        textPaneWorkerAddress.setText(contentAddress);
        textPaneWorkerPort.setText(contentPort);
    }
    
    public void displayResults(ArrayList<BigInteger> factorList, Long CPUTime, Long time, Integer cycles){
    	String contentResult = "";
    	String contentCPUTime = CPUTime.toString();
    	String contentTime = time.toString();
    	String contentCycles = cycles.toString();   	
    	for(BigInteger factor : factorList){
    		contentResult += factor.toString() + " * ";
    	}
    	if (contentResult.length()>3)
    		contentResult = contentResult.substring(0, contentResult.length()-3);
    	
    	lblDisplayCPUExecutionTime.setText(contentCPUTime);
    	lblDisplayExecutionTime.setText(contentTime);
    	lblDisplayNumberOfCycles.setText(contentCycles);
    	textAreaDisplayResults.setText(contentResult);
    }
    
    public void changeStateButton(boolean state){
    	btnAddWorker.setEnabled(state);
    	btnFactoriseN.setEnabled(state);
    	btnDeleteWorker.setEnabled(state);
    }
    
    public void cleanResults(){
    	lblDisplayCPUExecutionTime.setText("");
    	lblDisplayExecutionTime.setText("");
    	lblDisplayNumberOfCycles.setText("");
    	textAreaDisplayResults.setText("");
    }
}
