package tree;

import data.Data;

/**
 * Classe per modellare l'entità nodo fogliare.
 * @author Donato Lerario
 *
 */
public class LeafNode extends Node{
	/**
	 * valore dell'attributo di classe espresso nella foglia corrente
	 */
	private double predictedClassValue = 0.0;
	
	/**
	 * istanzia un oggetto invocando il costruttore della superclasse e avvalora
	l'attributo predictedClassValue (come media dei valori dell’attributo di classe che ricadono nella partizione---
	ossia la porzione di trainingSet compresa tra beginExampelIndex e endExampelIndex )
	 * @param trainingSet training set complessivo
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * indici estremi del sotto-insieme di training, coperto nella foglia
	 */
	LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		super(trainingSet, beginExampleIndex, endExampleIndex);
		for( int i = beginExampleIndex; i <= endExampleIndex; i++ ) {
			predictedClassValue += trainingSet.getClassValue(i);
		}
		predictedClassValue = predictedClassValue / (endExampleIndex - beginExampleIndex + 1);
	}
	
	/**
	 * restituisce il valore del membro predictedClassValue
	 * @return
	 */
	Double getPredictedClassValue() {
		return predictedClassValue;
	}
	
	/**
	 * restituisce il numero di split originanti dal nodo foglia, ovvero 0.
	 */
	int getNumberOfChildren() {
		return 0; //nodo foglia non genera dei nodi figli
	}
	
	/**
	 * invoca il metodo della superclasse e assegnando anche il valore di classe della foglia.
	 */
	@Override
	public String toString() {
		String v = "LEAF : class=";
		v += predictedClassValue + " " + super.toString();
		return v;
	}
}
