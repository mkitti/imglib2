package net.imglib2.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.label.BasePairBitType;
import net.imglib2.type.label.BasePairCharType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NativeARGBDoubleType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned128BitType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.integer.Unsigned4BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.volatiles.VolatileARGBType;
import net.imglib2.type.volatiles.VolatileByteType;
import net.imglib2.type.volatiles.VolatileDoubleType;
import net.imglib2.type.volatiles.VolatileFloatType;
import net.imglib2.type.volatiles.VolatileIntType;
import net.imglib2.type.volatiles.VolatileLongType;
import net.imglib2.type.volatiles.VolatileShortType;
import net.imglib2.type.volatiles.VolatileUnsignedByteType;
import net.imglib2.type.volatiles.VolatileUnsignedIntType;
import net.imglib2.type.volatiles.VolatileUnsignedLongType;
import net.imglib2.type.volatiles.VolatileUnsignedShortType;

/**
 * Test implementations of NativeType and NativeTypeAccess
 * 
 * @author Mark Kittisopikul <kittisopikulm@janelia.hhmi.org>
 *
 * @param <T>
 * @param <A>
 */
@RunWith( Parameterized.class )
public class NativeTypeTest<T extends NativeType<T> & NativeTypeAccess<T,A>,A> {
	
	private static final List< NativeType< ? > > nativeTypes = Arrays.asList(
			//new ARGBDoubleType(), //not NativeType
			new ARGBType(),
			new BasePairBitType(),
			new BasePairCharType(),
			new BitType(),
			//new BoolType(), //not NativeType
			new ByteType(),
			new ComplexDoubleType(),
			new ComplexFloatType(),
			new DoubleType(),
			new FloatType(),
			new IntType(),
			new LongType(),
			new ShortType(),
			new UnsignedByteType(),
			new UnsignedIntType(),
			new UnsignedLongType(),
			new UnsignedShortType(),
			new Unsigned128BitType(),
			new Unsigned2BitType(),
			new Unsigned4BitType(),
			new Unsigned12BitType(),
			new UnsignedVariableBitLengthType( 7 ),
			new VolatileARGBType(),
			new VolatileByteType(),
			new VolatileDoubleType(),
			new VolatileFloatType(),
			new VolatileIntType(),
			new VolatileLongType(),
			new VolatileShortType(),
			new VolatileUnsignedByteType(),
			new VolatileUnsignedIntType(),
			new VolatileUnsignedLongType(),
			new VolatileUnsignedShortType(),
			new NativeARGBDoubleType(),
			new NativeBoolType() //,
			//new VolatileNumericType<>( new DoubleType() ), //not NativeType
			//new VolatileRealType<>( new DoubleType() ) //not NativeType
	);

	private final T type;
	
	@Parameterized.Parameters( name = "{0}" )
	public static Collection< Object > data()
	{
		return nativeTypes.stream().map(
				type -> new Object[] { type.getClass().getSimpleName(), type }
		).collect( Collectors.toList() );
	}

	public NativeTypeTest( final String className, final T type )
	{
		this.type = type;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDecIndex() {
		int index = type.getIndex();
		type.decIndex();
		type.decIndex(2);
		assertEquals(index,type.getIndex() + 3);
	}
	
	@Test
	public void testIncIndex() {
		int index = type.getIndex();
		type.incIndex();
		type.incIndex(3);
		assertEquals(index,type.getIndex() - 4);
	}
	
	@Test
	public void testUpdateIndex() {
		int index = 4;
		type.updateIndex(index);
		assertEquals(index,type.getIndex());
	}

	@Test
	public void testGetNativeTypeFactory() {
		NativeTypeFactory<T,A> factory = (NativeTypeFactory<T, A>) type.getNativeTypeFactory();
		assertTrue(factory != null);
	}
	
	@Test
	public void testCreateImage() {
		NativeTypeFactory<T,A> factory = (NativeTypeFactory<T, A>) type.getNativeTypeFactory();
		ArrayImgFactory<T> aiFactory = new ArrayImgFactory<T>(type);
		//TODO: Make this casting unnecessary
		ArrayImg<T,A> img = (ArrayImg<T, A>) aiFactory.create(1,1);
		T linkedType = factory.createLinkedType(img);
		img.setLinkedType(linkedType);
		T dupType = linkedType.duplicateTypeOnSameNativeImg();
		A data = img.update(null);
		Assert.assertNotNull(dupType);
		Assert.assertNotNull(data);
		//if(!(data instanceof VolatileAccess))
		//	dupType.updateContainer(img.cursor());
	}

}
