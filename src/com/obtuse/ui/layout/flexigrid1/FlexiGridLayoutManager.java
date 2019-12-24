/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.ObtuseSwingUtils;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintCategory;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Hashtable;
import java.util.Optional;

/**
 A layout manager for possibly irregular grids of components.
 */

public class FlexiGridLayoutManager implements LayoutManager2 {

    private boolean _traceMode = false;

    private final String _name;

    private final FlexiGridContainer1 _target;

    private final Hashtable<Component, FlexiGridConstraintsTable> _constraints = new Hashtable<>();

    private FlexiGridCache1 _cache;

    private final FlexiGridItemInfo.FlexiItemInfoFactory _itemInfoFactory;
    private FlexiGridPanelModel _flexiGridPanelModel = null;

    public FlexiGridLayoutManager(
            final @NotNull String name,
            final @NotNull FlexiGridContainer1 target,
            FlexiGridItemInfo.FlexiItemInfoFactory itemInfoFactory
    ) {

        super();

        _name = name;

        _target = target;

        _itemInfoFactory = itemInfoFactory;

        preLoadCacheIfNecessary();

    }

    public String getName() {

        return _name;
    }

    @Override
    public synchronized void addLayoutComponent( final Component comp, final Object constraints ) {

        flushCache( "addLayoutComponent", _target );

        if ( comp == null ) {

            throw new IllegalArgumentException( "FlexiGridLayoutManager.addLayoutComponent( Component, Object ):  component is null" );

        }

        _constraints.remove( comp );
        FlexiGridConstraintsTable componentConstraints;

        if ( constraints == null ) {

            throw new IllegalArgumentException( "FlexiGridLayoutManager.addLayoutComponent( Component, Object ):  constraints is null" );

        }

        if ( constraints instanceof FlexiGridConstraint ) {

            FlexiGridConstraint singletonConstraint = (FlexiGridConstraint)constraints;

            componentConstraints = new FlexiGridConstraintsTable( singletonConstraint );

        } else if ( constraints instanceof FlexiGridConstraintsTable ) {

            componentConstraints = (FlexiGridConstraintsTable)constraints;

        } else {

            throw new IllegalArgumentException(
                    "FlexiGridLayoutManager.addLayoutComponent( Component, Object ):  constraints not a " +
                    FlexiGridConstraintsTable.class.getCanonicalName() + " or a " + FlexiGridConstraint.class.getCanonicalName()
            );

        }

        FlexiGridConstraintsTable copy = new FlexiGridConstraintsTable();
        for ( FlexiGridConstraintCategory key : componentConstraints.keySet() ) {

            FlexiGridConstraint value = componentConstraints.get( key );

            if ( value == null ) {

                throw new IllegalArgumentException(
                        "FlexiGridLayoutManager.addLayoutComponent( Component, Object ):  constraint named \"" + key + "\" is null" );

            }

            copy.put( key, value );

        }

        _constraints.put( comp, copy );

    }

    private void checkContainer( final String who, final Container target ) {

        if ( target != _target ) {

            throw new IllegalArgumentException( "FlexiGridLayoutManager(" +
                                                who +
                                                "):  this instance dedicated to " +
                                                _target +
                                                ", cannot be switched to " +
                                                target );

        }

    }

    public synchronized void preLoadCacheIfNecessary() {

        if ( _target.getComponentCount() == 0 ) {

            return;

        }

        if ( _cache == null ) {

            Logger.logMsg( "FlexiGridLayoutManager:  creating cache for " + _name + " containing " + _target.getComponentCount() + " components" );

            _cache = new FlexiGridCache1(
                    _name,
                    this,
                    _target,
                    _constraints,
                    _itemInfoFactory
            );

        }

    }

    @NotNull
    public FlexiGridConstraintsTable getMandatoryConstraints( final @NotNull Component component ) {

        FlexiGridConstraintsTable rval = _constraints.get( component );
        if ( rval == null ) {

            for ( Component cc : _constraints.keySet() ) {

                logMaybe( "cc=" + ObtuseSwingUtils.describeComponent( cc ) + " = " + _constraints.get( cc ) );

            }

            throw new HowDidWeGetHereError( "FlexiGridConstraintsTable.getMandatoryConstraints:  component " + ObtuseSwingUtils.describeComponent( component ) + " not found" );

        }

        return rval;

    }

