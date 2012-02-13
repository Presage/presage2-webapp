package uk.ac.imperial.presage2.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

public abstract class GenericPresageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected StorageService sto;

	@Inject
	protected GenericPresageServlet(DatabaseService db, StorageService sto)
			throws Exception {
		super();
		if (!db.isStarted())
			db.start();
		this.sto = sto;
	}

	protected int getIntegerParameter(HttpServletRequest req, String name,
			int defaultValue) {
		String param = req.getParameter(name);
		if (param == null || param == "") {
			return defaultValue;
		} else {
			return Integer.parseInt(param.toString());
		}
	}

}
