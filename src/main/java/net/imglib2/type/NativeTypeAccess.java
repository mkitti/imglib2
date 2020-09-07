package net.imglib2.type;

/**
 * Binds an Access interface to a concrete NativeType implementation
 * to fully specify the return type of getNativeTypeFactory()
 * 
 * @author Mark Kittisopikul <kittisopikulm@janelia.hhmi.org>
 *
 * @param <T> NativeType implementation
 * @param <A> Access interface
 */
public interface NativeTypeAccess<T extends NativeType<T>,A> {
	public NativeTypeFactory<T,A> getNativeTypeFactory();
}