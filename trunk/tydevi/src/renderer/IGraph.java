package renderer;

import java.util.Collection;

public interface IGraph
{
	/**
	 * Get nodes
	 * 
	 * @return collection collection of nodes
	 */
	public abstract Collection<INode> getNodes();

	/**
	 * Get edges
	 * 
	 * @return collection of edges
	 */
	public abstract Collection<IEdge> getEdges();

	/**
	 * Get incoming edges of node
	 * 
	 * @param thisNode
	 *            node
	 * @return collection of incoming edges
	 */
	public abstract Collection<IEdge> getIncomingEdges(final INode thisNode);

	/**
	 * Get outgoing edges of node
	 * 
	 * @param thisNode
	 *            node
	 * @return collection of outgoing edges
	 */
	public abstract Collection<IEdge> getOutgoingEdges(final INode thisNode);
}