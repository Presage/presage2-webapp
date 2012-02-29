/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

public abstract class GenericPresageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected final Logger logger = Logger.getLogger(this.getClass()
			.getCanonicalName());
	protected final StorageService sto;

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

	protected void logRequest(HttpServletRequest req) {
		logger.info(req.getMethod() + " " + req.getRequestURI() + "?"
				+ req.getQueryString());
	}

}
