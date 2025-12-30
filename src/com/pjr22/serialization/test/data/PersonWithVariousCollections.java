package com.pjr22.serialization.test.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Test class with various collection types to verify flexible collection deserialization.
 */
public class PersonWithVariousCollections {
    private String name;
    
    // Queue interface - should deserialize to LinkedList
    private Queue<String> activeEffects;
    
    // Deque interface - should deserialize to ArrayDeque
    private Deque<String> history;
    
    // List interface - should deserialize to ArrayList
    private List<String> friends;
    
    // Set interface - should deserialize to LinkedHashSet
    private Set<String> tags;
    
    // SortedSet interface - should deserialize to TreeSet
    private java.util.SortedSet<String> sortedItems;
    
    // Concrete ArrayList - should deserialize to ArrayList
    private ArrayList<String> concreteList;
    
    // Concrete LinkedList - should deserialize to LinkedList
    private LinkedList<String> concreteLinkedList;
    
    // Concrete HashSet - should deserialize to HashSet
    private HashSet<String> concreteHashSet;
    
    // Concrete LinkedHashSet - should deserialize to LinkedHashSet
    private LinkedHashSet<String> concreteLinkedHashSet;
    
    // Concrete TreeSet - should deserialize to TreeSet
    private TreeSet<String> concreteTreeSet;
    
    // Concrete PriorityQueue - should deserialize to PriorityQueue
    private PriorityQueue<String> concretePriorityQueue;
    
    // Concrete ArrayDeque - should deserialize to ArrayDeque
    private ArrayDeque<String> concreteArrayDeque;

    public PersonWithVariousCollections() {
        this.activeEffects = new LinkedList<>();
        this.history = new ArrayDeque<>();
        this.friends = new ArrayList<>();
        this.tags = new LinkedHashSet<>();
        this.sortedItems = new TreeSet<>();
        this.concreteList = new ArrayList<>();
        this.concreteLinkedList = new LinkedList<>();
        this.concreteHashSet = new HashSet<>();
        this.concreteLinkedHashSet = new LinkedHashSet<>();
        this.concreteTreeSet = new TreeSet<>();
        this.concretePriorityQueue = new PriorityQueue<>();
        this.concreteArrayDeque = new ArrayDeque<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Queue<String> getActiveEffects() {
        return activeEffects;
    }

    public void setActiveEffects(Queue<String> activeEffects) {
        this.activeEffects = activeEffects;
    }

    public Deque<String> getHistory() {
        return history;
    }

    public void setHistory(Deque<String> history) {
        this.history = history;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public java.util.SortedSet<String> getSortedItems() {
        return sortedItems;
    }

    public void setSortedItems(java.util.SortedSet<String> sortedItems) {
        this.sortedItems = sortedItems;
    }

    public ArrayList<String> getConcreteList() {
        return concreteList;
    }

    public void setConcreteList(ArrayList<String> concreteList) {
        this.concreteList = concreteList;
    }

    public LinkedList<String> getConcreteLinkedList() {
        return concreteLinkedList;
    }

    public void setConcreteLinkedList(LinkedList<String> concreteLinkedList) {
        this.concreteLinkedList = concreteLinkedList;
    }

    public HashSet<String> getConcreteHashSet() {
        return concreteHashSet;
    }

    public void setConcreteHashSet(HashSet<String> concreteHashSet) {
        this.concreteHashSet = concreteHashSet;
    }

    public LinkedHashSet<String> getConcreteLinkedHashSet() {
        return concreteLinkedHashSet;
    }

    public void setConcreteLinkedHashSet(LinkedHashSet<String> concreteLinkedHashSet) {
        this.concreteLinkedHashSet = concreteLinkedHashSet;
    }

    public TreeSet<String> getConcreteTreeSet() {
        return concreteTreeSet;
    }

    public void setConcreteTreeSet(TreeSet<String> concreteTreeSet) {
        this.concreteTreeSet = concreteTreeSet;
    }

    public PriorityQueue<String> getConcretePriorityQueue() {
        return concretePriorityQueue;
    }

    public void setConcretePriorityQueue(PriorityQueue<String> concretePriorityQueue) {
        this.concretePriorityQueue = concretePriorityQueue;
    }

    public ArrayDeque<String> getConcreteArrayDeque() {
        return concreteArrayDeque;
    }

    public void setConcreteArrayDeque(ArrayDeque<String> concreteArrayDeque) {
        this.concreteArrayDeque = concreteArrayDeque;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PersonWithVariousCollections other = (PersonWithVariousCollections) obj;
        if (!name.equals(other.name)) return false;
        if (!activeEffects.equals(other.activeEffects)) return false;
        if (!history.equals(other.history)) return false;
        if (!friends.equals(other.friends)) return false;
        if (!tags.equals(other.tags)) return false;
        if (!sortedItems.equals(other.sortedItems)) return false;
        if (!concreteList.equals(other.concreteList)) return false;
        if (!concreteLinkedList.equals(other.concreteLinkedList)) return false;
        if (!concreteHashSet.equals(other.concreteHashSet)) return false;
        if (!concreteLinkedHashSet.equals(other.concreteLinkedHashSet)) return false;
        if (!concreteTreeSet.equals(other.concreteTreeSet)) return false;
        if (!concretePriorityQueue.equals(other.concretePriorityQueue)) return false;
        if (!concreteArrayDeque.equals(other.concreteArrayDeque)) return false;
        return true;
    }
}