    public FlexiGridBasicConstraint getMandatoryBasicConstraint( final @NotNull Component component ) {

        FlexiGridConstraintsTable constraintsTable = getMandatoryConstraints( component );

        FlexiGridConstraint constraint = constraintsTable.get( FlexiGridConstraintCategory.BASIC );
        if ( constraint instanceof FlexiGridBasicConstraint ) {

            return (FlexiGridBasicConstraint)constraint;

        } else {

            throw new IllegalArgumentException( "FlexiGridLayoutManager.getMandatoryBasicConstraint:  component " + component.getName() + " does not have the mandatory FlexiGridBasicConstraint" );

        }

    }

    public Optional<FlexiGridLayoutManagerCache> getOptionalCache() {

        return Optional.ofNullable( _cache );

    }

    public Optional<Long> getCacheSerialNumber() {

        if ( _cache == null ) {

            return Optional.empty();

        } else {

            return Optional.of( _cache.getSerialNumber() );

        }

    }

    @Override
    public synchronized float getLayoutAlignmentX( final Container target ) {

        checkContainer( "getLayoutAlignmentX", target );
        preLoadCacheIfNecessary();

        if ( _cache == null ) {

            return 0.5f;

        }

        return _cache.getLayoutAlignmentX();

    }

    @Override
    public float getLayoutAlignmentY( final Container target ) {

        checkContainer( "getLayoutAlignmentY", target );
        preLoadCacheIfNecessary();

        if ( _cache == null ) {

            return 0.5f;

        }

        return _cache.getLayoutAlignmentY();

    }

    /**
     Provide a way for the general public to flush our cache.
     <p>Overuse of this method can slow things down although it probably takes quite a lot of overuse
     to make a visible difference.</p>
     <p>The cache will be automagically reloaded when we need it next.</p>
     @param requester who's asking.
     */

    public void flushCache( final @NotNull String requester ) {

        flushCache( requester, _target );

    }

    /**
     Flush our cache on behalf of a specified target container.
     <p>In practical terms, this variant exists because our public {@link #invalidateLayout(Container)} method (defined by the
     {@link LayoutManager2} interface that we implement) takes a target container reference.
     If it wasn't for that method, we could merge our public {@link #flushCache(String)} with this method.
     Sigh.
     </p>
     <p>The cache will be automagically reloaded when we need it next.</p>
     @param requester who's asking.
     @param target the target container specified by our caller (other than possibly in log messages, we ignore this parameter).
     */

