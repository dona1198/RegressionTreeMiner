package mapClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

/**
 * Classe Controller che definisce i metodi per far funzionare l'interfaccia grafica
 * @author Donato Lerario
 *
 */
public class Controller{
    @FXML
    /**
     * oggetto TextField per inserire il server IP
     */
    private TextField ip_field;
    
    @FXML
    /**
     * oggetto TextField per inserire la server Port
     */
    private TextField port_field;
    
    @FXML
    /**
     * oggetto TextField per inserire il nome della tabella
     */
    private TextField table_field;
    
    @FXML
    /**
     * oggetto Button da premere per iniziare la connessione con il server dopo aver inserito Server IP e Server Port
     */
    private Button connection_button;
    
    @FXML
    /**
     * oggetto Button da premere per iniziare la predizione dopo aver inizializzato la connessione e inserito il nome della tabella
     */
    private Button prediction_button;
    
    @FXML
    /**
     * oggetto RadioButton da premere se si vuole apprendere un albero di regressione da un training set e salvarlo su un file
     */
    private RadioButton button_data;
    
    @FXML
    /**
     * oggetto RadioButton da premere se si vuole caricare un albero di regressione precedentemente appreso dal file per predire nuovi esempi
     */
    private RadioButton button_archive;
    
    @FXML
    /**
     * oggetto TextArea per visualizzare l'output
     */
    private TextArea area_for_output;
    
    @FXML
    /**
     * oggetto Label che presenta la scritta "Choose the table"
     */
    private Label table_label;
    
    Socket socket = null;
	ObjectOutputStream out = null;
	ObjectInputStream in = null;
	boolean Connection = false;
	
	/**
	 * Metodo che parte all'avvio del Software, bloccando dei pulsanti che in quel momento non ha senso premere e inserendo dei valori di default nel campo IP e campo Port
	 */
	@FXML
	private void initialize() {
		ip_field.setText("localhost");
		port_field.setText("8080");
		prediction_button.setDisable(true);
		table_field.setDisable(true);
		button_data.setDisable(true);
		button_archive.setDisable(true);
		table_label.setVisible(false);
	}
	
	/**
	 * Metodo che inizializza la connessione con il server indicato dal campo IP e campo Port e riattiva gli oggetti precedentemente bloccati in modo da inserire i parametri necessari per
	 * iniziare la predizione
	 * @param actionEvent (pressione del bottone Connection)
	 */
	@FXML
	private void connection_action(ActionEvent actionEvent) {
    	InetAddress addr;
    	
    	if( Connection == true ) {
    		area_for_output.appendText( "Already connected\n" );
    		return;
    	}
    	
    	if( ip_field.getText().isEmpty() && port_field.getText().isEmpty() ) {
    		area_for_output.setText( "Please insert the IP and the Port to start the Connection" );
    	} else if( ip_field.getText().isEmpty() ) {
    		area_for_output.setText( "Please insert the IP to start the Connection" );
    	} else if( port_field.getText().isEmpty() ) {
    		area_for_output.setText( "Please insert the Port to start the Connection" );
    	} else {
    		try {
    			addr = InetAddress.getByName( ip_field.getText() );
    		} catch (UnknownHostException e) {
    			area_for_output.setText( "IP not valid" );
    			return;
    		}
    		try {
    			socket = new Socket(ip_field.getText(), Integer.parseInt( port_field.getText() ));
    			area_for_output.setText( socket.toString() + "\n" );		
    			out = new ObjectOutputStream(socket.getOutputStream());
    			in = new ObjectInputStream(socket.getInputStream());	; // stream con richieste del client
    			
    		}  catch (IOException e) {
    			area_for_output.setText( e.getMessage() );
    			return;
    		}
    		
    		Connection = true;
    		prediction_button.setDisable(false);
    		table_field.setDisable(false);
    		button_data.setDisable(false);
    		button_archive.setDisable(false);
    		table_label.setVisible(true);
    		button_data.setSelected(true);
    	}
    }
    
