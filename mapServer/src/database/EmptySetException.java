package database;
/**
 * Eccezione per modellare la restituzione di un resultset vuoto.
 * @author Donato Lerario
 *
 */
public class EmptySetException extends Exception{
    public EmptySetException() {
        super();
    }

    public EmptySetException(String msg) {
        super(msg);
    }
}