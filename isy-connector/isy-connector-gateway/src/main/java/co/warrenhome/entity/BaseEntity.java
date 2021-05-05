package co.warrenhome.entity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public abstract class BaseEntity {

    private transient final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(List<String> propertyNames, PropertyChangeListener listener){
        propertyNames.forEach(name -> pcs.addPropertyChangeListener(name, listener));
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener){
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(List<String> propertyNames, PropertyChangeListener listener){
        propertyNames.forEach(name -> pcs.removePropertyChangeListener(name, listener));
    }

    protected void firePropertyChange(String name, int oldValue, int newValue){
        pcs.firePropertyChange(name, oldValue, newValue);
    }

    protected void firePropertyChange(String name, boolean oldvalue, boolean newValue){
        pcs.firePropertyChange(name, oldvalue, newValue);
    }

    protected void firePropertyChange(String name, Object oldValue, Object newValue){
        pcs.firePropertyChange(name, oldValue, newValue);
    }

    protected void firePropertyChange(PropertyChangeEvent event){
        pcs.firePropertyChange(event);
    }

}
