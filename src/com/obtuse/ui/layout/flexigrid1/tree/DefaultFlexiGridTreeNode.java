package com.obtuse.ui.layout.flexigrid1.tree;

import com.obtuse.ui.layout.flexigrid1.model.FlexiGridTreeNode;
import com.obtuse.ui.layout.flexigrid1.tree.testing.TestFlexiGridTree;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 An implementation of the core {@link FlexiGridTreeNode} functionality.
 <p>This implementation makes no attempt to ensure that all the elements of a tree will be of the same Java class
 since it seems likely that some trees will contain instances of different classes. For example, it is easy to imagine
 a tree that uses one class for nodes which can have children (e.g. 'directory' nodes) and a different class
 for nodes which cannot have children (e.g. 'file' nodes).</p>
 <p>This is looking like the wrong way to build a tree-of-nodes manager.</p>
 @deprecated
 */

@Deprecated
public abstract class DefaultFlexiGridTreeNode implements FlexiGridTreeNode {

    private static SimpleUniqueIntegerIdGenerator s_idGenerator = new SimpleUniqueIntegerIdGenerator( "tree node id generator" );

    private final String _name;
    private final int _ourTreeId;
    private DefaultFlexiGridTreeNode _parent;
    private final int _id;
    @NotNull
    private final DefaultFlexiGridTreeNode _ourRootNode;
    private final boolean _childrenAllowed;
    private final SortedSet<DefaultFlexiGridTreeNode> _children = new TreeSet<>();

    private final SortedMap<Integer,DefaultFlexiGridTreeNode> _ourNodes;

//    @NotNull
//    private JLabel _jLabel;

    /**
     Create a default tree node.
     @param name the name of the node (used in messages).
     @param ourRootNode a reference to the root of the {@link FlexiGridTree} that this node is a member of.
     Pass {@code null} if this node is intended to be the root of a new tree.
     @param childrenAllowed {@code true} if this node is allowed to have children; {@code false} otherwise.
     */

    protected DefaultFlexiGridTreeNode(
            @NotNull String name,
            @Nullable final DefaultFlexiGridTreeNode ourRootNode,
            final boolean childrenAllowed
    ) {

        super();

        _name = name;
        _childrenAllowed = childrenAllowed;

        _id = s_idGenerator.getUniqueId();

        _ourRootNode = Objects.requireNonNullElse( ourRootNode, this );

        if ( isRootNode() ) {

            _ourNodes = new TreeMap<>();
            _ourTreeId = s_idGenerator.getUniqueId();

        } else {

            _ourNodes = null;
            _ourTreeId = _ourRootNode.getTreeId();

        }

        if ( !_ourRootNode.isRootNode() ) {

            throw new IllegalArgumentException( "DefaultFlexiGridTreeNode:  ourRootNode " +
                                                _ourRootNode +
                                                " is not actually a root node" );

        }

        if ( _ourRootNode.ownsNode( this ) ) {

            throw new IllegalArgumentException(
                    "DefaultFlexiGridTreeNode:  attempt to add a node " +
                    "(name=" + ObtuseUtil.enquoteToJavaString( _name ) + ",id=" + _id + ") " +
                    "that is already known to this tree"
            );

        }

        _ourRootNode._ourNodes.put( _id, this );

//        _jLabel = new JLabel( name );

    }

    public int getTreeId() {

        return _ourTreeId;

    }

    private boolean ownsNode( final FlexiGridTreeNode treeNode ) {

        return _ourRootNode._ourNodes.containsKey( treeNode.getTreeNodeId() );

    }

    @Override
    public @NotNull String getName() {

        return _name;

    }

//    @Override
//    public Component getGuiElements() {
//
//        return _jLabel;
//
//    }

    @Override
    public boolean isRootNode() {

        return _ourRootNode == this;

    }

    @Override
    public void addChild( @NotNull final DefaultFlexiGridTreeNode newChild ) {

        if ( newChild.isRootNode() ) {

            throw new IllegalArgumentException( "TestFlexiGridTree:  cannot add a root node to an existing tree" );

        }

        if ( areChildrenAllowed() ) {


            _children.add( newChild );

        }

    }

    @Override
    public boolean hasChildren() {

        return !isEmpty();

    }

    @Override
    public boolean isEmpty() {

        return _children.isEmpty();

    }

    @Override
    public boolean hasParent() {

        return _parent != null;

    }

    @Override
    public @NotNull Optional<FlexiGridTreeNode> getOptParentNode() {

        return Optional.ofNullable( _parent );

    }

    @Override
    public @NotNull FamilyStatus getFamilyStatus() {

        return _children.isEmpty() ?
                ( areChildrenAllowed() ? FamilyStatus.NO_CHILDREN_NOW : FamilyStatus.NO_CHILDREN_EVER ) :
                FamilyStatus.HAS_CHILDREN;

    }

    @Override
    public boolean areChildrenAllowed() {

        return _childrenAllowed;

    }

    @Override
    public List<FlexiGridTreeNode> getTreeNodeModelChildren() {

        return null;

    }

    public String toString() {

        return "DefaultFlexiGridTreeNode( name=" +
               ObtuseUtil.enquoteToJavaString( _name ) +
               ", root=" +
               ( _ourRootNode.isRootNode() ? "this node" : _ourRootNode ) +
               " )";

    }

}
