package javax.servlet.http;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;
import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

class MainServletTest {

	private static Method METHOD_TEST, METHOD_TO_INT_ARRAY, METHOD_COLLECT, METHOD_TEST_AND_ACCEPT, METHOD_CAST,
			METHOD_TEST_AND_GET = null;

	@BeforeSuite
	void beforeSuite() throws NoSuchMethodException {
		//
		final Class<?> clz = MainServlet.class;
		//
		(METHOD_TEST = clz.getDeclaredMethod("test", Predicate.class, Object.class)).setAccessible(true);
		//
		(METHOD_TO_INT_ARRAY = clz.getDeclaredMethod("toIntArray", String.class)).setAccessible(true);
		//
		(METHOD_COLLECT = clz.getDeclaredMethod("collect", Stream.class, Collector.class)).setAccessible(true);
		//
		(METHOD_COLLECT = clz.getDeclaredMethod("collect", Stream.class, Collector.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_ACCEPT = clz.getDeclaredMethod("testAndAccept", Predicate.class, Object.class,
				FailableConsumer.class)).setAccessible(true);
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_GET = clz.getDeclaredMethod("testAndGet", Boolean.TYPE, Supplier.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean test, isInstalled;

		private String servletPath;

		private Map<Object, Object> parameters = null;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			if (Objects.equals(getReturnType(method), Void.TYPE)) {
				//
				return null;
				//
			} // if
				//
			final String name = MainServletTest.getName(method);
			//
			if (proxy instanceof ServletResponse) {
				//
				if (Objects.equals(name, "getOutputStream")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof ServletRequest && Objects.equals(name, "getParameter") && args != null
					&& args.length > 0) {
				//
				return get(parameters = ObjectUtils.getIfNull(parameters, LinkedHashMap::new), ArrayUtils.get(args, 0));
				//
			} // if
				//
			if (proxy instanceof Member && Objects.equals(name, "getName")) {
				//
				return null;
				//
			} else if (proxy instanceof Function && Objects.equals(name, "apply")) {
				//
				return null;
				//
			} else if (proxy instanceof Predicate && Objects.equals(name, "test")) {
				//
				return test;
				//
			} else if (proxy instanceof Collection && Objects.equals(name, "stream")) {
				//
				return null;
				//
			} else if (proxy instanceof Stream) {
				//
				if (contains(Arrays.asList("collect", "filter"), name)) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof HttpServletRequest && Objects.equals(name, "getServletPath")) {
				//
				return servletPath;
				//
			} else if (Objects.equals(method != null ? getName(method.getDeclaringClass()) : null,
					"javax.servlet.http.MainServlet$Jna")) {
				//
				if (Objects.equals(getReturnType(method), String.class)) {
					//
					return null;
					//
				} else if (Objects.equals(name, "isInstalled")) {
					//
					return isInstalled;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(name);
			//
		}

		private static String getName(final Class<?> instance) {
			return instance != null ? instance.getName() : null;
		}

		private static <V> V get(final Map<?, V> instance, final Object key) {
			return instance != null ? instance.get(key) : null;
		}

	}

	private static class MH implements MethodHandler {

		@Override
		public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args)
				throws Throwable {
			//
			if (Objects.equals(getReturnType(thisMethod), Void.TYPE)) {
				//
				return null;
				//
			} // if
				//
			throw new Throwable(getName(thisMethod));
			//
		}

	}

	private MainServlet instance = null;

	private IH ih = null;

	@BeforeMethod
	void beforeMethod() throws IllegalAccessException, InvocationTargetException {
		//
		instance = cast(MainServlet.class, Narcissus.allocateInstance(MainServlet.class));
		//
		ih = new IH();
		//
	}

	@Test
	public void testCast() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(cast(Object.class, null));
		//
	}

	private static <T> T cast(final Class<T> clz, final Object value)
			throws IllegalAccessException, InvocationTargetException {
		return (T) invoke(METHOD_CAST, null, clz, value);
	}

	private static Object invoke(final Method method, final Object instance, final Object... args)
			throws IllegalAccessException, InvocationTargetException {
		return method != null ? method.invoke(instance, args) : null;
	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = MainServlet.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Boolean.TYPE)) {
					//
					add(collection, Boolean.FALSE);
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = Narcissus.invokeStaticMethod(m, os);
				//
			} else {
				//
				result = Narcissus.invokeMethod(instance = ObjectUtils.getIfNull(instance, MainServlet::new), m, os);
				//
			} // if
				//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), getReturnType(m))) {
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

	private static Class<?> getReturnType(final Method instance) {
		return instance != null ? instance.getReturnType() : null;
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = MainServlet.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Object result = null;
		//
		String toString, name = null;
		//
		Object[] os = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Collection<Object> collection = null;
		//
		if (ih != null) {
			//
			ih.test = Boolean.TRUE;
			//
		} // if
			//
		ProxyFactory proxyFactory = null;
		//
		Object object = null;
		//
		MH mh = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Class.class)) {
					//
					add(collection, Object.class);
					//
				} else if (Objects.equals(parameterType, Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Boolean.TYPE)) {
					//
					add(collection, Boolean.FALSE);
					//
				} else if (Objects.equals(parameterType, Executable.class)) {
					//
					add(collection, Object.class.getDeclaredMethod("toString"));
					//
				} else if (parameterType != null && parameterType.isArray()) {
					//
					add(collection, Array.newInstance(parameterType.getComponentType(), 0));
					//
				} else if (parameterType != null && Modifier.isInterface(parameterType.getModifiers())) {
					//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else if (parameterType != null && Modifier.isAbstract(parameterType.getModifiers())) {
					//
					(proxyFactory = new ProxyFactory()).setSuperclass(parameterType);
					//
					if ((object = newInstance(
							getDeclaredConstructor(proxyFactory.createClass()))) instanceof ProxyObject) {
						//
						((ProxyObject) object).setHandler(mh = ObjectUtils.getIfNull(mh, MH::new));
						//
					} // if
						//
					add(collection, object);
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(ArrayUtils.get(parameterTypes, j)));
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = Narcissus.invokeStaticMethod(m, os);
				//
			} else {
				//
				result = Narcissus.invokeMethod(instance = ObjectUtils.getIfNull(instance, MainServlet::new), m, os);
				//
			} // if
				//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), getReturnType(m))
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "getClass"),
							Arrays.equals(parameterTypes, new Object[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getName"),
							Arrays.equals(parameterTypes, new Object[] { Class.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "cast"),
							Arrays.equals(parameterTypes, new Object[] { Class.class, Object.class }))

			) {
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

	private static <T> Constructor<T> getDeclaredConstructor(final Class<T> instance, final Class<?>... parameterTypes)
			throws NoSuchMethodException {
		return instance != null ? instance.getDeclaredConstructor(parameterTypes) : null;
	}

	private static <T> T newInstance(final Constructor<T> instance, final Object... args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return instance != null ? instance.newInstance(args) : null;
	}

	private static boolean contains(final Collection<?> items, final Object item) {
		return items != null && items.contains(item);
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	@Test
	public void testDoGet() throws ClassNotFoundException, ServletException, IOException {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Iterable<Method> ms = Arrays
				.stream(Class.forName("javax.servlet.http.MainServlet$Jna").getDeclaredMethods())
				.filter(m -> m != null && m.getParameterCount() == 0).collect(Collectors.toList());
		//
		if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null && !IterableUtils.isEmpty(ms)) {
			//
			ih.servletPath = "/" + getName(IterableUtils.get(ms, 0));
			//
		} // if
			//
		final HttpServletRequest httpServletRequest = Reflection.newProxy(HttpServletRequest.class, ih);
		//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/getVoiceIds";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/getVoiceAttribute";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/getVoiceAttributes";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/getVoiceAttributes";
			//
			if ((ih.parameters = ObjectUtils.getIfNull(ih.parameters, LinkedHashMap::new)) != null) {
				//
				ih.parameters.put("id",
						"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Speech Server\\v11.0\\Voices\\Tokens\\TTS_MS_ja-JP_Haruka_11.0");
				//
			} // if
				//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/.wav";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
		if (ih != null) {
			//
			ih.servletPath = "/ .wav";
			//
		} // if
			//
		instance.doGet(httpServletRequest, null);
		//
	}

	@Test
	public void testTest() throws IllegalAccessException, InvocationTargetException {
		//
		if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
			//
			ih.test = Boolean.FALSE;
			//
		} // if
			//
		Assert.assertEquals(invoke(METHOD_TEST, null, Reflection.newProxy(Predicate.class, ih), null),
				ih != null ? ih.test : null);
		//
	}

	@Test
	public void testToIntArray() throws IllegalAccessException, InvocationTargetException {
		//
		final char c = ' ';
		//
		Assert.assertEquals(invoke(METHOD_TO_INT_ARRAY, null, new String(new char[] { c })), new int[] { c });
		//
	}

	@Test
	public void testCollect() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_COLLECT, null, Stream.empty(), null));
		//
		Assert.assertNull(invoke(METHOD_COLLECT, null,
				Reflection.newProxy(Stream.class, ih = ObjectUtils.getIfNull(ih, IH::new)), null));
		//
	}

	@Test
	public void testTestAndAccept() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_TEST_AND_ACCEPT, null, Predicates.alwaysTrue(), null, null));
		//
	}

	@Test
	public void testTestAndGet() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_TEST_AND_GET, null, Boolean.TRUE, null));
		//
		final Object object = new Object();
		//
		Assert.assertSame(invoke(METHOD_TEST_AND_GET, null, Boolean.TRUE, Suppliers.ofInstance(object)), object);
		//
	}

	@Test
	public void testJna() throws ClassNotFoundException {
		//
		final Class<?> clz = Class.forName("javax.servlet.http.MainServlet$Jna");
		//
		final Method[] ms = clz != null ? clz.getDeclaredMethods() : null;
		//
		Method m = null;
		//
		Object result = null;
		//
		String toString, name = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object jna = null;
		//
		if (ih != null) {
			//
			ih.isInstalled = Boolean.FALSE;
			//
		} // if
			//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = Narcissus.invokeStaticMethod(m, os);
				//
			} else {
				//
				result = Narcissus.invokeMethod(jna = ObjectUtils.getIfNull(jna, () -> Reflection.newProxy(clz, ih)), m,
						os);
				//
			} // if
				//
			if (Objects.equals(getReturnType(m), Boolean.TYPE)) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
			if (Boolean.logicalAnd(Objects.equals(name = getName(m), "writeVoiceToFile"),
					Arrays.equals(parameterTypes,
							new Class<?>[] { int[].class, Integer.TYPE, String.class, Integer.TYPE, Integer.TYPE,
									int[].class, Integer.TYPE }))
					|| Boolean.logicalAnd(Objects.equals(name, "speak"), Arrays.equals(parameterTypes,
							new Class<?>[] { int[].class, Integer.TYPE, String.class, Integer.TYPE, Integer.TYPE }))) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (parameterType != null && parameterType.isInterface()) {
					//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else if (parameterType != null && parameterType.isArray()) {
					//
					add(collection, Array.newInstance(parameterType, 0));
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			System.err.println(toString);
			//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = Narcissus.invokeStaticMethod(m, os);
				//
			} else {
				//
				result = Narcissus.invokeMethod(jna = ObjectUtils.getIfNull(jna, () -> Reflection.newProxy(clz, ih)), m,
						os);
				//
			} // if
				//
			if (Objects.equals(getReturnType(m), Boolean.TYPE)) {
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

}