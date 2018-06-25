/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.ImageIconUtils;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Along the lines of {@link javax.swing.JSlider} but capable of being configured to use
 * considerably less screen real estate and supports more than one slider point.
 */

@SuppressWarnings("UnusedDeclaration")
public class MultiPointSlider extends JComponent {

    private static final boolean FORCE_TEST_BACKGROUND_COLOR = false;

    public static final int MINIMUM_LINE_LENGTH = 25;
    public static final int MINIMUM_LABEL_GAP_SPACE = 2;
    private boolean _drawSliderLine = true;
    public static final boolean REAL_MODE = true;

    private static boolean s_traceSizeChanges = true;

    private Dictionary<Integer, MpsLabel> _labelTable = null;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Dictionary<Integer, BufferedImage> _cachedLabelTable = new Hashtable<>();
    private BoundedRangeModel _brm = null;
    private MpsKnobSize _knobSize;
    private PositionOnLine _positionOnLine = null;
    private boolean _isSelected = false;
    private Point _startingPoint = null;
    private int _startingValue = 0;
    private int _minorTickSpacing = 0;
    private int _majorTickSpacing = 0;
    private boolean _paintTicks;
    private boolean _paintLabels;
    private MpsKnob _knob;
    private ChangeListener _myChangeListener = new ChangeListener() {

        public void stateChanged( final ChangeEvent changeEvent ) {

            repaint();
            notifyListeners( changeEvent );

        }

    };

    private static final MpsKnob DEFAULT_KNOB;
    private Collection<ChangeListener> _changeListeners = new LinkedList<>();
    private int _minimumBreadth = 0;
    private int _minimumLength = 0;
    private static final int TIC_GAP = 2;

    private Dimension _minimumSize = null;
    private final String _name;
    private static final int BORDER_SIZE = 2;
    private static final int MINIMUM_TIC_ROOM = 5;
    private static final int GAP_BETWEEN_TICK_MARKS_AND_LABELS = 2;
    private int _linePosition = 0;
    private Dimension _lastMinimumSize = null;

    static {

        ImageIcon imageIcon = ImageIconUtils.fetchIconImage(
                "slider_knob_13x13.png",
                0,
                "com/obtuse/ui/resources"
        );

        DEFAULT_KNOB = new DefaultMpsKnob( imageIcon.getImage() );

    }

    public enum PositionOnLine {

        ABOVE,
        BELOW,
        LEFT,
        RIGHT

    }

    public MultiPointSlider( final String name, final BoundedRangeModel brm ) {

        super();

        _name = name;

        //noinspection OverridableMethodCallDuringObjectConstruction
        setModel( brm );

        _knob = MultiPointSlider.DEFAULT_KNOB;

        _paintTicks = false;
        _paintLabels = false;

        setPositionOnLine( PositionOnLine.ABOVE );

        _knobSize = MpsKnobSize.SIZE_13x13;

        setOpaque( false );
        setVisible( true );

        addMouseListener(
                new MouseListener() {

                    public void mouseClicked( final MouseEvent mouseEvent ) {

                        ObtuseUtil.doNothing();

                    }

                    public void mousePressed( final MouseEvent mouseEvent ) {

                        Point hotSpot = mapValueToPoint( _brm.getValue() );
                        boolean insideKnob = _knob.isPointOnKnob(
                                hotSpot,
                                _knobSize,
                                _isSelected,
                                _positionOnLine,
                                mouseEvent.getPoint()
                        );

                        if ( insideKnob ) {

                            _isSelected = true;
                            _startingPoint = mouseEvent.getPoint();
                            _startingValue = _brm.getValue();
                            repaint();

                        } else {

                            _isSelected = false;

                        }

                    }

                    public void mouseReleased( final MouseEvent mouseEvent ) {

                        //noinspection StatementWithEmptyBody
                        if ( _isSelected ) {

                            adjustValue( mouseEvent.getPoint() );

                            _isSelected = false;

                            repaint();

                        }

                    }

                    public void mouseEntered( final MouseEvent mouseEvent ) {

                        ObtuseUtil.doNothing();

                    }

                    public void mouseExited( final MouseEvent mouseEvent ) {

                        ObtuseUtil.doNothing();

                    }

                }
        );

        addMouseMotionListener(
                new MouseMotionListener() {

                    public void mouseDragged( final MouseEvent mouseEvent ) {

                        //noinspection StatementWithEmptyBody
                        if ( _isSelected ) {

                            adjustValue( mouseEvent.getPoint() );

                        }

                    }

                    public void mouseMoved( final MouseEvent mouseEvent ) {

                        ObtuseUtil.doNothing();

                    }

                }
        );

    }

