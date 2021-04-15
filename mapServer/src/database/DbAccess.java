package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che realizza l'accesso alla base di dati
 * @author Donato lerario
 *
 */
public class DbAccess {
	/**
	 * Per utilizzare questo Driver scaricare e aggiungere al classpath il connettore mysql connector
	 */
	private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	
    private static final String DBMS = "jdbc:mysql";
    /**
     * contiene l’identificativo del server su cui risiede la base di dati (per esempio localhost)
     */
    private static final String SERVER = "localhost";
    /**
     * contiene il nome della base di dati
     */
    private static final String DATABASE = "MapDB";
    /**
     * La porta su cui il DBMS MySQL accetta le connessioni
     */
    private static final int PORT = 3306;
    /**
     * contiene il nome dell’utente per l’accesso alla base di dati
     */
    private static final String USER_ID = "MapUser";
    /**
     * contiene la password di autenticazione per l’utente identificato da USER_ID
     */
    private static final String PASSWORD = "map";
    /**
     * gestisce una connessione
     */
    private Connection conn;
	
    /**
     * Costruttore di classe.
     * @throws DatabaseConnectionException 
     */
    public DbAccess() throws DatabaseConnectionException {
    	initConnection();
    }
    
    /**
     * Metodo che impartisce al class loader l’ordine di caricare il driver mysql, inizializza la connessione riferita da conn. 
     * Il metodo solleva e propaga una eccezione di tipo DatabaseConnectionException in caso di fallimento 
     * nella connessione al database.
     * @throws DatabaseConnectionException
     */
    public void initConnection() throws DatabaseConnectionException{
    	try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch(ClassNotFoundException e) {
			System.out.println("[!] Driver not found: " + e.getMessage());
			throw new DatabaseConnectionException();
		} catch(InstantiationException e){
			System.out.println("[!] Error during the instantiation : " + e.getMessage());
			throw new DatabaseConnectionException();
		} catch(IllegalAccessException e){
			System.out.println("[!] Cannot access the driver : " + e.getMessage());
			throw new DatabaseConnectionException();
		}
		String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
					+ "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
			
		System.out.println("Connection's String: " + connectionString);
			
			
		try {			
			conn = DriverManager.getConnection(connectionString);
		} catch(SQLException e) {
			System.out.println("[!] SQLException: " + e.getMessage());
			System.out.println("[!] SQLState: " + e.getSQLState());
			System.out.println("[!] VendorError: " + e.getErrorCode());
			throw new DatabaseConnectionException();
		}
    }
    
    /**
     * Metodo che restituisce conn
     * @return conn
     */
    public Connection getConnection() {
    	return this.conn;
    }
    
    /**
     * Metodo che chiude la connessione conn
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
    	conn.close();
    }
    
    @Override
	public String toString() {
    	return DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE;
	}
}
