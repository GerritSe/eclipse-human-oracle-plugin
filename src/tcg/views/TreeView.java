package tcg.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;

import com.thoughtworks.qdox.parser.ParseException;

import commands.SetExportStateCommand;
import listeners.AbstractDoubleClickListener;
import listeners.DefaultDoubleClickListener;
import listeners.ITreeInstanceListener;
import listeners.IWorkspaceListener;
import listeners.PartListener;

import org.eclipse.jface.viewers.*;

import java.io.IOException;

import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import tcg.tree.*;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class TreeView extends ViewPart implements IWorkspaceListener, ITreeInstanceListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tcg.views.TreeView";

	private TreeViewer treeViewer;
	private TreeInstanceManager treeInstanceManager;
	private MenuManager contextMenuManager;
	private Action actionToggleExport;

/**
	 * The constructor.
	 */
	public TreeView() {
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		treeInstanceManager = new TreeInstanceManager();
		treeInstanceManager.setTreeInstanceListener(this);

		treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);		
		treeViewer.setContentProvider(new ViewContentProvider());
		treeViewer.setInput(getViewSite());
		treeViewer.setLabelProvider(new ViewLabelProvider());

		getSite().setSelectionProvider(treeViewer);
		addDoubleClickListener(new DefaultDoubleClickListener(treeViewer));
		addPartListener();
		hookContextMenu();
		createActions();

	}

	/**
	 * The PartListener will notify the TreeView about changes in the Workspace
	 * via the IWorkspaceListener interface.
	 */
	private void addPartListener() {
		PartListener partListener = new PartListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(partListener);
	}
	
	private void createActions() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		
		actionToggleExport = new Action() {
			public void run() {
				new SetExportStateCommand(treeViewer).call();
			}
		};
		actionToggleExport.setEnabled(false);
		actionToggleExport.setText("Toggle export");
		actionToggleExport.setToolTipText("Toggle whether this method gets exported to the final Muggle test file.");
		
		actionToggleExport.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		
		contextMenuManager.add(actionToggleExport);
		toolBarManager.add(actionToggleExport);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object treeObject = selection.getFirstElement();
				
				if (selection.getFirstElement() == null || !(treeObject instanceof TreeParent))
					actionToggleExport.setEnabled(false);
				else
					actionToggleExport.setEnabled(true);
			}
		});
	}

	private void hookContextMenu() {
		contextMenuManager = new MenuManager("#PopupMenu");
		contextMenuManager.setRemoveAllWhenShown(false);
		Menu menu = contextMenuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(contextMenuManager, treeViewer);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	/**
	 * Implemented from IWorkspaceListener
	 * 
	 * Called when an Editor in the Workspace is closed.
	 */
	@Override
	public void onFileClose(String fileName) {
		treeInstanceManager.removeTreeInstanceByMuggleFileName(fileName);
	}

	/**
	 * Implemented from IWorkspaceListener
	 * 
	 * Called when an Editor in the Workspace is opened. In case multiple files are loaded
	 * and opened on startup, onFileOpened will never trigger for a file that is not active
	 * on start, but still already open. As the TreeInstance gets created anyway in onFileActivated
	 * if it does not yet exist, both cases can be treated equally.
	 */
	@Override
	public void onFileOpen(String fileName) {
		onFileActivate(fileName);
	}

	/**
	 * Implemented from IWorkspaceListener
	 * 
	 * Called when an Editor in the Workspace is gaining focus.
	 */
	@Override
	public void onFileActivate(String fileName) {
		try {
			TreeInstance treeInstance = createOrGetTreeInstance(fileName);
			treeViewer.setInput(treeInstance.getTreeInstanceRoot());
		} catch (ParseException | IOException | IllegalArgumentException e) {
			// TODO: Exception Handling
			e.printStackTrace();
		}
	}
	
	/**
	 * Implemented from ITreeInstanceListener
	 * 
	 * Called by TreeInstanceManager if the content of a TreeObject changes.
	 */
	@Override
	public void onTreeObjectContentChange(TreeInstance _treeInstance, ITreeObject treeObject) {
		treeViewer.refresh(treeObject.getParent(), true);
	}
	
	private void addDoubleClickListener(AbstractDoubleClickListener doubleClickListener) {
		treeViewer.addDoubleClickListener(doubleClickListener);
	}
	
	private TreeInstance createOrGetTreeInstance(String fileName) throws ParseException, IOException, IllegalArgumentException {
		TreeInstance treeInstance = treeInstanceManager.findTreeInstanceByMuggleFileName(fileName);
		
		if (treeInstance == null) {
			treeInstance = new TreeInstance(treeInstanceManager, fileName);
			treeInstance.loadFromMuggleFile().buildTree();
		}
		
		return treeInstance;
	}
}
