package ad.cass.application;

import javax.ws.rs.core.Application;

import ad.cass.rws.TestRWS;

import java.util.HashSet;
import java.util.Set;

public class RWSApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	public RWSApplication() {
		singletons.add(new TestRWS());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
