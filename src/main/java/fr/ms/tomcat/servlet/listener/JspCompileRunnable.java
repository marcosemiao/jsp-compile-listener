package fr.ms.tomcat.servlet.listener;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class JspCompileRunnable implements Runnable {

	private final ServletContext servletContext;
	private final String path;

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	JspCompileRunnable(final ServletContext servletContext, final String path,
			final HttpServletRequest request, final HttpServletResponse response) {
		this.servletContext = servletContext;
		this.path = path;
		this.request = request;
		this.response = response;
	}

	@Override
	public void run() {
		final RequestDispatcher requestDispatcher = servletContext
				.getRequestDispatcher(path);

		if (requestDispatcher == null) {
			return;
		}
		
		try {
			servletContext.log("Compiling : " + servletContext.getContextPath() +  path);
			requestDispatcher.include(request, response);
		} catch (final Exception e) {
			// Exception est tracé par le logger de tomcat.
			// Tomcat 7.0.50 - ApplicationDispatcher.invoke Line 772
		}
	}
}
