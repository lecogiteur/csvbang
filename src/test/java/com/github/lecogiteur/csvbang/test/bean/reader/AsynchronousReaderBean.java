/**
 * com.github.lecogiteur.csvbang.test.bean.reader.Ttest
 * <p>
 * Copyright (C) 2013-2016  Tony EMMA
 * <p>
 * This file is part of Csvbang.
 * <p>
 * Csvbang is a comma-separated values ( CSV ) API, written in JAVA and thread-safe.
 * <p>
 * Csvbang is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * <p>
 * Csvbang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Csvbang. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.lecogiteur.csvbang.test.bean.reader;

import com.github.lecogiteur.csvbang.annotation.CsvFile;

/**
 * Created by lecogiteur on 28/05/16.
 */
@CsvFile(asynchronousReader = true, registerThread=false)
public class AsynchronousReaderBean extends Read1{
}
