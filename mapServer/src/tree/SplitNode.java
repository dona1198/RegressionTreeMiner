package tree;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.Data;

/**
 * Classe astratta per modellare l'astrazione dell'entità nodo di split (continuo o discreto) estendendo la superclasse Node
 * @author Donato Lerario
 *
 */
abstract class SplitNode extends Node implements Comparable<SplitNode>{
	/**
	 * Classe che aggrega tutte le informazioni riguardanti un nodo di split
	 * @author Donato Lerario
	 *
	 */
	class SplitInfo implements Serializable{
		/**
		 * valore di tipo Object (di un attributo indipendente) che definisce uno split
		 */
		private Object splitValue;
		/**
		 * indice nell'array del training set del primo esempio coperto dal nodo corrente
		 */
		private int beginIndex;
		/**
		 * indice nell'array del training set dell'ultimo esempio coperto dal nodo corrente. 
		 */
		private int endIndex;
		/**
		 * numero di split (nodi figli) originanti dal nodo corrente
		 */
		private int numberChild;
		/**
		 * operatore matematico che definisce il test nel nodo corrente (“=” per valori discreti)
		 */
		private String comparator="=";
		
		/**
		 * Costruttore che avvalora gli attributi di classe per split a valori discreti
		 * @param splitValue
		 * @param beginIndex
		 * @param endIndex
		 * @param numberChild
		 */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}
		
		/**
		 * Costruttore che avvalora gli attributi di classe per generici split (da usare per valori continui)
		 * @param splitValue
		 * @param beginIndex
		 * @param endIndex
		 * @param numberChild
		 * @param comparator
		 */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}
		
		/**
		 * restituisce il valore dello split
		 * @return valore dello split
		 */
		Object getSplitValue() {
			return splitValue;
		}
		
		/**
		 * restituisce l'indice iniziale del nodo di Split
		 * @return indice iniziale del nodo di Split
		 */
		int getBeginIndex() {
			return beginIndex;
		}
		
		/**
		 * restituisce l'indice finale del nodo di Split
		 * @return indice finale del nodo di Split
		 */
		int getEndIndex() {
			return endIndex;
		}
		
		/**
		 * /restituisce il valore dell'operatore matematico che definisce il test
		 * @return
		 */
		String getComparator() {
			return comparator;
		}
		
		/**
		 * concatena in un oggetto String i valori di beginExampleIndex,endExampleIndex, child, splitValue, comparator e restituisce la
		 * stringa finale.
		 */
		public String toString() {
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
	}
	
	/**
	 * oggetto Attribute che modella l'attributo indipendente sul quale lo split è generato
	 */
	private Attribute attribute;
	/**
	 * ArrayList per memorizzare gli split candidati in una struttura dati di dimensione pari ai possibili valori di test
	 */
	List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();
	/**
	 * attributo che contiene il valore di varianza a seguito del partizionamento indotto dallo split corrente
	 */
	private double splitVariance;
	
	/**
	 * metodo abstract per generare le informazioni necessarie per ciascuno degli split candidati (in mapSplit[])
	 * @param trainingSet training set complessivo
	 * @param beginExampelIndex
	 * @param endExampleIndex
	 * indici estremi del sotto-insieme di training
	 * @param attribute attributo indipendente sul quale si definisce lo split
	 */
	abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
	/**
	 * metodo abstract per modellare la condizione di test (ad ogni valore di test c'è un ramo dallo split)
	 * @param value valore dell'attributo che si vuole testare rispetto a tutti gli split
	 * @return
	 */
	abstract int testCondition (Object value);
	
	/**
	 * Invoca il costruttore della superclasse, ordina i valori dell'attributo
	di input per gli esempi beginExampleIndex-endExampleIndex e sfrutta questo
	ordinamento per determinare i possibili split e popolare l'ArrayList mapSplit, computa lo SSE
	(splitVariance) per l'attributo usato nello split sulla base del partizionamento indotto dallo split (lo
	stesso è la somma degli SSE calcolati su ciascuno SplitInfo collezioanto in mapSplit)
	 * @param trainingSet training set complessivo
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * indici estremi del sotto-insieme di training
	 * @param attribute
	 * attributo indipendente sul quale si definisce lo split
	 */
	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
		super(trainingSet, beginExampleIndex,endExampleIndex);
		this.attribute=attribute;
		trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
		setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
					
		//compute variance
		splitVariance=0;
		for(int i=0;i<mapSplit.size();i++){
			double localVariance=new LeafNode(trainingSet, mapSplit.get(i).beginIndex,mapSplit.get(i).endIndex).getVariance();
			splitVariance+=(localVariance);
		}
	}
	
	/**
	 * restituisce l'oggetto per l'attributo usato per lo split
	 * @return
	 */
	Attribute getAttribute(){
		return attribute;
	}
	
	/**
	 * restituisce l'information gain per lo split corrente
	 */
	double getVariance(){
		return splitVariance;
	}
	
	/**
	 * restituisce il numero dei rami originati nel nodo corrente;
	 */
	int getNumberOfChildren(){
		return mapSplit.size();
	}
	
	/**
	 * restituisce le informazioni per il ramo in mapSplit indicizzato da child.
	 * @param child
	 * @return
	 */
	SplitInfo getSplitInfo(int child){
		return mapSplit.get(child);
	}

	/**
	 * concatena le informazioni di ciascuno test (attributo, operatore e valore) in una String finale. Necessario per la predizione di nuovi esempi
	 * @return
	 */
	String formulateQuery(){
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query+= (i + ":" + attribute.getName() + mapSplit.get(i).getComparator() +mapSplit.get(i).getSplitValue())+"\n";
		return query;
	}
	
	/**
	 *  Confrontare i valori di splitVariance dei due nodi e restituire l'esito
	 */
	@Override
	public int compareTo(SplitNode o) {
		if(this.getVariance() < o.getVariance()){
			return -1; //gain minore
		}else if(this.getVariance() > o.getVariance()){
			return 1; //gain maggiore
		}else{
			return 0; //uguali
		}
	}
	
	/**
	 * concatena le informazioni di ciascuno test (attributo, esempi coperti, varianza di Split) in una String finale.
	 */
	@Override
	public String toString(){
		String v= "SPLIT : attribute=" +attribute.getName() +" "+ super.toString()+  " Split Variance: " + getVariance()+ "\n" ;
		
		for(int i=0;i<mapSplit.size();i++){
			v+= "\t"+mapSplit.get(i)+"\n";
		}
		
		return v;
	}
}