    private synchronized void flushCache( final @NotNull String requester, final @Nullable FlexiGridContainer target ) {

        if ( _cache != null && LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {

            logMaybe( "layout invalidated by " + requester + " (target is " + target + ")" );

        }

        if ( _cache != null ) {

            ObtuseUtil.doNothing();

        }

        _cache = null;

    }

    @SuppressWarnings("Duplicates")
    @Override
    public synchronized Dimension preferredLayoutSize( final Container target ) {

        checkContainer( "preferredLayoutSize", target );
        preLoadCacheIfNecessary();

        // preLoadCacheIfNecessary will leave _cache null if-and-only-if the target container is empty.
        // If that's the case then we've no idea how small the target container might become.
        // Let's say [0,0] and see what happens.

        Dimension size = _cache == null ? new Dimension( 0, 0 ) : _cache.getPreferredSize();
        logMaybe( "FlexiGridLayoutManager.preferredLayoutSize:  " + size );

        if ( "outer".equals( getTarget().getName() ) ) {

            ObtuseUtil.doNothing();

        }

        return size;

    }

    @Override
    public synchronized Dimension minimumLayoutSize( final Container target ) {

        checkContainer( "minimumLayoutSize", target );
        preLoadCacheIfNecessary();

        // preLoadCacheIfNecessary will leave _cache null if-and-only-if the target container is empty.
        // If that's the case then we've no idea how small the target container might become.
        // Let's say [0,0] and see what happens.

        Dimension size = _cache == null ? new Dimension( 0, 0 ) : _cache.getMinimumSize();
        logMaybe( "FlexiGridLayoutManager.minimumLayoutSize:  " + size );

        return size;

    }

    @Override
    public synchronized Dimension maximumLayoutSize( final Container target ) {

        checkContainer( "maximumLayoutSize", target );
        preLoadCacheIfNecessary();

        // preLoadCacheIfNecessary will leave _cache null if-and-only-if the target container is empty.
        // If that's the case then we've no idea how small the target container might become.
        // Let's say [0,0] and see what happens.

        Dimension size = _cache == null ? new Dimension( 0, 0 ) : _cache.getMaximumSize();
        logMaybe( "FlexiGridLayoutManager.maximumLayoutSize:  " + size );

        return size;

    }

    @Override
    public synchronized void invalidateLayout( final Container target ) {

        checkContainer( "invalidateLayout", target );

        flushCache( "invalidateLayout", (FlexiGridContainer)target );

    }

    @Override
    public void addLayoutComponent( final String name, final Component comp ) {

        logMaybe( "addLayoutComponent( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )" );

        flushCache( "addLayoutComponent", _target );

    }

    @Override
    public synchronized void removeLayoutComponent( final Component comp ) {

        flushCache( "removeLayoutComponent", _target );

        if ( comp == null ) {

            throw new IllegalArgumentException( "FlexiGridLayoutManager.removeLayoutComponent( Component ):  component is null" );

        } else {

            _constraints.remove( comp );

        }

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void layoutContainer( final Container parent ) {

        if ( parent != _target.getAsContainer() ) {

            throw new IllegalArgumentException(
                    "FlexiGridLayoutManager.layoutContainer:  " +
                    "parent(" + ObtuseSwingUtils.describeComponent( parent ) + ") " +
                    "is not our target container " +
                    "(" + ObtuseSwingUtils.describeComponent( _target ) + ")"
            );

        }

        logMaybe( "laying out container currently sized at " + ObtuseUtil.fBounds( _target.getBounds() ) );

        logMaybe( "FlexiGridLayoutManager.layoutContainer( " + ObtuseSwingUtils.describeComponent( _target ) + " ):  it begins" );

        FlexiGridCache1 cache;

        synchronized ( this ) {

            if ( LinearLayoutUtil.isContainerOnWatchlist( _target ) ) {

                ObtuseUtil.doNothing();

            }

            checkContainer( "layoutContainer", _target );

            preLoadCacheIfNecessary();

            cache = _cache;

        }

        if ( cache == null ) {

            return;

        }

        cache.setComponentBounds();

        Dimension targetMinimumSize = _target.getMinimumSize();
        Dimension targetPreferredSize = _target.getPreferredSize();
        Dimension targetMaximumSize = _target.getMaximumSize();

        logMaybe(
                "FlexiGridLayoutManager.layoutContainer:  " +
                "min=" + ObtuseUtil.fDim( targetMinimumSize ) + ", " +
                "pref=" + ObtuseUtil.fDim( targetPreferredSize ) + ", " +
                "max=" + ObtuseUtil.fDim( targetMaximumSize )
        );

        int containerWidth = 0;
        int containerHeight = 0;

        for ( int i = 0; i < cache.getVisibleComponentCount(); i += 1 ) {

            Component c = cache.getVisibleComponent( i );
            Rectangle bounds = c.getBounds();
            containerWidth = Math.max( containerWidth, bounds.x + bounds.width );
            containerHeight = Math.max( containerHeight, bounds.y + bounds.height );

        }

        Rectangle bounds = new Rectangle( 0, 0, containerWidth, containerHeight );
        logMaybe( "FlexiGridLayoutManager.layoutContainer:  container will be " + ObtuseUtil.fBounds( bounds ) + ", insets=" + _target.getInsets() );

        Dimension dimensions = new Dimension( containerWidth, containerHeight );
        cache.addInsets( dimensions );
        logMaybe( "FlexiGridLayoutManager.layoutContainer:  dimensions including insets will be " + ObtuseUtil.fDim( dimensions ) );

        logMaybe( "FlexiGridLayoutManager.layoutContainer:  dimensions actually are " + ObtuseUtil.fBounds( _target.getBounds() ) );

        ObtuseUtil.doNothing();

    }

    /* package private */ void logMaybe( final String msg ) {

        if ( isTraceMode() ) {

            Logger.logMsg( msg );

        }

    }

    @SuppressWarnings("unused")
    public void setTraceMode( boolean traceMode ) {

        _traceMode = traceMode;

    }

    public boolean isTraceMode() {

        return _traceMode;

    }

    public FlexiGridContainer1 getTarget() {

        return _target;

    }

    public String toString() {

        return "FlexiGridLayoutManager( container=" + getTarget() + " )";

    }

    public void setFlexiGridPanelModel( final @NotNull FlexiGridPanelModel flexiGridPanelModel ) {

        if ( _flexiGridPanelModel != null && _flexiGridPanelModel != flexiGridPanelModel ) {

            throw new IllegalArgumentException( "FlexiGridLayoutManager.setFlexiGridPanelModel:  model cannot be changed once set" );

        }

        _flexiGridPanelModel = flexiGridPanelModel;

    }

    @NotNull
    public Optional<FlexiGridPanelModel> getFlexiGridPanelModel() {

        return Optional.ofNullable( _flexiGridPanelModel );

    }

}
