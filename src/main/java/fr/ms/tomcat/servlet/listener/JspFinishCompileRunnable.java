package fr.ms.tomcat.servlet.listener;

import javax.servlet.ServletContext;

class JspFinishCompileRunnable implements Runnable {

	private final ServletContext servletContext;

	private final int nbJsps;

	JspFinishCompileRunnable(final ServletContext servletContext, final int nbJsps) {
		this.servletContext = servletContext;
		this.nbJsps = nbJsps;
	}

	@Override
	public void run() {
		servletContext.log(nbJsps + " jsp files compiled - Context : " + servletContext.getContextPath());
	}
}
