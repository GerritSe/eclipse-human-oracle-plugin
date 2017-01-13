package tcg.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;

import com.thoughtworks.qdox.parser.ParseException;

import listeners.AbstractDoubleClickListener;
import listeners.DefaultDoubleClickListener;
import listeners.ITreeInstanceListener;
import listeners.IWorkspaceListener;
import listeners.PartListener;

import org.eclipse.jface.viewers.*;

import java.io.IOException;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;

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

	private TreeViewer viewer;
	private TreeInstanceManager treeInstanceManager;
	private Action action1;
	private Action action2;

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
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "TCG.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		contributeToActionBars();

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		addDoubleClickListener(new DefaultDoubleClickListener(viewer));
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new PartListener(this));
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"TreeView",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
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
			viewer.setInput(treeInstance.getTreeInstanceRoot());
		} catch (ParseException | IOException | IllegalArgumentException e) {
			// TODO: Exception Handling
			e.printStackTrace();
		}
	}
	
	private void addDoubleClickListener(AbstractDoubleClickListener doubleClickListener) {
		viewer.addDoubleClickListener(doubleClickListener);
	}
	
	private TreeInstance createOrGetTreeInstance(String fileName) throws ParseException, IOException, IllegalArgumentException {
		TreeInstance treeInstance = treeInstanceManager.findTreeInstanceByMuggleFileName(fileName);
		
		if (treeInstance == null) {
			treeInstance = new TreeInstance(treeInstanceManager, fileName);
			treeInstance.loadFromMuggleFile().buildTree();
		}
		
		return treeInstance;
	}

	@Override
	public void onTreeObjectContentChange(TreeInstance _treeInstance, ITreeObject treeObject) {
		viewer.refresh(treeObject.getParent(), true);
	}
}
