package com.jpodder.ui.swt.conf;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * A tree of the property categories in the application.
 */
public class ConfigurationTree {
    protected TreeViewer mViewer;
    protected Tree mTree;
    private ConfigurationNode mRootNode;
    
    public ConfigurationTree(Composite pView) {
        createTreeViewer(pView);
    }

    private void createTreeViewer(Composite pParent) {
        mTree = new Tree(pParent, SWT.BORDER);
        mViewer = new TreeViewer(mTree);
        ConfContentProvider lProvider = new ConfContentProvider();
        mViewer.setContentProvider(lProvider);
    }

    public TreeViewer getViewer() {
        return mViewer;
    }

    public Tree getTree() {
        return mTree;
    }

    /**
     * Custom Content provider.
     * @see ConfigurationNode
     */
    public class ConfContentProvider implements ITreeContentProvider {
        

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ConfigurationNode) {
                ConfigurationNode parentNode = (ConfigurationNode) parentElement;
                if (parentNode.mChildren != null) {
                    return parentNode.mChildren.values().toArray();
                }
            }
            return null;
        }

        public Object getParent(Object element) {
            if (element instanceof ConfigurationNode) {
                ConfigurationNode childNode = (ConfigurationNode) element;
                return childNode.mParent;
            }
            return null;
        }

        public boolean hasChildren(Object element) {
            if (element instanceof ConfigurationNode) {
                ConfigurationNode parentNode = (ConfigurationNode) element;

                return parentNode.mChildren == null ? false : true;
            }
            return false;
        }

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ConfigurationNode) {
                ConfigurationNode lNode = (ConfigurationNode) inputElement;
//                if(mRootNode == null){ // this is the root node.
//                    mRootNode = lNode;
//                    return new Object[]{lNode};
//                }else{
                    return getChildren(inputElement);    
//                }
            }
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
}