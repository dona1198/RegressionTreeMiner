package data;

import java.io.Serializable;

/**
 * La classe modella un generico attributo discreto o continuo
 * @author Donato Lerario
 *
 */
public abstract class Attribute implements Serializable{
	private String name;
	private int index;
	

	/**
	 * E' il costruttore di classe. Inizializza i valori dei membri name, index
	 * @param name nome simbolico dell'attributo
	 * @param index identificativo numerico dell'attributo
	 */
	Attribute(String name, int index){
		this.name = name;
		this.index = index;
	}

	/**
	 * Restituisce il valore nel membro name;
	 * @return name Nome simbolico dell'attributo (di tipo String)
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Restituisce il valore nel membro index;
	 * @return index Restituisce il valore nel membro index;
	 */
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return "Attribute: " + name + ", " + index;
	}
}
