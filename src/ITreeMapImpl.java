import com.sun.source.tree.Tree;

import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class ITreeMapImpl<T extends Comparable<T>, V> implements ITreeMap<T, V> {


    RedBlackTree RBTree;
    ITreeMapImpl(){
        RBTree = new RedBlackTree();
    }

    @Override
    public Map.Entry<T, V> ceilingEntry(T key) {

        return null;
    }

    @Override
    public T ceilingKey(T key) {
        return null;
    }

    @Override
    public void clear() {
        RBTree.clear();
    }

    @Override
    public boolean containsKey(T key) {
        return RBTree.contains(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public Set<Map.Entry<T, V>> entrySet() {
        Set<Map.Entry<T, V>> set = new LinkedHashSet<>();
        entrySet(RBTree.getRoot(), set);
        return set;
    }

    private void entrySet(INode<T, V> node, Set<Map.Entry<T, V>> set) {
        if (node == null || node.isNull()) {
            return;
        }
        // left
        entrySet(node.getLeftChild(), set);
        // me
        set.add( new AbstractMap.SimpleEntry<>(node.getKey(), node.getValue()) );
        // right
        entrySet(node.getRightChild(), set);
    }

    @Override
    public Map.Entry<T, V> firstEntry() {
        return leftMostChild(RBTree.getRoot());
    }

    Map.Entry<T,V> leftMostChild(INode node){

    }

    @Override
    public T firstKey() {
        return null;
    }

    @Override
    public Map.Entry<T, V> floorEntry(T key) {
        return null;
    }

    @Override
    public T floorKey(T key) {
        return null;
    }

    @Override
    public V get(T key) {
        return (V) RBTree.search(key);
    }

    @Override
    public ArrayList<Map.Entry<T, V>> headMap(T toKey) {
        return null;
    }

    @Override
    public ArrayList<Map.Entry<T, V>> headMap(T toKey, boolean inclusive) {
        return null;
    }

    @Override
    public Set<T> keySet() {
        return keySet(RBTree.getRoot());
    }

    private Set<T> keySet(INode node) {
        if(node.isNull() || node == null){
            return new HashSet<>();
        }

        Set<T> set = new TreeSet<>();
        Set<T> leftSet = new HashSet<>(), rightSet = new HashSet<>();

        if(!node.getLeftChild().isNull())
            leftSet = keySet(node.getLeftChild());

        if(!node.getRightChild().isNull())
            rightSet = keySet(node.getRightChild());

        set.addAll(leftSet);
        set.add((T)node.getKey());
        set.addAll(rightSet);

        return set;
    }

    @Override
    public Map.Entry<T, V> lastEntry() {
        return null;
    }

    @Override
    public T lastKey() {
        return null;
    }

    @Override
    public Map.Entry<T, V> pollFirstEntry() {
        return null;
    }

    @Override
    public Map.Entry<T, V> pollLastEntry() {
        return null;
    }

    @Override
    public void put(T key, V value) {
        RBTree.insert(key,value);
    }

    @Override
    public void putAll(Map<T, V> map) {
        for (Map.Entry<T,V> entry : map.entrySet()){
            RBTree.insert(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public boolean remove(T key) {
        return RBTree.delete(key);
    }

    @Override
    public int size() {
        return RBTree.size;
    }

    @Override
    public Collection<V> values() {
         return this.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public INode getRoot(){
        return  RBTree.getRoot();
    }

    public static void main(String args[]){
        ITreeMapImpl<Integer,String> t = new ITreeMapImpl<>();
        t.put(-5,"mo");
        t.put(20,"moaz");
        t.put(10,"ahmed");
        t.put(5,"omar");
        RBTreePrinter.print(t.getRoot());
        Set<Map.Entry<Integer,String>> s=  t.entrySet();
        t.values().forEach((String k)->{
            System.out.println(k);
        });
    }
}
