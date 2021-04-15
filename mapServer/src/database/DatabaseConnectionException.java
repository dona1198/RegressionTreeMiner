package database;
/**
 * Eccezione per modellare il fallimento nella connessione al database.
 * @author Donato Lerario
 *
 */
public class DatabaseConnectionException extends Exception{
    public DatabaseConnectionException() {
        super();
    }

    public DatabaseConnectionException(String msg) {
        super(msg);
    }
}