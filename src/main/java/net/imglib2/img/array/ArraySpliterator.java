/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.img.array;

import java.util.Spliterator;
import java.util.function.Consumer;
import net.imglib2.type.NativeType;

public class ArraySpliterator< T extends NativeType< T > > extends AbstractArrayCursor< T > implements Spliterator< T >
{
	public ArraySpliterator( final ArrayImg< T, ? > img )
	{
		super( img, 0, ( int ) img.size() );
	}

	protected ArraySpliterator( final ArrayImg< T, ? > img, final int offset, final int size )
	{
		super( img, offset, size );
	}

	protected ArraySpliterator( final ArraySpliterator< T > cursor )
	{
		super( cursor );
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( hasNext() )
		{
			action.accept( next() );
			return true;
		}
		else
			return false;
	}

	@Override
	public ArraySpliterator< T > trySplit()
	{
		int lo = type.getIndex(); // divide range in half
		int fence = lastIndex;
		int mid = ( ( lo + fence ) >>> 1 ) & ~1; // force midpoint to be even
		if ( lo < mid )
		{
			// reset this Spliterator's origin
			offset = mid + 1;
			type.updateIndex( mid );
			// split out left half
			return new ArraySpliterator<>( img, lo + 1, mid - lo );
		}
		else
		{
			// too small to split
			return null;
		}
	}

	@Override
	public void forEachRemaining( final Consumer< ? super T > action )
	{
		final int remaining = lastIndex - type.getIndex();
		for ( int i = 0; i < remaining; ++i )
		{
			type.incIndex();
			action.accept( type );
		}
	}

	@Override
	public long estimateSize()
	{
		return lastIndex - type.getIndex();
	}

	@Override
	public int characteristics()
	{
		return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
	}

	@Override
	public ArraySpliterator< T > copy()
	{
		return new ArraySpliterator<>( this );
	}

	@Override
	public ArraySpliterator< T > copyCursor()
	{
		return copy();
	}
}
