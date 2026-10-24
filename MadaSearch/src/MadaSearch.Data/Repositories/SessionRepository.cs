using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;
using MadaSearch.Data.Database;
using Microsoft.Data.Sqlite;

namespace MadaSearch.Data.Repositories;

public class SessionRepository : ISessionService
{
    private readonly SqliteConnectionFactory _factory;

    public SessionRepository(SqliteConnectionFactory factory)
    {
        _factory = factory;
    }

    public async Task SaveSessionAsync(IEnumerable<TabItemModel> tabs, Guid activeWorkspaceId, CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        await using var tx = conn.BeginTransaction();

        // Nettoyage de la session précédente
        using var clearCmd = conn.CreateCommand();
        clearCmd.Transaction = tx;
        clearCmd.CommandText = "DELETE FROM session_tabs";
        await clearCmd.ExecuteNonQueryAsync(ct);

        int order = 0;
        foreach (var tab in tabs)
        {
            // Ne pas enregistrer les onglets privés dans la session persistante
            if (tab.IsPrivate) continue;

            using var insertCmd = conn.CreateCommand();
            insertCmd.Transaction = tx;
            insertCmd.CommandText = @"
                INSERT INTO session_tabs (id, workspace_id, title, url, group_name, sort_order)
                VALUES (@id, @ws, @title, @url, @group, @order);";

            insertCmd.Parameters.AddWithValue("@id", tab.Id.ToString());
            insertCmd.Parameters.AddWithValue("@ws", (tab.WorkspaceId == Guid.Empty ? activeWorkspaceId : tab.WorkspaceId).ToString());
            insertCmd.Parameters.AddWithValue("@title", tab.Title);
            insertCmd.Parameters.AddWithValue("@url", tab.Url);
            insertCmd.Parameters.AddWithValue("@group", (object?)tab.GroupName ?? DBNull.Value);
            insertCmd.Parameters.AddWithValue("@order", order++);

            await insertCmd.ExecuteNonQueryAsync(ct);
        }

        await tx.CommitAsync(ct);
    }

    public async Task<(IReadOnlyList<TabItemModel> tabs, Guid activeWorkspaceId)?> RestoreSessionAsync(CancellationToken ct = default)
    {
        var list = new List<TabItemModel>();
        Guid wsId = Guid.Empty;

        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "SELECT id, workspace_id, title, url, group_name FROM session_tabs ORDER BY sort_order ASC";

        using var reader = await cmd.ExecuteReaderAsync(ct);
        while (await reader.ReadAsync(ct))
        {
            var tabWs = Guid.Parse(reader.GetString(1));
            if (wsId == Guid.Empty) wsId = tabWs;

            list.Add(new TabItemModel
            {
                Id = Guid.Parse(reader.GetString(0)),
                WorkspaceId = tabWs,
                Title = reader.GetString(2),
                Url = reader.GetString(3),
                GroupName = reader.IsDBNull(4) ? null : reader.GetString(4),
                IsPrivate = false
            });
        }

        if (list.Count == 0) return null;
        return (list, wsId);
    }

    public async Task ClearSessionAsync(CancellationToken ct = default)
    {
        await using var conn = await _factory.CreateConnectionAsync(ct);
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "DELETE FROM session_tabs";
        await cmd.ExecuteNonQueryAsync(ct);
    }
}
