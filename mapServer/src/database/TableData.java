package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Classe che modella l’insieme di transazioni collezionate in una tabella. 
 * La singola transazione è modellata dalla classe Example.
 * @author Donato Lerario
 *
 */
public class TableData {

	private DbAccess db;
	

	/**
	 * Costruttore di classe.
	 * @param db
	 */
	public TableData(DbAccess db) {
		this.db=db;
	}

	/**
	 * Ricava lo schema della tabella con nome table. Esegue una interrogazione
	per estrarre le tuple distinte da tale tabella. Per ogni tupla del resultset, si crea un oggetto,
	istanza della classe Example, il cui riferimento va incluso nella lista da restituire. In particolare,
	per la tupla corrente nel resultset, si estraggono i valori dei singoli campi (usando getFloat() o
	getString()), e li si aggiungono all’oggetto istanza della classe Example che si sta costruendo.
	 * @param table nome della tabella nel database
	 * @return Lista di transazioni memorizzate nella tabella. 
	 * @throws SQLException (in presenza di errori nella esecuzione della query)
	 * @throws EmptySetException  (se il resultset è vuoto)
	 */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException{
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema=new TableSchema(db,table);
		
		
		String query="select ";
		
		for(int i=0;i<tSchema.getNumberOfAttributes();i++){
			Column c=tSchema.getColumn(i);
			if(i>0)
				query+=",";
			query += c.getColumnName();
		}
		if(tSchema.getNumberOfAttributes()==0)
			throw new SQLException();
		query += (" FROM "+table);
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty=true;
		while (rs.next()) {
			empty=false;
			Example currentTuple=new Example();
			for(int i=0;i<tSchema.getNumberOfAttributes();i++)
				if(tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i+1));
				else
					currentTuple.add(rs.getString(i+1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if(empty) throw new EmptySetException();
		
		
		return transSet;

	}

	/**
	 * Formula ed esegue una interrogazione SQL per estrarre i valori distinti ordinati di column e 
	 * popolare un insieme da restituire (scegliere opportunamente in Set da utilizzare). 
	 * @param table  Nome della tabella
	 * @param column	Nome della colonna nella tabella
	 * @return  Insieme di valori distinti ordinati in modalità ascendente che l’attributo identificato da
		nome column assume nella tabella identificata dal nome table.
	 * @throws SQLException
	 * @throws EmptySetException 
	 */
	public Set<Object> getDistinctColumnValues (String table, Column column) throws SQLException, EmptySetException{
		Set<Object> ColValues = new HashSet<Object>();
		Statement statement;
		TableSchema tSchema=new TableSchema(db,table);
		
		String query="SELECT DISTINCT " + column.getColumnName();
		if(tSchema.getNumberOfAttributes()==0)
			throw new SQLException();
		query += (" FROM "+table + " ORDER BY " + column.getColumnName() + " ASC"); //formulazione della query
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty=true;
		while (rs.next()) {
			empty=false;
			ColValues.add(rs.getObject(1)); //inseriamo nell'HashSet i valori distinti ordinati all'interno della colonna column
		}
		rs.close();
		statement.close();
		if(empty) throw new EmptySetException();
		
		return ColValues;
	}

	@Override
	public String toString() {
		return db.toString();
	}
}
