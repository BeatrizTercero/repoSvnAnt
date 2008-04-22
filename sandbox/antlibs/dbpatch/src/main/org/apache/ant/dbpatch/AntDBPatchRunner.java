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
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tools.ant.Project;

/**
 * A DataPatchRunner that uses Ant loggers.
 * 
 * @author andrus
 */
class AntDBPatchRunner extends DBPatchRunner {
	private DBPatchTask parentTask;

	public AntDBPatchRunner(DBPatchTask parentTask, Connection connection,
			File patchIndex, String patchTableName, String patchReleaseId) {
		super(connection, patchIndex, patchTableName, patchReleaseId);
		this.parentTask = parentTask;
	}

	void log(String message) {
		parentTask.log(message);
	}

	void logVerbose(String message) {
		parentTask.log(message, Project.MSG_VERBOSE);
	}

	void applyPatch(String patchName) throws SQLException {
		File patchFile = new File(patchDir, patchName);
		if (!patchFile.isFile()) {
			throw new SQLException("Missing or invalid patch file: "
					+ patchFile.getAbsolutePath());
		}

		logVerbose("Will run patch: " + patchName);

		parentTask.setSrc(patchFile);
		parentTask.runSQL();
	}
}
