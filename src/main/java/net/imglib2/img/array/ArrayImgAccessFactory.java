package net.imglib2.img.array;

import net.imglib2.Dimensions;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.type.NativeTypeAccess;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

/**
 * Factory for {@link ArrayImg}s with fully parameterized Access.
 * 
 * 
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 * @author Mark Kittisopikul
 *
 * @param <T>
 * @param <A>
 */
public class ArrayImgAccessFactory<T extends NativeTypeAccess<T,A>,A> extends ArrayImgFactory<T> {

	public ArrayImgAccessFactory(T type) {
		super(type);
	}
	
	public ArrayImg< T, A > create(final A data, final long... dimensions )
	{
		final ArrayImg< T, A > img = create(data, dimensions, type(), ( NativeTypeFactory<T,A> ) type().getNativeTypeFactory() );
		return img;
	}

	public ArrayImg< T, A > create(final A data, final Dimensions dimensions )
	{
		return create( data, Intervals.dimensionsAsLongArray( dimensions ) );
	}

	public ArrayImg< T, A > create(final A data, final int[] dimensions )
	{
		return create( data, Util.int2long( dimensions ) );
	}
	
	@Override
	public ArrayImg< T, A > create( final long... dimensions )
	{
		final ArrayImg< T, A > img = create( dimensions, type(), ( NativeTypeFactory<T,A> ) type().getNativeTypeFactory() );
		return img;
	}

	@Override
	public ArrayImg< T, A > create( final Dimensions dimensions )
	{
		return create( Intervals.dimensionsAsLongArray( dimensions ) );
	}

	@Override
	public ArrayImg< T, A > create( final int[] dimensions )
	{
		return create( Util.int2long( dimensions ) );
	}
	
	private ArrayImg< T, A > create(
			final long[] dimensions,
			final T type,
			final NativeTypeFactory< T, A > typeFactory )
	{
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );
		final A data = ArrayDataAccessFactory.get( typeFactory ).createArray( numEntities );
		return create(data, dimensions, type, typeFactory);
	}

	private ArrayImg< T, A > create(
			final A data,
			final long[] dimensions,
			final T type,
			final NativeTypeFactory< T, A > typeFactory )
	{
		Dimensions.verify( dimensions );
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final ArrayImg< T, A > img = new ArrayImg<>( data, dimensions, entitiesPerPixel );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		return img;
	}

}
