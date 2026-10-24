using MadaSearch.Data.Database;
using Microsoft.Data.Sqlite;
using Microsoft.Extensions.Logging;

namespace MadaSearch.Data.Migrations;

public class MigrationRunner
{
    private readonly SqliteConnectionFactory _factory;
    private readonly ILogger<MigrationRunner>? _logger;

    public MigrationRunner(SqliteConnectionFactory factory, ILogger<MigrationRunner>? logger = null)
    {
        _factory = factory;
        _logger = logger;
    }

    public async Task ApplyMigrationsAsync(CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        await using var tx = conn.BeginTransaction();

        try
        {
            // Table de versionnement
            using var cmdVersion = conn.CreateCommand();
            cmdVersion.Transaction = tx;
            cmdVersion.CommandText = @"
                CREATE TABLE IF NOT EXISTS schema_migrations (
                    version INTEGER PRIMARY KEY,
                    applied_at TEXT NOT NULL
                );";
            await cmdVersion.ExecuteNonQueryAsync(ct);

            var appliedVersions = new HashSet<int>();
            using (var readCmd = conn.CreateCommand())
            {
                readCmd.Transaction = tx;
                readCmd.CommandText = "SELECT version FROM schema_migrations";
                using var reader = await readCmd.ExecuteReaderAsync(ct);
                while (await reader.ReadAsync(ct))
                {
                    appliedVersions.Add(reader.GetInt32(0));
                }
            }

            // Migration 1 : Tables de base
            if (!appliedVersions.Contains(1))
            {
                _logger?.LogInformation("Application de la migration 1 : Initial Schema");

                using var mig1 = conn.CreateCommand();
                mig1.Transaction = tx;
                mig1.CommandText = @"
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        url TEXT NOT NULL,
                        folder TEXT NOT NULL DEFAULT 'Favoris',
                        added_at TEXT NOT NULL
                    );
                    CREATE INDEX IF NOT EXISTS idx_bookmarks_url ON bookmarks(url);

                    CREATE TABLE IF NOT EXISTS history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        url TEXT NOT NULL,
                        visited_at TEXT NOT NULL,
                        visit_count INTEGER NOT NULL DEFAULT 1
                    );
                    CREATE INDEX IF NOT EXISTS idx_history_visited_at ON history(visited_at DESC);
                    CREATE INDEX IF NOT EXISTS idx_history_url ON history(url);

                    CREATE TABLE IF NOT EXISTS workspaces (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        color_hex TEXT NOT NULL,
                        icon TEXT NOT NULL,
                        is_default INTEGER NOT NULL DEFAULT 0,
                        created_at TEXT NOT NULL
                    );

                    CREATE TABLE IF NOT EXISTS session_tabs (
                        id TEXT PRIMARY KEY,
                        workspace_id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        url TEXT NOT NULL,
                        group_name TEXT NULL,
                        sort_order INTEGER NOT NULL DEFAULT 0
                    );

                    INSERT INTO schema_migrations (version, applied_at)
                    VALUES (1, datetime('now'));
                ";
                await mig1.ExecuteNonQueryAsync(ct);
            }

            await tx.CommitAsync(ct);
            _logger?.LogInformation("Migrations de la base de données appliquées avec succès.");
        }
        catch (Exception ex)
        {
            _logger?.LogError(ex, "Échec de l'application des migrations SQLite.");
            await tx.RollbackAsync(ct);
            throw;
        }
    }
}
