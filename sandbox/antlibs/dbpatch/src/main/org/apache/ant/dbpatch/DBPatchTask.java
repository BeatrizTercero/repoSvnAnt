/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.ant.dbpatch;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.SQLExec;

/**
 * An Ant task that supports maintaining evolving database schema via
 * incremental SQL "patches". Patches are written in a native SQL dialect of the
 * target database. Patch status is tracked via a special
 */
// many things are cloned from SQLExec Ant task...
public class DBPatchTask extends SQLExec {

    static final String PATCH_TABLE_NAME = "patch_tracking";

    protected String patchReleaseId;
    protected File patchIndex;
    protected String patchTableName;

    /**
     * A required file that stores an index of patch SQL files in the order they
     * should be applied.
     */
    public void setPatchIndex(File patchIndex) {
        this.patchIndex = patchIndex;
    }

    /**
     * An optional attribute specifying the table name to store database
     * patches. Default value is "patch_tracking".
     */
    public void setPatchTableName(String patchTableName) {
        this.patchTableName = patchTableName;
    }

    /**
     * An optional release ID String that defines patch files namespace.
     */
    public void setPatchReleaseId(String patchReleaseId) {
        this.patchReleaseId = patchReleaseId;
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setSrc(File srcFile) {
        throw new UnsupportedOperationException(
                "'src' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setOutput(File output) {
        throw new UnsupportedOperationException(
                "'output' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setPrint(boolean print) {
        throw new UnsupportedOperationException(
                "'print' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setAppend(boolean append) {
        throw new UnsupportedOperationException(
                "'append' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setOnerror(OnError action) {
        throw new UnsupportedOperationException(
                "'onerror' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setRdbms(String rdbms) {
        throw new UnsupportedOperationException(
                "'rdbms' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setVersion(String version) {
        throw new UnsupportedOperationException(
                "'version' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setCaching(boolean enable) {
        throw new UnsupportedOperationException(
                "'caching' parameter is not supported");
    }

    /**
     * Blocks super implementation by throwing an UnsupportedOperationException
     * to prevent users from using it.
     */
    public void setExpandProperties(boolean expandProperties) {
        throw new UnsupportedOperationException(
                "'caching' parameter is not supported");
    }

    /**
     * A callback method repeatedly invoked by {@link AntDBPatchRunner} to
     * execute each patch.
     */
    void runPatch(File patchFile) {
        super.setSrc(patchFile);
        runSQL();
    }

    public void execute() throws BuildException {
        validate();
        initDefaults();

        Connection connection = getConnection();
        try {
            DBPatchRunner runner = new AntDBPatchRunner(this, connection,
                    patchIndex, patchTableName, patchReleaseId);
            runner.execute();
        } catch (SQLException e) {
            throw new BuildException("SQL exception " + e.getMessage(), e);
        } catch (IOException e) {
            throw new BuildException("IO exception " + e.getMessage(), e);
        } finally {
            try {
                connection.close();
            } catch (SQLException ignore) {
            }
        }
    }

    void runSQL() throws BuildException {
        super.execute();
    }

    private void initDefaults() {
        if (patchTableName == null) {
            patchTableName = PATCH_TABLE_NAME;
        }
    }

    private void validate() throws BuildException {
        if (patchIndex == null) {
            throw new BuildException("No 'patchIndex' file specified.");
        }

        if (!patchIndex.isFile()) {
            throw new BuildException("Invalid 'patchIndex' file specified: "
                    + patchIndex.getAbsolutePath());
        }
    }
}
