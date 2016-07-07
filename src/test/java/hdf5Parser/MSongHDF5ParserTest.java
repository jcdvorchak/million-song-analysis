package hdf5Parser;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by jcdvorchak on 7/6/2016.
 */
public class MSongHDF5ParserTest {
    byte[] fileContent;

    @Before
    public void setup() throws Exception {
        fileContent = IOUtils.toByteArray(this.getClass().getResourceAsStream("/TRAAAAW128F429D538.h5"));
    }

    @Test
    public void readHDF5FileTest() throws Exception {
        Track track = MSongHDF5Parser.readHDF5File(fileContent);

        System.out.println(track.getPrettyName());
    }

}