    public static boolean traceSizeChanges() {

        return MultiPointSlider.s_traceSizeChanges;

    }

    public static void setTraceSizeChanges( final boolean traceSizeChanges ) {

        MultiPointSlider.s_traceSizeChanges = traceSizeChanges;

    }

    public MpsKnobSize getKnobSize() {

        return _knobSize;

    }

    public void setKnobSize( final MpsKnobSize knobSize ) {

        _knobSize = knobSize;

    }

    public MpsKnob getKnob() {

        return _knob;

    }

    public static MpsKnob getDefaultKnob() {

        return MultiPointSlider.DEFAULT_KNOB;

    }

    public void setKnob( final MpsKnob knob ) {

        if ( knob == null ) {

            throw new IllegalArgumentException( "knob is null" );

        }

        _knob = knob;

        _minimumSize = null;

    }

    private void notifyListeners( final ChangeEvent changeEvent ) {

        for ( ChangeListener listener : _changeListeners ) {

            listener.stateChanged( changeEvent );

        }

    }

    public void addChangeListener( final ChangeListener listener ) {

        removeChangeListener( listener );
        _changeListeners.add( listener );

    }

    public void removeChangeListener( final ChangeListener listener ) {

        _changeListeners.remove( listener );

    }

    public boolean isVerticalOrientation() {

        return _positionOnLine == PositionOnLine.LEFT || _positionOnLine == PositionOnLine.RIGHT;

    }

    private void adjustValue( final Point clickPoint ) {

        computeDrawingParameters();

        int moveDistance = isVerticalOrientation() ? clickPoint.y - _startingPoint.y : clickPoint.x - _startingPoint.x;

        Point startingHotSpot = mapValueToPoint( _startingValue );

        Point newPoint = isVerticalOrientation() ?
                         new Point(
                                 startingHotSpot.x,
                                 startingHotSpot.y + moveDistance
                         ) :
                         new Point(
                                 startingHotSpot.x + moveDistance,
                                 startingHotSpot.y
                         );

        int newValue = mapPointToValue( newPoint );

        // Set the value in our model.
        // The change listener that we have attached to this model will be triggered
        // if this setValue called actually changes the model's value.

        _brm.setValue( newValue );

    }

    @SuppressWarnings("UnusedDeclaration")
    public void showPixels() {

        for ( int x = 0; x < _knob.getImage().getWidth( null ); x += 1 ) {

            for ( int y = 0; y < _knob.getImage().getHeight( null ); y += 1 ) {

                Logger.logMsg( ObtuseUtil.hexvalue( _knob.getImage().getRGB( x, y ) ) + " " );

            }

            Logger.logMsg( "" );

        }

    }

    public MultiPointSlider( final String name, final int min, final int max ) {

        this( name, min, max, min );

    }

    public MultiPointSlider( final String name, final int min, final int max, final int value ) {

        this(
                name,
                new DefaultBoundedRangeModel( value, 0, min, max )
        );

    }

    public BoundedRangeModel getModel() {

        return _brm;

    }

    public void setMinimum( final int minimum ) {

        _brm.setMinimum( minimum );
        repaint();

    }

    public void setMaximum( final int maximum ) {

        _brm.setMaximum( maximum );
        repaint();

    }

    public void setExtent( final int extent ) {

        _brm.setExtent( extent );
        repaint();

    }

    public void setValue( final int value ) {

        _brm.setValue( value );
        repaint();

    }

    public int getValue() {

        return _brm.getValue();

    }

    private Point mapValueToPoint( final int value ) {

        if ( _positionOnLine == PositionOnLine.ABOVE || _positionOnLine == PositionOnLine.BELOW ) {

            return new Point(
                    _endSpace + _length * ( value - _brm.getMinimum() ) / ( _brm.getMaximum() - _brm.getMinimum() ),
                    _linePosition
            );

        } else {

            return new Point(
                    _linePosition,
                    _endSpace + _length * ( value - _brm.getMinimum() ) / ( _brm.getMaximum() - _brm.getMinimum() )
            );

        }

    }

    private int mapPointToValue( final Point p ) {

        return _brm.getMinimum() +
               Math.round(
                       (
                               ( ( isVerticalOrientation() ? p.y : p.x ) - _endSpace ) *
                               ( _brm.getMaximum() - _brm.getMinimum() )
                       ) / (float)_length
               );

    }

