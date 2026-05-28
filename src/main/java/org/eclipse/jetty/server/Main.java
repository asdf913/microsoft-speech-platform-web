package org.eclipse.jetty.server;

import javax.servlet.http.MainServlet;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

	public static void main(final String[] args) throws Exception {
		//
		final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		//
		servletContextHandler.setContextPath("/");
		//
		servletContextHandler.addServlet(new ServletHolder(new MainServlet()), "/");
		//
		final Server server = new Server(8080);
		//
		server.setHandler(servletContextHandler);
		//
		server.start();
		//
		server.join();
		//
	}

}