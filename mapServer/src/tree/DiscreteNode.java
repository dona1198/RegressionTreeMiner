package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;

/**
 * Classe per modellare l'entità nodo di split relativo ad un attributo indipendente discreto.
 * @author Donato Lerario
 *
 */
public class DiscreteNode extends SplitNode{
	
	/**
	 * Istanzia un oggetto invocando il costruttore della superclasse con il parametro attribute
	 * @param trainingSet training set complessivo
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * indici estremi del sotto-insieme di training
	 * @param attribute attributo indipendente sul quale si definisce lo split
	 */
	DiscreteNode(Data trainingSet,int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * istanzia oggetti SpliInfo (definita come inner
	 * class in Splitnode) con ciascuno dei valori discreti dell’attributo relativamente al sotto-insieme di training
	 * corrente (ossia la porzione di trainingSet compresa tra beginExampelIndex e endExampelIndex), quindi
	 * popola l'ArrayList mapSplit con tali oggetti.
	 */
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		Object current = trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		int start = beginExampleIndex;
		int numChild = 0;
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			if (current.equals(trainingSet.getExplanatoryValue(i, attribute.getIndex())) == false) {
				mapSplit.add(new SplitInfo(current, start, i - 1, numChild));
				current = trainingSet.getExplanatoryValue(i, attribute.getIndex());
				start = i;
				numChild++;
			}
		}
		mapSplit.add(new SplitInfo(current, start, endExampleIndex, numChild));
	}
	
	/**
	 * effettua il confronto del valore in input
	 * rispetto al valore contenuto nell’attributo splitValue di ciascuno degli oggetti SplitInfo collezionati in
	 * mapSplit e restituisce l'identificativo dello split (indice della posizione dell'ArrayList mapSplit) con cui
	 * il test è positivo
	 */
	int testCondition (Object value) {
		for(int i = 0; i < mapSplit.size(); i++) {
			if(value.equals(mapSplit.get(i).getSplitValue())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * invoca il metodo della superclasse specializzandolo per discreti
	 */
	@Override
	public String toString() {
		return "DISCRETE " + super.toString();
	}
}
