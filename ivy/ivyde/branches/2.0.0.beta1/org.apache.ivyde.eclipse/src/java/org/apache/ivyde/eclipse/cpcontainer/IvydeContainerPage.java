/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ivyde.eclipse.cpcontainer;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivyde.eclipse.IvyDEException;
import org.apache.ivyde.eclipse.IvyPlugin;
import org.apache.ivyde.eclipse.ui.AcceptedSuffixesTypesComposite;
import org.apache.ivyde.eclipse.ui.ConfTableViewer;
import org.apache.ivyde.eclipse.ui.IvyFilePathText;
import org.apache.ivyde.eclipse.ui.RetrieveComposite;
import org.apache.ivyde.eclipse.ui.SettingsPathText;
import org.apache.ivyde.eclipse.ui.IvyFilePathText.IvyXmlPathListener;
import org.apache.ivyde.eclipse.ui.SettingsPathText.SettingsPathListener;
import org.apache.ivyde.eclipse.ui.preferences.IvyDEPreferenceStoreHelper;
import org.apache.ivyde.eclipse.ui.preferences.IvyPreferencePage;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class IvydeContainerPage extends NewElementWizardPage implements IClasspathContainerPage,
        IClasspathContainerPageExtension {

    private IJavaProject project;

    private IvyFilePathText ivyFilePathText;

    private ConfTableViewer confTableViewer;

    private SettingsPathText settingsText;

    private Combo alphaOrderCheck;

    private Button resolveInWorkspaceCheck;

    private Button settingsProjectSpecificButton;

    private Button advancedProjectSpecificButton;

    private Link mainGeneralSettingsLink;

    private Link advancedGeneralSettingsLink;

    private IvyClasspathContainerConfiguration conf;

    private IClasspathEntry entry;

    private TabFolder tabs;

    private RetrieveComposite retrieveComposite;

    private AcceptedSuffixesTypesComposite acceptedSuffixesTypesComposite;

    private Button retrieveProjectSpecificButton;

    private Link retrieveGeneralSettingsLink;

    /**
     * Constructor
     */
    public IvydeContainerPage() {
        super("IvyDE Container");
    }

    void checkCompleted() {
        String error;
        if (ivyFilePathText.getIvyFilePath().length() == 0) {
            error = "Choose an ivy file";
        } else {
            error = null;
        }
        setErrorMessage(error);
        setPageComplete(error == null);
    }

    void checkIvyXmlPath() {
        ModuleDescriptor md;
        try {
            md = conf.getModuleDescriptor();
            ivyFilePathText.setIvyXmlError(null);
        } catch (IvyDEException e) {
            md = null;
            ivyFilePathText.setIvyXmlError(e);
        }
        confTableViewer.setModuleDescriptor(md);
        checkCompleted();
    }

    public boolean finish() {
        conf.confs = confTableViewer.getSelectedConfigurations();
        if (settingsProjectSpecificButton.getSelection()) {
            conf.ivySettingsPath = settingsText.getSettingsPath();
            conf.acceptedTypes = acceptedSuffixesTypesComposite.getAcceptedTypes();
            conf.sourceTypes = acceptedSuffixesTypesComposite.getSourcesTypes();
            conf.javadocTypes = acceptedSuffixesTypesComposite.getJavadocTypes();
            conf.sourceSuffixes = acceptedSuffixesTypesComposite.getSourceSuffixes();
            conf.javadocSuffixes = acceptedSuffixesTypesComposite.getJavadocSuffixes();
            conf.doRetrieve = retrieveComposite.isRetrieveEnabled();
            conf.retrievePattern = retrieveComposite.getRetrievePattern();
            conf.retrieveSync = retrieveComposite.isSyncEnabled();
            conf.retrieveConfs = retrieveComposite.getRetrieveConfs();
            conf.retrieveTypes = retrieveComposite.getRetrieveTypes();
            conf.alphaOrder = alphaOrderCheck.getSelectionIndex() == 1;
            conf.resolveInWorkspace = resolveInWorkspaceCheck.getSelection();
        } else {
            conf.ivySettingsPath = null;
        }
        if (retrieveProjectSpecificButton.getSelection()) {
            conf.isRetrieveProjectSpecific = true;
            conf.doRetrieve = retrieveComposite.isRetrieveEnabled();
            conf.retrievePattern = retrieveComposite.getRetrievePattern();
            conf.retrieveSync = retrieveComposite.isSyncEnabled();
        } else {
            conf.isRetrieveProjectSpecific = false;
        }
        if (advancedProjectSpecificButton.getSelection()) {
            conf.isAdvancedProjectSpecific = true;
            conf.acceptedTypes = acceptedSuffixesTypesComposite.getAcceptedTypes();
            conf.sourceTypes = acceptedSuffixesTypesComposite.getSourcesTypes();
            conf.javadocTypes = acceptedSuffixesTypesComposite.getJavadocTypes();
            conf.sourceSuffixes = acceptedSuffixesTypesComposite.getSourceSuffixes();
            conf.javadocSuffixes = acceptedSuffixesTypesComposite.getJavadocSuffixes();
            conf.alphaOrder = alphaOrderCheck.getSelectionIndex() == 1;
            conf.resolveInWorkspace = resolveInWorkspaceCheck.getSelection();
        } else {
            conf.isAdvancedProjectSpecific = false;
        }
        entry = JavaCore.newContainerEntry(conf.getPath());
        return true;
    }

    public IClasspathEntry getSelection() {
        return entry;
    }

    public void setSelection(IClasspathEntry entry) {
        if (entry == null) {
            conf = new IvyClasspathContainerConfiguration(project, "ivy.xml", true);
        } else {
            conf = new IvyClasspathContainerConfiguration(project, entry.getPath(), true);
        }
    }

    public void createControl(Composite parent) {
        setTitle("IvyDE Managed Libraries");
        setDescription("Choose ivy file and its configurations.");

        tabs = new TabFolder(parent, SWT.BORDER);
        tabs.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        TabItem mainTab = new TabItem(tabs, SWT.NONE);
        mainTab.setText("Main");
        mainTab.setControl(createMainTab(tabs));

        TabItem retrieveTab = new TabItem(tabs, SWT.NONE);
        retrieveTab.setText("Retrieve");
        retrieveTab.setControl(createRetrieveTab(tabs));

        TabItem advancedTab = new TabItem(tabs, SWT.NONE);
        advancedTab.setText("Advanced");
        advancedTab.setControl(createAdvancedTab(tabs));

        tabs.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ivyFilePathText.updateErrorMarker();
                settingsText.updateErrorMarker();
            }
        });

        setControl(tabs);

        loadFromConf();
        checkCompleted();
        tabs.setFocus();
    }

    private Control createMainTab(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite headerComposite = new Composite(composite, SWT.NONE);
        headerComposite.setLayout(new GridLayout(2, false));
        headerComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        settingsProjectSpecificButton = new Button(headerComposite, SWT.CHECK);
        settingsProjectSpecificButton.setText("Enable project specific settings");
        settingsProjectSpecificButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateFieldsStatusSettings();
                conf.ivySettingsPath = settingsText.getSettingsPath();
                settingsUpdated();
            }
        });

        mainGeneralSettingsLink = new Link(headerComposite, SWT.NONE);
        mainGeneralSettingsLink.setFont(headerComposite.getFont());
        mainGeneralSettingsLink.setText("<A>Configure Workspace Settings...</A>");
        mainGeneralSettingsLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(),
                    IvyPreferencePage.PEREFERENCE_PAGE_ID, null, null);
                dialog.open();
            }
        });
        mainGeneralSettingsLink.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        Label horizontalLine = new Label(headerComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

        Composite configComposite = new Composite(composite, SWT.NONE);
        configComposite.setLayout(new GridLayout(3, false));
        configComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Label label = new Label(configComposite, SWT.NONE);
        label.setText("Ivy settings path:");

        settingsText = new SettingsPathText(configComposite, SWT.NONE);
        settingsText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
        settingsText.addListener(new SettingsPathListener() {
            public void settingsPathUpdated(String path) {
                conf.ivySettingsPath = path;
                settingsUpdated();
            }
        });

        horizontalLine = new Label(configComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));

        // Label for ivy file field
        Label pathLabel = new Label(configComposite, SWT.NONE);
        pathLabel.setText("Ivy File");

        ivyFilePathText = new IvyFilePathText(configComposite, SWT.NONE, project);
        ivyFilePathText.addListener(new IvyXmlPathListener() {
            public void ivyXmlPathUpdated(String path) {
                conf.ivyXmlPath = path;
                checkIvyXmlPath();
            }
        });
        ivyFilePathText
                .setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

        // Label for ivy configurations field
        Label confLabel = new Label(configComposite, SWT.NONE);
        confLabel.setText("Configurations");

        // table for configuration selection
        confTableViewer = new ConfTableViewer(configComposite, SWT.NONE);
        confTableViewer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        // refresh
        Button refreshConf = new Button(configComposite, SWT.NONE);
        refreshConf.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        refreshConf.setText("Refresh");
        refreshConf.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                ModuleDescriptor md;
                try {
                    md = conf.getModuleDescriptor();
                } catch (IvyDEException e) {
                    md = null;
                    e.show(IStatus.ERROR, "Ivy configuration error",
                        "The configurations of the ivy.xml could not be retrieved: ");
                }
                confTableViewer.setModuleDescriptor(md);
            }
        });

        return composite;
    }

    private Control createRetrieveTab(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite headerComposite = new Composite(composite, SWT.NONE);
        headerComposite.setLayout(new GridLayout(2, false));
        headerComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        retrieveProjectSpecificButton = new Button(headerComposite, SWT.CHECK);
        retrieveProjectSpecificButton.setText("Enable project specific settings");
        retrieveProjectSpecificButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateFieldsStatusRetrieve();
            }
        });

        retrieveGeneralSettingsLink = new Link(headerComposite, SWT.NONE);
        retrieveGeneralSettingsLink.setFont(composite.getFont());
        retrieveGeneralSettingsLink.setText("<A>Configure Workspace Settings...</A>");
        retrieveGeneralSettingsLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(),
                    IvyPreferencePage.PEREFERENCE_PAGE_ID, null, null);
                dialog.open();
            }
        });
        retrieveGeneralSettingsLink.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        Label horizontalLine = new Label(headerComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

        Composite configComposite = new Composite(composite, SWT.NONE);
        configComposite.setLayout(new GridLayout());
        configComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        retrieveComposite = new RetrieveComposite(configComposite, SWT.NONE);
        retrieveComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        return composite;
    }

    private Control createAdvancedTab(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite headerComposite = new Composite(composite, SWT.NONE);
        headerComposite.setLayout(new GridLayout(2, false));
        headerComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        advancedProjectSpecificButton = new Button(headerComposite, SWT.CHECK);
        advancedProjectSpecificButton.setText("Enable project specific settings");
        advancedProjectSpecificButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateFieldsStatusAdvanced();
            }
        });

        advancedGeneralSettingsLink = new Link(headerComposite, SWT.NONE);
        advancedGeneralSettingsLink.setFont(composite.getFont());
        advancedGeneralSettingsLink.setText("<A>Configure Workspace Settings...</A>");
        advancedGeneralSettingsLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(),
                    IvyPreferencePage.PEREFERENCE_PAGE_ID, null, null);
                dialog.open();
            }
        });
        advancedGeneralSettingsLink.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        Label horizontalLine = new Label(headerComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

        Composite configComposite = new Composite(composite, SWT.NONE);
        configComposite.setLayout(new GridLayout(3, false));
        configComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        resolveInWorkspaceCheck = new Button(configComposite, SWT.CHECK);
        resolveInWorkspaceCheck.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
                false, 3, 1));
        resolveInWorkspaceCheck.setText("Resolve dependencies in workspace (EXPERIMENTAL)");
        resolveInWorkspaceCheck
                .setToolTipText("Will replace jars on the classpath with workspace projects");

        Label label = new Label(configComposite, SWT.NONE);
        label.setText("Order of the classpath entries:");

        alphaOrderCheck = new Combo(configComposite, SWT.READ_ONLY);
        alphaOrderCheck
                .setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
        alphaOrderCheck.setToolTipText("Order of the artifacts in the classpath container");
        alphaOrderCheck.add("From the ivy.xml");
        alphaOrderCheck.add("Lexical");

        acceptedSuffixesTypesComposite = new AcceptedSuffixesTypesComposite(configComposite,
                SWT.NONE);
        acceptedSuffixesTypesComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                true, false, 3, 1));

        return composite;
    }

    void settingsUpdated() {
        try {
            conf.ivySettingsLastModified = -1;
            conf.getIvy();
            settingsText.setSettingsError(null);
        } catch (IvyDEException e) {
            settingsText.setSettingsError(e);
        }
        checkIvyXmlPath();
    }

    private void loadFromConf() {
        ivyFilePathText.init(conf.ivyXmlPath);
        confTableViewer.init(conf.confs);

        IvyDEPreferenceStoreHelper helper = IvyPlugin.getPreferenceStoreHelper();

        if (conf.isSettingsProjectSpecific()) {
            settingsProjectSpecificButton.setSelection(true);
            settingsText.init(conf.ivySettingsPath);
        } else {
            settingsProjectSpecificButton.setSelection(false);
            settingsText.init(helper.getIvySettingsPath());
        }

        if (conf.isRetrieveProjectSpecific()) {
            retrieveProjectSpecificButton.setSelection(true);
            retrieveComposite.init(conf.doRetrieve, conf.retrievePattern, conf.retrieveConfs,
                conf.retrieveTypes, conf.retrieveSync);
        } else {
            retrieveProjectSpecificButton.setSelection(false);
            retrieveComposite.init(helper.getDoRetrieve(), helper.getRetrievePattern(), helper.getRetrieveConfs(), helper.getRetrieveTypes(), helper
                    .getRetrieveSync());
        }

        if (conf.isAdvancedProjectSpecific()) {
            advancedProjectSpecificButton.setSelection(true);
            acceptedSuffixesTypesComposite.init(conf.acceptedTypes, conf.sourceTypes,
                conf.sourceSuffixes, conf.javadocTypes, conf.javadocSuffixes);
            alphaOrderCheck.select(conf.alphaOrder ? 1 : 0);
            resolveInWorkspaceCheck.setSelection(this.conf.resolveInWorkspace);
        } else {
            advancedProjectSpecificButton.setSelection(false);
            acceptedSuffixesTypesComposite.init(helper.getAcceptedTypes(), helper.getSourceTypes(),
                helper.getSourceSuffixes(), helper.getJavadocTypes(), helper.getJavadocSuffixes());
            alphaOrderCheck.select(helper.isAlphOrder() ? 1 : 0);
            resolveInWorkspaceCheck.setSelection(helper.isResolveInWorkspace());
        }

        updateFieldsStatusSettings();
        updateFieldsStatusRetrieve();
        updateFieldsStatusAdvanced();
    }

    void updateFieldsStatusSettings() {
        boolean projectSpecific = settingsProjectSpecificButton.getSelection();
        mainGeneralSettingsLink.setEnabled(!projectSpecific);
        settingsText.setEnabled(projectSpecific);
    }

    void updateFieldsStatusRetrieve() {
        boolean projectSpecific = retrieveProjectSpecificButton.getSelection();
        retrieveGeneralSettingsLink.setEnabled(!projectSpecific);
        retrieveComposite.setEnabled(projectSpecific);
    }

    void updateFieldsStatusAdvanced() {
        boolean projectSpecific = advancedProjectSpecificButton.getSelection();
        advancedGeneralSettingsLink.setEnabled(!projectSpecific);
        acceptedSuffixesTypesComposite.setEnabled(projectSpecific);
        alphaOrderCheck.setEnabled(projectSpecific);
        resolveInWorkspaceCheck.setEnabled(projectSpecific);
    }

    public void initialize(IJavaProject p, IClasspathEntry[] currentEntries) {
        this.project = p;
    }

}