    private int _width = -1;
    private int _height = -1;
    private int _length = -1;
    private int _endSpace = -1;

    private void computeDrawingParameters() {

        int width = getWidth();
        int height = getHeight();

        _width = width;
        _height = height;

        computeMinimumSize();

    }

    public BufferedImage getLabel( final Graphics2D g2d, final int value ) {

        BufferedImage labelImage = _cachedLabelTable.get( value );
        if ( labelImage == null ) {

            MpsLabel sliderLabel = null;
            if ( _labelTable != null ) {

                sliderLabel = _labelTable.get( value );

            }

            if ( sliderLabel == null ) {

                if ( _majorTickSpacing != 0 && ( value - _brm.getMinimum() ) % _majorTickSpacing == 0 ) {

                    sliderLabel = new MpsLabel( value );

                }

            }

            if ( sliderLabel == null ) {

                return null;

            }

            labelImage = sliderLabel.getGeneratedImage( g2d );
            _cachedLabelTable.put( value, labelImage );

        }

        return labelImage;

    }

    private boolean isInteresting() {

        return "weight".equals( _name ) || "center".equals( _name ) || "standard deviation".equals( _name );

    }

    @SuppressWarnings("ConstantConditions")
    public Dimension computeMinimumSize() {

        OrientedImage orientedImage = _knob.getOrientedImage( _knobSize, _positionOnLine, _isSelected );

        int knobBreadth;
        if ( isVerticalOrientation() ) {

            knobBreadth = orientedImage.getScreenWidth();

        } else {

            knobBreadth = orientedImage.getScreenHeight();

        }

        int breadth = 0;
        breadth += knobBreadth;

        if ( _paintTicks && ( _minorTickSpacing > 0 || _majorTickSpacing > 0 ) ) {

            int ticSpace = 0;
            if ( _minorTickSpacing > 0 ) {

                ticSpace = drawTickMarks( null, _minorTickSpacing, 2 );

            }

            if ( _majorTickSpacing > 0 ) {

                ticSpace = Math.max( ticSpace, drawTickMarks( null, _majorTickSpacing, 6 ) );

            }

            breadth += ticSpace;

        }

        // Part of the knob will protrude out before the start of the line if it is
        // moved to the very start of the line.  Similarly, part of the knob will
        // protrude out after the end of the line if the knob is moved to the very
        // end of the line.  We need to know just how much the knob could protrude
        // out each end.  Note that the knob is not necessarily symmetric or, more
        // to the point, the hot spot is not necessarily located at the midpoint
        // of the knob.

        int minValueOverhang;
        int maxValueOverhang;
        if ( isVerticalOrientation() ) {

            minValueOverhang = orientedImage.getHotSpotWithinImage().y;
            maxValueOverhang = orientedImage.getScreenHeight() - orientedImage.getHotSpotWithinImage().y;

        } else {

            minValueOverhang = orientedImage.getHotSpotWithinImage().x;
            maxValueOverhang = orientedImage.getScreenWidth() - orientedImage.getHotSpotWithinImage().x;

        }

        // Don't let the overhangs go negative (seems unlikely but the consequences would be pretty confusing).

        minValueOverhang = Math.max( 0, minValueOverhang );
        maxValueOverhang = Math.max( 0, maxValueOverhang );

        /*
         * Do we need to account for the space consumed by labels?
         */

        int length = 0;
        if ( _paintLabels && _majorTickSpacing > 0 ) {

            Graphics2D g2d = (Graphics2D)getGraphics();
            if ( g2d == null ) {

                g2d = (Graphics2D)orientedImage.getImage().getGraphics();

            }
            g2d.setFont( getFont() );

            int maxLabelBreadth = 0;
            int maxLabelLength = 0;
            int labelCount = 0;
            BufferedImage firstLabel = null;
            BufferedImage lastLabel = null;
            for ( int value = _brm.getMinimum(); value <= _brm.getMaximum(); value += _majorTickSpacing ) {

                BufferedImage labelImage = getLabel( g2d, value );

                if ( labelImage == null ) {

                    Logger.logMsg( "label image is null when computing size" );

                } else {

                    if ( firstLabel == null ) {

                        firstLabel = labelImage;

                    }

                    lastLabel = labelImage;

                    labelCount += 1;

                    if ( isVerticalOrientation() ) {

                        maxLabelBreadth = Math.max( maxLabelBreadth, labelImage.getWidth() );
                        maxLabelLength = Math.max( maxLabelLength, labelImage.getHeight() );

                    } else {

                        maxLabelBreadth = Math.max( maxLabelBreadth, labelImage.getHeight() );
                        maxLabelLength = Math.max( maxLabelLength, labelImage.getWidth() );

                    }

                }

            }

            breadth += MultiPointSlider.GAP_BETWEEN_TICK_MARKS_AND_LABELS + maxLabelBreadth;

            // Half of the first label and (sometimes) half of the knob protrude out before the start
            // of the line.  Similarly, half of the last label and (sometimes) half of the knob
            // protrudes out past the end of the line.  We need to remember the larger of
            // half the width of the first label and however much of the knob could protrude
            // out before the start of the line as well as the larger of half the width of the last
            // label and however much of the knob could protrude out before the end of the line.
            // We computed how much of the knob might protrude out each end earlier so we just
            // need to remember the max of each pair.

            if ( isVerticalOrientation() ) {

                if ( firstLabel != null ) {

                    minValueOverhang = Math.max( ( firstLabel.getHeight() + 1 ) / 2, minValueOverhang );

                }

                if ( lastLabel != null ) {

                    maxValueOverhang = Math.max( ( lastLabel.getHeight() + 1 ) / 2, maxValueOverhang );

                }

            } else {

                if ( firstLabel != null ) {

                    minValueOverhang = Math.max( ( firstLabel.getWidth() + 1 ) / 2, minValueOverhang );

                }

                if ( lastLabel != null ) {

                    maxValueOverhang = Math.max( ( lastLabel.getWidth() + 1 ) / 2, maxValueOverhang );

                }

            }

            // Compute the minimum line length while making sure we don't end up with a teensy tiny line.

            int minLabelSpace = labelCount * maxLabelLength + ( labelCount - 1 ) *
                                                              MultiPointSlider.MINIMUM_LABEL_GAP_SPACE;

            length = Math.max( MultiPointSlider.MINIMUM_LINE_LENGTH, minLabelSpace );

        } else {

            String when;
            if ( _paintTicks ) {

                //noinspection UnusedAssignment
                when = "1";
                if ( _minorTickSpacing > 0 ) {

                    //noinspection UnusedAssignment
                    when = "2";
                    //noinspection UnnecessaryParentheses
                    length = ( ( _brm.getMaximum() - _brm.getMinimum() ) / _minorTickSpacing ) *
                             MultiPointSlider.MINIMUM_TIC_ROOM;

                }

                if ( _majorTickSpacing > 0 ) {

                    //noinspection UnusedAssignment
                    when = "3";
                    //noinspection UnnecessaryParentheses
                    length = Math.max(
                            ( ( _brm.getMaximum() - _brm.getMinimum() ) / _majorTickSpacing ) *
                            MultiPointSlider.MINIMUM_TIC_ROOM, length
                    );

                }

            } else {

                //noinspection UnusedAssignment
                when = "4";
                length = MultiPointSlider.MINIMUM_LINE_LENGTH;

            }

        }

        _length = length;
        _endSpace = MultiPointSlider.BORDER_SIZE + minValueOverhang;

        length = minValueOverhang + length + maxValueOverhang;

        // We've got it!

        Dimension actualSize;

        if ( isVerticalOrientation() ) {

            _minimumSize = new Dimension(
                    2 * MultiPointSlider.BORDER_SIZE + breadth,
                    2 * MultiPointSlider.BORDER_SIZE + length
            );
            actualSize = new Dimension(
                    Math.max( _minimumSize.width, getWidth() ),
                    Math.max( _minimumSize.height, getHeight() )
            );
            if ( _minimumSize.height < actualSize.height ) {

                _length =
                        actualSize.height - ( 2 * MultiPointSlider.BORDER_SIZE + minValueOverhang + maxValueOverhang );
                _minimumSize.height = actualSize.height;

            }

        } else {

            _minimumSize = new Dimension(
                    2 * MultiPointSlider.BORDER_SIZE + length,
                    2 * MultiPointSlider.BORDER_SIZE + breadth
            );
            actualSize = new Dimension(
                    Math.max( _minimumSize.width, getWidth() ),
                    Math.max( _minimumSize.height, getHeight() )
            );

            if ( _minimumSize.width < actualSize.width ) {

                _length = actualSize.width - ( 2 * MultiPointSlider.BORDER_SIZE + minValueOverhang + maxValueOverhang );
                _minimumSize.width = actualSize.width;

            }

        }

        switch ( _positionOnLine ) {

            case ABOVE:
                _linePosition = MultiPointSlider.BORDER_SIZE + orientedImage.getHotSpotWithinImage().y;
                break;

            case BELOW:
                _linePosition = MultiPointSlider.BORDER_SIZE + breadth - orientedImage.getImage().getHeight( null ) +
                                orientedImage.getHotSpotWithinImage().y;
                break;

            case LEFT:
                _linePosition = MultiPointSlider.BORDER_SIZE + orientedImage.getHotSpotWithinImage().x;
                break;

            case RIGHT:
                _linePosition = MultiPointSlider.BORDER_SIZE + breadth - orientedImage.getImage().getWidth( null ) +
                                orientedImage.getHotSpotWithinImage().x;

        }

        if ( isInteresting() && (
                _lastMinimumSize == null || _lastMinimumSize.width != _minimumSize.width ||
                _lastMinimumSize.height != _minimumSize.height
        ) ) {

            Logger.logMsg( _name + "  computePreferredSize() returning " + _minimumSize );
            _lastMinimumSize = _minimumSize;

        }

        return _minimumSize;

    }

