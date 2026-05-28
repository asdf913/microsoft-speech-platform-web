package javax.servlet.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import com.sun.jna.Library;
import com.sun.jna.Native;

import io.github.toolfactory.narcissus.Narcissus;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = -5176135849319893425L;

	private interface Jna extends Library {

		Jna INSTANCE = cast(Jna.class, Native.load("MicrosoftSpeechApi.dll", Jna.class));

		public boolean isInstalled();

		public void speak(final int[] text, final int length, final String voiceId, final int rate, final int volume);

		public void writeVoiceToFile(final int[] text, final int textLength, final String voiceId, final int rate,
				final int volume, final int[] fileName, final int fileNameLength);

		public String getVoiceIds(final String requiredAttributes, final String optionalAttributes);

		public String getVoiceAttribute(final String voiceId, final String attribute);

		public String getProviderName();

		public String getProviderVersion();

		public String getProviderPlatform();

	}

	private static <T> T cast(final Class<T> clz, final Object value) {
		return clz != null && clz.isInstance(value) ? clz.cast(value) : null;
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		//
		final String servletPath = request != null ? request.getServletPath() : null;
		//
		final Iterable<Method> ms = collect(
				filter(Arrays.stream(Jna.class.getDeclaredMethods()),
						m -> m != null && Objects.equals("/" + getName(m), servletPath) && m.getParameterCount() == 0),
				Collectors.toList());
		//
		final Jna jna = Jna.INSTANCE;
		//
		final boolean isWindows = Objects.equals(getName(getClass(FileSystems.getDefault())),
				"sun.nio.fs.WindowsFileSystem");
		//
		if (IterableUtils.size(ms) == 1 && jna != null && isWindows) {
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
		if (Objects.equals(servletPath, "/getVoiceIds") && jna != null && isWindows) {
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				write(os, getBytes(jna.getVoiceIds(null, null)));
				//
			} // try
				//
			return;
			//
		} else if (Objects.equals(servletPath, "/wav") && jna != null && isWindows) {
			//
			File file = null;
			//
			try (final OutputStream os = getOutputStream(response)) {
				//
				final String text = getParameter(request, "text");
				//
				int[] ints = toIntArray(text);
				//
				final String absolutePath = getAbsolutePath(
						file = File.createTempFile(RandomStringUtils.secureStrong().nextAlphabetic(3), null));
				//
				if (ints != null) {
					//
					jna.writeVoiceToFile(ints, length(ints), getParameter(request, "voiceId"), 0, 100,
							ints = toIntArray(absolutePath), length(ints));
					//
				} // if
					//
				final byte[] bs = FileUtils.readFileToByteArray(file);
				//
				setContentType(response, getMimeType(getContentType(new ContentInfoUtil().findMatch(bs))));
				//
				setContentLength(response, bs != null ? bs.length : 0);
				//
				write(os, bs);
				//
			} finally {
				//
				FileUtils.delete(file);
				//
			} // try
				//
			return;
			//
		} // if
			//
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
		return instance != null ? instance.getParameter(parameter) : null;
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
						f -> Objects.equals(getName(f), "value")), Collectors.toList()),
				x -> IterableUtils.get(x, 0), null);
		//
		testAncAccept(x -> !isAccessible(x), value, x -> setAccessible(x, true));
		//
		return value == null || Narcissus.getField(instance, value) != null ? instance.getBytes() : null;
		//
	}

	private static void setAccessible(final AccessibleObject instance, final boolean flag) {
		if (instance != null) {
			instance.setAccessible(flag);
		}
	}

	private static boolean isAccessible(final AccessibleObject instance) {
		return instance != null && instance.isAccessible();
	}

	private static <T> void testAncAccept(final Predicate<T> predicate, final T value, final Consumer<T> consumer) {
		if (test(predicate, value)) {
			accept(consumer, value);
		}
	}

	private static <T> void accept(final Consumer<T> consumer, final T value) {
		if (consumer != null) {
			consumer.accept(value);
		}
	}

	private static <T> boolean test(final Predicate<T> predicate, final T value) {
		return predicate != null && predicate.test(value);
	}

	private static String getAbsolutePath(final File instance) {
		return instance != null && instance.getPath() != null ? instance.getAbsolutePath() : null;
	}

	private static int length(final int[] instance) {
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
						f -> Objects.equals(getName(f), "value")), Collectors.toList()),
				x -> IterableUtils.get(x, 0), null);
		//
		testAncAccept(x -> !isAccessible(x), value, x -> setAccessible(x, true));
		//
		final char[] cs = value == null || Narcissus.getField(text, value) != null ? text.toCharArray() : null;
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