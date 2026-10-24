using System.Data.Common;
using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;
using MadaSearch.Data.Database;
using Microsoft.Data.Sqlite;

namespace MadaSearch.Data.Repositories;

public class BookmarkRepository : IBookmarkRepository
{
    private readonly SqliteConnectionFactory _factory;

    public BookmarkRepository(SqliteConnectionFactory factory)
    {
        _factory = factory;
    }

    public async Task<IReadOnlyList<BookmarkItem>> GetAllAsync(CancellationToken ct = default)
    {
        var list = new List<BookmarkItem>();
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "SELECT id, title, url, folder, added_at FROM bookmarks ORDER BY title ASC";

        using var reader = await cmd.ExecuteReaderAsync(ct);
        while (await reader.ReadAsync(ct))
        {
            list.Add(new BookmarkItem
            {
                Id = reader.GetInt64(0),
                Title = reader.GetString(1),
                Url = reader.GetString(2),
                Folder = reader.GetString(3),
                AddedAt = DateTime.Parse(reader.GetString(4))
            });
        }
        return list;
    }

    public async Task<BookmarkItem> AddAsync(string title, string url, string folder = "Favoris", CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = @"
            INSERT INTO bookmarks (title, url, folder, added_at)
            VALUES (@title, @url, @folder, @added_at);
            SELECT last_insert_rowid();";

        cmd.Parameters.AddWithValue("@title", title);
        cmd.Parameters.AddWithValue("@url", url);
        cmd.Parameters.AddWithValue("@folder", folder);
        var now = DateTime.UtcNow;
        cmd.Parameters.AddWithValue("@added_at", now.ToString("o"));

        var scalar = await cmd.ExecuteScalarAsync(ct);
        long newId = Convert.ToInt64(scalar);

        return new BookmarkItem
        {
            Id = newId,
            Title = title,
            Url = url,
            Folder = folder,
            AddedAt = now
        };
    }

    public async Task DeleteAsync(long id, CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "DELETE FROM bookmarks WHERE id = @id";
        cmd.Parameters.AddWithValue("@id", id);
        await cmd.ExecuteNonQueryAsync(ct);
    }

    public async Task<bool> ExistsAsync(string url, CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "SELECT COUNT(1) FROM bookmarks WHERE url = @url";
        cmd.Parameters.AddWithValue("@url", url);
        var count = Convert.ToInt64(await cmd.ExecuteScalarAsync(ct));
        return count > 0;
    }
}