    public Dimension getMinimumSize() {

        Dimension minimumSize = computeMinimumSize();
        Dimension rval = super.getMinimumSize();
        if ( isInteresting() ) {

            Logger.logMsg( _name + ": getMinimumSize() returned " + rval );

        }

        return rval;

    }

    public Dimension getMaximumSize() {

        Dimension rval = super.getMaximumSize();
        if ( isInteresting() ) {

            Logger.logMsg( _name + ": getMaximumSize() returned " + rval );

        }

        return rval;

    }

    public Dimension getPreferredSize() {

        Dimension preferredSize = computeMinimumSize();
        Dimension rval = super.getPreferredSize();
        rval.width = Math.max( preferredSize.width, rval.width );
        rval.height = Math.max( preferredSize.height, rval.height );
        if ( isInteresting() ) {

            Logger.logMsg( _name + ": getPreferredSize() returned " + rval );

        }

        return rval;

    }

    public Rectangle getBounds() {

        Rectangle rval = super.getBounds();
        if ( isInteresting() ) {

            Logger.logMsg( _name + ":  getBounds() returned " + rval );

        }

        return rval;

    }

    public void setMinimumSize( final Dimension size ) {

        if ( MultiPointSlider.s_traceSizeChanges && isInteresting() ) {

            Logger.logMsg( _name + ":  call to setMinimumSize( " + size + ")" );

        }

        super.setMinimumSize( size );

    }

