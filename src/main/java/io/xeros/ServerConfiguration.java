package io.xeros;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.xeros.sql.DatabaseCredentials;

/**
 * Contains the configuration stored in YAML format.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class ServerConfiguration {

    public static final String CONFIGURATION_FILE = "config.yaml";

    public static ServerConfiguration getDefault() {
        ServerConfiguration configuration = new ServerConfiguration();
        configuration.serverState = ServerState.DEBUG;
        return configuration;
    }

    @JsonProperty("server_state")
    private ServerState serverState;

    @JsonProperty("embedded_password")
    private String embeddedPassword;

    @JsonProperty("local_database")
    private DatabaseCredentials localDatabase;

    @JsonProperty("store_database")
    private DatabaseCredentials storeDatabase;

    @JsonProperty("vote_database")
    private DatabaseCredentials voteDatabase;

    @JsonProperty("hiscores_database")
    private DatabaseCredentials hiscoresDatabase;

    @JsonProperty("backup_ftp_credentials")
    private DatabaseCredentials backupFtpCredentials;

    /**
     * Path to the folder that contains mysqldump.exe (if applicable), include a leading slash.
     */
    @JsonProperty("mysqldump_path")
    private String mysqlDumpPath;

    public ServerConfiguration(ServerState serverState, String embeddedPassword, DatabaseCredentials storeDatabase,
                               DatabaseCredentials voteDatabase) {
        this.serverState = serverState;
        this.embeddedPassword = embeddedPassword;
        this.storeDatabase = storeDatabase;
        this.voteDatabase = voteDatabase;
    }

    public ServerConfiguration() {
    }

    @JsonIgnore
    public boolean isDisplayNamesDisabled() {
        return !isLocalDatabaseEnabled() || Configuration.DISABLE_DISPLAY_NAMES;
    }

    @JsonIgnore
    public boolean isLocalDatabaseEnabled() {
        return serverState.isSqlEnabled() && localDatabase != null;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    public String getEmbeddedPassword() {
        return embeddedPassword;
    }

    public void setEmbeddedPassword(String embeddedPassword) {
        this.embeddedPassword = embeddedPassword;
    }

    public DatabaseCredentials getLocalDatabase() {
        return localDatabase;
    }

    public void setLocalDatabase(DatabaseCredentials localDatabase) {
        this.localDatabase = localDatabase;
    }

    public DatabaseCredentials getStoreDatabase() {
        return storeDatabase;
    }

    public void setStoreDatabase(DatabaseCredentials storeDatabase) {
        this.storeDatabase = storeDatabase;
    }

    public DatabaseCredentials getVoteDatabase() {
        return voteDatabase;
    }

    public void setVoteDatabase(DatabaseCredentials voteDatabase) {
        this.voteDatabase = voteDatabase;
    }

    public DatabaseCredentials getHiscoresDatabase() {
        return hiscoresDatabase;
    }

    public void setHiscoresDatabase(DatabaseCredentials hiscoresDatabase) {
        this.hiscoresDatabase = hiscoresDatabase;
    }

    public DatabaseCredentials getBackupFtpCredentials() {
        return backupFtpCredentials;
    }

    public void setBackupFtpCredentials(DatabaseCredentials backupFtpCredentials) {
        this.backupFtpCredentials = backupFtpCredentials;
    }

    public String getMysqlDumpPath() {
        return mysqlDumpPath;
    }
}
