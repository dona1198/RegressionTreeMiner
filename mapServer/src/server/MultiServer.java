package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe per la gestione del MultiServer attivo sulla macchina che permette la creazione di più ServerOneClient per gestire diversi client
 * contemporaneamente.
 * @author Donato Lerario
 *
 */
public class MultiServer {
	private int PORT = 8080;
	
	/**
	 * Costruttore di classe. Inizializza la porta ed invoca run().
	 * @param PORT
	 * @throws IOException 
	 */
	public MultiServer(int PORT) throws IOException {
		this.PORT = PORT;
		run();
	}
	
	/**
	 * Istanzia un oggetto istanza della classe ServerSocket che pone in attesa di richiesta di
		connessioni da parte del client. Ad ogni nuova richiesta connessione si istanzia ServerOneClient.
	 * @throws IOException 
	 */
	private void run() throws IOException {
		ServerSocket s = new ServerSocket();
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", PORT);
        s.bind(serverAddress);
        System.out.println(s);	
		System.out.println("Server Partito");
		try {
			while(true) {
				Socket socket = s.accept();
				try {
					new ServerOneClient(socket);
				} catch(IOException e) {
					socket.close();
				}
			}
		} finally {
			s.close();
		}
	}
	
	@Override
	public String toString() {
    	return "" + PORT;
	}
}
