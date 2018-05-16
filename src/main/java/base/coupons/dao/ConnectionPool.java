package base.coupons.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import base.coupons.exceptions.CouponException;

public class ConnectionPool {
	private static final ConnectionPool instance = new ConnectionPool();
	private final static int MAX_CONS = 10;
	private final static String DRIVER = "org.apache.derby.jdbc.ClientDriver";
	private String url = "jdbc:derby://localhost:1527/csdb; create=true;";

	private Set<Connection> connections = new HashSet<Connection>();
	Connection con;

	private Set<Connection> usedConnections = new HashSet<>();

	private ConnectionPool() {
		super();
		initializePool();
	}

	/**
	 * getter for Instance of ConnectionPool
	 * 
	 * @return
	 */
	public static ConnectionPool getInstance() {
		return instance;
	}

	/**
	 * Private method called by constructor.<br>
	 * first, calls the driver for this database.<br>
	 * then, fills Singleton's HashSet collection with 10 connections to be given
	 * and returned to and from all DAO methods.
	 * 
	 * @throws RuntimeException
	 *             - if Database is down, or a connection error has occurred.
	 * @throws ClassNotFoundException
	 *             - if driver loader was not found (shouldn't happen..)
	 */
	private void initializePool() {

		try {
			Class.forName(DRIVER);
			while (connections.size() < MAX_CONS) {
				url = "jdbc:derby://localhost:1527/csdb; create=true;";
				this.con = DriverManager.getConnection(url);
				connections.add(con);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Connection cannot be established.");

		} catch (ClassNotFoundException e) {
			System.out.println("failed to automatically load driver.");
		}

	}

	/**
	 * gets a Connection from the connections HashSet. if no connections are
	 * available, wait.<br>
	 * The connection returned from the method will be stored in usedConnections
	 * HashSet to make sure it is properly returned and for futureproofing.
	 * 
	 * @return a single Connection object.
	 */
	// pop
	public synchronized Connection getConnection() {
		Iterator<Connection> it = connections.iterator();

		try {
			while (!it.hasNext()) {
				wait();
			}
		} catch (InterruptedException e) {
			// will need to re-do message once i figure out when this exception will trigger
			System.out.println(e.getMessage());
		}
		Connection con = it.next();
		usedConnections.add(con);
		connections.remove(con);
		// System.out.println("==== Connections Size===" + connections.size() +
		// "=================");
		return con;
	}

	/**
	 * removes the given connection from the usedConnections HashSet and returns it
	 * to the connections set.<br>
	 * Notifies the getConnection method of newly arrived Connection.
	 * 
	 * @param con
	 */
	// push
	public synchronized void returnConnection(Connection con) {
		// System.out.println("returned connection!");
		connections.add(con);
		usedConnections.remove(con);
		// System.out.println(usedConnections.size() + "currently used or floating
		// connections");
		notify();

	}

	/**
	 * kills all Connections in the connections HashSet. <br>
	 * When this method is called, all connections will be terminated. it will not
	 * wait for any remaining procedure - that is on purpose, as was requested.<br>
	 * Will be called on CouponSystem Singleton's shutdown() method.
	 * 
	 * @throws CouponException
	 */
	public synchronized void closeAllConnections() throws CouponException {
		Iterator<Connection> it = connections.iterator();
		try {
			while (it.hasNext()) {
				// System.out.println("=== TEST BEGINNING CLOSE CONNECTIONS METHOD ====");
				it.next().close();
				it.remove();
				// System.out.println("Connection Removed. Pool Size " + connections.size());
			}
			connections.clear();
		} catch (SQLException e) {

			while (it.hasNext()) {
				it.remove();
			}
			connections.clear();
			throw new CouponException("Database error has occured. Pool was cleared and will shut down.");
		}
	}

}
