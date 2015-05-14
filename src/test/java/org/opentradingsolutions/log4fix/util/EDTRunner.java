package org.opentradingsolutions.log4fix.util;

import javax.swing.SwingUtilities;

import org.junit.Ignore;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Runs JUnit tests in the Event Dispatch Thread (EDT).
 *
 * To accomplish this, timeouts are not considered.
 *
 * @BeforeClass and @AfterClass methods are NOT run in the EDT
 *
 * @author Marcelo J. Ruiz.
 */
@Ignore
public class EDTRunner extends BlockJUnit4ClassRunner {

	public EDTRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void runChild(final FrameworkMethod method,
			final RunNotifier notifier) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					hookMethod(method, notifier);
				}
			});
		} catch (Exception ex) {
			throw new RuntimeException("Failed While Running in EDT:"
					+ ex.getMessage(), ex);
		}
	}

	private void hookMethod(final FrameworkMethod method,
			final RunNotifier notifier) {
		super.runChild(method, notifier);
	}

	@Override
	protected Statement withPotentialTimeout(final FrameworkMethod method,
			final Object test, final Statement next) {
		return next;
	}

}