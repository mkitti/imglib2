package net.imglib2.type.numeric.real;

/**
 * Abstract type to represent all floating point numerics such as FloatType and DoubleType.
 * 
 * @author Mark Kittispikul <kittisopikulm@janelia.hhmi.org>
 *
 * @param <T>
 */
public abstract class AbstractFloatingPointType< T extends AbstractRealType< T > > extends AbstractRealType<T> {
	
	public abstract T getMaxValue(T type);
	public abstract T getMinNormal(T type);
	public abstract T getMinValue(T type);
	public abstract T getNaN(T type);
	public abstract T getNegativeInfinity(T type);
	public abstract T getPositiveInfinity(T type);
	
	public abstract double getMaxValue();
	public abstract double getMinNormal();
	public abstract double getMinValue();
	public abstract double getNaN();
	public abstract double getNegativeInfinity();
	public abstract double getPositiveInfinity();
	
}
