package tree;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;
import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import server.UnknownValueException;
import data.ContinuousAttribute;

/**
 * Classe per modellare l'entità l'intero albero di decisione come insieme di sotto-alberi
 * @author Donato Lerario
 *
 */
public class RegressionTree implements Serializable{
		
	private Node root; // radice del sotto-albero corrente
	private RegressionTree childTree[]; // array di sotto-alberi originanti nel nodo root:vi è un elemento nell’array per ogni figlio del nodo
	
	/**
	 * Costruttore di classe.
	 * istanzia un sotto-albero dell'intero albero
	 */
	private RegressionTree(){	}
	
	/**
	 * Costruttore di classe.
	 *  istanzia un sotto-albero dell'intero albero e avvia l'induzione dell'albero dagli esempi di training in input
	 * @param trainingSet training set complessivo
	 */
	public RegressionTree(Data trainingSet){
		
		learnTree(trainingSet,0,trainingSet.getNumberOfExamples()-1,trainingSet.getNumberOfExamples()*10/100);
	}
	
	/**
	 * Verifica se il sotto-insieme corrente può essere coperto da un nodo foglia
	controllando che il numero di esempi del training set compresi tra begin ed end sia minore uguale di
	numberOfExamplesPerLeaf.
	 * @param trainingSet training set complessivo
	 * @param begin
	 * @param end
	 * indici estremi del sotto-insieme di training
	 * @param numberOfExamplesPerLeaf numero minimo che una foglia deve contenere
	 * @return esito sulle condizioni per i nodi fogliari
	 */
	private boolean isLeaf(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf) {
		if (end - begin <= numberOfExamplesPerLeaf)
			return true;
		else return false;
	}
	
	/**
	 * Per ciascun attributo indipendente istanzia il DiscreteNode o ContinuousNode associato e seleziona il
	nodo di split con minore varianza tra i nodi istanziati. Ordina la porzione di trainingSet corrente
	(tra begin ed end) rispetto all’attributo indipendente del nodo di split selezionato. Restituisce il nodo
	selezionato. Utilizzata l'RTTI per distinuguere un nodo discreto da un nodo continuo.
	 * @param trainingSet training set complessivo
	 * @param begin
	 * @param end
	 * indici estremi del sotto-insieme di training
	 * @return nodo di split migliore per il sotto-insieme di training
	 */
	private SplitNode determineBestSplitNode(Data trainingSet,int begin,int end) {
		SplitNode currentNode;
		TreeSet<SplitNode> ts = new TreeSet<SplitNode>();
		for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
			Attribute a=trainingSet.getExplanatoryAttribute(i);
			if(a instanceof DiscreteAttribute) {
				DiscreteAttribute attribute=(DiscreteAttribute)trainingSet.getExplanatoryAttribute(i);
				currentNode = new DiscreteNode(trainingSet,begin,end,attribute);
			}
			else {
				ContinuousAttribute attribute=(ContinuousAttribute)trainingSet.getExplanatoryAttribute(i);
				currentNode = new ContinuousNode(trainingSet,begin,end,attribute);
			}
			ts.add(currentNode);
		}
		
		trainingSet.sort(ts.first().getAttribute(), begin, end);

