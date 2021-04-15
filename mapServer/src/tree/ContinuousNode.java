package tree;

import java.util.ArrayList;
import java.util.List;
import data.Attribute;
import data.Data;

/**
 * Classe che rappresenta un nodo corrispondente ad un attributo continuo.
 * @author Donato Lerario
 *
 */
public class ContinuousNode extends SplitNode{
	
	/**
	 * Costruttore di classe.
	 * @param trainingSet
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * @param attribute
	 */
	ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * Implementazione del metodo setSplitInfo da SplitNode considerando un attributo continuo
	 */
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute){
			//Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
			Double currentSplitValue= (Double)trainingSet.getExplanatoryValue(beginExampleIndex,attribute.getIndex());
			double bestInfoVariance=0;
			List <SplitInfo> bestMapSplit=null;
			
			for(int i=beginExampleIndex+1;i<=endExampleIndex;i++){
				Double value=(Double)trainingSet.getExplanatoryValue(i,attribute.getIndex());
				if(value.doubleValue()!=currentSplitValue.doubleValue()){
				//	System.out.print(currentSplitValue +" var ");
					double localVariance=new LeafNode(trainingSet, beginExampleIndex,i-1).getVariance();
					double candidateSplitVariance=localVariance;
					localVariance=new LeafNode(trainingSet, i,endExampleIndex).getVariance();
					candidateSplitVariance+=localVariance;
					//System.out.println(candidateSplitVariance);
					if(bestMapSplit==null){
						bestMapSplit=new ArrayList<SplitInfo>();
						bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
						bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
						bestInfoVariance=candidateSplitVariance;
					}
					else{		
												
						if(candidateSplitVariance<bestInfoVariance){
							bestInfoVariance=candidateSplitVariance;
							bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
							bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
						}
					}
					currentSplitValue=value;
				}
			}
			
			if(bestMapSplit != null){
				mapSplit=bestMapSplit;
				//rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)

				if(mapSplit.get(1).getBeginIndex() == mapSplit.get(1).getEndIndex()){
					mapSplit.remove(1);
				}

			}
			
	 }

	@Override
	int testCondition(Object value) {
		return 0;
	}
	
	@Override
	public String toString(){
		return "CONTINUOUS " + super.toString();
	}

}
