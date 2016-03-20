
import java.util.List;
import java.util.ArrayList;

class BoxPair<T, T2> {
	T key;
	T2 value;
	public BoxPair(T k, T2 v) {
		this.key = k;
		this.value = v;
	}
	public T getKey() { return key;}
	public T2 getValue(){ return value;}
	
}
public class TestMergeJoin {

	public void mergeJoin(List<BoxPair<Integer, String>> la, List<BoxPair<Integer,String>> lb) {
		
		int indexA = 0;
		int indexB = 0;
		boolean done = false;
		
		while(!done) {
			if(la.get(indexA).key == lb.get(indexB).key) {
				System.out.println("Key: " + la.get(indexA).key + " value: " + lb.get(indexB).value);
			}
			if(la.get(indexA).key < lb.get(indexB).key) {
				indexA++;
				if(indexA >= la.size()) {
					done = true;
				}
				continue;
			}
			indexB++;
			if(indexB >= lb.size()) {
				done = true;
				continue;
			}
			
			
		}
		
	}
	public static void main(String args[] ) {
		
		TestMergeJoin program = new TestMergeJoin();
		List<BoxPair<Integer,String>> boxPairList1 = new ArrayList<>();
		List<BoxPair<Integer,String>> boxPairList2 = new ArrayList<>();
		
		/*
		 * 1 2 4 5 7 9
		 */
		BoxPair<Integer,String> boxPair1 = new BoxPair<Integer,String>(1,"josh");
		BoxPair<Integer,String> boxPair2 = new BoxPair<Integer,String>(2,"josh2");
		BoxPair<Integer,String> boxPair4 = new BoxPair<Integer,String>(4,"josh4");
		BoxPair<Integer,String> boxPair5 = new BoxPair<Integer,String>(5,"josh5");
		BoxPair<Integer,String> boxPair7 = new BoxPair<Integer,String>(7,"josh7");
		BoxPair<Integer,String> boxPair9 = new BoxPair<Integer,String>(9,"josh9");
		boxPairList1.add(boxPair1);
		boxPairList1.add(boxPair2);
		boxPairList1.add(boxPair4);
		boxPairList1.add(boxPair5);
		boxPairList1.add(boxPair7);
		boxPairList1.add(boxPair9);
		/*
		 * 1 4 6 7 8 9
		 */
		BoxPair<Integer,String> boxPair21 = new BoxPair<Integer,String>(1,"josh");
		BoxPair<Integer,String> boxPair24 = new BoxPair<Integer,String>(4,"josh4");
		BoxPair<Integer,String> boxPair26 = new BoxPair<Integer,String>(6,"josh6");
		BoxPair<Integer,String> boxPair27 = new BoxPair<Integer,String>(7,"josh7");
		BoxPair<Integer,String> boxPair28 = new BoxPair<Integer,String>(8,"josh8");
		BoxPair<Integer,String> boxPair29 = new BoxPair<Integer,String>(9,"josh9");
		
		boxPairList2.add(boxPair21);
		boxPairList2.add(boxPair24);
		boxPairList2.add(boxPair26);
		boxPairList2.add(boxPair27);
		boxPairList2.add(boxPair28);
		boxPairList2.add(boxPair29);
		
		program.mergeJoin(boxPairList2, boxPairList1);
		
		
	}
}

