using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;
using MadaSearch.Data.Database;
using Microsoft.Data.Sqlite;

namespace MadaSearch.Data.Repositories;

public class HistoryRepository : IHistoryRepository
{
    private readonly SqliteConnectionFactory _factory;

    public HistoryRepository(SqliteConnectionFactory factory)
    {
        _factory = factory;
    }

    public async Task<IReadOnlyList<HistoryEntry>> GetRecentAsync(int limit = 100, CancellationToken ct = default)
    {
        var list = new List<HistoryEntry>();
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "SELECT id, title, url, visited_at, visit_count FROM history ORDER BY visited_at DESC LIMIT @limit";
        cmd.Parameters.AddWithValue("@limit", limit);

        using var reader = await cmd.ExecuteReaderAsync(ct);
        while (await reader.ReadAsync(ct))
        {
            list.Add(new HistoryEntry
            {
                Id = reader.GetInt64(0),
                Title = reader.GetString(1),
                Url = reader.GetString(2),
                VisitedAt = DateTime.Parse(reader.GetString(3)),
                VisitCount = reader.GetInt32(4)
            });
        }
        return list;
    }

    public async Task<IReadOnlyList<HistoryEntry>> SearchAsync(string query, CancellationToken ct = default)
    {
        var list = new List<HistoryEntry>();
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = @"
            SELECT id, title, url, visited_at, visit_count
            FROM history
            WHERE title LIKE @q OR url LIKE @q
            ORDER BY visited_at DESC LIMIT 50";
        cmd.Parameters.AddWithValue("@q", $"%{query}%");

        using var reader = await cmd.ExecuteReaderAsync(ct);
        while (await reader.ReadAsync(ct))
        {
            list.Add(new HistoryEntry
            {
                Id = reader.GetInt64(0),
                Title = reader.GetString(1),
                Url = reader.GetString(2),
                VisitedAt = DateTime.Parse(reader.GetString(3)),
                VisitCount = reader.GetInt32(4)
            });
        }
        return list;
    }

    public async Task AddEntryAsync(string title, string url, CancellationToken ct = default)
    {
        if (string.IsNullOrWhiteSpace(url) || url.StartsWith("madasearch://", StringComparison.OrdinalIgnoreCase))
            return;

        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = @"
            INSERT INTO history (title, url, visited_at, visit_count)
            VALUES (@title, @url, @visited_at, 1)
            ON CONFLICT(id) DO UPDATE SET
                visited_at = @visited_at,
                visit_count = visit_count + 1,
                title = @title;";

        cmd.Parameters.AddWithValue("@title", string.IsNullOrWhiteSpace(title) ? url : title);
        cmd.Parameters.AddWithValue("@url", url);
        cmd.Parameters.AddWithValue("@visited_at", DateTime.UtcNow.ToString("o"));

        await cmd.ExecuteNonQueryAsync(ct);
    }

    public async Task DeleteEntryAsync(long id, CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "DELETE FROM history WHERE id = @id";
        cmd.Parameters.AddWithValue("@id", id);
        await cmd.ExecuteNonQueryAsync(ct);
    }

    public async Task ClearAllAsync(CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "DELETE FROM history";
        await cmd.ExecuteNonQueryAsync(ct);
    }

    public async Task ClearTimeRangeAsync(DateTime from, DateTime to, CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "DELETE FROM history WHERE visited_at >= @from AND visited_at <= @to";
        cmd.Parameters.AddWithValue("@from", from.ToString("o"));
        cmd.Parameters.AddWithValue("@to", to.ToString("o"));
        await cmd.ExecuteNonQueryAsync(ct);
    }
}
