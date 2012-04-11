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
import java.io.PrintStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

/**
 * {@link TableExporter} implementation to
 * 
 * @author Sam Macbeth
 * 
 */
class CSVTableExporter implements TableExporter {

	String filename = "PresageExport.csv";
	protected final String COLUMN_SEPARATOR;
	protected final String ROW_SEPARATOR;

	CSVTableExporter() {
		this(",", System.getProperty("line.separator", "\n"));
	}

	CSVTableExporter(String filename) {
		this();
		this.filename = filename;
	}

	CSVTableExporter(String cOLUMN_SEPARATOR, String rOW_SEPARATOR) {
		super();
		COLUMN_SEPARATOR = cOLUMN_SEPARATOR;
		ROW_SEPARATOR = rOW_SEPARATOR;
	}

	@Override
	public void exportTable(Iterable<Iterable<String>> table, OutputStream os)
			throws IOException {
		PrintStream p = new PrintStream(os);
		for (Iterable<String> row : table) {
			Iterator<String> it = row.iterator();
			while (it.hasNext()) {
				p.print(it.next() + COLUMN_SEPARATOR);
			}
			p.print(ROW_SEPARATOR);
		}
	}

	@Override
	public void httpExportTable(Iterable<Iterable<String>> table,
			HttpServletResponse resp) throws IOException {
		resp.setContentType("text/csv");
		resp.setHeader("Content-disposition", "attachment;filename=" + filename);
		exportTable(table, resp.getOutputStream());
	}

}
