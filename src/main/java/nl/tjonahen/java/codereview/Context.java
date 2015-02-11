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
package nl.tjonahen.java.codereview;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class Context {

    private static Context instance = null;
    private ThreadLocal<String> myThreadLocal = new ThreadLocal<>();

    public static final Context instance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }
    
    
    
    public void set(String value) {
        myThreadLocal.set(value);
    }
    
    public String get() {
        return myThreadLocal.get();
    }
    
}
