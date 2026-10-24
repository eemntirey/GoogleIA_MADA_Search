using Microsoft.Data.Sqlite;
using Microsoft.Extensions.Logging;

namespace MadaSearch.Data.Database;

public class SqliteConnectionFactory
{
    private readonly string _connectionString;
    private readonly ILogger<SqliteConnectionFactory>? _logger;

    public SqliteConnectionFactory(string databasePath, ILogger<SqliteConnectionFactory>? logger = null)
    {
        _logger = logger;
        var directory = Path.GetDirectoryName(databasePath);
        if (!string.IsNullOrEmpty(directory) && !Directory.Exists(directory))
        {
            Directory.CreateDirectory(directory);
        }

        var builder = new SqliteConnectionStringBuilder
        {
            DataSource = databasePath,
            Mode = SqliteOpenMode.ReadWriteCreate,
            Cache = SqliteCacheMode.Shared
        };
        _connectionString = builder.ToString();
    }

    public async Task<SqliteConnection> CreateConnectionAsync(CancellationToken ct = default)
    {
        var conn = new SqliteConnection(_connectionString);
        await conn.OpenAsync(ct);
        using var pragma = conn.CreateCommand();
        pragma.CommandText = "PRAGMA journal_mode = WAL; PRAGMA foreign_keys = ON;";
        await pragma.ExecuteNonQueryAsync(ct);
        return conn;
    }
}
