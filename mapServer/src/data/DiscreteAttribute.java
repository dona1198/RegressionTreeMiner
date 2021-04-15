package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe per definire attributo di tipo Discreto (valori letterali)
 * @author Donato Lerario
 *
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>{
	/**
	 * TreeSet di valori distinti dell'attributo discreto ordinati in modo ascendente
	 */
	private Set<String> values=new TreeSet<>();
	
	/**
	 * Costruttore di classe.
	 * @param name
	 * @param index
	 * @param values
	 */
	DiscreteAttribute(String name, int index, Set<String> values) {
		super(name,index);
		this.values=values;
	}
	
	/**
	 * Restituisce numero dei valori distinti dell'attributo discreto
	 * @return numero dei valori distinti dell'attributo discreto
	 */
	public int getNumberOfDistinctValues(){
		return values.size();
	}

	@Override
	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return values.iterator();
	}
	
	
}

