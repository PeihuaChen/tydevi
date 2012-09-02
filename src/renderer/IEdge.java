package renderer;

public interface IEdge
{
	public abstract INode getSource();

	public abstract INode getTarget();

	public abstract String getLabel();
}