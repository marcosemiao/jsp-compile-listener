package fr.ms.tomcat.servlet.listener;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

public class JspCompileListener implements ServletContextListener {

  public void contextInitialized(final ServletContextEvent servletContextEvent) {

    final ServletContext servletContext = servletContextEvent.getServletContext();

    final HttpServletRequest request = JspCompileHelper.createHttpServletRequest();

    final Set<String> jsps = JspCompileHelper.findFilesInDirectory(servletContext, "/", "jsp", "jspx");

    final ThreadFactory threadFactory = new JspCompileThreadFactory(servletContext.getContextPath());
    final Executor executorService = Executors.newSingleThreadExecutor(threadFactory);

    for (final String jsp : jsps) {
      final Runnable task = new JspCompileRunnable(servletContext, jsp, request, null);
      executorService.execute(task);
    }
  }

  public void contextDestroyed(final ServletContextEvent sce) {

  }
}
