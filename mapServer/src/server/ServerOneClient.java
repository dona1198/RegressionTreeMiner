package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;

/**
 * Classe che crea un thread per poter gestire la richiesta di un certo client.
 * @author Donato Lerario
 *
 */
public class ServerOneClient extends Thread{
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	/**
	 * Costruttore di classe. Inizializza gli attributi socket, in e out. Avvia il thread.
	 * @param s Socket
	 * @throws IOException
	 */
	public ServerOneClient(Socket s) throws IOException{
		Thread td;
		socket = s;
		in = new ObjectInputStream( socket.getInputStream() );

		out = new ObjectOutputStream( socket.getOutputStream() );

		td = new Thread(this);
		td.start(); // Chiama run()
	}
	
	/**
	 * Riscrive il metodo run della superclasse Thread al fine di gestire le richieste del client e rispondere
	 */
	public void run() {
		System.out.println("Connessione accettata!");
		
		int decision;
		String table;
		RegressionTree tree = null;
		Data trainingSet = null;
		
		try {
			decision = (int) in.readObject();
			table = in.readObject().toString();
			if ( decision == 0 ) {
				
				trainingSet= new Data( table );
				out.writeObject("OK");
				
				decision = (int) in.readObject();
				if ( decision == 1 ) {
					tree = new RegressionTree(trainingSet);
					out.writeObject("OK");
				}
				tree.salva(table + ".dmp");
			} else {
				if ( decision == 2 ) {
					tree = RegressionTree.carica( table +".dmp");
					out.writeObject("OK");
				}
				
			}

			do {
				decision = (int) in.readObject();
				try {
					tree.predictClass(in, out);
				} catch (UnknownValueException e) {
					e.printStackTrace();
					out.writeObject(e.getMessage());
				}
            } while ( decision == 3 );
		} catch(SocketException | EOFException e1) {
			System.err.println("Connessione chiusa dal Client");
		} catch(IOException | ClassNotFoundException | TrainingDataException | SQLException e2) {
			try {
				e2.printStackTrace();
				out.writeObject(e2.getMessage() + "\nInterrotto");
			} catch (IOException e3) {
				System.err.println(e3.toString());
				System.err.println("Connessione chiusa dal Client");
			}
		}
	}
	
	@Override
	public String toString() {
		return socket + " " + in + " " + out;
	}
}
