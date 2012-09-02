package tydevi;

import java.util.ArrayList;
import java.util.Collection;

import renderer.IEdge;
import renderer.IGraph;
import renderer.INode;

public class Graph implements IGraph
{
	/**
	 * Nodes
	 */
	private final ArrayList<INode> nodes;

	/**
	 * Edges
	 */
	private final ArrayList<IEdge> edges;

	/**
	 * Constructor
	 */
	public Graph()
	{
		this.nodes = new ArrayList<INode>();
		this.edges = new ArrayList<IEdge>();
	}

	/**
	 * Add node
	 * 
	 * @param lex
	 *            lex
	 * @param pos
	 *            part-of-speech
	 */
	protected void addNode(final String lex, final String pos)
	{
		this.nodes.add(new Node(this.nodes.size(), lex, pos));
	}

	/**
	 * Add edge
	 * 
	 * @param thisSourceIndex
	 *            source node index
	 * @param thisTargetIndex
	 *            target node index
	 * @param thisLabel
	 *            edge label
	 */
	public void addEdge(final int thisSourceIndex, final int thisTargetIndex, final String thisLabel)
	{
		final INode source = this.nodes.get(thisSourceIndex);
		final INode target = this.nodes.get(thisTargetIndex);
		final Edge e = new Edge(this.edges.size(), source, target, thisLabel);
		this.edges.add(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IGraph#getNodes()
	 */
	@Override
	public Collection<INode> getNodes()
	{
		return this.nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IGraph#getEdges()
	 */
	@Override
	public Collection<IEdge> getEdges()
	{
		return this.edges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IGraph#getInComingEdges(tydevi.INode)
	 */
	@Override
	public Collection<IEdge> getIncomingEdges(final INode thisNode)
	{
		final Collection<IEdge> theseEdges = new ArrayList<IEdge>();
		for (final IEdge e : this.edges)
		{
			if (e.getTarget().equals(thisNode))
			{
				theseEdges.add(e);
			}
		}
		return theseEdges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IGraph#getOutgoingEdges(tydevi.INode)
	 */
	@Override
	public Collection<IEdge> getOutgoingEdges(final INode thisNode)
	{
		final Collection<IEdge> theseEdges = new ArrayList<IEdge>();
		for (final IEdge e : this.edges)
		{
			if (e.getSource().equals(thisNode))
			{
				theseEdges.add(e);
			}
		}
		return theseEdges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuffer b = new StringBuffer();
		for (final INode n : getNodes())
		{
			b.append(n).append('\n');
		}
		for (final IEdge e : getEdges())
		{
			b.append(e).append('\n');
		}
		return b.toString();
	}

}