	/**
	 * Metodo che fa partire la predizione dall'albero di regressione in base al nome della tabella inserito ed alla scelta se predirre da trainingSet o caricare un albero di regressione
	 * prededentemente appreso da file
	 * @param actionEvent (pressione del bottone Start Prediction)
	 */
	@FXML
	private void prediction_action(ActionEvent actionEvent) {
    	String answer="";
    	
    	String tableName="";
    	
    	if( table_field.getText().isEmpty() ) {
    		area_for_output.appendText( "Insert the table name before starting the prediction!\n" );
    		return;
    	}
		tableName = table_field.getText();
		try{
		
		if( button_data.isSelected() )
		{
			area_for_output.appendText( "Starting data acquisition phase!\n" );
			
			
			
			out.writeObject(0);
			out.writeObject(tableName);
			answer=in.readObject().toString();
			if(!answer.equals("OK")){
				area_for_output.appendText( answer + "\n" );
				return;
			}
			
			area_for_output.appendText( "Starting learning phase!" );
			out.writeObject(1);
			
		
		}
		else
		{
			area_for_output.appendText( "Starting loading phase!" );
			
			out.writeObject(2);
			out.writeObject(tableName);
			
		}
		
		answer=in.readObject().toString();
		if(!answer.equals("OK")){
			area_for_output.appendText( "\n" + answer );
			return;
		}

		
		char scelta = 'y';
		int path;
		Alert alert = new Alert(AlertType.NONE);
		ButtonType cancel_button = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType Yes = new ButtonType("Yes");
		ButtonType No = new ButtonType("No");
		
		do{
			out.writeObject(3);
			
			area_for_output.appendText( "\nStarting prediction phase!\n" );
			answer=in.readObject().toString();
		
			alert.setTitle("Choose the Path");
			while(answer.equals("QUERY")){
				answer=in.readObject().toString();
				area_for_output.appendText( answer + "\n" );
				String[] options = answer.split("\n");
				ArrayList<ButtonType> options_buttons = new ArrayList<>();
				for (int i = 0; i < options.length; i++) {
					options_buttons.add(new ButtonType(options[i]));
				}
				alert.getButtonTypes().clear();
				alert.getButtonTypes().addAll(options_buttons);
				alert.getButtonTypes().add(cancel_button);
				alert.setContentText("");
				Optional<ButtonType> result = alert.showAndWait();
				ButtonType choice = result.get();
				path = options_buttons.indexOf(choice);
				out.writeObject(path);
				answer=in.readObject().toString();
			}
		
			if(answer.equals("OK"))
			{
				answer=in.readObject().toString();
				area_for_output.appendText( "Predicted class: "+answer + "\n" );
				
			}
			else
			{
				area_for_output.appendText( answer + "\n");
			}
			
			alert.setTitle("Would you repeat ?");
			alert.setContentText("");
			alert.getButtonTypes().clear();

			alert.getButtonTypes().addAll(Yes, No);
			Optional<ButtonType> result = alert.showAndWait();
			ButtonType choice = result.get();
			if (choice.equals(Yes))	scelta = 'Y';
			else	scelta = 'N';
				
		} while (Character.toUpperCase(scelta)=='Y');

		} catch( IOException | ClassNotFoundException e) {
			area_for_output.appendText(e.getMessage());
		} finally {
			close_connection();
			Connection = false;
			initialize();
		}
    }
    
	/**
	 * Metodo che svuota tutti i campi presenti nel Software
	 */
	@FXML
	private void delete_fields() {
        table_field.clear();
        button_data.setSelected(false);
        button_archive.setSelected(false);
        area_for_output.clear();
	}
	
	/**
	 * Metodo che mostra una schermata con il nome dell'autore
	 */
	@FXML
	private void show_author() {
		Alert alert2 = new Alert(AlertType.NONE);
		ButtonType cancel_button = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert2.setTitle("Author Information");
		alert2.setContentText( "Project developed by Donato Lerario, student of Computer Science in University of Bari");
		alert2.getButtonTypes().add(cancel_button);
		Optional<ButtonType> result = alert2.showAndWait();
	}
	
	/**
	 * Metodo che chiude la connessione con il Server
	 */
	@FXML
	private void close_connection() {
		try {
			out.writeObject(0);
			out.close();
			in.close();
			socket.close();
		} catch (IOException | NullPointerException e) {
			
		}
	}
}
