package javax.servlet.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

import io.github.toolfactory.narcissus.Narcissus;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = -5176135849319893425L;

	private static final String VALUE = "value";

	private static final String APPLICATION_JSON = "application/json";

	private interface Jna extends Library {

		public boolean isInstalled();

		public void speak(final int[] text, final int length, final String voiceId, final int rate, final int volume);

		public void writeVoiceToFile(final int[] text, final int textLength, final String voiceId, final int rate,
				final int volume, final int[] fileName, final int fileNameLength);

		public String getVoiceIds(final String requiredAttributes, final String optionalAttributes);

		public String getVoiceAttribute(final String voiceId, final String attribute);

		public String getProviderName();

		public String getProviderVersion();

		public String getProviderPlatform();

		static String getVoiceIds(final Jna instance, final String requiredAttributes,
				final String optionalAttributes) {
			return instance != null ? instance.getVoiceIds(requiredAttributes, optionalAttributes) : null;
		}

		static String getVoiceAttribute(final Jna instance, final String voiceId, final String attribute) {
			return instance != null ? instance.getVoiceAttribute(voiceId, attribute) : null;
		}

		static void writeVoiceToFile(final Jna instance, final int[] text, final int textLength, final String voiceId,
				final int rate, final int volume, final int[] fileName, final int fileNameLength) {
			if (instance != null) {
				instance.writeVoiceToFile(text, textLength, voiceId, rate, volume, fileName, fileNameLength);
			}
		}

	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		//
		final String servletPath = getServletPath(request);
		//
		final Iterable<Method> ms = collect(filter(Arrays.stream(Jna.class.getDeclaredMethods()),
				m -> Boolean.logicalAnd(Objects.equals("/" + getName(m), servletPath), getParameterCount(m) == 0)),
				Collectors.toList());
		//
		final boolean isWindows = Objects.equals(getName(getClass(FileSystems.getDefault())),
				"sun.nio.fs.WindowsFileSystem");
		//
		final Jna jna = testAndGet(isWindows, () -> Native.load("MicrosoftSpeechApi.dll", Jna.class));
		//
		if (IterableUtils.size(ms) == 1 && jna != null) {
			//
			final Method method = IterableUtils.get(ms, 0);
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				write(os, getBytes(Objects.toString(method != null ? method.invoke(jna) : null)));
				//
			} catch (final IllegalAccessException | InvocationTargetException e) {
				//
				throw new ServletException(e);
				//
			} // try
				//
			return;
			//
		} // if
			//
		if (Objects.equals(servletPath, "/getVoiceIds")) {
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				setContentType(response, APPLICATION_JSON);
				//
				write(os,
						new ObjectMapper().writeValueAsBytes(StringUtils.split(Jna.getVoiceIds(jna, null, null), ",")));
				//
			} // try
				//
		} else if (Objects.equals(servletPath, "/getVoiceAttribute")) {
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				setContentType(response, APPLICATION_JSON);
				//
				write(os, new ObjectMapper().writeValueAsBytes(StringUtils.split(
						Jna.getVoiceAttribute(jna, getParameter(request, "id"), getParameter(request, "attribute")),
						",")));
				//
			} // try
				//
		} else if (Objects.equals(servletPath, "/getVoiceAttributes")) {
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				setContentType(response, APPLICATION_JSON);
				//
				final String id = getParameter(request, "id");
				//
				final String[] ss = StringUtils.split(id, '\\');
				//
				String key = StringUtils.substringAfter(id, '\\');
				//
				boolean registryKeyExists = false;
				//
				HKEY hkey = null;
				//
				if (length(ss) > 0 && (hkey = testAndApply(x -> IterableUtils.size(x) == 1,
						collect(filter(stream(FieldUtils.getAllFieldsList(WinReg.class)),
								f -> Boolean.logicalAnd(Objects.equals(getType(f), HKEY.class),
										Objects.equals(getName(f), ArrayUtils.get(ss, 0))))
								.map(f -> cast(HKEY.class, Narcissus.getStaticField(f))), Collectors.toList()),
						x -> IterableUtils.get(x, 0), null)) != null) {
					//
					registryKeyExists = testAndTest(isWindows, Advapi32Util::registryKeyExists, hkey, key);
					//
				} // if
					//
				if (Boolean.logicalAnd(hkey != null, registryKeyExists)) {
					//
					final String[] keys = Advapi32Util.registryGetKeys(hkey, key);
					//
					if (length(keys) == 1 && Objects.equals(ArrayUtils.get(keys, 0), "Attributes") && Advapi32Util
							.registryKeyExists(hkey, key = String.join("\\", key, ArrayUtils.get(keys, 0)))) {
						//
						write(os, new ObjectMapper().writeValueAsBytes(Advapi32Util.registryGetValues(hkey, key)));
						//
					} // if
						//
				} // if
					//
			} // try
				//
		} else if (startsWith(servletPath, "/") && endsWith(servletPath, ".wav")
				&& StringUtils.length(servletPath) > 5) {
			//
			File file = null;
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				int[] ints = toIntArray(StringUtils.substring(servletPath, 1, StringUtils.length(servletPath) - 4));
				//
				final String absolutePath = getAbsolutePath(file = File
						.createTempFile(RandomStringUtils.secureStrong().nextAlphabetic(3), null, new File(".")));
				//
				if (ints != null) {
					//
					Jna.writeVoiceToFile(jna, ints, length(ints), getParameter(request, "voiceId")
					//
							, NumberUtils.toInt(getParameter(request, "rate"), 0) // rate
							, NumberUtils.toInt(getParameter(request, "volume"), 100) // volume
							//
							, ints = toIntArray(absolutePath), length(ints));
					//
				} // if
					//
				final byte[] bs = FileUtils.readFileToByteArray(file);
				//
				setContentType(response, getMimeType(getContentType(new ContentInfoUtil().findMatch(bs))));
				//
				setContentLength(response, length(bs));
				//
				write(os, bs);
				//
			} finally {
				//
				testAndAccept(Objects::nonNull, file, FileUtils::delete);
				//
			} // try
				//
		} // if
			//
	}

	private static boolean startsWith(final String a, final String b) {
		//
		if (a == null || b == null) {
			//
			return false;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				collect(filter(stream(FieldUtils.getAllFieldsList(getClass(a))),
						f -> Objects.equals(getName(f), VALUE)), Collectors.toList()),
				x -> IterableUtils.get(x, 0), null);
		//
		return value == null
				|| Boolean.logicalAnd(Narcissus.getField(a, value) != null, Narcissus.getField(b, value) != null)
						&& a.startsWith(b);
		//
	}

	private static boolean endsWith(final String a, final String b) {
		//
		if (a == null) {
			//
			return false;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				collect(filter(stream(FieldUtils.getAllFieldsList(getClass(a))),
						f -> Objects.equals(getName(f), VALUE)), Collectors.toList()),
				x -> IterableUtils.get(x, 0), null);
		//
		return value == null
				|| Boolean.logicalAnd(Narcissus.getField(a, value) != null, Narcissus.getField(b, value) != null)
						&& a.endsWith(b);
		//
	}

	private static <T, U> boolean testAndTest(final boolean condition, final BiPredicate<T, U> predicate, final T t,
			final U u) {
		return condition && predicate != null && predicate.test(t, u);
	}

	private static Class<?> getType(final Field instance) {
		return instance != null ? instance.getType() : null;
	}

	private static <T> T testAndGet(final boolean condition, final Supplier<T> supplier) {
		return condition && supplier != null ? supplier.get() : null;
	}

	private static <T> T cast(final Class<T> clz, final Object value) {
		return clz != null && clz.isInstance(value) ? clz.cast(value) : null;
	}

	private static int getParameterCount(final Executable instance) {
		return instance != null ? instance.getParameterCount() : 0;
	}

	private static <T, E extends Throwable> void testAndAccept(final Predicate<T> predicate, final T value,
			final FailableConsumer<T, E> consumer) throws E {
		if (test(predicate, value) && consumer != null) {
			consumer.accept(value);
		}
	}

	private static String getServletPath(final HttpServletRequest instance) {
		return instance != null ? instance.getServletPath() : null;
	}

	private static void setContentLength(final ServletResponse instance, final int len) {
		if (instance != null) {
			instance.setContentLength(len);
		}
	}

	private static void setContentType(final ServletResponse instance, final String type) {
		if (instance != null) {
			instance.setContentType(type);
		}
	}

	private static String getParameter(final ServletRequest instance, final String parameter) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Class<?> clz = getClass(instance);
		//
		if (clz != null && Proxy.isProxyClass(clz)) {
			//
			final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
					collect(filter(stream(FieldUtils.getAllFieldsList(getClass(parameter))),
							f -> Objects.equals(getName(f), VALUE)), Collectors.toList()),
					x -> IterableUtils.get(x, 0), null);
			//
			if (value != null && Narcissus.getField(parameter, value) == null) {
				//
				return null;
				//
			} // if
				//
		} // if
			//
		return instance.getParameter(parameter);
		//
	}

	private static ContentType getContentType(final ContentInfo instance) {
		return instance != null ? instance.getContentType() : null;
	}

	private static String getMimeType(final ContentType instance) {
		return instance != null ? instance.getMimeType() : null;
	}

	private static ServletOutputStream getOutputStream(final ServletResponse instance) throws IOException {
		return instance != null ? instance.getOutputStream() : null;
	}

	private static void write(final OutputStream instance, final byte[] bs) throws IOException {
		if (instance != null) {
			instance.write(bs);
		}
	}

	private static byte[] getBytes(final String instance) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				collect(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), VALUE)), Collectors.toList()),
				x -> IterableUtils.get(x, 0), null);
		//
		return value == null || Narcissus.getField(instance, value) != null ? instance.getBytes() : null;
		//
	}

	private static <T> boolean test(final Predicate<T> predicate, final T value) {
		return predicate != null && predicate.test(value);
	}

	private static String getAbsolutePath(final File instance) {
		return instance != null && instance.getPath() != null ? instance.getAbsolutePath() : null;
	}

	private static int length(final byte[] instance) {
		return instance != null ? instance.length : 0;
	}

	private static int length(final int[] instance) {
		return instance != null ? instance.length : 0;
	}

	private static int length(final Object[] instance) {
		return instance != null ? instance.length : 0;
	}

	private static int[] toIntArray(final String text) {
		//
		if (text == null) {
			//
			return null;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				collect(filter(stream(FieldUtils.getAllFieldsList(getClass(text))),
						f -> Objects.equals(getName(f), VALUE)), Collectors.toList()),
				x -> IterableUtils.get(x, 0), null);
		//
		final char[] cs = !isTestMode() || value == null || Narcissus.getField(text, value) != null ? text.toCharArray()
				: null;
		//
		final int[] ints = cs != null ? new int[cs.length] : null;
		//
		for (int i = 0; cs != null && ints != null && i < cs.length; i++) {
			//
			ints[i] = cs[i];
			//
		} // for
			//
		return ints;
		//
	}

	private static boolean isTestMode() {
		try {
			return Class.forName("org.testng.annotations.Test") != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static <T, R> R testAndApply(final Predicate<T> predicate, final T t, final Function<T, R> functionTrue,
			final Function<T, R> functionFalse) {
		return test(predicate, t) ? apply(functionTrue, t) : apply(functionFalse, t);
	}

	private static <T, R> R apply(final Function<T, R> instance, final T t) {
		return instance != null ? instance.apply(t) : null;
	}

	private static <T, R, A> R collect(final Stream<T> instance, final Collector<? super T, A, R> collector) {
		//
		return instance != null && (collector != null || Proxy.isProxyClass(getClass(instance)))
				? instance.collect(collector)
				: null;
		//
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

}