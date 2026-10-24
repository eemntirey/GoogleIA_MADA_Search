using MadaSearch.Data.Database;
using MadaSearch.Data.Migrations;
using MadaSearch.Data.Repositories;
using Xunit;

namespace MadaSearch.Data.Tests;

public class SqliteRepositoryTests : IAsyncLifetime
{
    private readonly string _tempDbPath;
    private readonly SqliteConnectionFactory _factory;
    private readonly MigrationRunner _migrator;

    public SqliteRepositoryTests()
    {
        _tempDbPath = Path.Combine(Path.GetTempPath(), $"madasearch_test_{Guid.NewGuid()}.db");
        _factory = new SqliteConnectionFactory(_tempDbPath);
        _migrator = new MigrationRunner(_factory);
    }

    public async Task InitializeAsync()
    {
        await _migrator.ApplyMigrationsAsync();
    }

    public Task DisposeAsync()
    {
        if (File.Exists(_tempDbPath))
        {
            try { File.Delete(_tempDbPath); } catch { }
        }
        return Task.CompletedTask;
    }

    [Fact]
    public async Task BookmarkRepository_AddAndRetrieve_WorksCorrectly()
    {
        var repo = new BookmarkRepository(_factory);

        var item = await repo.AddAsync("Midi Madagasikara", "https://midi-madagasikara.mg", "Presse");
        Assert.True(item.Id > 0);

        var all = await repo.GetAllAsync();
        Assert.Contains(all, b => b.Url == "https://midi-madagasikara.mg");

        bool exists = await repo.ExistsAsync("https://midi-madagasikara.mg");
        Assert.True(exists);

        await repo.DeleteAsync(item.Id);
        bool existsAfter = await repo.ExistsAsync("https://midi-madagasikara.mg");
        Assert.False(existsAfter);
    }

    [Fact]
    public async Task HistoryRepository_AddAndSearch_WorksCorrectly()
    {
        var repo = new HistoryRepository(_factory);

        await repo.AddEntryAsync("Orange Madagascar", "https://orange.mg");
        await repo.AddEntryAsync("Telma Madagascar", "https://telma.mg");

        var searchResults = await repo.SearchAsync("Orange");
        Assert.Single(searchResults);
        Assert.Equal("https://orange.mg", searchResults[0].Url);

        await repo.ClearAllAsync();
        var recent = await repo.GetRecentAsync();
        Assert.Empty(recent);
    }
}
