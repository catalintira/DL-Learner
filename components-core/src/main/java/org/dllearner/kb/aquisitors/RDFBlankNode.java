/**
 * Copyright (C) 2007 - 2016, Jens Lehmann
 *
 * This file is part of DL-Learner.
 *
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.dllearner.kb.aquisitors;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;

/**
 * This class is used to save disambiguated information for a blankNode in an RDF
 * Graph. It is used to silently transport the info to the layer above.
 * 
 * @author Sebastian Hellmann
 *
 */
public class RDFBlankNode implements RDFNode {
	
	
	private RDFNode blankNode;
	private int bNodeId;
		
	public RDFBlankNode(int id, RDFNode blankNode) {
		super();
		this.bNodeId = id;
		this.blankNode = blankNode;
	}
	
	public int getBNodeId(){
		return bNodeId;
	}
	
	
	@Override
	public String toString(){
		//RBC
		return "bnodeid: "+bNodeId+" ||"+blankNode;
	}
	
	// overidden Functions
	@SuppressWarnings("all")
	public RDFNode as(Class view) {
		
		return blankNode.as(view);
	}

	@SuppressWarnings("all")
	public boolean canAs(Class view) {
		
		return blankNode.canAs(view);
	}

	@Override
	public RDFNode inModel(Model m) {
		
		return blankNode.inModel(m);
	}

	@Override
	public boolean isAnon() {
		
		return blankNode.isAnon();
	}

	@Override
	public boolean isLiteral() {
		
		return blankNode.isLiteral();
	}

	@Override
	public boolean isResource() {
		
		return blankNode.isResource();
	}

	@Override
	public boolean isURIResource() {
		
		return blankNode.isURIResource();
	}

	@Override
	public Object visitWith(RDFVisitor rv) {
		
		return blankNode.visitWith(rv);
	}

	@Override
	public Node asNode() {
		
		return blankNode.asNode();
	}

    /**
     *{@inheritDoc}
     */
    @Override
    public Model getModel() {
        return blankNode.getModel();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Resource asResource() {
        return blankNode.asResource();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Literal asLiteral() {
        return blankNode.asLiteral();
    }
}
