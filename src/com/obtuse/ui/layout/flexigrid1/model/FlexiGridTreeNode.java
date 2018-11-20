package com.obtuse.ui.layout.flexigrid1.model;

import com.obtuse.ui.layout.flexigrid1.tree.DefaultFlexiGridTreeNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 Describes a node in a {@link com.obtuse.ui.layout.flexigrid1.tree.FlexiGridTree}.
 <p>This is looking like the wrong way to build a tree-of-nodes manager.</p>
 @deprecated
 */

@Deprecated
public interface FlexiGridTreeNode {

    enum FamilyStatus {
        HAS_CHILDREN,
        NO_CHILDREN_NOW,
        NO_CHILDREN_EVER
    }

    /**
     Get this node's <b><u>unique-within-its-tree</u></b> name.
     @return this node's <b><u>unique-within-its-tree</u></b> name.
     */

    @NotNull
    String getName();

    /**
     Get the GUI component that will display this element in the GUI.
     <p>Probably a good idea to make each node's GUI height about the same
     and to try to keep the GUI height of each node fairly small.</p>
     @return This node's GUI component.
     */

    Component getGuiElements();

    /**
     Determine if this node is the root node.
     <p>Being the root node is a lifetime sinecture (once a root node, always a root node) that doesn't pay very well.</p>
     <p>While it is true that root nodes never have a parent node,
     it is also true that nodes which are being constructed might temporarily not have a parent node either.
     <u>Therefore, do not use the lack of a parent as an indication of whether or not a node is the root node.</u>
     On the other hand, a node that has a parent is <u>obviously</u> not a root node.</p>
     */

    boolean isRootNode();

    /**
     Add a child to this node.
     @param newChild the new child.
     @throws IllegalArgumentException (should be thrown) if the new child already has a parent, is a root node, or does not have the same root node as this node.
     */

    void addChild( @NotNull DefaultFlexiGridTreeNode newChild );

    /**
     Determine if this node is empty (has no children).
     @return {@code true} if this node has no children; {@code false} otherwise.
     */

    boolean isEmpty();

    /**
     Determine if this node currently has a parent.
     @return {@code true} if this currently has a parent; {@code false} otherwise.
     Do not use this method to determine if this node is a root node because a node without a parent
     might have been created to be the root node of an about-to-be created tree.
     */

    boolean hasParent();

    /**
     Get this node's optional parent.
     @return an {@link Optional} which contains this node's parent as a {@link FlexiGridTreeNode} instance if it has one.
     */

    @NotNull
    Optional<FlexiGridTreeNode> getOptParentNode();

    /**
     Get this node's family status.
     @return <ul>
     <li>{@link FamilyStatus#HAS_CHILDREN} if this node has children right now.</li>
     <li>{@link FamilyStatus#NO_CHILDREN_NOW} if this node has no children now but could have in the future.</li>
     <li>{@link FamilyStatus#NO_CHILDREN_EVER} if this node cannot currently have children.
     A node which returns this value will not be allowed to be given children by the human using
     the {@link com.obtuse.ui.layout.flexigrid1.tree.FlexiGridTree} GUI.
     More accurately, an attempt to give children to this node results in this method being called.
     If that call returns {@link FamilyStatus#NO_CHILDREN_EVER} then the attempt to give this node children will be rejected.
     </li>
     </ul>
     */

    @NotNull
    FamilyStatus getFamilyStatus();

    /**
     Determine if this node has children right now.
     @return {@code true} if this node has children; {@code false} otherwise.
     */

    default boolean hasChildren() {

        return !isEmpty();

    }

    /**
     Get this node's tree-wide unique id.
     <ul>
     <li>The id of a node must not change while the node is actually a member of a tree (reachable from any existent tree's root node).</li>
     <li>Each node in a tree must have an id which is not shared with any other node in the tree.</li>
     </ul>
     @return an int which is uniquely associated with this node.
     */

    int getTreeNodeId();

    /**
     Determine if this node can ever have children.
     @return {@code true} if this node has children now or could have them in the future; {@code false} otherwise.
     */

    boolean areChildrenAllowed();

    /**
     Get this node's children.
     @return A list containing this node's children as {@link FlexiGridTreeNode} instances.
     */

    List<FlexiGridTreeNode> getTreeNodeModelChildren();

}
