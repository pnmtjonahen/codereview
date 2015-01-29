/*
 * Copyright (C) 2015 Philippe Tjon - A - Hen, philippe@tjonahen.nl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tjonahen.java.codereview.files;

import java.io.File;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class FindTest {

    @Test
    public void testFindTestJavaFiles() throws FileNotFoundException {
        Find f = new Find(new File("./src/test/java/nl/tjonahen/java/codereview/files"));
        
        assertEquals(1, f.find().size());
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindNullDirectory() throws FileNotFoundException {
        Find f = new Find(null);
        
        f.find();
    }
    
    @Test(expected = FileNotFoundException.class)
    public void testFindNonExsistingDirectory() throws FileNotFoundException {
        Find f = new Find(new File("dummy"));
        
        f.find();
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindNonDirectory() throws FileNotFoundException {
        Find f = new Find(new File("./pom.xml"));
        
        f.find();
    }
    
    
}
