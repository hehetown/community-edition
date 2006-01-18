/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.admin.patch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.domain.AppliedPatch;
import org.alfresco.service.cmr.admin.PatchException;
import org.alfresco.service.cmr.admin.PatchInfo;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Manages patches applied against the repository.
 * <p>
 * Patches are injected into this class and any attempted applications are recorded
 * for later auditing.
 * 
 * @since 1.2
 * @author Derek Hulley
 */
public class PatchServiceImpl implements PatchService
{
    private static Log logger = LogFactory.getLog(PatchServiceImpl.class);
    
    private DescriptorService descriptorService;
    private PatchDaoService patchDaoService;
    private List<Patch> patches;

    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }

    public void setPatchDaoService(PatchDaoService patchDaoService)
    {
        this.patchDaoService = patchDaoService;
    }

    public void setPatches(List<Patch> patches)
    {
        this.patches = patches;
    }
    
    public List<PatchInfo> getAppliedPatches()
    {
        // get all the persisted patches
        List<AppliedPatch> appliedPatches = patchDaoService.getAppliedPatches();
        List<PatchInfo> patchInfos = new ArrayList<PatchInfo>(appliedPatches.size());
        for (AppliedPatch patch : appliedPatches)
        {
            PatchInfo patchInfo = new PatchInfo(patch);
            patchInfos.add(patchInfo);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved list of " + patchInfos.size() + " applied patches: \n");
        }
        return patchInfos;
    }
    
    public boolean applyOutstandingPatches()
    {
        // construct a map of all known patches by ID
        Map<String, Patch> allPatchesById = new HashMap<String, Patch>(23);
        for (Patch patch : patches)
        {
            allPatchesById.put(patch.getId(), patch);
        }
        // construct a list of executed patches by ID
        Map<String, PatchInfo> appliedPatchInfosById = new HashMap<String, PatchInfo>(23);
        List<PatchInfo> appliedPatches = getAppliedPatches();
        for (PatchInfo patchInfo : appliedPatches)
        {
            // ignore unsuccessful attempts - we need to try them again
            if (!patchInfo.getSucceeded())
            {
                continue;
            }
            appliedPatchInfosById.put(patchInfo.getId(), patchInfo);
        }
        // go through all the patches and apply them where necessary
        boolean success = true;
        for (Patch patch : allPatchesById.values())
        {
            // apply the patch
            success = applyPatchAndDependencies(patch, appliedPatchInfosById);
            if (!success)
            {
                // we failed to apply a patch or one of its dependencies - terminate
                break;
            }
        }
        // done
        return success;
    }
    
    /**
     * Reentrant method that ensures that a patch and all its dependencies get applied.
     * The process terminates on the first failure.
     * 
     * @param patchInfos all the executed patch data.  If there was a failure, then this
     *      is the list of successful executions only.
     * @param patch the patch (containing dependencies) to apply
     * @param appliedPatchInfosById already applied patches
     * @return Returns true if the patch and all its dependencies were successfully applied.
     */
    private boolean applyPatchAndDependencies(Patch patch, Map<String, PatchInfo> appliedPatchInfosById)
    {
        String id = patch.getId();
        // check if it has already been done
        PatchInfo patchInfo = appliedPatchInfosById.get(id); 
        if (patchInfo != null && patchInfo.getSucceeded())
        {
            // this has already been done
            return true;
        }
        
        // ensure that dependencies have been done
        List<Patch> dependencies = patch.getDependsOn();
        for (Patch dependencyPatch : dependencies)
        {
            boolean success = applyPatchAndDependencies(dependencyPatch, appliedPatchInfosById);
            if (!success)
            {
                // a patch failed to be applied
                return false;
            }
        }
        // all the dependencies were successful
        patchInfo = applyPatch(patch);
        if (!patchInfo.getSucceeded())
        {
            // this was a failure
            return false;
        }
        else
        {
            // it was successful - add it to the map of successful patches
            appliedPatchInfosById.put(id, patchInfo);
            return true;
        }
    }
    
    private PatchInfo applyPatch(Patch patch)
    {
        // get the patch from the DAO
        AppliedPatch appliedPatch = patchDaoService.getAppliedPatch(patch.getId());
        if (appliedPatch != null && appliedPatch.getSucceeded())
        {
            // it has already been applied
            PatchInfo patchInfo = new PatchInfo(appliedPatch);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Patch was already successfully applied: \n" +
                        "   patch: " + patchInfo);
            }
            return patchInfo;
        }
        // the execution report
        String report = null;
        boolean success = false;
        // first check whether the patch is relevant to the repo
        Descriptor repoDescriptor = descriptorService.getRepositoryDescriptor();
        boolean applies = applies(repoDescriptor, patch);
        if (!applies)
        {
            // create a dummy report
            StringBuilder sb = new StringBuilder(128);
            sb.append("Patch ").append(patch.getId()).append(" was not relevant.");
            report = sb.toString();
            success = true;             // this succeeded because it didn't need to be applied
        }
        else
        {
            // perform actual execution
            try
            {
                report = patch.apply();
                success = true;
            }
            catch (PatchException e)
            {
                // failed
                report = e.getMessage();
                success = false;
            }
        }
        // create a record for the execution
        appliedPatch = patchDaoService.newAppliedPatch(patch.getId());
        // fill in the record's details
        appliedPatch.setDescription(patch.getDescription());
        appliedPatch.setApplyToVersion(patch.getApplyToVersion());
        appliedPatch.setSucceeded(success);
        appliedPatch.setAppliedOnVersion(repoDescriptor.getVersion());
        appliedPatch.setAppliedOnDate(new Date());
        appliedPatch.setReport(report);
        // create the info for returning
        PatchInfo patchInfo = new PatchInfo(appliedPatch);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Applied patch: \n" + patchInfo);
        }
        return patchInfo;
    }
    
    /**
     * Check whether or not the patch should be applied to the repository, given the descriptor.
     * This helper is required to 
     * 
     * @param repoDescriptor contains the version details of the repository
     * @param patch the patch whos version must be checked
     * @return Returns true if the patch should be applied to the repository
     */
    private boolean applies(Descriptor repoDescriptor, Patch patch)
    {
        // resolve the version numbers of the repo
        int[] repoVersions = new int[] {0, 0, 0};
        try
        {
            if (repoDescriptor.getVersionMajor() != null)
            {
                repoVersions[0] = Integer.parseInt(repoDescriptor.getVersionMajor());
            }
            if (repoDescriptor.getVersionMinor() != null)
            {
                repoVersions[1] = Integer.parseInt(repoDescriptor.getVersionMinor());
            }
            if (repoDescriptor.getVersionRevision() != null)
            {
                repoVersions[2] = Integer.parseInt(repoDescriptor.getVersionRevision());
            }
        }
        catch (NumberFormatException e)
        {
            throw new AlfrescoRuntimeException("Non-numeric repository version are not allowed: " +
                    repoDescriptor.getVersion());
        }

        // by default, the patch won't apply.  All the revision numbers will have to be greater
        // than that of the repo to apply
        int[] applyToVersions = new int[] {0, 0, 0};
        try
        {
            // break the patch version up
            String applyToVersion = patch.getApplyToVersion();
            StringTokenizer tokens = new StringTokenizer(applyToVersion, ".");
            for (int i = 0; i < 3 && tokens.hasMoreTokens(); i++)
            {
                String versionStr = tokens.nextToken();
                applyToVersions[i] = Integer.parseInt(versionStr);
            }
        }
        catch (NumberFormatException e)
        {
            throw new AlfrescoRuntimeException("Non-numeric patch apply version: " + patch);
        }
        
        // compare each version number
        // in the event of a match, we don't apply as the patch version is the 
        boolean apply = true;
        for (int i = 0; i < 3; i++)
        {
            if (repoVersions[i] < applyToVersions[i])
            {
                // repo is old and patch is newer - we definitely need to apply
                apply = true;
                break;
            }
            else if (repoVersions[i] > applyToVersions[i])
            {
                // the repo version number is high enough (repo is new enough) that patch doesn't apply
                apply = false;
                break;
            }
            else
            {
                // so far, the two match
            }
        }
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Patch version number check against repo version: \n" +
                    "   repo version: " + repoDescriptor.getVersion() + "\n" +
                    "   patch: " + patch);
        }
        return apply;
    }
}
