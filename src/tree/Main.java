package tree;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;

import com.thoughtworks.qdox.parser.ParseException;

import commands.AddCommentCommand;
import commands.ExportCommand;
import commands.SetExportStateCommand;
import listeners.AbstractDoubleClickListener;
import listeners.DefaultDoubleClickListener;
import listeners.ITreeInstanceListener;
import listeners.IWorkspaceListener;
import listeners.PartListener;
import org.eclipse.jface.viewers.*;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

public class Main extends ViewPart implements IWorkspaceListener, ITreeInstanceListener {
	public static final String ID = "tcg.views.TreeView";

	private TreeViewer treeViewer;
	private TreeInstanceManager treeInstanceManager;
	private MenuManager contextMenuManager;
	private Action actionToggleExport, actionSaveFile, actionExport, actionComment;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
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

	@SuppressWarnings("deprecation")
	private void createActions() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();

		actionToggleExport = new SetExportStateCommand(treeViewer);
		actionToggleExport.setEnabled(false);
		actionToggleExport.setText("Eignung ändern");
		actionToggleExport.setToolTipText("Umschalten, ob diese Methode in die finale Muggl Test-Datei exportiert werden soll.");
		actionToggleExport.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

		actionSaveFile = new Action() {
			public void run() {
				if (treeInstanceManager.getActiveTreeInstance() != null) {
					try {
						treeInstanceManager.getActiveTreeInstance().saveToMugglFile();
					} catch (IOException e) {
						MessageDialog.openError(treeViewer.getControl().getShell(), "I/O Fehler",
								"Datei konnte nicht gespeichert werden.");
					}
				}
			}
		};
		actionSaveFile.setEnabled(false);
		actionSaveFile.setText("Testfälle speichern");
		actionSaveFile.setToolTipText("Persistiert die Änderungen an der Eignung von Testfällen.");
		actionSaveFile.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));

		actionExport = new ExportCommand(treeInstanceManager, treeViewer);
		actionExport.setEnabled(false);
		actionExport.setText("Geeignete Testfälle exportieren");
		actionExport.setToolTipText("Schreibt alle nicht als ungeeignet markierten Testfälle in eine neue Datei.");
		actionExport.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OPEN_MARKER));
		
		actionComment = new AddCommentCommand(treeViewer);
		actionComment.setEnabled(false);
		actionComment.setText("Kommentar hinzufügen");
		actionComment.setToolTipText("Kommentar in Form eines Java-Doc Kommentars hinzufügen");
		actionComment.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		contextMenuManager.add(actionToggleExport);
		contextMenuManager.add(actionComment);
		toolBarManager.add(actionToggleExport);
		toolBarManager.add(actionComment);
		toolBarManager.add(actionSaveFile);
		toolBarManager.add(actionExport);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object treeObject = selection.getFirstElement();

				Boolean isParentSelected = selection.getFirstElement() != null && treeObject instanceof TreeParent;
				actionToggleExport.setEnabled(isParentSelected);
				actionComment.setEnabled(isParentSelected);
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
	public void onFileClose(IFile file, IFile activeFile) {
		treeInstanceManager.removeTreeInstanceByFile(file);

		if (activeFile == null) {
			treeInstanceManager.setActiveTreeInstance(null);
			treeViewer.setInput(null);
			actionSaveFile.setEnabled(false);
			actionExport.setEnabled(false);
		}
	}

	/**
	 * Implemented from IWorkspaceListener
	 * 
	 * Called when an Editor in the Workspace is opened. In case multiple files
	 * are loaded and opened on startup, onFileOpened will never trigger for a
	 * file that is not active on start, but still already open. As the
	 * TreeInstance gets created anyway in onFileActivated if it does not yet
	 * exist, both cases can be treated equally.
	 */
	@Override
	public void onFileOpen(IFile file) {
		onFileActivate(file);
	}

	/**
	 * Implemented from IWorkspaceListener
	 * 
	 * Called when an Editor in the Workspace is gaining focus.
	 */
	@Override
	public void onFileActivate(IFile file) {
		try {
			TreeInstance treeInstance = createOrGetTreeInstance(file);
			treeViewer.setInput(treeInstance.getTreeInstanceRoot());
			treeInstanceManager.setActiveTreeInstance(treeInstance);
			actionSaveFile.setEnabled(true);
			actionExport.setEnabled(true);
		} catch (ParseException | IOException | IllegalArgumentException e) {
			displayEmptyTreeWithMessage(e.getMessage());
			actionSaveFile.setEnabled(false);
			actionExport.setEnabled(false);
		}
	}

	/**
	 * Implemented from IWorkspaceListener
	 * 
	 * Called when the method name under the caret changes. Even though we can
	 * internally distinguish between overloaded methods with the same name, we
	 * only get the name of the method as a string here.
	 */
	@Override
	public void onMethodUnderCaretChange(String methodName) {
		if (treeInstanceManager.getActiveTreeInstance() == null || methodName == null)
			return;
		
		ArrayList<ITreeObject> treeObjects = treeInstanceManager.getActiveTreeInstance().findRootLevelTreeObjectsByContentDescription(methodName);

		if (treeObjects != null && treeObjects.size() > 0) {
			treeViewer.collapseAll();
			for (ITreeObject treeObject : treeObjects)
				treeViewer.setExpandedState(treeObject, true);
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

	private TreeInstance createOrGetTreeInstance(IFile file)
			throws ParseException, IOException, IllegalArgumentException {
		TreeInstance treeInstance = treeInstanceManager.findTreeInstanceByFile(file);

		if (treeInstance == null) {
			treeInstance = new TreeInstance(file);
			treeInstance.loadFromMugglFile().buildTree();
			treeInstanceManager.addTreeInstance(treeInstance);
		}

		return treeInstance;
	}

	private void displayEmptyTreeWithMessage(String message) {
		TreeParent invisibleRoot = new TreeParent(null);
		TreeObject messageObject = new TreeObject(new TreePropertyObjectContent(message));

		treeInstanceManager.setActiveTreeInstance(null);
		invisibleRoot.addChild(messageObject);
		treeViewer.setInput(invisibleRoot);
	}
}
