package data;
/**
 * Eccezione per gestire il caso di acquisizione errata del Training set (file inesistente, schema mancante,
 * training set vuoto o training set privo di variabile target numerica)
 * @author Donato Lerario
 *
 */
public class TrainingDataException extends Exception {
	public TrainingDataException() {
		super();
	}
	
	public TrainingDataException(String s) {
		super(s);
	}

}
