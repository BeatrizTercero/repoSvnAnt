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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * A runner for the DataPatchTask.
 * 
 * @author andrus
 */
abstract class DBPatchRunner {

	static final String PATCH_UTILITY_VERSION = "1.0.0";

	private Connection connection;
	private File patchIndex;
	private String patchTableName;
	private String patchReleaseId;
	protected File patchDir;

	DBPatchRunner(Connection connection, File patchIndex,
			String patchTableName, String patchReleaseId) {

		this.connection = connection;
		this.patchTableName = patchTableName;
		this.patchReleaseId = patchReleaseId;

		this.patchIndex = patchIndex;
		this.patchDir = patchIndex.getParentFile();

		if (patchDir == null || !patchDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid patch directory: "
					+ patchDir);
		}
	}

	abstract void log(String message);

	abstract void logVerbose(String message);

	void execute() throws SQLException, IOException {

		setupPatchTable();

		Collection patches = new LinkedHashSet();
		initNewPatchList(patches);
		int maxId = filterOutAppliedPatches(patches);
		applyPatches(patches, maxId + 1);
	}

	private void setupPatchTable() throws SQLException {
		String tableName = patchTableName;
		String schema = null;

		int dot = patchTableName.indexOf('.');
		if (dot > 0 && dot < patchTableName.length() - 1) {
			schema = patchTableName.substring(0, dot);
			tableName = patchTableName.substring(dot + 1);
		}

		boolean patchTableExists;
		ResultSet rs = connection.getMetaData().getTables(null, schema,
				tableName, null);
		try {
			patchTableExists = rs.next();
		} finally {
			rs.close();
		}

		if (!patchTableExists) {
			StringBuilder createTable = new StringBuilder();
			createTable.append("CREATE TABLE ");
			createTable.append(patchTableName);
			createTable.append(" (ID INTEGER NOT NULL,");
			createTable.append(" PATCH_NAME VARCHAR(100),");
			createTable.append(" PATCH_RELEASE_ID VARCHAR(20),");
			createTable.append(" APPLIED_TIMESTAMP TIMESTAMP,");
			createTable.append(" PATCH_UTILITY_VERSION VARCHAR(20))");

			String createTableString = createTable.toString();
			logVerbose(createTableString);

			PreparedStatement st = connection
					.prepareStatement(createTableString);
			try {
				st.execute();
			} finally {
				st.close();
			}
		}
	}

	private void initNewPatchList(Collection patches) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(patchIndex));

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}

				if (line.startsWith("#") || line.startsWith("//")) {
					continue;
				}

				patches.add(line);
			}
		} finally {
			reader.close();
		}
	}

	private int filterOutAppliedPatches(Collection patches) throws SQLException {
		PreparedStatement st = connection
				.prepareStatement("SELECT ID, PATCH_NAME FROM "
						+ patchTableName
						+ " WHERE PATCH_RELEASE_ID = ? ORDER BY ID");

		try {
			st.setString(1, patchReleaseId);

			int lastId = 0;

			ResultSet rs = st.executeQuery();
			try {
				while (rs.next()) {
					lastId = rs.getInt(1);
					String appliedPatch = rs.getString(2);

					if (patches.remove(appliedPatch)) {
						logVerbose("Previously applied patch: " + appliedPatch
								+ ", ignoring");
					}
				}
			} finally {
				rs.close();
			}

			return lastId;

		} finally {
			st.close();
		}
	}

	private void applyPatches(Collection patches, int startId)
			throws SQLException {
		if (patches.isEmpty()) {
			return;
		}

		PreparedStatement patchSt = connection
				.prepareStatement("INSERT INTO "
						+ patchTableName
						+ " (ID, PATCH_NAME, PATCH_RELEASE_ID, APPLIED_TIMESTAMP, PATCH_UTILITY_VERSION) "
						+ "VALUES (?, ?, ?, ?, ?)");
		try {

			Iterator it = patches.iterator();
			while (it.hasNext()) {
				
				String patch = (String) it.next();

				applyPatch(patch);

				// insert patch record...
				patchSt.setInt(1, startId++);
				patchSt.setString(2, patch);
				patchSt.setString(3, patchReleaseId);
				patchSt.setTimestamp(4, new Timestamp(System
						.currentTimeMillis()));
				patchSt.setString(5, PATCH_UTILITY_VERSION);
				patchSt.executeUpdate();

				// commit each patch record separately...
				connection.commit();
			}
		} finally {
			patchSt.close();
		}
	}

	abstract void applyPatch(String patchName) throws SQLException;
}
