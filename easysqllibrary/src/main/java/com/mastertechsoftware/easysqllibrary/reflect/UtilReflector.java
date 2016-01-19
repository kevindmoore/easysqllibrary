package com.mastertechsoftware.easysqllibrary.reflect;


import com.mastertechsoftware.logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
* Author: Kevin Moore
* Descrption: Class to get the fields from another class
* Note: Put a wrapper class around this class to access fields and methods from class in other packages
*/
public class UtilReflector {

	/**
	 * Return the class object associated with this string.
	 *
	 * @return Class
	 */
	public static Class getClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	/**
	 * Return a list of constructors for this class.
	 *
	 * @param mainClass class to get the constructors from
	 * @return Constructor[]
	 */
	public static Constructor[] getConstructors(Object mainClass) {
		return mainClass.getClass().getConstructors();
	}

	/**
	 * Return a list of constructors for this class.
	 *
	 * @param mainClass class to get the constructors from
	 * @return Constructor[]
	 */
	public static Constructor<?>[] getConstructors(Class mainClass) {
		try {
			return mainClass.getConstructors();
		} catch (Exception e) {
			Logger.error("getConstructors for " + mainClass.getName(), e);
		} catch (Error e) {
			Logger.error("getConstructors for " + mainClass.getName(), e);
		}
		return null;
	}

	/**
	 * Return the default constructor (no params)
	 *
	 * @return Constructor
	 */
	public static Constructor getDefaultConstructor(Class mainClass) {
		Constructor<?>[] constructors = getConstructors(mainClass);
		if (constructors == null) {
			return null;
		}
		for (int i = 0; i < constructors.length; i++) {
			Constructor<?> constructor = constructors[i];
			if (constructor.getParameterTypes().length == 0) {
				return constructor;
			}
		}
		return null;
	}

	/**
	 * Create a new instance of a class.
	 *
	 * @param constructor constructor class. Use the getConstructors method above
	 * @param args        arguments for constructor
	 * @return new Object
	 */
	public static Object createInstance(Constructor constructor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		return constructor.newInstance(args);
	}

	/**
	 * Create a new instance of this class with no args
	 */
	public static Object createInstance(Constructor constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		return constructor.newInstance();
	}

	/**
	 * getField - method to return a field object
	 *
	 * @param mainClass class to check for field
	 * @param fieldName field name  to check for
	 * @return field object
	 */
	public static Object getField(Object mainClass, String fieldName) {
		if (mainClass == null) {
			Logger.error("Object is null for field " + fieldName);
			return null;
		}

		Class mainClassObject = mainClass.getClass();
		if (mainClassObject == null) {
			Logger.error("Class object is null for field " + fieldName);
			return null;
		}
		boolean found = false;
		while (!found) {
			try {
				Field fieldObject = mainClassObject.getDeclaredField(fieldName);
				fieldObject.setAccessible(true);
				Object itemObject = fieldObject.get(mainClass);
				return itemObject;
			} catch (NoSuchFieldException nsf) {
				mainClassObject = mainClassObject.getSuperclass();
				if (mainClassObject == null) {
					Logger.error("No Such Field Exception: " + nsf.getMessage() + " for field " + fieldName);
					return null;
				}
			} catch (SecurityException security) {
				Logger.error("Security Exception: " + security.getMessage() + " for class " + mainClassObject.getName() + " & field " + fieldName);
				return null;
			} catch (Exception e) {
				Logger.error("Error in getting field " + fieldName + " for Class " + mainClassObject.getName());
				Logger.error("Unknown Error", e);
				return null;
			}
		}
		return null;
	}

	/**
	 * Return the field associated with the field name
	 */
	public static Field getFieldClass(Object mainClass, String fieldName) {
		if (mainClass == null) {
			Logger.error("Object is null for field " + fieldName);
			return null;
		}

		Class mainClassObject = mainClass.getClass();
		if (mainClassObject == null) {
			Logger.error("Class object is null for field " + fieldName);
			return null;
		}
		boolean found = false;
		while (!found) {
			try {
				Field fieldObject = mainClassObject.getDeclaredField(fieldName);
				fieldObject.setAccessible(true);
				return fieldObject;
			} catch (NoSuchFieldException nsf) {
				mainClassObject = mainClassObject.getSuperclass();
				if (mainClassObject == null) {
					Logger.error("No Such Field Exception: " + nsf.getMessage() + " for field " + fieldName);
					return null;
				}
			} catch (SecurityException security) {
				Logger.error("Security Exception: " + security.getMessage() + " for class " + mainClassObject.getName() + " & field " + fieldName);
				return null;
			} catch (Exception e) {
				Logger.error("Error in getting field " + fieldName + " for Class " + mainClassObject.getName());
				Logger.error("Unknown Error", e);
				return null;
			}
		}
		return null;
	}

