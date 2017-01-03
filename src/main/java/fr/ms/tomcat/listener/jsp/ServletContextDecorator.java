package fr.ms.tomcat.listener.jsp;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.apache.catalina.LifecycleState;

public class ServletContextDecorator implements InvocationHandler {

	private final ServletContext servletContext;
	private final MBeanServer mbs;
	private final ObjectName name;

	private LifecycleState state = LifecycleState.STARTING;

	private ServletContextDecorator(final ServletContext servletContext, MBeanServer mbs, ObjectName name) {
		this.servletContext = servletContext;
		this.mbs = mbs;
		this.name = name;

	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		final String nameMethod = method.getName();

		if ("getRequestDispatcher".equals(nameMethod)) {
			while (!this.state.equals(LifecycleState.STARTED)) {
				this.state = receiveState();
				Thread.sleep(1000L);
			}
		}

		return method.invoke(servletContext, args);
	}

	private LifecycleState receiveState() {
		try {
			final String attribute = (String) mbs.getAttribute(name, "stateName");
			final LifecycleState state = LifecycleState.valueOf(attribute);
			return state;
		} catch (final Throwable e) {
			// NO-OP
		}

		return null;
	}

	public static ServletContext decorateServletContext(final ServletContext servletContext) {
		final String serverInfo = servletContext.getServerInfo();

		final int start = serverInfo.indexOf("/");
		final int end = serverInfo.indexOf(".", start);

		final String versionString = serverInfo.substring(start + 1, end);

		final Integer version = Integer.valueOf(versionString);

		if (version > 7) {
			try {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				ObjectName name = new ObjectName("Catalina:type=Server");

				final InvocationHandler handler = new ServletContextDecorator(servletContext, mbs, name);

				final ClassLoader classLoader = servletContext.getClass().getClassLoader();
				final Class<?>[] interfaces = new Class<?>[] { ServletContext.class };

				return (ServletContext) Proxy.newProxyInstance(classLoader, interfaces, handler);
			} catch (MalformedObjectNameException e) {
				// NO-OP
			}
		}
		return servletContext;
	}
}
