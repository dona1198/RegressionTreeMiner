package server;
/**
 * Eccezione per gestire il caso di acquisizione di valore mancante o fuori range di un attributo di un 
 * nuovo esempio da classificare.
 * @author Donato Lerario
 *
 */
public class UnknownValueException extends Exception {
	public UnknownValueException(String s) {
        super(s);
    }
}
