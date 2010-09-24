package com.googlecode.simpleobjectassembler.utils;

import net.sf.cglib.proxy.Enhancer;

import org.hibernate.proxy.HibernateProxy;

/**
 * Utility for resolving details about classes proxied by cglib
 * 
 * @author robmonie
 * 
 */
public class CglibUtils {

	private static boolean hibernateAvailable = false;

	private CglibUtils() {
		// hidden contructor
	}

	static {
		try {
			Class.forName("org.hibernate.proxy.HibernateProxy");
			hibernateAvailable = true;
		} catch (ClassNotFoundException e) {
			hibernateAvailable = false;
		}
	}

	/**
	 * Attempts to compare the class of two objects. If either objects are
	 * proxed using CGLIB, an attempt will be made to compare on the actual
	 * target proxied classes.
	 * 
	 * @param objectOne
	 * @param objectTwo
	 * @return
	 */
	public static boolean cglibAwareProxiedClassEquals(Object objectOne,
			Object objectTwo) {

		try {
			return getTargetClass(objectOne).equals(getTargetClass(objectTwo));
		} catch (ClassNotFoundException e) {
			return false;
		}

	}

	/**
	 * Attempts to resolve a potentially proxied target class
	 * 
	 * @param object
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> resolveTargetClassIfProxied(Object object)
			throws ClassNotFoundException {
		return getTargetClass(object);
	}

	/**
	 * TODO: I'm sure there's a better way to do this. It's also limited to
	 * checking proxied objects in the context of Hibernate. Not tested in other
	 * scenarios
	 * 
	 * @param object
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Class<?> getTargetClass(Object object)
			throws ClassNotFoundException {
		if (object == null) {
			return null;
		} else if (hibernateAvailable && Enhancer.isEnhanced(object.getClass())) {
			Class<?> clazz = object.getClass();
			if (object instanceof HibernateProxy) {
				clazz = ((HibernateProxy) object).getHibernateLazyInitializer()
						.getImplementation().getClass();

			} else if (clazz.equals(Object.class)
					|| object.getClass().getName().indexOf("$$") >= 0) {
            
				final String className = object.getClass().getName();

				// TODO - this way of figuring out interfaces proxied can
				// possibly be made more robust although unsure of how to
				// get to the specific interface proxied. Would be better to
				// actually get to the target object if possible ??
				clazz = Class.forName(className.substring(0, className.indexOf("$$")));

            if (clazz.equals(HibernateProxy.class)) {
					clazz = ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation().getClass();
				}

			}

			return clazz;

		} else {
			return object.getClass();
		}
	}
}
