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
package nl.tjonahen.java.codereview.javaparsing.visitor;

import com.github.javaparser.ast.Node;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class SourceLocation {
    private final String source;
    private final int beginLine;
    private final int beginColumn;
    private final int endLine;
    private final int endColumn;

    public SourceLocation(String source, int beginLine, int beginColumn, int endLine, int endColumn) {
        this.source = source;
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public SourceLocation(String source, Node n) {
        this.source = source;
        this.beginColumn = n.getBeginColumn();
        this.beginLine = n.getBeginLine();
        this.endColumn = n.getEndColumn();
        this.endLine = n.getEndLine();
    }

    public String getSource() {
        return source;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getBeginColumn() {
        return beginColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public String toString() {
        return source + " [" + beginLine + ":" + beginColumn + "][" +endLine + ":" + endColumn + "]";
    }
    
    

}
