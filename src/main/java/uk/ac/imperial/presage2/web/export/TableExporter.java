/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.web.export;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * A TableExporter takes a table of data described as an {@link Iterable} and
 * outputs it to an {@link OutputStream} with an implementation specific
 * formatting.
 * 
 * @author Sam Macbeth
 * 
 */
interface TableExporter {

	/**
	 * Exports <code>table</code> to the {@link OutputStream} <code>os</code>.
	 * 
	 * @param table
	 *            Table to export.
	 * @param os
	 *            {@link OutputStream} to write to.
	 * @throws IOException
	 */
	void exportTable(Iterable<Iterable<String>> table, OutputStream os)
			throws IOException;

	/**
	 * <p>
	 * Exports <code>table</code> as a {@link HttpServletResponse}.
	 * </p>
	 * 
	 * <p>
	 * The implementation should send appropriate http headers for the format it
	 * is sending.
	 * </p>
	 * 
	 * @param table
	 *            Table to export.
	 * @param resp
	 *            {@link HttpServletResponse} to write to.
	 * @throws IOException
	 */
	void httpExportTable(Iterable<Iterable<String>> table,
			HttpServletResponse resp) throws IOException;

}
