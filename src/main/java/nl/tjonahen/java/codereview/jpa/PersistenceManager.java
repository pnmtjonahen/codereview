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
package nl.tjonahen.java.codereview.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {

    INSTANCE;

    private EntityManagerFactory emFactory;

    private PersistenceManager() {
    // "jpa-example" was the value of the name attribute of the
        // persistence-unit element.
        emFactory = Persistence.createEntityManagerFactory("nl.tjonahen.java_codereview_jar_1.0-SNAPSHOTPU");
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
    }
}
