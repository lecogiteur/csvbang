/**
 * com.github.lecogiteur.csvbang.test.reader.AsynchronousReaderTest
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
package com.github.lecogiteur.csvbang.test.reader;

/**
 * Created by lecogiteur on 28/05/16.
 * @version 1.0.0
 * @since 1.0.0
 */

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.FactoryCsvbang;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.reader.CsvReader;
import com.github.lecogiteur.csvbang.test.bean.reader.Read1;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.ConcurrentSkipListSet;

@RunWith(BlockJUnit4ClassRunner.class)
public class AsynchronousReaderTest {


    @Test
    public void should_read_simple_file_asynchronously() throws URISyntaxException, CsvBangException, ParseException {
        File file = new File(this.getClass().getResource("/csvbang/read/read1.csv").toURI());

        final FactoryCsvbang factory = new FactoryCsvbang();
        final CsvReader<Read1> reader = factory.createCsvReader(Read1.class, new FileName(file.getAbsolutePath(), null));
        factory.setNumberOfThread(5);
        ConcurrentSkipListSet<Integer> ids = new ConcurrentSkipListSet<Integer>();
        for (int i=1; i<2001; i++){
            ids.add(i);
        }


        SimpleReaderTest.ReaderCsv r1 = new SimpleReaderTest.ReaderCsv(reader, true, ids);
        r1.run();

        Assert.assertFalse(r1.hasError);

        Assert.assertEquals(2000, r1.counter);
        Assert.assertEquals(0, ids.size());

    }

}