	/**
	 * checkField - method to check for the existance of a field
	 *
	 * @param mainClass class to check for field
	 * @param fieldName field name  to check for
	 * @return true if field exists
	 */
	public static boolean checkField(Object mainClass, String fieldName) {
		Object field = getField(mainClass, fieldName);
		if (field == null)
			return false;
		else
			return true;
	}

	/**
	 * getFields - method to return the names of all of the fields
	 *
	 * @param mainClass class to check for field
	 * @return array of fields
	 */
	public static Field[] getFields(Class mainClass) {
		try {
			Field[] fields = mainClass.getDeclaredFields();
			return fields;
		} catch (SecurityException security) {
			Logger.error("Security Exception: " + security.getMessage() + " for class " + mainClass.getName());
			return null;
		} catch (Exception e) {
			Logger.error("Unknown Error", e);
			return null;
		}
	}

	/**
	 * Remove all Transient Fields
	 * @param fields
	 * @return
	 */
	public static Field[] removeTransient(Field[] fields) {
		ArrayList<Field> newFieldList = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			int modifiers = fields[i].getModifiers();
			if (!Modifier.isTransient(modifiers)) {
				newFieldList.add(fields[i]);
			}
		}
		return newFieldList.toArray(new Field[newFieldList.size()]);
	}

	/**
	 * Remove all Transient Fields
	 * @param fields
	 * @return
	 */
	public static ArrayList<Field> removeTransient(ArrayList<Field> fields) {
		ArrayList<Field> newFieldList = new ArrayList<Field>();
		for (int i = 0; i < fields.size(); i++) {
			int modifiers = fields.get(i).getModifiers();
			if (!Modifier.isTransient(modifiers)) {
				newFieldList.add(fields.get(i));
			}
		}
		return newFieldList;
	}

	/**
	 * getAllFields - method to return the names of all of the fields and the fields of the superclasses
	 *
	 * @param mainClass class to check for field
	 * @return array of fields
	 */
	public static ArrayList<Field> getAllFields(Class mainClass) {
		ArrayList<Field> list = new ArrayList<Field>();
		while (mainClass != null) {
			Field[] fields = getFields(mainClass);
			// For Enums, only get the ordinal
			if (mainClass.isEnum()) {
				mainClass = mainClass.getSuperclass();
				fields = getFields(mainClass);
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].getName().equalsIgnoreCase("ordinal")) {
						list.add(fields[i]);
						return list;
					}
				}

			} else {
				for (int i = 0; i < fields.length; i++) {
					if (!fields[i].getType().getName().equalsIgnoreCase(android.os.Parcelable.Creator.class.getName())) {
						list.add(fields[i]);
					}
				}
			}
			mainClass = mainClass.getSuperclass();
		}
		return list;
	}

	/**
	 * getFieldNames - method to return the names of all of the fields
	 *
	 * @param mainClass class to check for field
	 * @return array of field names
	 */
	public static String[] getFieldNames(Class mainClass) {
		try {
			Field[] fields = mainClass.getDeclaredFields();
			String[] fieldNames = new String[fields.length];
			for (int i = 0; i < fields.length; i++) {
				fieldNames[i] = fields[i].getName();
			}
			return fieldNames;
		} catch (SecurityException security) {
			Logger.error("Security Exception: " + security.getMessage() + " for class " + mainClass.getName());
			return null;
		} catch (Exception e) {
			Logger.error("Unknown Error", e);
			return null;
		}
	}

	/**
	 * Get a list of all interfaces for this class and it's super class
	 *
	 * @return List<Class>
	 */
	public static List<Class> getInterfaces(Class mainClass) {
		List<Class> interfaces = new ArrayList<Class>();
		addInterfaces(mainClass, interfaces);
		return interfaces;
	}

	private static void addInterfaces(Class mainClass, List<Class> interfaces) {
		if (isBasicType(mainClass)) {
			return;
		}
		Class[] interfaceClasses = mainClass.getInterfaces();
		for (int i = 0; i < interfaceClasses.length; i++) {
			Class interfaceClass = interfaceClasses[i];
			interfaces.add(interfaceClass);
		}
		Class superclass = mainClass.getSuperclass();
		if (superclass != null) {
			addInterfaces(superclass, interfaces);
		}
	}

	/**
	 * Check that a class implements the given interface
	 */
	public static boolean hasInterface(Class mainClass, Class interfaceClass) {
		if (isBasicType(mainClass)) {
			return false;
		}
		List<Class> interfaces = UtilReflector.getInterfaces(mainClass);
		for (Class andInterface : interfaces) {
			if (andInterface.equals(interfaceClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check to see if this class is a basic Java class
	 */
	public static boolean isBasicType(Class mainClass) {
		if (mainClass == int.class || mainClass == Integer.class) {
			return true;
		} else if (mainClass == float.class || mainClass == Float.class) {
			return true;
		} else if (mainClass == boolean.class || mainClass == Boolean.class) {
			return true;
		} else if (mainClass == long.class || mainClass == Long.class) {
			return true;
		} else if (mainClass == double.class || mainClass == Double.class) {
			return true;
		} else if (mainClass == Number.class) {
			return true;
		} else if (mainClass == String.class || mainClass == Character.class) {
			return true;
		} else if (mainClass == Object.class) {
			return true;
		}
		return false;
	}

	/**
	 * checkMethod - method to check for the existance of a method
	 *
	 * @param mainClass  class to check for method
	 * @param methodName method name  to check for
	 * @param params     array of classes describing parameters
	 * @return true if method exists
	 */
	public static boolean checkMethod(Object mainClass, String methodName, Class[] params) {
		Method aMethod = getMethod(mainClass, methodName, params);
		if (aMethod == null)
			return false;
		else
			return true;
	}

	/**
	 * Get the static method for the given class
	 * @param methodClass
	 * @param methodName
	 * @param params
	 * @return Method
	 */
	public static Method getStaticMethod(Class methodClass, String methodName, Class[] params) {
		try {
			Method method = methodClass.getMethod(methodName, params);
			return method;
		} catch (NoSuchMethodException nsm) {
			Logger.error("No Such Method Exception: " + nsm.getMessage() + " for method " + methodName);
		}
		return null;
	}

	/**
	 * Execute the the method for the given class
	 * @param methodClass
	 * @param methodName
	 * @param params
	 * @param args
	 * @return result
	 */
	public static Object executeStaticMethod(String methodClass, String methodName, Class[] params, Object[] args) {
		Class classToRun = null;
		try {
			classToRun = Class.forName(methodClass);
		} catch (ClassNotFoundException e) {
			Logger.error("executeStaticMethod: Class not found for  " + methodClass);
			return null;
		}
		return executeStaticMethod(classToRun, methodName, params, args);
	}
	/**
	 * Execute the the method for the given class
	 * @param methodClass
	 * @param methodName
	 * @param params
	 * @param args
	 * @return result
	 */
	public static Object executeStaticMethod(Class methodClass, String methodName, Class[] params, Object[] args) {
		Method methodObject = getMethod(methodClass, methodName, params);

		if (methodObject == null)
		{
			Logger.error("Error: Null method object in executing method " + methodName);
			return null;
		}
		try
		{
			methodObject.setAccessible(true);
			Object returnObject = methodObject.invoke(methodClass, args);
			return returnObject;
		}
		catch (IllegalAccessException iae)
		{
			Logger.error("Illegal Access Exception: " + iae.getMessage() + " for method " + methodObject.getName());
			return null;
		}
		catch (IllegalArgumentException iArge)
		{
			Logger.error("Illegal Argument Exception: " + iArge.getMessage() + " for method " + methodObject.getName());
			return null;
		}
		catch (InvocationTargetException ite)
		{
			Logger.error("Invocation Target Exception: " + ite.getMessage() + " for method " + methodObject.getName());
			return null;
		}
		catch (Exception e)
		{
			Logger.error("Unknown Error", e);
			return null;
		}
	}

	/**
	* getMethod - return a method object
	* @param mainClass class to check for method
	* @param methodName method name  to check for
    * @param params array of classes describing parameters
	* @return Method object
	*/
	public static Method getMethod(Object mainClass, String methodName, Class[] params)
	{
		if (mainClass == null)
		{
			Logger.error("Error: Null main class in getting method " + methodName);
			return null;
		}
		Class mainClassObject = null;
        if (mainClass instanceof Class)
            mainClassObject = (Class)mainClass;
        else
            mainClassObject = mainClass.getClass();
		if (mainClassObject == null)
		{
			Logger.error("Error: Null main class object in getting method " + methodName);
			return null;
		}
		boolean found = false;
		while (!found)
		{
	    	try
	    	{
			   	Method methodObject = mainClassObject.getDeclaredMethod(methodName, params);
				found = true;
				return methodObject;
        	}
	    	catch (NoSuchMethodException nsm)
	    	{
	    		mainClassObject = mainClassObject.getSuperclass();
	    		if (mainClassObject == null)
				{
		    		Logger.error("No Such Method Exception: " + nsm.getMessage() + " for method " + methodName);
	    			return null;
	    		}
	    	}
	    	catch (SecurityException security)
	    	{
	    		Logger.error("Security Exception: " + security.getMessage() + " for class " + mainClassObject.getName());
	    		return null;
	    	}
	    	catch (Exception e)
	    	{
	    		Logger.error("Unknown Error", e);
	    		return null;
	    	}
	    }
	    return null;
	}

    /**
    * return an array of method object
    * @param mainClass class to check for method
    * @return Method[]
    */
    public static Method[] getMethods(Object mainClass)
    {
        if (mainClass == null)
        {
            Logger.error("Error: Null main class in getting methods ");
            return null;
        }
        Class mainClassObject = null;
        if (mainClass instanceof Class)
            mainClassObject = (Class)mainClass;
        else
            mainClassObject = mainClass.getClass();
        if (mainClassObject == null)
        {
            Logger.error("Error: Null main class object in getting methods ");
            return null;
        }
        boolean found = false;
        while (!found)
        {
            try
            {
                Method[] methodObjects = mainClassObject.getDeclaredMethods();
                found = true;
                return methodObjects;
            }
            catch (SecurityException security)
            {
                Logger.error("Security Exception: " + security.getMessage() + " for class " + mainClassObject.getName());
                return null;
            }
            catch (Exception e)
            {
                Logger.error("Unknown Error", e);
                return null;
            }
        }
        return null;
        
    }

	/**
	* executeMethod - return a method object
	* @param mainClass class to check for method
	* @param methodName method name  to check for
    * @param params array of parameter classes
    * @param args array of actual arguments
	* @return Method object
	*/
	public static Object executeMethod(Object mainClass, String methodName, Class[] params, Object[] args)
	{
		if (mainClass == null)
		{
			Logger.error("Error: Null main class in executing method " + methodName);
			return null;
		}
		Method methodObject = getMethod(mainClass, methodName, params);

		if (methodObject == null)
		{
			Logger.error("Error: Null method object in executing method " + methodName);
			return null;
		}
        return executeMethod(mainClass, args, methodObject);
    }

    public static Object executeMethod(Object mainClass, Object[] args, Method methodObject) {
        try
        {
            methodObject.setAccessible(true);
            Object returnObject = methodObject.invoke(mainClass, args);
            return returnObject;
		}
        catch (IllegalAccessException iae)
        {
            Logger.error("Illegal Access Exception: " + iae.getMessage() + " for method " + methodObject.getName());
            return null;
        }
        catch (IllegalArgumentException iArge)
        {
            Logger.error("Illegal Arguement Exception: " + iArge.getMessage() + " for method " + methodObject.getName());
            return null;
        }
        catch (InvocationTargetException ite)
        {
            Logger.error("Invocation Target Exception: " + ite.getMessage() + " for method " + methodObject.getName());
            return null;
        }
        catch (Exception e)
        {
            Logger.error("Unknown Error", e);
            return null;
        }
    }

    /**
	* executeMethod - return a method object
	* @param readMethod class to check for method
	* @param classInstance method name  to check for
	* @return Method object
	*/
	public static Object getReadMethodObject(Method readMethod, Object classInstance)
	{
		if (readMethod == null)
		{
	    	Logger.error("Read Method null in getReadMethodObject");
			return null;
		}
		Object returnValue = null;
		try
		{
			Class[] params = readMethod.getParameterTypes();
			Object[] args = new Object[params.length];
			for (int j=0; j < params.length; j++)
			{
				Logger.error("Parameter Name: " + params[j].getName());
				args[j] = params[j].newInstance();
			}

			if (Modifier.isStatic(readMethod.getModifiers()))
			{
				if (params.length == 0)
					returnValue = readMethod.invoke(null, (Object[])null);
				else
					returnValue = readMethod.invoke(null, args);
			}
			else
			{
				if (params.length == 0)
					returnValue = readMethod.invoke(classInstance, (Object[])null);
				else
					returnValue = readMethod.invoke(classInstance, args);
			}
		}
	    catch (Exception e)
	    {
	    	Logger.error("Unknown Error", e);
	    }
	    return returnValue;
	}

}
