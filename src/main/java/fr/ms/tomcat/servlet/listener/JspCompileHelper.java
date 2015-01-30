package fr.ms.tomcat.servlet.listener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class JspCompileHelper {

  static Set<String> findFilesInDirectory(final ServletContext servletContext, final String dirPath,
      final String... fileExtensions) {

    final Set<String> files = new HashSet<String>();

    for (final String path : servletContext.getResourcePaths(dirPath)) {

      if (path.endsWith("/")) {
        files.addAll(findFilesInDirectory(servletContext, path, fileExtensions));
      } else {
        for (final String extension : fileExtensions) {
          if (path.endsWith("." + extension)) {
            files.add(path);
          }
        }
      }
    }
    return files;
  }

  static HttpServletRequest createHttpServletRequest() {
    final InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String methodName = method.getName();
        /**
         * <p>
         * Look for a <em>precompilation request</em> as described in Section 8.4.2 of the JSP 1.2 Specification.
         * </p>
         */
        if (methodName.equals("getQueryString")) {
          return "jsp_precompile";
        }
        if (methodName.equals("isAsyncSupported")) {
          return false;
        }
        if (methodName.equals("isAsyncStarted")) {
          return false;
        }

        return null;
      }
    };

    return (HttpServletRequest) Proxy.newProxyInstance(JspCompileHelper.class.getClassLoader(),
        new Class<?>[]{HttpServletRequest.class}, handler);
  }

  static HttpServletResponse createHttpServletResponse() {
    final InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return null;
      }
    };

    return (HttpServletResponse) Proxy.newProxyInstance(JspCompileHelper.class.getClassLoader(),
        new Class<?>[]{HttpServletResponse.class}, handler);
  }

  static Executor createSyncExecutor() {
    final Executor executor = new Executor() {

      @Override
      public void execute(final Runnable command) {
        command.run();
      }
    };

    return executor;
  }
}
