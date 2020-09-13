package net.imglib2.img.array;

import java.util.Set;

import net.imglib2.Dimensions;
import net.imglib2.img.basictypeaccess.AccessFlags;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.img.basictypeaccess.ByteAccess;
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
 * Factory for {@link ArrayImg}s with parameterized Access interface.
 *
 * @param <T> NativeTypeAccess implementation that constrains the Access interface
 * @param <A> ArrayDataAccess constrained by the second parameter of NativeTypeAccess
 *
 * @author Mark Kittisopikul
 * @author Tobias Pietzsch
 * 
 */
public class ArrayImgAccessFactory< T extends NativeTypeAccess< T, ? super A >, A extends ArrayDataAccess< A > > extends ArrayImgFactory< T >
{
	private final A access;
	
	/**
	 * Create a factory based on a NativeType that implements NativeTypeAccess.
	 * The ArrayDataAccess is constrained by the second parameter of NativeTypeAccess
	 * 
	 * @param NativeTypeAccess<T,B> implementation
	 * 
	 * @return Factory that produces ArrayImg<T,? extends B> 
	 */
	public ArrayImgAccessFactory( T type ) {
		this( type, ArrayDataAccessFactory.get( type ) );
	}

	/**
	 * Specify both a NativeTypeAccess implementation and Access interface.
	 * The ArrayDataAccess is constrained by the second parameter of NativeTypeAccess
	 * 
	 * @param type NativeTypeAccess<T,B> implementation
	 * @param access Access interface A where A extends B and extends ArrayDataAccess<A>
	 */
	public ArrayImgAccessFactory( T type, A access )
	{
		super( type );
		this.access = access;
	}
		
	/**
	 * Construct ArrayImgAccessFactory from AccessFlags using ArrayDataAccessFactory
	 * 
	 * @param <T>
	 * @param <A> Specified from ArrayDataAccessFactory
	 * @param type NativeTypeAccess whose second parameter constrains the ArrayDataAccessFactory
	 * @param flags AccessFlags from {@link AccessFlags.setOf}
	 * @return
	 * 
	 * @see ArrayDataAccessFactory
	 */
	public static < T extends NativeTypeAccess< T, ? super A >, A extends ArrayDataAccess< A > > ArrayImgAccessFactory<T,A> fromFlags(T type, Set<AccessFlags> flags) {
		return new ArrayImgAccessFactory< T,A >(type, ArrayDataAccessFactory.get(type,flags) );
	}

	/**
	 *
	 * Create an ArrayImg with the specified dimensions.
	 * 
	 * This method does not involve any unchecked casts
	 * unlike ArrayImgFactory.
	 *
	 */
	@Override
	public ArrayImg< T, A > create( final long... dimensions )
	{
		final ArrayImg< T, A > img = create( dimensions, type(), type().getNativeTypeFactory() );
		return img;
	}

	/**
	 *
	 * Create an ArrayImg with the specified dimensions.
	 * 
	 * This method does not involve any unchecked casts
	 * unlike ArrayImgFactory.
	 *
	 */
	@Override
	public ArrayImg< T, A > create( final Dimensions dimensions )
	{
		return create( Intervals.dimensionsAsLongArray( dimensions ) );
	}

	/**
	 *
	 * Create an ArrayImg with the specified dimensions.
	 * 
	 * This method does not involve any unchecked casts
	 * unlike ArrayImgFactory.
	 *
	 */
	@Override
	public ArrayImg< T, A > create( final int[] dimensions )
	{
		return create( Util.int2long( dimensions ) );
	}
	
	/**
	 * Create an ArrayImg with a given Access interface and specified dimensions
	 * along with a linked type.
	 * 
	 * @param access Access interface such as ByteAccess or ByteArray
	 * @param dimensions Dimensions of the ArrayImg
	 * @return ArrayImg that uses access as its array
	 */
	public ArrayImg< T,A > create(final A access, final long... dimensions) {
		return create(access, dimensions, type(), type().getNativeTypeFactory());
	}
	
	/**
	 * Create an ArrayImg with a given Access interface and specified dimensions
	 * along with a linked type.
	 * 
	 * @param access Access interface such as ByteAccess or ByteArray
	 * @param dimensions Dimensions of the ArrayImg
	 * @return ArrayImg that uses access as its array
	 */
	public ArrayImg< T,A > create(final A access, final Dimensions dimensions) {
		return create(access, Intervals.dimensionsAsLongArray( dimensions ));
	}

	/**
	 * Create an ArrayImg with a given Access interface and specified dimensions
	 * along with a linked type.
	 * 
	 * @param access Access interface such as ByteAccess or ByteArray
	 * @param dimensions Dimensions of the ArrayImg
	 * @return ArrayImg that uses access as its array
	 */
	public ArrayImg< T,A > create(final A access, final int[] dimensions) {
		return create(access, Util.int2long( dimensions ));
	}

	/**
	 * This private method creates a new ArrayDataAccess and uses that to construct an image.
	 * 
	 * @param dimensions
	 * @param type
	 * @param typeFactory Usually from type.getNativeFactory()
	 * @return
	 */
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

	/**
	 * This private method uses an existing ArrayDataAccess
	 * 
	 * @param data
	 * @param dimensions
	 * @param type
	 * @param typeFactory Usually from type.getNativeFactory()
	 * @return
	 */
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
		//TODO: Move to test class
		final ArrayImg< UnsignedByteType, ByteArray > bytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new ByteArray( 0 ) ).create( 1 );
		final ArrayImg< UnsignedByteType, DirtyByteArray > dirtyBytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new DirtyByteArray( 0 ) ).create( 1 );
		final ArrayImg< UnsignedByteType, VolatileByteArray > volatileBytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new VolatileByteArray( 0, true ) ).create( 1 );
		final ArrayImgAccessFactory< UnsignedByteType, DirtyVolatileByteArray > dirtyVolatileBytes = new ArrayImgAccessFactory<>( new UnsignedByteType(), new DirtyVolatileByteArray( 0, true ) );
		
		
		final ArrayImgAccessFactory< UnsignedByteType, ByteArray > byteFactory = new ArrayImgAccessFactory<>(new UnsignedByteType(), new ByteArray( 0 ));
		byteFactory.create(new ByteArray(5),5,1);
		
		final ArrayImgAccessFactory<UnsignedByteType, ? extends ByteAccess> genericFactory = new ArrayImgAccessFactory<>( new UnsignedByteType() );
		final ArrayImg< UnsignedByteType, ? extends ByteAccess > genericBytes = genericFactory.create( 1 );
	}
}
