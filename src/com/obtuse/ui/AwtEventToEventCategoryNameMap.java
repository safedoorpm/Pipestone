package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.*;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Describe AWT event masks.
 */

public class AwtEventToEventCategoryNameMap {

    public static SortedMap<Long,String> s_maskToEventCategoryNameMap = new TreeMap<>();

    static {

        // Component events
        s_maskToEventCategoryNameMap.put( AWTEvent.COMPONENT_EVENT_MASK, "COMPONENT_EVENT_MASK" );

        // Container events
        s_maskToEventCategoryNameMap.put( AWTEvent.CONTAINER_EVENT_MASK, "CONTAINER_EVENT_MASK" );

        // Focus events
        s_maskToEventCategoryNameMap.put( AWTEvent.FOCUS_EVENT_MASK, "FOCUS_EVENT_MASK" );

        // Key events
        s_maskToEventCategoryNameMap.put( AWTEvent.KEY_EVENT_MASK, "KEY_EVENT_MASK" );

        // Mouse events
        s_maskToEventCategoryNameMap.put( AWTEvent.MOUSE_EVENT_MASK, "MOUSE_EVENT_MASK" );

        // Mouse motion events
        s_maskToEventCategoryNameMap.put( AWTEvent.MOUSE_MOTION_EVENT_MASK, "MOUSE_MOTION_EVENT_MASK" );

        // Window events
        s_maskToEventCategoryNameMap.put( AWTEvent.WINDOW_EVENT_MASK, "WINDOW_EVENT_MASK" );

        // Action events
        s_maskToEventCategoryNameMap.put( AWTEvent.ACTION_EVENT_MASK, "ACTION_EVENT_MASK" );

        // Adjustment events
        s_maskToEventCategoryNameMap.put( AWTEvent.ADJUSTMENT_EVENT_MASK, "ADJUSTMENT_EVENT_MASK" );

        // Item events
        s_maskToEventCategoryNameMap.put( AWTEvent.ITEM_EVENT_MASK, "ITEM_EVENT_MASK" );

        // Text events
        s_maskToEventCategoryNameMap.put( AWTEvent.TEXT_EVENT_MASK, "TEXT_EVENT_MASK" );

        // Input method events
        s_maskToEventCategoryNameMap.put( AWTEvent.INPUT_METHOD_EVENT_MASK, "INPUT_METHOD_EVENT_MASK" );

    // This one is package-private so we don't get to see them unless we do dumb things.
    //        /*
    //         * The pseudo event mask for enabling input methods.
    //         * We're using one bit in the eventMask so we don't need
    //         * a separate field inputMethodsEnabled.
    //         */
    //        s_maskToEventCategoryNameMap.put( AWTEvent.INPUT_METHODS_ENABLED_MASK, "x" );

        // Paint events
        s_maskToEventCategoryNameMap.put( AWTEvent.PAINT_EVENT_MASK, "PAINT_EVENT_MASK" );

        // Invocation events
        s_maskToEventCategoryNameMap.put( AWTEvent.INVOCATION_EVENT_MASK, "INVOCATION_EVENT_MASK" );

        // Hierarchy events
        s_maskToEventCategoryNameMap.put( AWTEvent.HIERARCHY_EVENT_MASK, "HIERARCHY_EVENT_MASK" );

        // Hierarchy bounds events
        s_maskToEventCategoryNameMap.put( AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK, "HIERARCHY_BOUNDS_EVENT_MASK" );

        // Mouse wheel events
        s_maskToEventCategoryNameMap.put( AWTEvent.MOUSE_WHEEL_EVENT_MASK, "MOUSE_WHEEL_EVENT_MASK" );

        // Window state events
        s_maskToEventCategoryNameMap.put( AWTEvent.WINDOW_STATE_EVENT_MASK, "WINDOW_STATE_EVENT_MASK" );

        // Window focus events
        s_maskToEventCategoryNameMap.put( AWTEvent.WINDOW_FOCUS_EVENT_MASK, "WINDOW_FOCUS_EVENT_MASK" );

    }

    /**
     Emphasise that this is a utility class.
     */

    private AwtEventToEventCategoryNameMap() {
        super();
    }

    private static long extractMask( int id, int firstId, int lastId, long mask ) {

        if ( id >= firstId && id <= lastId ) {

            return mask;

        } else {

            return 0;

        }

    }

    private static long extractMask( int id, int[] ids, long mask ) {

        for ( int xId : ids ) {

            if ( id == xId ) {

                return mask;

            }

        }

        return 0;

    }

    public static long getComponentEventMask( @NotNull final AWTEvent event ) {

        int id = event.getID();
        long mask = 0L;

        mask |= extractMask( id, ComponentEvent.COMPONENT_FIRST, ComponentEvent.COMPONENT_LAST, AWTEvent.COMPONENT_EVENT_MASK );
        mask |= extractMask( id, ContainerEvent.CONTAINER_FIRST, ContainerEvent.CONTAINER_LAST, AWTEvent.CONTAINER_EVENT_MASK );
        mask |= extractMask( id, FocusEvent.FOCUS_FIRST, FocusEvent.FOCUS_LAST, AWTEvent.FOCUS_EVENT_MASK );
        mask |= extractMask( id, KeyEvent.KEY_FIRST, KeyEvent.KEY_LAST, AWTEvent.KEY_EVENT_MASK );
        mask |= extractMask( id, MouseEvent.MOUSE_WHEEL, MouseEvent.MOUSE_WHEEL, AWTEvent.MOUSE_WHEEL_EVENT_MASK );
        mask |= extractMask( id, new int[] { MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_DRAGGED }, AWTEvent.MOUSE_MOTION_EVENT_MASK );
        mask |= (
                id != MouseEvent.MOUSE_MOVED &&
                id != MouseEvent.MOUSE_DRAGGED &&
                id != MouseEvent.MOUSE_WHEEL &&
                id >= MouseEvent.MOUSE_FIRST && id <= MouseEvent.MOUSE_LAST
                )
                ? AWTEvent.MOUSE_EVENT_MASK
                : 0L;
        mask |= extractMask( id, InputMethodEvent.INPUT_METHOD_FIRST, InputMethodEvent.INPUT_METHOD_LAST, AWTEvent.INPUT_METHOD_EVENT_MASK );
        mask |= extractMask( id, HierarchyEvent.HIERARCHY_CHANGED, HierarchyEvent.HIERARCHY_CHANGED, AWTEvent.HIERARCHY_EVENT_MASK );
        mask |= extractMask( id, new int[] { HierarchyEvent.ANCESTOR_MOVED, HierarchyEvent.ANCESTOR_RESIZED }, AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK );

        return mask;

    }

}
