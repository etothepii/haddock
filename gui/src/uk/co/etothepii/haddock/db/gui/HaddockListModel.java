/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.gui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import uk.co.etothepii.db.DataAccessObject;
import uk.co.etothepii.db.DataAccessObjectFactory;

/**
 *
 * @author jrrpl
 */
public class HaddockListModel<T extends DataAccessObject> 
        implements ListModel, List<T> {

    private ArrayList<T> data;
    private ArrayList<ListDataListener> listDataListeners;
    private final Object sync = new Object();
    private DataAccessObjectFactory<T> factory;

    public HaddockListModel(DataAccessObjectFactory factory) {
        this(new ArrayList<T>(), factory);
    }

    public HaddockListModel(ArrayList<T> data,
            DataAccessObjectFactory<T> factory) {
        this.data = data;
        this.listDataListeners = new ArrayList<ListDataListener>();
        this.factory = factory;
    }

    public synchronized int getSize() {
        return data.size();
    }

    public synchronized T getElementAt(int index) {
        return data.get(index);
    }

    public synchronized void addListDataListener(ListDataListener l) {
        listDataListeners.add(l);
    }

    public synchronized void removeListDataListener(ListDataListener l) {
        listDataListeners.remove(l);
    }

    public synchronized boolean add(T t) {
        int index = data.size();
        boolean toRet = data.add(t);
        if (toRet)
            intervalAdded(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, index, index));
        return toRet;
    }

    public synchronized void add(int index, T t) {
        data.add(index, t);
        intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,
                index, index));
    }

    public synchronized boolean addAll(Collection<? extends T> c) {
        int index = data.size();
        boolean toRet = data.addAll(c);
        if (toRet)
            intervalAdded(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, index, index));
        return toRet;
    }

    public synchronized boolean addAll(int index, Collection<? extends T> c) {
        boolean toRet = data.addAll(index, c);
        if (toRet)
            intervalAdded(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, index,
                    index + c.size() - 1));
        return toRet;
    }

    public synchronized void addElement(T t) {
        add(t);
    }

    private void contentsChanged(final ListDataEvent e) {
        for (final ListDataListener l : listDataListeners) {
            new Thread(new Runnable() {
                public void run() {
                    l.contentsChanged(e);
                }
            }).start();
        }
    }

    private void intervalAdded(final ListDataEvent e) {
        for (final ListDataListener l : listDataListeners) {
            new Thread(new Runnable() {
                public void run() {
                    l.intervalAdded(e);
                }
            }).start();
        }
    }

    private void intervalRemoved(final ListDataEvent e) {
        for (final ListDataListener l : listDataListeners) {
            new Thread(new Runnable() {
                public void run() {
                    l.intervalRemoved(e);
                }
            }).start();
        }
    }

    public synchronized int size() {
        return data.size();
    }

    public synchronized boolean isEmpty() {
        return data.isEmpty();
    }

    public synchronized boolean contains(Object o) {
        return data.contains((T)o);
    }

    public synchronized Iterator<T> iterator() {
        return data.iterator();
    }

    public synchronized Object[] toArray() {
        return data.toArray();
    }

    public  synchronized <T> T[] toArray(T[] a) {
        return data.toArray(a);
    }

    public synchronized boolean remove(Object o) {
        T t = (T)o;
        int index = data.indexOf(t);
        boolean toRet = data.remove(t);
        if (toRet)
            intervalRemoved(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, index, index));
        return toRet;
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    public synchronized boolean removeAll(Collection<?> c) {
        int min = data.size();
        int max = 0;
        for (Object o : c) {
            int first = data.indexOf(o);
            int last = data.lastIndexOf(o);
            if (first < min) min = first;
            if (last > max) max = last;
        }
        boolean toRet = data.removeAll(c);
        if (toRet)
            contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, min, max));
        return toRet;
    }

    public synchronized boolean retainAll(Collection<?> c) {
        int start = 0;
        int stop = data.size();
        boolean toRet = data.retainAll(c);
        if (toRet) contentsChanged(new ListDataEvent(this,
                ListDataEvent.CONTENTS_CHANGED, start, stop));
        return toRet;
    }

    public synchronized void clear() {
        int start = 0;
        int stop = data.size();
        data.clear();
        if (stop > 0)
            contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, start, stop));
    }

    public synchronized T get(int index) {
        return data.get(index);
    }

    public synchronized T set(int index, T element) {
        T toRet = data.set(index, element);
        if (!toRet.equals(element))
            contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, index, index));
        return toRet;
    }

    public synchronized T remove(int index) {
        T toRet = data.remove(index);
        if (toRet != null)
            intervalRemoved(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, index, index));
        return toRet;
    }

    public synchronized int indexOf(Object o) {
        return data.indexOf((T)o);
    }

    public synchronized int lastIndexOf(Object o) {
        return data.lastIndexOf((T)o);
    }

    public synchronized ListIterator<T> listIterator() {
        return data.listIterator();
    }

    public synchronized ListIterator<T> listIterator(int index) {
        return data.listIterator();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return data.subList(fromIndex, toIndex);
    }

}
