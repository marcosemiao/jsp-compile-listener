/*
 * Copyright 2015 Marco Semiao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package fr.ms.tomcat.listener.jsp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Méthodes utilitaires utilisées pour
 * {@link ServletContext#getResourcePaths(String)} et
 * {@link HttpServletRequest}.
 * 
 * <ul>
 * <li>Méthode retournant une collection des chemins d'accès des resources web.
 * <li>Méthode retournant un Proxy de {@link HttpServletRequest}.
 * </ul>
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * @see <a href="https://github.com/marcosemiao/jsp-compile-listener">GitHub</a>
 * 
 * @author Marco Semiao
 * 
 */
final class JspCompileHelper {

	/**
	 * Retourne une collection contenant tous les chemins d'accès aux fichiers
	 * correspondant à la recherche.
	 * <p>
	 * <b>Par exemple</b> pour une application contenant : <br>
	 * 
	 * <br>
	 * /welcome.html<br>
	 * /index.jsp<br>
	 * /catalog/index.html<br>
	 * /catalog/products.html<br>
	 * /catalog/offers/books.html<br>
	 * /catalog/offers/music.jspx<br>
	 * /catalog/offers/tv.jsp<br>
	 * /customer/login.jsp<br>
	 * /WEB-INF/web.xml<br>
	 * /WEB-INF/classes/com.acme.OrderServlet.class,<br>
	 * 
	 * <br>
	 * findFilesInDirectory(sc,"/","jsp","jspx") returns {"/index.jsp",
	 * "/catalog/offers/music.jspx", "/catalog/offers/tv.jsp",
	 * "/customer/login.jsp"}<br>
	 * 
	 * <br>
	 * findFilesInDirectory(sc,"/","jsp",) returns {"/index.jsp",
	 * "/customer/login.jsp", "/catalog/offers/tv.jsp"}<br>
	 * 
	 * <br>
	 * findFilesInDirectory(sc,"/","jspx") returns
	 * {"/catalog/offers/music.jspx"}<br>
	 * 
	 * <br>
	 * findFilesInDirectory(sc,"/catalog/","jsp","jspx") returns
	 * {"/catalog/offers/music.jspx", "/catalog/offers/tv.jsp"} <br>
	 * 
	 * @param servletContext
	 *            La {@link ServletContext} à utiliser pour la recherche.
	 * 
	 * @param dirPath
	 *            Le chemin d'accès utilisé pour la recherche des ressources, le
	 *            chemin d'accès doit commencé par "/".
	 * 
	 * @param fileExtensions
	 *            Les extensions recherchés.
	 * 
	 * @return Une collection contenant la liste des fichiers contenant la ou
	 *         les extensions spécifiées.
	 */
	static Set<String> findFilesInDirectory(final ServletContext servletContext, final String dirPath,
			final String... fileExtensions) {

		final Set<String> files = new HashSet<String>();

		Set<String> resourcePaths = servletContext.getResourcePaths(dirPath);

		if (resourcePaths != null && !resourcePaths.isEmpty()) {
			for (final String path : resourcePaths) {

				if (path.endsWith("/")) {
					final Set<String> findFilesInDirectory = findFilesInDirectory(servletContext, path, fileExtensions);
					files.addAll(findFilesInDirectory);
				} else {
					for (final String extension : fileExtensions) {
						if (path.endsWith("." + extension)) {
							files.add(path);
						}
					}
				}
			}
		}
		return files;
	}

	/**
	 * Création d'un proxy {@link HttpServletRequest} à utiliser uniquement pour
	 * la compilation des jsp.
	 * <p>
	 * Ce Proxy rajoute toujours le parametre "jsp_precompile" pour toutes les
	 * requetes. Ce parametre permet d'arreter le traitement juste après la
	 * compilation des jsp.
	 * <p>
	 * Plus d'informations sur ce parametre dans la <a href=
	 * "https://jcp.org/aboutJava/communityprocess/first/jsr053/jsp12.pdf">spécification
	 * JSP, section 8.4.2.<a>
	 * 
	 * @return Proxy de {@link HttpServletRequest}
	 */
	static HttpServletRequest createHttpServletRequest() {
		final InvocationHandler handler = new InvocationHandler() {

			public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
				final String methodName = method.getName();

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
				new Class<?>[] { HttpServletRequest.class }, handler);
	}

	/**
	 * Création d'un proxy {@link HttpServletResponse} à utiliser uniquement
	 * pour la compilation des jsp.
	 * 
	 * Ce Proxy ne fourni aucune implémentation, il permet d'avoir juste une
	 * instance quelconque de HttpServletResponse.
	 * 
	 * @return Proxy de {@link HttpServletResponse}
	 */
	static HttpServletResponse createHttpServletResponse() {
		final InvocationHandler handler = new InvocationHandler() {
			public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
				return null;
			}
		};

		return (HttpServletResponse) Proxy.newProxyInstance(JspCompileHelper.class.getClassLoader(),
				new Class<?>[] { HttpServletResponse.class }, handler);
	}
}
