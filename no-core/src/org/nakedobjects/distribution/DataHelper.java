package org.nakedobjects.distribution;

import org.nakedobjects.NakedObjects;
import org.nakedobjects.object.DirtyObjectSet;
import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.persistence.Oid;
import org.nakedobjects.object.reflect.NakedObjectAssociation;
import org.nakedobjects.object.reflect.NakedObjectField;
import org.nakedobjects.object.reflect.OneToManyAssociation;
import org.nakedobjects.object.reflect.OneToOneAssociation;
import org.nakedobjects.object.reflect.PojoAdapterFactory;
import org.nakedobjects.object.reflect.ReflectiveActionException;


public class DataHelper {

    public static Naked recreate(Data data) {
        if(data instanceof ValueData) {
            return recreateValue((ValueData) data);
        } else {
            return recreateObject((ObjectData) data);
        }
    }
    
    private static Naked recreateValue(ValueData valueData) {
        Naked value = NakedObjects.getPojoAdapterFactory().createAdapterForValue(valueData.getValue());
        return value;
    }

    public  static NakedObject recreateObject(ObjectData data) {
        Oid oid = data.getOid();
        String type = data.getType();
        NakedObjectSpecification specification = NakedObjects.getSpecificationLoader().loadSpecification(type);
        PojoAdapterFactory objectLoader = NakedObjects.getPojoAdapterFactory();
        NakedObject object;
        if(oid == null) {
            object = objectLoader.createTransientInstance(specification);
            recreateObjectsInFields(data, object);
        } else {
            object = objectLoader.recreateAdapter(oid, specification);
	        objectLoader.loading(object, data.isResolved());
	        recreateObjectsInFields(data, object);
	        objectLoader.loaded(object, data.isResolved());
        }
/*        if (oid != null && loadedObjects().isLoaded(oid)) {
            object = loadedObjects().getLoadedObject(oid);
        } else {
            NakedObjectSpecification specification = NakedObjects.getSpecificationLoader().loadSpecification(type);
            object = (NakedObject) specification.acquireInstance();
            if (oid != null) {
                object.setOid(oid);
                loadedObjects().loaded(object);
            }
        }
        if(data.isResolved() && ! object.isResolved()) {
            object.setResolved();
        }
  */
        return object;
    }

    private static void recreateObjectsInFields(ObjectData data, NakedObject object) {
      Object[] fieldContent = data.getFieldContent();
      if (fieldContent != null && fieldContent.length > 0) {
        NakedObjectField[] fields = object.getSpecification().getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isCollection()) {
                if (fieldContent[i] != null) {
                    ObjectData collection = (ObjectData) fieldContent[i];
                    NakedObject[] instances = new NakedObject[collection.getFieldContent().length];
                    for (int j = 0; j < instances.length; j++) {
                        instances[j] = recreateObject(((ObjectData) collection.getFieldContent()[j]));
                    }
                    object.initOneToManyAssociation((OneToManyAssociation) fields[i], instances);
                }
            } else if (fields[i].isValue()) {
                object.initValue((OneToOneAssociation) fields[i], fieldContent[i]);
            } else {
                if (fieldContent[i] != null) {
                    NakedObjectAssociation field = (NakedObjectAssociation) fields[i];
                    NakedObject value = recreateObject(((ObjectData) fieldContent[i]));
                    object.initAssociation(field, value);
                }
            }
        }
      }
    }

    public static void update(ObjectData data, DirtyObjectSet updateNotifier) {
        Oid oid = data.getOid();
        Object[] fieldContent = data.getFieldContent();

        PojoAdapterFactory objectLoader = NakedObjects.getPojoAdapterFactory();
        if(!objectLoader.isIdentityKnown(oid)) {
            // if we don't have an object in use then ignore the data about it.
            return;
        }
        
        NakedObject object;
        object = objectLoader.getAdapterFor(oid);
        
        /*
        String type = data.getType();
        if (oid != null && loadedObjects().isLoaded(oid)) {
            object = loadedObjects().getLoadedObject(oid);
        } else {
            NakedObjectSpecification specification = NakedObjects.getSpecificationLoader().loadSpecification(type);
            object = (NakedObject) specification.acquireInstance();
            if (oid != null) {
                object.setOid(oid);
                loadedObjects().loaded(object);
            }
        }
        
		if (!object.isResolved() && data.isResolved()){
			object.setResolved();
		}
		*/
        
        objectLoader.loading(object, data.isResolved());
		
        NakedObjectField[] fields = object.getSpecification().getFields();
        if (fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isCollection()) {
                    if (fieldContent[i] != null) {
                        ObjectData collection = (ObjectData) fieldContent[i];
                        Object[] elements = collection.getFieldContent();
                        NakedObject[] instances = new NakedObject[elements.length];
                        for (int j = 0; j < instances.length; j++) {
                            NakedObject instance = recreateObject((ObjectData) elements[j]);
                            instances[j] = instance;
                        }
                        object.initOneToManyAssociation((OneToManyAssociation) fields[i], instances);
                    }

                } else if (fields[i].isValue()) {
                    object.initValue((OneToOneAssociation) fields[i], fieldContent[i]);
                } else {
                    if (fieldContent[i] != null) {
                        NakedObject field = recreateObject((ObjectData) fieldContent[i]);
                        object.initAssociation((NakedObjectAssociation) fields[i], field);
                    }
                }
            }
        }
        objectLoader.loaded(object, data.isResolved());
		updateNotifier.addDirty(object);
    }

    public static void throwRemoteException(ExceptionData data) throws ReflectiveActionException {
        throw new NakedObjectsRemoteException(data.getType(), data.getMessage(), data.getStackTrace());
    }
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2005 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */