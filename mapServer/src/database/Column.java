package database;
/**
 * Classe che modella lo schema di una tabella nel database relazionale
 * @author Donato Lerario
 *
 */
public class Column{
	private String name;
	private String type;
	
	/**
	 * Costruttore di classe.
	 * @param name
	 * @param type
	 */
	Column(String name,String type){
		this.name=name;
		this.type=type;
	}
	
	/**
	 * Restituisce nome della colonna
	 * @return nome della colonna
	 */
	public String getColumnName(){
		return name;
	}
	
	/**
	 * Definisce se la colonna è numerica o no
	 * @return
	 */
	public boolean isNumber(){
		return type.equals("number");
	}
	
	@Override
	public String toString(){
		return name+":"+type;
	}
}