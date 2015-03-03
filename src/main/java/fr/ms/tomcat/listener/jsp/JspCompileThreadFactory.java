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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Retourne une fabrique de thread en utilisant la fabrique par défaut de l'api standard
 * {@link Executors#defaultThreadFactory()}.<br>
 * Cette fabrique crée les threads comme la fabrique par défaut à l'exception :<br>
 * <br>
 * <ul>
 * <li>Le thread aura comme nom "<b>jspCompile</b>" suivi par le nom donné en parametre du constructeur. <br>
 * <b>Par exemple :</b> si name est "<b>/example</b>" le thread aura comme nom "<b>JspCompile /example</b>"
 * <li>Le thread sera un thread démon.
 * <li>Le thread aura {@link Thread#MIN_PRIORITY la priorité minimale}.
 * <ul>
 * <br>
 * <br>
 * 
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * @see <a href="https://github.com/marcosemiao/jsp-compile-listener">GitHub</a>
 * 
 * @author Marco Semiao
 * 
 */
class JspCompileThreadFactory implements ThreadFactory {

  private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

  private final String name;

  JspCompileThreadFactory(final String name) {
    this.name = name;
  }

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
