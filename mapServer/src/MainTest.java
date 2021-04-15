import java.io.IOException;

import server.MultiServer;

/**
 * Classe Main che permette di generare un albero di decisione da un dataset di dati strutturati,
 * composti sia da attributi discreti che continui
 * @author Donato Lerario
 *
 */
public class MainTest {
	
	public static void main(String[] args) throws IOException{
		new MultiServer(8080);
	}
}
