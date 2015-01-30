package fr.ms.tomcat.servlet.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class JspCompileThreadFactory implements ThreadFactory {

  private final ThreadFactory threadFactory;

  private String name;

  {
    threadFactory = Executors.defaultThreadFactory();
  }

  JspCompileThreadFactory(String name) {
    this.name = name;
  }

  @Override
  public Thread newThread(final Runnable r) {
    final Thread newThread = threadFactory.newThread(r);

    if (name != null) {
      newThread.setName("jspCompile " + name);
    }
    newThread.setDaemon(true);
    newThread.setPriority(Thread.MIN_PRIORITY);

    return newThread;
  }
}
