package data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import database.Column;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.Example;
import database.TableData;
import database.TableSchema;

/**
 * Classe che modella l'insieme di esempi di training 
 * @author Donato Lerario
 *
 */
public class Data {
	/**
	 * ArrayList di tipo Example che contiene il training set.
	 */
	private List<Example> data=new ArrayList<Example>();
	
	/**
	 * Cardinalità del training set 
	 */
	private int numberOfExamples;
	
	/**
	 * LinkedList di oggetti di tipo Attribute per rappresentare gli attributi indipendenti.
	 */
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	
	/**
	 * Oggetto per modellare l'attributo di classe (target attribute). L'attributo di classe è un attributo numerico.
	 */
	private ContinuousAttribute classAttribute;
	
	/**
	 * E' il costruttore di classe. Si occupa di caricare i dati (schema e esempi) di 
	 * addestramento da una tabella della base di dati. Il nome della tabella è un parametro 
	 * del costruttore. Il costruttore solleva una eccezione di tipo TrainingDataException 
	 * se la connessione al database fallisce, la tabella non esiste, la tabella ha meno di 
	 * due colonne, la tabella ha zero tuble, l’attributo corrispondente all’ultima colonna 
	 * della tabella non è numerico.
	 * @param table Nome della tabella della base di dati
	 * @throws TrainingDataException
	 */
	public Data(String table)throws TrainingDataException, SQLException{
		
		Iterator<Example> transSet = null;
		DbAccess database = null;
		try {
			database = new DbAccess();
		} catch(DatabaseConnectionException e) {
			throw new TrainingDataException(e.getMessage() + ": Impossibile connettersi al database");
		}
		TableSchema TSchema = null;
		try {
			TSchema = new TableSchema( database, table );
		} catch (SQLException e) {
			throw new TrainingDataException(e.getMessage() + ": Impossibile trovare tabella " + table);
		}

		TableData TData = new TableData( database );
		
		try {
			transSet = TData.getTransazioni(table).iterator();
		} catch (SQLException | EmptySetException e) {
			throw new TrainingDataException("Errore nell'acquisizione delle Transazioni nella Tabella");
		}
		
		int i = 0;
		while (transSet.hasNext()){
			data.add(transSet.next());
			i++;
		}
		numberOfExamples = i;
		Iterator<Column> Colonne = TSchema.iterator();
		Column Colonna = null;
		i = 0;
		
		while(Colonne.hasNext()) {
			Colonna = Colonne.next();
			if( Colonna.isNumber() ) {
				explanatorySet.add(i, new ContinuousAttribute(Colonna.getColumnName() ,i));
				i++;
			} else {
				Set<String> discreteValues = null;
				try {
					discreteValues = TData.getDistinctColumnValues(table, Colonna).stream().map(s -> s.toString()).collect(Collectors.toSet());
				} catch (SQLException | EmptySetException e) {
					throw new TrainingDataException(e.getMessage() + ": Errore nell'acquisizione dei Valori Discreti");
				}
				explanatorySet.add(i, new DiscreteAttribute(Colonna.getColumnName(), i, discreteValues));
				i++;
			}
		}

		if( i < 2 )	throw new TrainingDataException("Colonne non sufficienti");
		
		explanatorySet.remove(explanatorySet.size() - 1);
		classAttribute = new ContinuousAttribute(Colonna.getColumnName(), explanatorySet.size());
		
		try {
			database.closeConnection();
		} catch (SQLException e) {
			throw new TrainingDataException(e.getMessage() + ": Errore nella chiusura del database");
		}
	}
	
	/**
	 * Restituisce il valore del membro numberOfExamples 
	 * @return Cardinalità dell'insieme di esempi 
	 */
	public int getNumberOfExamples() {
		return numberOfExamples;
	}
	
	/**
	 * Restituisce la lunghezza della LinkedList explanatorySet 
	 * @return Cardinalità dell'insieme degli attributi indipendenti
	 */
	public int getNumberOfExplanatoryAttributes()  {
		return explanatorySet.size();
	}
	
