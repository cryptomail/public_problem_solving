/**
 * // This is the interface that allows for creating nested lists.
 * // You should not implement it, or speculate about its implementation
 * public interface NestedInteger {
 *
 *     // @return true if this NestedInteger holds a single integer, rather than a nested list.
 *     public boolean isInteger();
 *
 *     // @return the single integer that this NestedInteger holds, if it holds a single integer
 *     // Return null if this NestedInteger holds a nested list
 *     public Integer getInteger();
 *
 *     // @return the nested list that this NestedInteger holds, if it holds a nested list
 *     // Return null if this NestedInteger holds a single integer
 *     public List<NestedInteger> getList();
 * }
 */
 
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
public class NestedIterator implements Iterator<Integer> {

    private int idx;
    private List<Integer> list = new ArrayList<Integer>();
    
    
    public void buildList(NestedInteger i) {
        if(i.isInteger()) {
            list.add(i.getInteger());
        } else {
            for(NestedInteger x: i.getList()) {
                buildList(x);
            }
        }
    }
    public NestedIterator(List<NestedInteger> nestedList) {
        idx = 0;
        for(NestedInteger i : nestedList) {
            buildList(i);  
        }
    }

    @Override
    public Integer next() {
        if(idx >= list.size()) {
            return null;
        }
        
        Integer i = list.get(idx);
        idx++;
        
        return i;
    }

    @Override
    public boolean hasNext() {
        return idx < list.size();
    }
}

/**
 * Your NestedIterator object will be instantiated and called as such:
 * NestedIterator i = new NestedIterator(nestedList);
 * while (i.hasNext()) v[f()] = i.next();
 */
