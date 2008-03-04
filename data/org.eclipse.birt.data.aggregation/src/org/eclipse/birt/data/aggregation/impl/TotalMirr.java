/*
 *************************************************************************
 * Copyright (c) 2004, 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *  
 *************************************************************************
 */

package org.eclipse.birt.data.aggregation.impl;

import java.util.ArrayList;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.aggregation.api.IBuildInAggregation;
import org.eclipse.birt.data.aggregation.i18n.Messages;
import org.eclipse.birt.data.aggregation.i18n.ResourceConstants;
import org.eclipse.birt.data.engine.aggregation.SummaryAccumulator;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;
import org.eclipse.birt.data.engine.core.DataException;

/**
 * 
 * Implements the built-in Total.mirr aggregation
 */
public class TotalMirr extends AggrFunction
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.engine.aggregation.Aggregation#getName()
	 */
	public String getName( )
	{
		return IBuildInAggregation.TOTAL_MIRR_FUNC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.engine.aggregation.Aggregation#getType()
	 */
	public int getType( )
	{
		return SUMMARY_AGGR;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.data.engine.api.aggregation.IAggregation#getDateType()
	 */
	public int getDataType( )
	{
		return DataType.DOUBLE_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.engine.aggregation.Aggregation#getParameterDefn()
	 */
	public IParameterDefn[] getParameterDefn( )
	{
		return new IParameterDefn[]{
				new ParameterDefn( Constants.DATA_FIELD_NAME,
						Constants.DATA_FIELD_DISPLAY_NAME,
						false,
						true,
						SupportedDataTypes.INTEGER_DOUBLE,
						"" ),//$NON-NLS-1$
				new ParameterDefn( "finance rate", Messages.getString( "TotalMirr.param.finance_rate" ), true, false, SupportedDataTypes.INTEGER_DOUBLE, "" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				new ParameterDefn( "reinvestment rate", Messages.getString( "TotalMirr.param.reinvestment_rate" ), true, false, SupportedDataTypes.INTEGER_DOUBLE, "" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.engine.aggregation.Aggregation#newAccumulator()
	 */
	public Accumulator newAccumulator( )
	{
		return new MyAccumulator( );
	}

	private class MyAccumulator extends SummaryAccumulator
	{

		private ArrayList list;

		private double frate = 0D;

		private double rrate = 0D;

		private Double ret = null;

		public void start( )
		{
			super.start( );
			frate = 0D;
			rrate = 0D;
			list = new ArrayList( );
			ret = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.birt.data.engine.aggregation.Accumulator#onRow(java.lang.Object[])
		 */
		public void onRow( Object[] args ) throws DataException
		{
			assert ( args.length > 2 );
			if ( args[0] != null && args[1] != null && args[2] != null )
			{
				try
				{
					double value = DataTypeUtil.toDouble( args[0] )
							.doubleValue( );
					if ( list.size( ) == 0 )
					{
						frate = DataTypeUtil.toDouble( args[1] ).doubleValue( );
						rrate = DataTypeUtil.toDouble( args[2] ).doubleValue( );
					}
					list.add( new Double( value ) );
				}
				catch ( BirtException e )
				{
					throw DataException.wrap( new AggrException( ResourceConstants.DATATYPEUTIL_ERROR,
							e ) );
				}
			}
		}

		public void finish( ) throws DataException
		{
			if ( list.size( ) > 0 )
			{
				double[] values = new double[list.size( )];
				for ( int i = 0; i < list.size( ); i++ )
				{
					values[i] = ( (Double) list.get( i ) ).doubleValue( );
				}

				ret = new Double( Finance.mirr( values, frate, rrate ) );
			}
			super.finish( );
		}

		/* (non-Javadoc)
		 * @see org.eclipse.birt.data.engine.aggregation.SummaryAccumulator#getSummaryValue()
		 */
		public Object getSummaryValue( )
		{
			return ret;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.data.engine.api.aggregation.IAggrFunction#getDescription()
	 */
	public String getDescription( )
	{
		return Messages.getString( "TotalMirr.description" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.birt.data.engine.api.aggregation.IAggrFunction#getDisplayName()
	 */
	public String getDisplayName( )
	{
		return Messages.getString( "TotalMirr.displayName" ); //$NON-NLS-1$
	}
	
}