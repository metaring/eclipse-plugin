/**
 *    Copyright 2019 MetaRing s.r.l.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.metaring.plugin.eclipse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.metaring.plugin.ProjectProvider;
import com.metaring.plugin.WorkspaceProvider;

public class EclipseWorkspaceProvider extends WorkspaceProvider {

    public EclipseWorkspaceProvider() {
        super();
    }

    @Override
    protected void startUpdateCallback() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> update());
    }

    @Override
    public final ProjectProvider[] listAllProjects() {
        List<ProjectProvider> list = new ArrayList<>();
        try {
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            for(IProject project : workspaceRoot.getProjects()) {
               String name = project.getName();
               if(name.startsWith("/")) {
                   name = name.substring(1);
               }
               String path = new File(project.getLocationURI()).getAbsolutePath().replace("\\", "/");
               if(path.startsWith("/")) {
                   path = path.substring(1);
               }
               if(!path.endsWith("/")) {
                   path += "/";
               }
               list.add(new EclipseProject(name, path, project));
            }
         } catch(Exception e) {
         }
        return list.toArray(new ProjectProvider[list.size()]);
    }

    private final class EclipseProject extends ProjectProvider {

        private final IProject project;

        private EclipseProject(String name, String path, IProject project) {
            super(name, path);
            this.project = project;
        }

        @Override
        protected final void doRefresh() throws Exception {
            this.project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        }
    }
}