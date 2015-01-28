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
package nl.tjonahen.java.codereview.visitor;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class CallScopeType {

    private final String packageName;
    private final String typeName;
    private final String methodName;

    public CallScopeType(final String packageName, final String typeName, final String methodName) {
        this.packageName = packageName;
        this.typeName = typeName;
        this.methodName = methodName;
    }

    public CallScopeType(final String packageName) {
        this(packageName, "", "");
    }

    public CallScopeType(final String packageName, final String typeName) {
        this(packageName, typeName, "");
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "ScopeType{" + "packageName=" + packageName + ", typeName=" + typeName + ", methodName=" + methodName + '}';
    }

}
