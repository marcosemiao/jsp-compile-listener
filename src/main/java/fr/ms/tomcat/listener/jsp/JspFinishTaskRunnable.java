package fr.ms.tomcat.listener.jsp;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;

public class JspFinishTaskRunnable implements Runnable {

	private final ServletContext servletContext;

	private final Set<String> jsps;
	private final List<Future<Boolean>> futures;

	public JspFinishTaskRunnable(final ServletContext servletContext, final Set<String> jsps,
			final List<Future<Boolean>> futures) {
		this.servletContext = servletContext;
		this.jsps = jsps;
		this.futures = futures;
	}

	public void run() {
		if (jsps != null && !jsps.isEmpty() && futures != null && !futures.isEmpty()) {
			final int total = jsps.size();
			int compiles = 0;
			for (final Future<Boolean> future : futures) {
				try {
					final Boolean success = future.get();
					if (success) {
						compiles++;
					}
				} catch (final InterruptedException e) {
					// NO-OP
				} catch (final ExecutionException e) {
					// NO-OP
				}
			}

			final String contextPath = servletContext.getContextPath();

			System.out.println(contextPath + " : " + compiles + " successful compilation on " + total);
		}
	}
}
