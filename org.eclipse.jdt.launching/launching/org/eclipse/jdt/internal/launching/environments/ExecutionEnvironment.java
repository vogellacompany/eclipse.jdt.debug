/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.environments;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;

/**
 * A contributed execution environment.
 * 
 * @since 3.2
 */
class ExecutionEnvironment implements IExecutionEnvironment {
	
	private IConfigurationElement fElement;
	
	/**
	 * Set of compatible vms - just the strictly compatible ones
	 */
	private Set fStrictlyCompatible = new HashSet();
	
	/** 
	 * All compatible vms
	 */
	private List fCompatibleVMs = new ArrayList();
	
	/**
	 * default vm install or <code>null</code> if none
	 */
	private IVMInstall fDefault = null;
	
	ExecutionEnvironment(IConfigurationElement element) {
		fElement = element;
	}
	
	private void init() {
		EnvironmentsManager manager = EnvironmentsManager.getDefault();
		manager.initializeCompatibilities();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.environments.IExecutionEnvironment#getId()
	 */
	public String getId() {
		return fElement.getAttribute("id"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.environments.IExecutionEnvironment#getDescription()
	 */
	public String getDescription() {
		return fElement.getAttribute("description"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.environments.IExecutionEnvironment#getCompatibleVMs()
	 */
	public IVMInstall[] getCompatibleVMs() {
		init();
		return (IVMInstall[]) fCompatibleVMs.toArray(new IVMInstall[fCompatibleVMs.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.environments.IExecutionEnvironment#isStrictlyCompatible(org.eclipse.jdt.launching.IVMInstall)
	 */
	public boolean isStrictlyCompatible(IVMInstall vm) {
		init();
		return fStrictlyCompatible.contains(vm);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.environments.IExecutionEnvironment#getDefaultVM()
	 */
	public IVMInstall getDefaultVM() {
		init();
		return fDefault;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.environments.IExecutionEnvironment#setDefaultVM(org.eclipse.jdt.launching.IVMInstall)
	 */
	public void setDefaultVM(IVMInstall vm) {
		init();
		if (vm != null && !fCompatibleVMs.contains(vm)) {
			throw new IllegalArgumentException(MessageFormat.format(EnvironmentMessages.EnvironmentsManager_0, new String[]{getId()}));
		}
		if (vm != null && vm.equals(fDefault)) {
			return;
		}
		fDefault = vm;
		EnvironmentsManager.getDefault().updateDefaultVMs();
	}

	void add(IVMInstall vm, boolean strictlyCompatible) {
		if (fCompatibleVMs.contains(vm)) {
			return;
		}
		fCompatibleVMs.add(vm);
		if (strictlyCompatible) {
			fStrictlyCompatible.add(vm);
		}
	}
	
	void remove(IVMInstall vm) {
		fCompatibleVMs.remove(vm);
		fStrictlyCompatible.remove(vm);
	}
	
	void initDefaultVM(IVMInstall vm) {
		fDefault = vm;
	}
}
