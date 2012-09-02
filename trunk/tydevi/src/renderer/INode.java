package renderer;

public interface INode extends Comparable<INode>
{
	public abstract String getLex();

	public abstract String getPos();

	public abstract String getLabel();
}