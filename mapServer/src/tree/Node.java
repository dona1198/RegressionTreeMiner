package tree;

import java.io.Serializable;

import data.Data;

/**
 * Classe per modellare l'astrazione dell'entità nodo (fogliare o intermedio) dell'albero di decisione
 * @author Donato Lerario
 *
 */
abstract class Node  implements Serializable{
	/**
	 * contatore dei nodi generati nell'albero
	 */
	private static int idNodeCount=0;
	/**
	 * identificativo numerico del nodo
	 */
	private int idNode;
	/**
	 * indice nell'array del training set del primo esempio coperto dal nodo corrente
	 */
	private int beginExampleIndex;
	/**
	 * indice nell'array del training set dell'ultimo esempio coperto dal nodo corrente. beginExampleIndex e endExampleIndex individuano un sotto-insieme di training.
	 */
	private int endExampleIndex;
	/**
	 * valore dello SSE calcolato, rispetto all'attributo di classe, nel sotto-insieme di training del nodo
	 */
	private double variance;
	
	/**
	 * Costruttore di classe.
	 * Avvalora gli attributi primitivi di classe, inclusa la varianza che viene calcolata
	 * rispetto all'attributo da predire nel sotto-insieme di training coperto dal nodo
	 * @param trainingSet oggetto di classe Data contenente il training set completo
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * i due indici estremi che identificano il sotto-insieme di training coperto dal nodo corrente
	 */
	Node(Data trainingSet, int beginExampleIndex, int endExampleIndex){
		idNode = ++idNodeCount;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		double Somma = 0.0;
		double SommaQuadrati = 0.0;
		for(int i = beginExampleIndex; i <= endExampleIndex; i++) {
			Somma += trainingSet.getClassValue(i);
			SommaQuadrati += Math.pow(trainingSet.getClassValue(i) , 2);
		}
		Somma *= Somma;
		variance = SommaQuadrati - (( Somma )  /(  endExampleIndex - beginExampleIndex + 1 ));
	}
	
	/**
	 * Restituisce il valore del membro idNode
	 * @return identificativo numerico del nodo
	 */
	int getIdNode() {
		return idNode;
	}

	/**
	 * Restituisce il valore del membro beginExampleIndex
	 * @return indice del primo esempio del sotto-insieme rispetto al training set complessivo
	 */
	int getBeginExampleIndex() {
		return beginExampleIndex;
	}
	
	/**
	 * Restituisce il valore del membro endExampleIndex
	 * @return indice del ultimo esempio del sotto-insieme rispetto al training set complessivo
	 */
	int getEndExampleIndex() {
		return endExampleIndex;
	}
	
	/**
	 * Restituisce il valore del membro variance
	 * @return valore dello SSE dell’attributo da predire rispetto al nodo corrente
	 */
	double getVariance() {
		return variance;
	}
	
	/**
	 * E' un metodo astratto la cui implementazione riguarda i nodi di tipo test (split node)
	 * dai quali si possono generare figli, uno per ogni split prodotto. Restituisce il numero di tali nodi figli.
	 * @return valore del numero di nodi sottostanti
	 */
	abstract int getNumberOfChildren();
	
	/**
	 * Concatena in un oggetto String i valori di beginExampleIndex,endExampleIndex,
	 * variance e restituisce la stringa finale.
	 */
	@Override
	public String toString() {
		String v = "Nodo: [Examples:" + beginExampleIndex + "-" + endExampleIndex + "] variance:" + variance;
		return v;
	}
}