    public void setMaximumSize( final Dimension size ) {

        if ( MultiPointSlider.s_traceSizeChanges && isInteresting() ) {

            Logger.logMsg( _name + ":  call to setMaximumSize( " + size + ")" );

        }

        super.setMaximumSize( size );

    }

    public void setPreferredSize( final Dimension size ) {

        if ( MultiPointSlider.s_traceSizeChanges && isInteresting() ) {

            Logger.logMsg( _name + ":  call to setPreferredSize( " + size + ")" );

        }

        super.setPreferredSize( size );

    }

    public void setBounds( final int x, final int y, final int width, final int height ) {

        if ( MultiPointSlider.s_traceSizeChanges && isInteresting() ) {

            Logger.logMsg( _name + ":  call to setBounds( " + x + ", " + y + ", " + width + ", " + height + ")" );

        }

        super.setBounds( x, y, width, height );

    }

    public void setBounds( final Rectangle bounds ) {

        if ( MultiPointSlider.s_traceSizeChanges && isInteresting() ) {

            Logger.logMsg( _name + ":  call to setBounds( " + bounds + ")" );

        }

        super.setBounds( bounds );

    }

    public void paint( final Graphics g ) {

        Graphics2D g2d = (Graphics2D)g;
        computeDrawingParameters();

        if ( isInteresting() ) {

            Logger.logMsg(
                    "painting " + _name + " with size ( " + _width + ", " + _height + " ) and background " +
                    getBackground()
            );

        }

        //noinspection ConstantConditions
        g.setColor( MultiPointSlider.FORCE_TEST_BACKGROUND_COLOR ? Color.WHITE : getBackground() );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        g.setColor( Color.BLACK );

        int ticSpace = 0;

        if ( _paintTicks && ( _minorTickSpacing > 0 || _majorTickSpacing > 0 ) ) {

            if ( _minorTickSpacing > 0 ) {

                ticSpace = drawTickMarks( g, _minorTickSpacing, 2 );

            }

            if ( _majorTickSpacing > 0 ) {

                ticSpace = drawTickMarks( g, _majorTickSpacing, 6 );

            }

        }

        if ( _paintLabels && _majorTickSpacing > 0 ) {

            ticSpace += MultiPointSlider.GAP_BETWEEN_TICK_MARKS_AND_LABELS;

            for ( int value = _brm.getMinimum(); value <= _brm.getMaximum(); value += _majorTickSpacing ) {

                Point valuePoint = mapValueToPoint( value );
                BufferedImage labelImage = getLabel( g2d, value );
                if ( labelImage == null ) {

                    Logger.logMsg( "no label image when drawing slider" );

                } else {

                    switch ( _positionOnLine ) {

                        case ABOVE:
                            g.drawImage(
                                    labelImage,
                                    valuePoint.x - labelImage.getWidth() / 2,
                                    valuePoint.y + ticSpace,
                                    this
                            );
                            break;

                        case BELOW:
                            g.drawImage(
                                    labelImage,
                                    valuePoint.x - labelImage.getWidth() / 2,
                                    valuePoint.y - ( labelImage.getHeight() + ticSpace ),
                                    this
                            );
                            break;

                        case LEFT:
                            g.drawImage(
                                    labelImage,
                                    valuePoint.x + ticSpace,
                                    valuePoint.y - labelImage.getHeight() / 2,
                                    this
                            );
                            break;

                        case RIGHT:
                            g.drawImage(
                                    labelImage,
                                    valuePoint.x - ( labelImage.getWidth() + ticSpace ),
                                    valuePoint.y - labelImage.getHeight() / 2,
                                    this
                            );
                            break;

                    }

                }

            }

        }

        g.setColor( Color.BLACK );
        Point lineStart = mapValueToPoint( _brm.getMinimum() );
        Point lineEnd = mapValueToPoint( _brm.getMaximum() );
        if ( _drawSliderLine ) {

            g.drawLine( lineStart.x, lineStart.y, lineEnd.x, lineEnd.y );

        }

        Point hotSpot = mapValueToPoint( _brm.getValue() );

        if ( MultiPointSlider.REAL_MODE ) {

            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    _isSelected,
                    _positionOnLine,
                    this
            );

        } else {

            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.ABOVE,
                    this
            );
            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.BELOW,
                    this
            );
            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.LEFT,
                    this
            );
            _knob.drawKnob(
                    g2d,
                    hotSpot,
                    _knobSize,
                    false, PositionOnLine.RIGHT,
                    this
            );

        }

    }

    private int drawTickMarks( @Nullable final Graphics g, final int tickSpacing, final int tickLength ) {

        if ( g != null ) {

            for ( int value = _brm.getMinimum(); value <= _brm.getMaximum(); value += tickSpacing ) {

                Point mark = mapValueToPoint( value );
                switch ( _positionOnLine ) {

                    case ABOVE:
                        g.drawLine(
                                mark.x,
                                mark.y + 1 + MultiPointSlider.TIC_GAP,
                                mark.x,
                                mark.y + 1 + MultiPointSlider.TIC_GAP + tickLength
                        );
                        break;

                    case BELOW:
                        g.drawLine(
                                mark.x,
                                mark.y - ( 1 + MultiPointSlider.TIC_GAP ),
                                mark.x,
                                mark.y - ( 1 + MultiPointSlider.TIC_GAP + tickLength )
                        );
                        break;

                    case LEFT:
                        g.drawLine(
                                mark.x + 1 + MultiPointSlider.TIC_GAP,
                                mark.y,
                                mark.x + 1 + MultiPointSlider.TIC_GAP + tickLength,
                                mark.y
                        );
                        break;

                    case RIGHT:
                        g.drawLine(
                                mark.x - ( 1 + MultiPointSlider.TIC_GAP ),
                                mark.y,
                                mark.x - ( 1 + MultiPointSlider.TIC_GAP + tickLength ),
                                mark.y
                        );
                        break;

                }

            }

        }

        return 1 + MultiPointSlider.TIC_GAP + tickLength;

    }

    public void setPaintLabels( final boolean paintLabels ) {

        _paintLabels = paintLabels;
        _minimumSize = null;

    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean paintLabels() {

        return _paintLabels;

    }

    public void setLabelTable( final Dictionary<Integer, MpsLabel> labelTable ) {

        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        _labelTable = labelTable;
        _minimumSize = null;

    }

    public Dictionary<Integer, MpsLabel> getLabelTable() {

        return _labelTable;

    }

    public String getName() {

        return _name;

    }

    @SuppressWarnings("MagicNumber")
    public static void main( final String[] args ) {

        @SuppressWarnings("UseOfObsoleteCollectionType")
        Dictionary<Integer, MpsLabel> labels = new Hashtable<>();
        labels.put( 2, new MpsLabel( "two" ) );
        labels.put( 20, new MpsLabel( "twenty" ) );

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "test MultiPointSlider", null );
        JFrame frame = new JFrame( "Hello" );
        frame.setTitle( "Hi there" );
        JPanel bluePanel = new JPanel();
        bluePanel.setLayout( new BoxLayout( bluePanel, BoxLayout.Y_AXIS ) );
        MultiPointSlider slider = new MultiPointSlider( "s1", 0, 10 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        slider = new MultiPointSlider( "s2", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.BELOW );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.getModel().setValue( slider.getModel().getMinimum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        slider = new MultiPointSlider( "s3", 0, 100 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 5 );
        slider.setMajorTickSpacing( 10 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( false );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        slider = new MultiPointSlider( "s4", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.BELOW );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( false );
        slider.getModel().setValue( slider.getModel().getMinimum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        bluePanel.add( slider );

        JPanel redPanel = new JPanel();
        redPanel.setLayout( new BoxLayout( redPanel, BoxLayout.X_AXIS ) );
        slider = new MultiPointSlider( "s5", 0, 10 );
        slider.setLabelTable( labels );
        slider.setMinorTickSpacing( 1 );
        slider.setMajorTickSpacing( 2 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setPositionOnLine( PositionOnLine.LEFT );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        final MultiPointSlider leftSlider = slider;
        slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged( final ChangeEvent changeEvent ) {

                        Logger.logMsg( "left slider changed:  value is " + leftSlider.getModel().getValue() );

                    }

                }
        );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        slider = new MultiPointSlider( "s6", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.RIGHT );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.getModel().setValue( slider.getModel().getMinimum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        slider = new MultiPointSlider( "s7", 0, 100 );
        slider.setMinorTickSpacing( 5 );
        slider.setMajorTickSpacing( 10 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( false );
        slider.setPositionOnLine( PositionOnLine.LEFT );
        slider.getModel().setValue( slider.getModel().getMaximum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        slider = new MultiPointSlider( "s8", 0, 1000 );
        slider.setLabelTable( labels );
        slider.setPositionOnLine( PositionOnLine.RIGHT );
        slider.setMinorTickSpacing( 50 );
        slider.setMajorTickSpacing( 100 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( false );
        slider.getModel().setValue( slider.getModel().getMinimum() );
        slider.setMinimumSize( slider.computeMinimumSize() );
        redPanel.add( slider );

        bluePanel.add( redPanel );
        JScrollPane jScrollPane = new JScrollPane( bluePanel );
        jScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        jScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        frame.setContentPane( jScrollPane );
        frame.pack();
        frame.setVisible( true );

    }

    public void setModel( final BoundedRangeModel brm ) {

        if ( _brm != null ) {

            _brm.removeChangeListener( _myChangeListener );

        }

        _brm = brm;

        _brm.addChangeListener( _myChangeListener );
        _minimumSize = null;

    }

    public PositionOnLine getPositionOnLine() {

        return _positionOnLine;

    }

    public final void setPositionOnLine( final PositionOnLine positionOnLine ) {

        if ( _positionOnLine != null && _positionOnLine == positionOnLine ) {

            return;

        }

        _positionOnLine = positionOnLine;

        _minimumSize = null;

    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean drawSliderLine() {

        return _drawSliderLine;

    }

    public void setDrawSliderLine( final boolean drawSliderLine ) {

        _drawSliderLine = drawSliderLine;
        _minimumSize = null;

    }

    public int getMinorTickSpacing() {

        return _minorTickSpacing;

    }

    public void setMinorTickSpacing( final int minorTickSpacing ) {

        _minorTickSpacing = minorTickSpacing;
        _minimumSize = null;

    }

    public int getMajorTickSpacing() {

        return _majorTickSpacing;

    }

    public void setMajorTickSpacing( final int majorTickSpacing ) {

        _majorTickSpacing = majorTickSpacing;
        _minimumSize = null;

    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean paintTickMarks() {

        return _paintTicks;

    }

    public void setPaintTicks( final boolean paintTicks ) {

        _paintTicks = paintTicks;
        _minimumSize = null;

    }

    /**
     * Manage a particular orientation of an image.
     * <p/>
     * Intended to be used by the {@link MultiPointSlider} class.  Probably not all that useful in other contexts.
     */

    public static class OrientedImage {

        private final Point _hotSpotWithinImage;
        private final BufferedImage _image;

        public OrientedImage( final Point hotSpotWithinImage, final BufferedImage image ) {

            super();

            _hotSpotWithinImage = new Point( hotSpotWithinImage.x, hotSpotWithinImage.y );
            _image = image;

        }

        public boolean isPointInImage( final Point actualHotSpot, final Point p ) {

            Point imageLocation = new Point(
                    actualHotSpot.x - _hotSpotWithinImage.x,
                    actualHotSpot.y - _hotSpotWithinImage.y
            );
            Point computedHotSpot = new Point(
                    imageLocation.x + _hotSpotWithinImage.x,
                    imageLocation.y + _hotSpotWithinImage.y
            );
            Point pointWithinImage = new Point(
                    p.x - imageLocation.x,
                    p.y - imageLocation.y
            );
            Point imageSize = new Point(
                    _image.getWidth(),
                    _image.getHeight()
            );

            boolean isInside;
            if (
                    pointWithinImage.x >= 0 && pointWithinImage.x < imageSize.x
                    &&
                    pointWithinImage.y >= 0 && pointWithinImage.y < imageSize.y
                    ) {

                int argb = _image.getRGB( pointWithinImage.x, pointWithinImage.y );
                //noinspection MagicNumber
                isInside = ( argb & 0xff000000 ) != 0;

            } else {

                isInside = false;

            }

            //        Logger.logMsg(
            //                "image @ " + imageLocation +
            //                ", point @ " + p +
            //                ", point within image @ " + pointWithinImage +
            //                ", INSIDE is " + isInside +
            //                ", hotspot @ " + actualHotSpot +
            //                ", computed hotspot @ " + computedHotSpot
            //        );

            return isInside;

        }

        public void drawImage( final Graphics2D g, final Point actualHotSpot ) {

            g.translate( actualHotSpot.x, actualHotSpot.y );
            //            g.drawLine( -5, 0, 5, 0 );
            //            g.drawLine(  0, -5, 0, 5 );
            g.drawImage( _image, -_hotSpotWithinImage.x, -_hotSpotWithinImage.y, null );
            g.translate( -actualHotSpot.x, -actualHotSpot.y );

        }

        /**
         * Retrieve this image's hotspot.
         *
         * @return a copy of this image's hotspot (to ensure that caller does not modify the hotspot).
         */

        public Point getHotSpotWithinImage() {

            return new Point( _hotSpotWithinImage.x, _hotSpotWithinImage.y );

        }

        public Image getImage() {

            return _image;

        }

        //    public int getScreenWidth( ImageObserver imageObserver ) {
        //
        //        int width = _image.getWidth( imageObserver );
        //        if ( _hotSpotWithinImage.x < 0 ) {
        //
        //            width += -_hotSpotWithinImage.x;
        //
        //        } else if ( _hotSpotWithinImage.x > width ) {
        //
        //            width = _hotSpotWithinImage.x;
        //
        //        }
        //
        //        return width;
        //
        //    }

        /**
         * Compute the amount of vertical screen space this image will consume.
         * <p/>
         * The computed value takes this instance' hotspot into account if the hotspot is either
         * above or below the image.
         *
         * @return the vertical screen space consumed by this image.
         */

        public int getScreenHeight() {

            int height = _image.getHeight( null );
            int before = height;
            if ( _hotSpotWithinImage.y < 0 ) {

                height += -_hotSpotWithinImage.y;
                //            Logger.logMsg( "- from " + before + " to " + height + ", adjustment " + -_hotSpotWithinImage.y );

            } else if ( _hotSpotWithinImage.y > height ) {

                height = _hotSpotWithinImage.y + 1;     // deal with zero-origin x and y values
                //            Logger.logMsg( "+ from " + before + " to " + height + ", adjustment =" + _hotSpotWithinImage.y + "+1");

                //        } else {
                //
                //            Logger.logMsg( "no adjustment" );

            }

            return height;

        }

        public int getScreenWidth() {

            int width = _image.getHeight( null );
            if ( _hotSpotWithinImage.x < 0 ) {

                width += -_hotSpotWithinImage.x;

            } else if ( _hotSpotWithinImage.x > width ) {

                width = _hotSpotWithinImage.x + 1;  // deal with zero-origin x and y values

            }

            return width;

        }

        public String toString() {

            return "OrientedImage( " + getScreenWidth() + 'x' + getScreenHeight() + ", hs = " + _hotSpotWithinImage +
                   " )";

        }

    }

}