	/**
	 * Restituisce il valore dell'attributo di classe per l'esempio exampleIndex
	 * @param exampleIndex indice per l'ArrayList data per uno specifico esempio
	 * @return valore dell'attributo di classe per l'esempio indicizzato in input
	 */
	public Double getClassValue(int exampleIndex) {
		return (Double) data.get(exampleIndex).get(explanatorySet.size());
	}
	
	/**
	 * Restituisce il valore dell' attributo indicizzato da attributeIndex per l'esempio exampleIndex
	 * @param exampleIndex indice per l'ArrayList data per uno specifico esempio
	 * @param attributeIndex
	 * @return Object associato all'attributo indipendente per l'esempio indicizzato in input
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
		return data.get(exampleIndex).get(attributeIndex);
	}
	
	/**
	 * Restituisce l'attributo indicizzato da index in explanatorySet
	 * @param index indice della LinkedList explanatorySet per uno specifico attributo indipendente
	 * @return oggetto Attribute indicizzato da index 
	 */
	public Attribute getExplanatoryAttribute(int index) {
		return explanatorySet.get(index);
	}
	
	/**
	 * restituisce l'oggetto corrispondente all'attributo di classe
	 * @return Oggetto ContinuousAttribute associato al membro classAttribute;
	 */
	public ContinuousAttribute getClassAttribute() {
		return classAttribute;
	}
	
	public List<Attribute> getExplanatoryAttributes() {
		return explanatorySet;
	}

	/**
	 * Ordina il sottoinsieme di esempi compresi nell'intervallo [inf,sup] in data rispetto
	allo specifico attributo attribute. Usa l'Algoritmo quicksort per l'ordinamento di un array di interi usando
	come relazione d'ordine totale maggiore/uguale. L'array, in questo caso, è dato dai valori assunti dall'attributo passato in
	input. Vengono richiamati i metodi: private void quicksort(Attribute attribute, int inf,
	int sup); private int partition(DiscreteAttribute attribute, int inf, int sup)
 	e private void swap(int i,int j) 
	 * @param attribute Attributo i cui valori devono essere ordinati
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	// scambio esempio i con esempi oj
		private void swap(int i,int j){
			Object temp1 = data.get(i);
			Object temp2 = data.get(j);
			data.remove(i);
			data.add(i, (Example) temp2);
			data.remove(j);
			data.add(j, (Example) temp1);
		}
	

	
	
	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private  int partition(DiscreteAttribute attribute, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		String x=(String)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){ 
				i++; 
				
			}
		
			while(((String)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;
			
			}
			
			if(i<j) { 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}
	
	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private  int partition(ContinuousAttribute attribute, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		Double x=(Double)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((Double)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){ 
				i++; 
				
			}
		
			while(((Double)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;
			
			}
			
			if(i<j) { 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}
	
	/*
	 * Algoritmo quicksort per l'ordinamento di un array di interi A
	 * usando come relazione d'ordine totale "<="
	 * @param A
	 */
	private void quicksort(Attribute attribute, int inf, int sup){
		
		if(sup>=inf){
			
			int pos;
			if(attribute instanceof DiscreteAttribute)
				pos=partition((DiscreteAttribute)attribute, inf, sup);
			else
				pos=partition((ContinuousAttribute)attribute, inf, sup);
					
			if ((pos-inf) < (sup-pos+1)) {
				quicksort(attribute, inf, pos-1); 
				quicksort(attribute, pos+1,sup);
			}
			else
			{
				quicksort(attribute, pos+1, sup); 
				quicksort(attribute, inf, pos-1);
			}
			
			
		}
		
	}
	
	/**
	 * legge i valori di tutti gli attributi per ogni esempio da data e li concatena in un
	 * oggetto String che restituisce come risultato finale in forma di sequenze di testi.
	 */
	@Override
	public String toString(){
		String value="";
		for(int i=0;i<numberOfExamples;i++){
			for(int j=0;j<explanatorySet.size();j++)
				value+= data.get(i).get(j) +",";
			
			value+= getClassValue(i) +"\n";
		}
		return value;
	}
}
