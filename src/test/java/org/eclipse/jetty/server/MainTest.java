package org.eclipse.jetty.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.github.toolfactory.narcissus.Narcissus;

class MainTest {

	private static Method METHOD_TO_MAP = null;

	@BeforeSuite
	void beforeSuite() throws NoSuchMethodException {
		//
		(METHOD_TO_MAP = Main.class.getDeclaredMethod("toMap", String[].class)).setAccessible(true);
		//
	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = Main.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()) {
				//
				continue;
				//
			} // if
				//
			toString = Objects.toString(m);
			//
			result = Narcissus.invokeStaticMethod(m, toArray(Collections.nCopies(m.getParameterCount(), null)));
			//
			if (contains(Arrays.asList(Boolean.TYPE, Integer.TYPE), m.getReturnType())) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static boolean contains(final Collection<?> items, final Object item) {
		return items != null && items.contains(item);
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	public void testToMap() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_TO_MAP, null, (Object) new String[] { "=" }),
				Collections.singletonMap("", ""));
		//
		Assert.assertEquals(invoke(METHOD_TO_MAP, null, (Object) new String[] { " =" }),
				Collections.singletonMap(" ", ""));
		//
		Assert.assertEquals(invoke(METHOD_TO_MAP, null, (Object) new String[] { "= " }),
				Collections.singletonMap("", " "));
		//
		Assert.assertEquals(invoke(METHOD_TO_MAP, null, (Object) new String[] { " = " }),
				Collections.singletonMap(" ", " "));
		//
		Assert.assertEquals(invoke(METHOD_TO_MAP, null, (Object) new String[] { "== " }),
				Collections.singletonMap("", "= "));
		//
	}

	private static Object invoke(final Method method, final Object instance, final Object... args)
			throws IllegalAccessException, InvocationTargetException {
		return method != null ? method.invoke(instance, args) : null;
	}

}