		return ts.first();
	}
	
	/**
	 * Genera un sotto-albero con il sotto-insieme di input istanziando un nodo
	fogliare (isLeaf()) o un nodo di split. In tal caso determina il miglior nodo rispetto al sotto-insieme di
	input (determineBestSplitNode()), ed a tale nodo esso associa un sotto-albero avente radice il nodo
	medesimo (root) e avente un numero di rami pari al numero dei figli determinati dallo split (childTree[]).
	Ricorsivamente ogni oggetto RegressionTree in childTree[] sarà re-invocato il metodo
	learnTree() per l'apprendimento su un insieme ridotto del sotto-insieme attuale (begin... end). Nella
	condizione in cui il nodo di split non origina figli, il nodo diventa fogliare.
	 * @param trainingSet training set complessivo
	 * @param begin
	 * @param end
	 * indici estremi del sotto-insieme di training
	 * @param numberOfExamplesPerLeaf numero max che una foglia deve contenere
	 */
	private void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf){
		if( isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
			//determina la classe che compare più frequentemente nella partizione corrente
			root=new LeafNode(trainingSet,begin,end);
		}
		else //split node
		{
			root=determineBestSplitNode(trainingSet, begin, end);
		
			if(root.getNumberOfChildren()>1){
				childTree=new RegressionTree[root.getNumberOfChildren()];
				for(int i=0;i<root.getNumberOfChildren();i++){
					childTree[i]=new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).getBeginIndex(), ((SplitNode)root).getSplitInfo(i).getEndIndex(), numberOfExamplesPerLeaf);
				}
			}
			else
				root=new LeafNode(trainingSet,begin,end);
			
		}
	}
		
	public void printTree(){
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}
	
	/**
	 * Scandisce ciascun ramo dell'albero completo dalla radice alla foglia concatenando
	le informazioni dei nodi di split fino al nodo foglia. In particolare per ogni sotto-albero (oggetto
	DecisionTree) in childTree[] concatena le informazioni del nodo root: se è di split discende
	ricorsivamente l'albero per ottenere le informazioni del nodo sottostante (necessario per
	ricostruire le condizioni in AND) di ogni ramo-regola, se è di foglia (leaf) termina
	l'attraversamento visualizzando la regola.
	 * @return
	 */
	public String printRules() {
		Node currentNode = root;
		String current = "";
		String finalString = "\n********* RULES **********\n\n";
		
		if(currentNode instanceof LeafNode) {
			System.out.println(((LeafNode) currentNode).toString());
		} else {
			current += ((SplitNode) currentNode).getAttribute().getName();
			for(int i = 0; i < childTree.length; i++) {
				finalString += childTree[i].printRules(current + ((SplitNode) currentNode).getSplitInfo(i).getComparator() + ((SplitNode) currentNode).getSplitInfo(i).getSplitValue());
			}
		}
		finalString+= "*************************\n";
		System.out.println(finalString);
		return finalString;
	}
	
	/**
	 * Supporta il metodo public void printRules(). Concatena alle informazioni in
	current del precedente nodo quelle del nodo root del corrente sotto-albero (oggetto DecisionTree): se il
	nodo corrente è di split il metodo viene invocato ricorsivamente con current e le informazioni del nodo
	corrente, se è di fogliare (leaf) visualizza tutte le informazioni concatenate.
	 * @param current Informazioni del nodo di split del sotto-albero al livello superiore
	 * @return
	 */
	private String printRules(String current) {
		Node currentNode = root;
		String finalString = "";

		if(currentNode instanceof LeafNode) {
			return current + "  ==> Class = " + ((LeafNode) currentNode).getPredictedClassValue() + "\n";
		}  else {
			current += " AND " + ((SplitNode) currentNode).getAttribute().getName();
			for(int i = 0; i < childTree.length; i++) {
				finalString += childTree[i].printRules(current + ((SplitNode) currentNode).getSplitInfo(i).getComparator() + ((SplitNode) currentNode).getSplitInfo(i).getSplitValue());
			}
		}

		return finalString;
	}
	
	/**
	 * Visualizza le informazioni di ciascuno split dell'albero
	(SplitNode.formulateQuery()) e per il corrispondente attributo acquisisce il valore dell'esempio
	da predire da tastiera. Se il nodo root corrente è leaf termina l'acquisizione e visualizza la
	predizione per l’attributo classe, altrimenti invoca ricorsivamente sul figlio di root in childTree[]
	individuato dal valore acquisito da tastiera.
	Il metodo sollevare l'eccezione UnknownValueException qualora la risposta dell’utente non permetta di
	selezionare una ramo valido del nodo di split. L'eccezione sarà gestita nel metodo che invoca
	predictClass() .
	 * @param out 
	 * @param in
	 * @throws UnknownValueException
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NumberFormatException 
	 */
	public void predictClass(ObjectInputStream in, ObjectOutputStream out) throws UnknownValueException, IOException, ClassNotFoundException{
		if(root instanceof LeafNode){
			out.writeObject("OK");
			out.writeObject(((LeafNode) root).getPredictedClassValue().toString());
		} else {
			int risp;
			out.writeObject("QUERY");
			out.writeObject(((SplitNode) root).formulateQuery());
			risp = Integer.parseInt(in.readObject().toString());
			if(risp==-1 || risp>=root.getNumberOfChildren())
				throw new UnknownValueException("The answer should be an integer between 0 and " 
						+(root.getNumberOfChildren()-1)+"!");
				else
					childTree[risp].predictClass(in, out);
		}
	}
	
	/**
	 * Serializza l'albero in un file
	 * @param nomeFile Nome del file in cui salvare l'albero
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException{
		FileOutputStream outFile = new FileOutputStream( nomeFile );
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(this);
		outFile.close();
	}
	
	/**
	 * Carica un albero di regressione salvato in un file
	 * @param nomeFile Nome del file in cui è salvato l'albero
	 * @return L'albero contenuto nel file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static RegressionTree carica(String nomeFile) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream( nomeFile ));
		RegressionTree rs =(RegressionTree)in.readObject();
		in.close();
		return rs;
	}
	
	/**
	 * Concatena in una String tutte le informazioni di root-childTree[] correnti
	invocando i relativi metodo toString(): nel caso il root corrente è di split vengono concatenate anche
	le informazioni dei rami.
	 */
	@Override
	public String toString(){
		String tree=root.toString()+"\n";
		
		if( root instanceof LeafNode){
		
		}
		else //split node
		{
			for(int i=0;i<childTree.length;i++)
				tree +=childTree[i];
		}
		return tree;
	}
}
		
