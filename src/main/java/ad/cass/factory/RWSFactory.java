package ad.cass.factory;

import java.io.Serializable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ad.cass.session.TestBeanRemote;

/**
 * The <code><b>RWSFactory</b></code> singleton class performs common tasks to initialize <b>remote</b> EJB access
 * and to locate and maintain the references to all Session interfaces.
 * <p/>
 * The class is a <b>context listener</b> and implements methods <code>contextInitialized()</code> and
 * <code>contextDestroyed()</code>. The listener registration is done in file <code>web.xml</code>.<p/>
 * Web service resource classes should use the instance of this class (created during web application startup) to obtain
 * access to business methods in EJBs.
 * <p/>
 * Getting the instance must be done in the following way:<blockquote>
 * <font color="blue" size="3"><code><b>RWsFactory.getInstance();</b></code></font>
 * </blockquote>
 * The class contains logging statements which show the initialization progress in the JBoss application server log. Instantiating EJBs and testing
 * basic business methods to validate the business logic availability is visible in this log.
 *
 * @author Victor
 */
public class RWSFactory implements ServletContextListener, Serializable {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private static final long serialVersionUID = 1L;
	private Context initialContext;
	private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
	private static RWSFactory INSTANCE = null; // This will hold the instance of this class
	private boolean initialized = false;

	// SERVICES
	private TestBeanRemote testBean = null;

	/**
	 * Default constructor. Will be called by the web application startup process, so it must be <b>public</b>.
	 */
	public RWSFactory() {
		_log.info("RWSFactory INSTANTIATED");
	}

	/**
	 * This method is called when this web application is stopped
	 *
	 * @param servletContext The servlet context instance
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		this.initialized = false;
		_log.info("Servet context DESTROYED");
	}

	/**
	 * Returns the instance of this class, created by the web application environment
	 *
	 * @return The instance. If the web application context is not yet initialized, this method will return <b>null</b>.
	 */
	public static RWSFactory getInstance() {
		return INSTANCE;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public TestBeanRemote getTestService() {
		return testBean;
	}


	/**
	 * This method is called when this web application is started
	 *
	 * @param servletContextEvent The servlet context instance
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		_log.info("Servlet context INITIALIZED");
		// Prepare the initial context for remote EJB access at JBoss-7.1 server:
		Properties properties = new Properties();
		properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
		try {
			initialContext = new InitialContext(properties);
			_log.info("Initial context created");

			// GET EJB REFERENCE
			_log.info("Look up ["+ TestBeanRemote.class.getName() +"] EJB...");
			testBean = (TestBeanRemote) initialContext.lookup(
					getLookupEJBName("cass-core", "TestBean", TestBeanRemote.class.getName()));
			_log.info("["+ TestBeanRemote.class.getName() +"] EJB LOCATED");


			// Store the reference to this instance to make it available via method getInstance():
			INSTANCE = this; // Set the instance to this one.
			this.initialized = true; // Mark factory initialized
			_log.info("Application INITIALIZED");
		} catch (Exception ex) {
			this.initialized = false;
			_log.info("Failed to obtain the EJB references");
			_log.error(ex.getStackTrace());
		}

	}
	
	/**
	 * Returns the complete name to use to look up a remote EJB session bean from a remote client.
	 *
	 * @param module		The name of the module. Example: "<code><b>MyService-Core</b></code>"
	 * @param beanName      The name of the bean implementation. Example: "<code><b>MyServiceBean</b></code>"
	 * @param interfaceName The full classified name of the session interface.
	 *                      Example: "<code><b>my.package.MyServiceRemote</b></code>". This name can be obtained by
	 *                      using the expression:
	 *                      <blockquote>
	 *                      <font color="navy" size="3"><code><b>
	 *                      ClassName.class.getName(); -- <font color="black">Example: </font><font color="maroon">MyServiceRemote.class.getname();
	 *                      </font></b></code></font>
	 *                      </blockquote>
	 * @param module
	 * @return The name as it can be used to perform a remote lookup.<br/>
	 * Example: <blockquote><font color="maroon" size="3">
	 * <code><b>ejb:/MyService-Core//MyServiceBean!my.package.MyServiceRemote</b></code>
	 * </font></blockquote>
	 * Returns <b>null</b> if invalid parameters have been specified or if the EJB could not be located.
	 */
	public static String getLookupEJBName(String module, String beanName, String interfaceName) {
		// Create a look up string name and return it:
		String distinctName = "";
		return "ejb:" + "" + "/" + module + "/" + distinctName + "/" + beanName + "!" + interfaceName;
	}

}
