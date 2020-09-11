package net.imglib2.img.array;

import net.imglib2.Dimensions;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DirtyByteArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileByteAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileByteArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileByteArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeAccess;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

/**
 * Factory for {@link ArrayImg}s with fully parameterized Access.
 *
 * @param <T>
 * @param <A>
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 * @author Mark Kittisopikul
 */
public class ArrayImgAccessFactory< T extends NativeTypeAccess< T, ? super A >, A extends ArrayDataAccess< A > > extends ArrayImgFactory< T >
{
	private final A access;

	public ArrayImgAccessFactory( T type, A access )
	{
		super( type );
		this.access = access;
	}

	@Override
	public ArrayImg< T, A > create( final long... dimensions )
	{
		final ArrayImg< T, A > img = create( dimensions, type(), type().getNativeTypeFactory() );
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
			final NativeTypeFactory< T, ? super A > typeFactory )
	{
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );
		final A data = access.createArray( numEntities );
		return create( data, dimensions, type, typeFactory );
	}

	private ArrayImg< T, A > create(
			final A data,
			final long[] dimensions,
			final T type,
			final NativeTypeFactory< T, ? super A > typeFactory )
	{
		Dimensions.verify( dimensions );
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final ArrayImg< T, A > img = new ArrayImg<>( data, dimensions, entitiesPerPixel );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		return img;
	}

	public static void main( String[] args )
	{
		final ArrayImg< UnsignedByteType, ByteArray > bytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new ByteArray( 0 ) ).create( 1 );
		final ArrayImg< UnsignedByteType, DirtyByteArray > dirtyBytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new DirtyByteArray( 0 ) ).create( 1 );
		final ArrayImg< UnsignedByteType, VolatileByteArray > volatileBytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new VolatileByteArray( 0, true ) ).create( 1 );
		final ArrayImgAccessFactory< UnsignedByteType, DirtyVolatileByteArray > dirtyVolatileBytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new DirtyVolatileByteArray( 0, true ) );
	}
}
